# IT设备管理平台

面向企业 IT 设备资产、Linux 服务器 SSH 纳管、运行监控和告警闭环的管理平台。项目基于 RuoYi 后端二次开发，当前默认前端为 `ruoyi-ui-vue3`。

## 当前能力

| 模块 | 说明 |
| --- | --- |
| 首页工作台 | 展示资产、服务器、在线率、告警、资源趋势和风险服务器概览 |
| 资产中心 | 设备资产入库、资产变更、服务器类资产纳入监控 |
| 监控中心 | 服务器 SSH 连接配置、连接测试、立即采集、监控数据、告警事件和告警规则 |
| 远程运维 | 基于预设命令查看磁盘、内存、负载、进程和监听端口 |
| 平台能力 | 登录、动态菜单、权限按钮、个人中心、明暗主题 |

## 技术栈

| 层级 | 技术 |
| --- | --- |
| 后端 | Java 17、Spring Boot 3.4.5、RuoYi、Spring Security、MyBatis、Quartz、sshj |
| 数据 | MySQL 8、Redis 7、Druid |
| 前端 | Vue3、Vite、Element Plus、Pinia、Vue Router 4、Axios、ECharts |
| 部署 | Docker Compose、Nginx、MySQL、Redis |

## 项目结构

```text
asset-management/
├── ruoyi-admin/          # 后端启动模块和 Web 控制器
├── ruoyi-common/         # 通用工具、注解、常量和基础实体
├── ruoyi-framework/      # 安全认证、数据权限、Web 配置
├── ruoyi-generator/      # RuoYi 代码生成模块
├── ruoyi-quartz/         # 定时任务模块
├── ruoyi-system/         # 系统、资产、监控业务模块
├── ruoyi-ui-vue3/        # 默认 Vue3 + Vite 前端
├── sql/                  # 数据库初始化脚本
├── docker/               # Nginx、MySQL、Redis 运行配置
├── docker-compose.yml    # Docker Compose 编排
├── Dockerfile.backend    # 后端镜像构建
├── Dockerfile.frontend   # Vue3 前端镜像构建
└── README.md
```

## Docker 部署

1. 准备环境变量：

```bash
cp .env.example .env
```

2. 修改 `.env` 中的数据库密码、Token 密钥、Druid 账号密码和 `SSH_CREDENTIAL_KEY`。

`SSH_CREDENTIAL_KEY` 用于加密服务器 SSH 密码。生产环境必须使用足够长的随机值，并且不要提交到 Git。

3. 构建并启动：

```bash
docker compose up -d --build
```

4. 查看状态：

```bash
docker compose ps
docker compose logs -f backend
```

默认访问：

```text
前端：http://localhost
后端：http://localhost:8080
```

## 本地开发

后端：

```bash
mvn -DskipTests package
java -jar ruoyi-admin/target/ruoyi-admin.jar
```

前端：

```powershell
cd ruoyi-ui-vue3
npm.cmd install
npm.cmd run dev
```

前端开发地址：

```text
http://localhost:5173/index
```

构建验证：

```powershell
mvn -DskipTests package
cd ruoyi-ui-vue3
npm.cmd run build
```

## SSH 监控使用

1. 在 VMware 或真实服务器中准备 Linux 主机。
2. 确认平台后端所在机器可以访问 Linux 主机 IP 和 SSH 端口。
3. Linux 主机开启 SSH：

```bash
sudo systemctl status ssh
```

如未安装：

```bash
sudo apt install openssh-server
sudo systemctl enable --now ssh
```

4. 在平台进入 `监控中心 -> 服务器管理 -> 新增服务器`。
5. 填写服务器 IP、SSH 端口、SSH 用户名和 SSH 密码。
6. 保存后点击 `连接测试`。
7. 点击 `立即采集`，采集成功后可在监控数据和首页工作台看到数据变化。
8. 点击 `远程命令` 可执行平台内置的安全预设命令。

当前版本采用 SSH 主动采集，不再使用 Agent 上报作为主线。

## 数据库初始化

Docker MySQL 首次启动会按顺序导入：

```text
sql/ry_20260417.sql
sql/quartz.sql
sql/asset_monitor_tables.sql
sql/system_init.sql
```

MySQL 和初始化 SQL 已配置 `utf8mb4`，用于避免中文菜单和业务数据乱码。

## 安全说明

- 不提交 `.env`。
- 不提交数据库、Redis、上传目录等运行数据。
- 不在配置模板中写入真实密码、Token 或密钥。
- SSH 密码只以密文保存，列表、详情和导出不返回明文。
- 远程命令第一版只开放预设命令，不开放任意 Shell 输入。
- 生产环境应定期更换系统初始账号密码，并妥善保存 `SSH_CREDENTIAL_KEY`。

## 常见问题

### PowerShell 无法执行 npm

如果 PowerShell 提示 `npm.ps1` 被执行策略阻止，使用：

```powershell
npm.cmd install
npm.cmd run dev
npm.cmd run build
```

### Docker 前端中文乱码

确认 MySQL 初始化使用 UTF-8，并清理旧的坏数据卷后重新初始化：

```powershell
docker compose down
docker compose up -d --build
```

如果旧数据已经乱码，需要备份后清空 `docker/mysql/data/` 再重新初始化。

## License

本项目基于 RuoYi 二次开发，遵循仓库中的 [LICENSE](LICENSE)。
