# HR 核心代码展示说明

## 项目定位

本项目是基于 RuoYi-Vue 二次开发的企业级前后端分离系统，当前主线已收敛为 IT 设备管理与 Linux 服务器 SSH 监控平台。

核心业务闭环：

1. 设备资产入库和生命周期维护。
2. Linux 服务器 SSH 纳管。
3. 采集 CPU、内存、磁盘、负载、进程等运行数据。
4. 页面展示监控状态、采集结果、告警事件。
5. 支持连接测试、立即采集、预设远程命令下发和数据导出。

## 技术栈对应

| 描述要求 | 当前实现 |
| --- | --- |
| RuoYi-Vue 前后端分离 | 后端沿用 RuoYi 多模块结构，前端使用 `ruoyi-ui-vue3` |
| Spring Boot 3 RESTful 接口 | 后端已调整为 Spring Boot 3.4.5，监控接口位于 `/monitor/server` |
| MyBatis 数据持久化 | Mapper XML 管理资产、服务器、监控数据、告警数据 SQL |
| Spring Security + JWT + RBAC | 沿用 RuoYi 权限体系，接口使用 `@PreAuthorize` 控制权限 |
| SSH 采集 Linux 运行数据 | `SshMonitorCollector` 基于 sshj 执行 Linux 命令并解析指标 |
| 异常提醒 | 采集失败会写入连接失败状态并生成告警事件 |
| 远程指令下发 | 提供磁盘、内存、负载、进程、端口等预设命令 |
| Docker 部署 | Compose 编排 Nginx、后端、MySQL、Redis |

## 项目文件架构

```text
asset-management/
├── ruoyi-admin/              # 后端启动模块和 Web Controller
│   └── src/main/java/com/ruoyi/web/controller/monitor/
├── ruoyi-common/             # 通用工具、注解、序列化、安全基础能力
├── ruoyi-framework/          # Spring Security、JWT、Druid、Web 配置
├── ruoyi-system/             # 资产、服务器、监控、告警核心业务模块
│   ├── src/main/java/com/ruoyi/system/service/impl/
│   ├── src/main/java/com/ruoyi/system/service/monitor/
│   └── src/main/resources/mapper/system/
├── ruoyi-quartz/             # 定时任务，周期性采集服务器监控数据
├── ruoyi-generator/          # RuoYi 代码生成模块，保留但非当前主线
├── ruoyi-ui-vue3/            # Vue3 + Vite + Element Plus 前端
├── sql/                      # 数据库初始化脚本
├── docker/                   # Nginx、MySQL、Redis 配置
├── docker-compose.yml        # 本地/演示环境编排
└── README.md                 # 项目说明与部署手册
```

## 建议给 HR 展示的核心代码

### 1. RESTful 接口与 RBAC 权限控制

文件：`ruoyi-admin/src/main/java/com/ruoyi/web/controller/monitor/MonitorServerController.java`

展示点：

- `@RestController` 暴露服务器监控 REST API。
- `@PreAuthorize` 接入 RuoYi 的 RBAC 权限体系。
- 提供连接测试、立即采集、远程命令下发等核心接口。

代表代码：

```java
@PreAuthorize("@ss.hasPermi('monitor:server:edit')")
@Log(title = "SSH监控采集", businessType = BusinessType.UPDATE)
@PostMapping("/{id}/collect")
public AjaxResult collect(@PathVariable("id") Long id) {
    return success(monitorServerService.collectSshMonitorData(id));
}

@PreAuthorize("@ss.hasPermi('monitor:server:edit')")
@Log(title = "远程命令下发", businessType = BusinessType.UPDATE)
@PostMapping("/{id}/command")
public AjaxResult executeCommand(@PathVariable("id") Long id, @RequestBody Map<String, String> body) {
    return success(monitorServerService.executePresetCommand(id, body.get("commandKey")));
}
```

### 2. 服务器纳管、采集和告警闭环

文件：`ruoyi-system/src/main/java/com/ruoyi/system/service/impl/MonitorServerServiceImpl.java`

展示点：

- 新增/修改服务器时统一做 IP 唯一性、默认 SSH 端口、状态初始化。
- SSH 密码加密后存储。
- 采集失败时更新服务器状态并生成告警。

代表代码：

```java
@Override
@Transactional
public MonitorData collectSshMonitorData(Long id) {
    MonitorServer server = authServer(id);
    try {
        MonitorData data = sshMonitorCollector.collect(server);
        return monitorDataService.reportMonitorData(data);
    } catch (ServiceException e) {
        markConnectionFailure(server, e.getMessage());
        throw e;
    }
}
```

### 3. SSH 采集 Linux 基础运行指标

文件：`ruoyi-system/src/main/java/com/ruoyi/system/service/monitor/SshMonitorCollector.java`

展示点：

- 基于 sshj 建立 SSH 连接。
- 通过 `/proc/stat`、`/proc/meminfo`、`df`、`/proc/loadavg` 等 Linux 原生命令采集指标。
- 将命令输出解析成 `MonitorData`，供图表和告警使用。

代表代码：

```java
long[] firstCpu = readCpuSample(ssh);
sleepQuietly(1000);
long[] secondCpu = readCpuSample(ssh);
data.setCpuUsage(calculateCpuUsage(firstCpu, secondCpu));

applyMemory(data, execute(ssh,
        "awk '/MemTotal/{t=$2}/MemAvailable/{a=$2}END{printf \"%.2f %.2f %.2f\", (t-a)/t*100, (t-a)/1048576, t/1048576}' /proc/meminfo",
        COMMAND_TIMEOUT_SECONDS));

applyDisk(data, execute(ssh,
        "df -Pk / | awk 'NR==2{printf \"%.2f %.2f %.2f\", $5+0, $3/1048576, $2/1048576}'",
        COMMAND_TIMEOUT_SECONDS));
```

### 4. SSH 凭据加密

文件：`ruoyi-system/src/main/java/com/ruoyi/system/service/monitor/SshCredentialCipher.java`

展示点：

- 使用 AES/GCM 加密 SSH 密码。
- 密钥通过 `SSH_CREDENTIAL_KEY` 环境变量注入，不写死在代码里。
- 支持已加密数据幂等保存，避免重复加密。

代表代码：

```java
Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
cipher.init(Cipher.ENCRYPT_MODE, keySpec(), new GCMParameterSpec(TAG_LENGTH_BITS, iv));
byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
return PREFIX + Base64.getEncoder().encodeToString(buffer.array());
```

### 5. MyBatis 查询与敏感字段隔离

文件：`ruoyi-system/src/main/resources/mapper/system/MonitorServerMapper.xml`

展示点：

- 服务器列表左关联资产表，展示资产编号/名称。
- 普通列表查询不返回 `ssh_password`。
- 只有内部鉴权采集查询 `selectMonitorServerAuthById` 才读取密文密码。

代表代码：

```xml
<sql id="selectMonitorServerVo">
    SELECT ms.id, ms.asset_id, ai.asset_code, ai.asset_name,
           ms.server_ip, ms.hostname, ms.os_name, ms.cpu_cores, ms.memory_gb, ms.disk_gb,
           ms.monitor_status, ms.last_collect_time, ms.connection_status, ms.ssh_port,
           ms.ssh_username, ms.agent_path, ms.agent_enabled, ms.del_flag, ms.remark,
           ms.create_by, ms.create_time, ms.update_by, ms.update_time
    FROM monitor_server ms
    LEFT JOIN asset_info ai ON ai.id = ms.asset_id AND ai.del_flag = '0'
</sql>
```

### 6. 前端监控交互页面

文件：`ruoyi-ui-vue3/src/views/monitor/Server.vue`

展示点：

- 服务器列表、状态标签、连接测试、立即采集、远程命令弹窗。
- 新增/编辑服务器时录入 SSH 连接信息。
- 通过 API 模块调用后端 REST 接口。

代表代码：

```js
async function collectNow(row) {
  await collectServer(row.id)
  ElMessage.success('采集完成，监控数据已入库')
  loadList()
}

async function runCommand() {
  if (!activeServer.value) return
  commandLoading.value = true
  try {
    const response = await executeServerCommand(activeServer.value.id, commandKey.value)
    commandOutput.value = response.data?.output || response.output || '命令执行完成，但没有输出。'
  } finally {
    commandLoading.value = false
  }
}
```

## 当前完成度判断

对照 HR 描述，目前项目主干已经基本符合：

- 架构：Spring Boot 3 + MyBatis + Spring Security/JWT + RBAC + Vue3 前端已具备。
- 功能：设备资产、服务器 SSH 纳管、指标采集、状态展示、远程命令、导出能力已形成主流程。
- 数据：MySQL 持久化、Redis 缓存、Docker Compose、Nginx 部署已具备。

仍可继续增强的方向：

- 告警规则更细化，例如 CPU/内存/磁盘阈值按服务器或资产分组配置。
- 监控趋势图进一步按时间窗口聚合。
- SSH 主机密钥校验从演示模式升级为生产白名单模式。
- 操作审计可以记录远程命令的执行人、目标服务器、耗时和结果摘要。
