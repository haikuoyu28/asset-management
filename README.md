# 企业 IT 设备与服务器资产管理平台

基于 RuoYi-Vue 二次开发的企业 IT 资产管理系统，面向设备台账、资产流转、服务器监控、告警处理、系统权限和运维基础数据管理等场景。

项目采用 Spring Boot + Vue 2 前后端分离架构，保留 RuoYi 的用户、角色、菜单、字典、日志、定时任务和代码生成能力，并扩展了资产管理与服务器监控业务模块。

## 功能模块

| 模块 | 说明 |
| --- | --- |
| 系统管理 | 用户、角色、菜单、部门、岗位、字典、参数配置 |
| 资产管理 | 设备资产信息维护、查询、导出、详情查看 |
| 资产流程 | 资产领用、归还、报修、维修、巡检、报废等流转记录 |
| 运维监控 | 服务器管理、监控数据、监控告警 |
| 定时任务 | Quartz 任务配置与执行日志 |
| 日志审计 | 登录日志、操作日志 |
| 代码生成 | 基于 RuoYi 的代码生成能力 |

## 技术栈

| 分类 | 技术 | 版本 / 说明 |
| --- | --- | --- |
| 后端 | Java | 17 |
| 后端 | Spring Boot | 4.0.3 |
| 后端 | RuoYi | 3.9.2 |
| 后端 | MyBatis | 持久层框架 |
| 后端 | Druid | 数据库连接池与数据监控 |
| 后端 | MySQL | 8.0 |
| 后端 | Redis | 7 |
| 后端 | Quartz | 定时任务 |
| 后端 | Spring Security | 登录认证与权限控制 |
| 后端 | Springdoc OpenAPI | 接口文档 |
| 前端 | Vue | 2.6.12 |
| 前端 | Vue CLI | 4.4.6 |
| 前端 | Element UI | 2.15.14 |
| 前端 | Vue Router | 3.4.9 |
| 前端 | Vuex | 3.6.0 |
| 前端 | Axios | 0.30.3 |
| 前端 | ECharts | 5.4.0 |

## 项目结构

```text
asset-management/
├── ruoyi-admin/          # 后端启动模块与 Web 控制器
├── ruoyi-common/         # 通用工具、注解、常量和基础实体
├── ruoyi-framework/      # 安全认证、数据权限、Web 配置等框架能力
├── ruoyi-generator/      # RuoYi 代码生成模块
├── ruoyi-quartz/         # 定时任务模块
├── ruoyi-system/         # 系统管理、资产管理、监控管理业务模块
├── ruoyi-ui/             # Vue 2 + Element UI 前端项目
├── sql/                  # 数据库初始化脚本
├── docker/               # Nginx 配置与运行数据目录
├── docker-compose.yml    # Docker Compose 编排文件
├── Dockerfile.backend    # 后端镜像构建文件
├── Dockerfile.frontend   # 前端镜像构建文件
└── README.md
```

## 快速启动

### 环境要求

- JDK 17
- Maven 3.8+
- Node.js 16 或 18 LTS
- npm
- MySQL 8.0
- Redis 7.x

前端依赖基于 Vue CLI 4 和 Vue 2，建议优先使用 Node.js 16 或 18 LTS。

### 初始化数据库

创建数据库 `ry_cloud`，然后按顺序导入：

```text
sql/ry_20260417.sql
sql/quartz.sql
sql/asset_monitor_tables.sql
sql/system_init.sql
```

### 启动后端

本地开发可通过环境变量覆盖数据库和 Redis 配置：

```bash
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DATABASE=ry_cloud
MYSQL_USERNAME=root
MYSQL_PASSWORD=your-db-password
REDIS_HOST=localhost
REDIS_PORT=6379
TOKEN_SECRET=change-me-to-a-long-random-string
DRUID_LOGIN_USERNAME=ruoyi
DRUID_LOGIN_PASSWORD=your-druid-password
```

构建并启动：

```bash
mvn clean package -DskipTests
java -jar ruoyi-admin/target/ruoyi-admin.jar
```

后端默认地址：

```text
http://localhost:8080
```

### 启动前端

```bash
cd ruoyi-ui
npm install --registry=https://registry.npmmirror.com
npm run dev
```

前端开发服务默认通过 `/dev-api` 代理到后端。

## Docker 部署

Docker Compose 编排包含 MySQL、Redis、后端服务和前端 Nginx。

### 1. 准备环境变量

复制示例文件并修改密码、密钥和镜像仓库地址：

```bash
cp .env.example .env
```

关键变量：

```env
REGISTRY=localhost:5000
VERSION=latest

MYSQL_ROOT_PASSWORD=change-me
MYSQL_USERNAME=root
MYSQL_PASSWORD=change-me
MYSQL_DATABASE=ry_cloud
MYSQL_PORT=3306

REDIS_PORT=6379
REDIS_PASSWORD=

TOKEN_SECRET=change-me-to-a-long-random-string
DRUID_LOGIN_USERNAME=ruoyi
DRUID_LOGIN_PASSWORD=change-me

BACKEND_PORT=8080
FRONTEND_PORT=80
```

真实 `.env` 已被 `.gitignore` 排除，不要提交到仓库。

### 2. 构建并推送镜像

Windows PowerShell：

```powershell
.\build-and-push.ps1 -Registry localhost:5000 -Version latest
```

脚本会构建并推送：

```text
asset-management-backend
asset-management-frontend
```

前端镜像会在 Docker 构建阶段执行 `npm install` 和 `npm run build:prod`，不需要提交 `ruoyi-ui/dist`。

### 3. 启动服务

```bash
docker compose up -d
```

查看状态和日志：

```bash
docker compose ps
docker compose logs -f backend
```

停止服务：

```bash
docker compose down
```

## 默认账号

| 用户名 | 密码 | 说明 |
| --- | --- | --- |
| `admin` | `admin123` | 超级管理员 |

首次公开部署后，请立即修改默认密码，并替换生产环境中的数据库密码、Druid 密码和 Token 密钥。

## 数据监控

数据监控页面使用 Druid：

```text
系统监控 -> 数据监控
```

直接访问：

```text
http://localhost:8080/druid/login.html
```

Druid 登录账号密码由 `.env` 中的 `DRUID_LOGIN_USERNAME` 和 `DRUID_LOGIN_PASSWORD` 控制。

## 常见问题

### PowerShell 无法执行 npm

如果 PowerShell 提示 `npm.ps1` 被执行策略阻止，可以使用：

```powershell
npm.cmd install
npm.cmd run dev
npm.cmd run build:prod
```

### 端口被占用

Windows 下可检查常用端口：

```powershell
Get-NetTCPConnection -State Listen |
  Where-Object { $_.LocalPort -in 80,8080,3306,6379 } |
  Select-Object LocalAddress,LocalPort,OwningProcess
```

如需修改端口，在 `.env` 中调整：

```env
FRONTEND_PORT=8081
BACKEND_PORT=8082
MYSQL_PORT=3307
REDIS_PORT=6380
```

### 重新初始化数据库

Docker 部署下数据库数据位于：

```text
docker/mysql/data/
```

如需重新初始化，先备份数据，再停止服务并清理该目录。

## 发布说明

- `.env` 不进入 Git，也已从当前可达 Git 历史中移除。
- `target/`、`ruoyi-ui/dist/`、`node_modules/`、Docker 数据目录均被忽略。
- 默认登录账号用于初始化体验，生产环境必须修改。

## 许可证

本项目基于 RuoYi 二次开发，遵循仓库中的 [LICENSE](LICENSE)。
