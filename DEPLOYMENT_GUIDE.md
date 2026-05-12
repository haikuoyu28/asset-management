# 企业IT资产与服务器资产管理平台部署指南

## 📋 目录
1. [系统概述](#1-系统概述)
2. [数据库配置](#2-数据库配置)
3. [后端配置](#3-后端配置)
4. [前端配置](#4-前端配置)
5. [菜单权限配置](#5-菜单权限配置)
6. [Linux监控脚本部署](#6-linux监控脚本部署)
7. [MinIO配置](#7-minio配置)
8. [定时任务配置](#8-定时任务配置)
9. [启动与测试](#9-启动与测试)

---

## 1. 系统概述

### 技术栈
- **后端框架**: Spring Boot + MyBatis-Plus
- **前端框架**: Vue 2 + Element UI
- **数据库**: MySQL 8.0+
- **缓存**: Redis
- **文件存储**: MinIO
- **服务器监控**: Linux Shell + Agent

### 功能模块
| 模块 | 功能 |
|------|------|
| 资产管理 | 设备管理、资产流程 |
| 运维监控 | 服务器管理、实时监控、告警管理 |

---

## 2. 数据库配置

### 2.1 执行SQL脚本

**Navicat操作步骤：**
1. 打开Navicat，连接到MySQL数据库
2. 创建数据库（如果不存在）：
```sql
CREATE DATABASE `ry_cloud` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `ry_cloud`;
```

3. 右键点击数据库 → "运行SQL文件"
4. 选择文件：`sql/asset_monitor_tables.sql`
5. 点击"开始"执行

**命令行操作：**
```bash
mysql -u root -p < sql/asset_monitor_tables.sql
```

### 2.2 创建的表结构

| 表名 | 说明 |
|------|------|
| `asset_info` | 设备资产表 |
| `asset_flow` | 资产流程表 |
| `monitor_server` | 监控服务器表 |
| `monitor_data` | 监控数据表 |
| `monitor_alarm` | 监控告警表 |

### 2.3 初始化字典数据

脚本已自动创建以下字典类型：
- `asset_type` - 资产类型
- `asset_status` - 资产状态
- `asset_flow_type` - 资产流程类型
- `monitor_status` - 监控状态
- `connection_status` - 连接状态
- `alarm_type` - 告警类型
- `alarm_level` - 告警级别
- `alarm_status` - 告警状态

---

## 3. 后端配置

### 3.1 项目结构

```
ruoyi-admin/src/main/java/com/ruoyi/web/controller/
├── asset/                    # 资产管理模块
│   ├── domain/             # 实体类
│   │   ├── AssetInfo.java
│   │   └── AssetFlow.java
│   ├── mapper/             # Mapper接口
│   │   ├── AssetInfoMapper.java
│   │   └── AssetFlowMapper.java
│   ├── service/            # 服务层
│   │   ├── IAssetInfoService.java
│   │   ├── IAssetFlowService.java
│   │   └── impl/
│   └── AssetInfoController.java
│   └── AssetFlowController.java
├── monitor/                # 运维监控模块
│   ├── domain/
│   │   ├── MonitorServer.java
│   │   ├── MonitorData.java
│   │   └── MonitorAlarm.java
│   ├── mapper/
│   │   ├── MonitorServerMapper.java
│   │   ├── MonitorDataMapper.java
│   │   └── MonitorAlarmMapper.java
│   ├── service/
│   │   ├── IMonitorServerService.java
│   │   ├── IMonitorDataService.java
│   │   ├── IMonitorAlarmService.java
│   │   └── impl/
│   ├── task/
│   │   ├── ServerMonitorTask.java
│   │   └── MonitorAlarmTask.java
│   ├── MonitorServerController.java
│   ├── MonitorDataController.java
│   └── MonitorAlarmController.java
```

### 3.2 Mapper XML文件

```
ruoyi-admin/src/main/resources/mapper/
├── asset/
│   ├── AssetInfoMapper.xml
│   └── AssetFlowMapper.xml
└── monitor/
    ├── MonitorServerMapper.xml
    ├── MonitorDataMapper.xml
    └── MonitorAlarmMapper.xml
```

### 3.3 application.yml 配置

确保数据库连接配置正确：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/ry_cloud?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true
    username: root
    password: your_password

  # MinIO配置
  minio:
    endpoint: http://localhost:9000
    accessKey: minioadmin
    secretKey: minioadmin
    bucketName: ry-cloud
```

---

## 4. 前端配置

### 4.1 API文件

```
ruoyi-ui/src/api/
├── asset/
│   ├── info.js          # 设备资产管理
│   └── flow.js          # 资产流程
└── monitor/
    ├── server.js        # 服务器管理
    ├── data.js          # 监控数据
    └── alarm.js         # 告警管理
```

### 4.2 Vue页面文件

```
ruoyi-ui/src/views/
├── asset/
│   ├── info/
│   │   └── index.vue    # 设备管理页面
│   └── flow/
│       └── index.vue    # 资产流程页面
└── monitor/
    ├── server/
    │   └── index.vue    # 服务器管理页面（含ECharts图表）
    └── alarm/
        └── index.vue    # 告警管理页面
```

### 4.3 ECharts集成

服务器监控页面已集成ECharts图表：
- CPU使用率折线图
- 内存使用率折线图
- 实时数据更新（30秒刷新）

---

## 5. 菜单权限配置

### 5.1 菜单路径

登录系统后，进入 **系统管理 → 菜单管理**，按以下结构添加菜单：

```
资产管理 (asset)
├── 设备管理 (asset:info:view)
│   └── 路由：/asset/info
├── 资产流程 (asset:flow:view)
│   └── 路由：/asset/flow

运维监控 (monitor)
├── 服务器管理 (monitor:server:view)
│   └── 路由：/monitor/server
├── 监控数据 (monitor:data:view)
│   └── 路由：/monitor/data
└── 告警管理 (monitor:alarm:view)
    └── 路由：/monitor/alarm
```

### 5.2 添加菜单步骤

1. **资产管理目录**
   - 菜单名称：资产管理
   - 路由地址：/asset
   - 菜单图标：computer
   - 显示排序：10

2. **设备管理菜单**
   - 菜单名称：设备管理
   - 路由地址：/asset/info
   - 组件路径：asset/info/index
   - 权限标识：asset:info:view
   - 显示排序：1

3. **资产流程菜单**
   - 菜单名称：资产流程
   - 路由地址：/asset/flow
   - 组件路径：asset/flow/index
   - 权限标识：asset:flow:view
   - 显示排序：2

4. **运维监控目录**
   - 菜单名称：运维监控
   - 路由地址：/monitor
   - 菜单图标：monitor
   - 显示排序：20

5. **服务器管理菜单**
   - 菜单名称：服务器管理
   - 路由地址：/monitor/server
   - 组件路径：monitor/server/index
   - 权限标识：monitor:server:view
   - 显示排序：1

6. **告警管理菜单**
   - 菜单名称：告警管理
   - 路由地址：/monitor/alarm
   - 组件路径：monitor/alarm/index
   - 权限标识：monitor:alarm:view
   - 显示排序：3

### 5.3 分配权限

1. 进入 **系统管理 → 角色管理**
2. 选择要分配权限的角色
3. 在"菜单权限"中勾选新建的菜单
4. 点击"保存"

---

## 6. Linux监控脚本部署

### 6.1 上传脚本

```bash
# 上传脚本到Linux服务器
scp scripts/server_monitor.sh root@192.168.1.100:/usr/local/bin/

# 添加执行权限
chmod +x /usr/local/bin/server_monitor.sh
```

### 6.2 测试脚本

```bash
# 测试JSON输出
/usr/local/bin/server_monitor.sh json

# 输出示例：
# {
#     "hostname": "server01",
#     "collectTime": "2024-01-01 12:00:00",
#     "cpuUsage": "25.50",
#     "memoryUsage": "45.30",
#     "diskUsage": "67.80",
#     ...
# }
```

### 6.3 定时执行（可选）

```bash
# 编辑crontab
crontab -e

# 添加定时任务（每5分钟执行一次）
*/5 * * * * /usr/local/bin/server_monitor.sh json >> /var/log/monitor.log 2>&1
```

### 6.4 配合Java Agent使用

在被监控服务器上部署Agent，Agent会：
1. 定时执行监控脚本
2. 解析JSON数据
3. 通过HTTP POST发送到Java后台

---

## 7. MinIO配置

### 7.1 MinIO安装

```bash
# Docker部署
docker run -d \
  --name minio \
  -p 9000:9000 \
  -p 9001:9001 \
  -e "MINIO_ROOT_USER=minioadmin" \
  -e "MINIO_ROOT_PASSWORD=minioadmin" \
  -v /data/minio:/data \
  minio/minio server /data --console-address ":9001"
```

### 7.2 创建存储桶

1. 访问 http://localhost:9001
2. 登录后创建桶：`ry-cloud`
3. 设置桶策略为公开读取

### 7.3 后端集成

在 `application.yml` 中配置：

```yaml
minio:
  endpoint: http://localhost:9000
  accessKey: minioadmin
  secretKey: minioadmin
  bucketName: ry-cloud
```

### 7.4 使用场景

- 设备图片上传
- 资产附件上传
- 告警截图上传

---

## 8. 定时任务配置

### 8.1 监控数据采集任务

在 **系统监控 → 定时任务** 中添加：

| 配置项 | 值 |
|--------|-----|
| 任务名称 | serverMonitorTask |
| 任务分组 | monitor |
| 调用方式 | Bean调用 |
| Bean名称 | serverMonitorTask |
| 方法名 | collectMonitorData |
| cron表达式 | `0 */5 * * * ?` (每5分钟) |
| 状态 | 正常 |

### 8.2 清理过期数据任务

| 配置项 | 值 |
|--------|-----|
| 任务名称 | cleanMonitorDataTask |
| 任务分组 | monitor |
| 调用方式 | Bean调用 |
| Bean名称 | serverMonitorTask |
| 方法名 | cleanExpiredData |
| cron表达式 | `0 0 2 * * ?` (每天凌晨2点) |
| 状态 | 正常 |

### 8.3 告警规则

| 告警项 | 阈值 | 级别 |
|--------|------|------|
| CPU使用率 | > 90% | 警告 |
| 内存使用率 | > 90% | 警告 |
| 磁盘使用率 | > 95% | 严重 |

---

## 9. 启动与测试

### 9.1 启动后端

```bash
# 进入项目目录
cd RuoYi-Vue-master

# Maven打包
mvn clean package -DskipTests

# 启动
java -jar ruoyi-admin/target/ruoyi-admin.jar
```

### 9.2 启动前端

```bash
# 进入前端目录
cd ruoyi-ui

# 安装依赖
npm install

# 开发模式启动
npm run dev

# 生产环境打包
npm run build:prod
```

### 9.3 访问系统

- 前端地址：http://localhost:80
- 后端API：http://localhost:8080
- 默认账号：admin / admin123

### 9.4 功能测试

1. ✅ 登录系统
2. ✅ 进入"资产管理 → 设备管理"，新增设备
3. ✅ 进入"资产管理 → 资产流程"，发起流程
4. ✅ 进入"运维监控 → 服务器管理"，添加服务器
5. ✅ 进入"运维监控 → 告警管理"，查看告警列表

---

## 📞 技术支持

如有问题，请检查：
1. 数据库连接配置
2. Redis连接配置
3. MinIO连接配置
4. 日志文件查看

日志路径：`ruoyi-admin/logs/ry-admin.log`

---

**版本**: v1.0
**更新日期**: 2024-01-01
**框架版本**: RuoYi-Vue 3.x
