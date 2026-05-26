# 企业 IT 设备与服务器资产管理平台

基于 RuoYi-Vue 二次开发的企业 IT 资产管理系统，面向设备台账、资产流转、服务器监控、系统权限和运维基础数据管理等场景。

## 项目状态

当前仓库已经补充 Docker 部署配置，并在 Windows + Docker Desktop 环境下完成验证。

已验证服务：

| 服务 | 容器名 | 默认访问 |
| --- | --- | --- |
| 前端 Nginx | `asset-frontend` | http://localhost |
| 后端服务 | `asset-backend` | http://localhost:8080 |
| MySQL | `asset-mysql` | localhost:3307 |
| Redis | `asset-redis` | localhost:6379 |

说明：MySQL 容器内部端口仍是 `3306`，宿主机默认映射到 `3307`，用于避开本机已有 MySQL 的 `3306` 端口占用。

## 技术栈

### 后端

| 技术 | 版本 / 说明 |
| --- | --- |
| Java | 17 |
| Spring Boot | 4.0.3 |
| RuoYi | 3.9.2 |
| MyBatis / MyBatis Spring Boot | MyBatis 体系 |
| Druid | 数据库连接池 |
| MySQL | 8.0 |
| Redis | 7-alpine |
| Quartz | 定时任务 |
| Spring Security | 权限认证 |

### 前端

| 技术 | 版本 / 说明 |
| --- | --- |
| Vue | 2.6.12 |
| Element UI | 2.15.14 |
| Vue Router | 3.4.9 |
| Vuex | 3.6.0 |
| Axios | 0.30.3 |
| Vue CLI | 4.4.6 |

## 功能模块

- 系统管理：用户、角色、菜单、部门、岗位、字典、参数配置。
- 权限认证：登录认证、角色权限、菜单权限、接口权限控制。
- 资产管理：设备资产信息、资产状态、资产流转记录。
- 服务器监控：服务器信息、监控数据、告警记录。
- 定时任务：基于 Quartz 的任务配置与执行。
- 日志审计：登录日志、操作日志。
- 代码生成：沿用 RuoYi 代码生成能力。

## 目录结构

```text
asset-management/
├── ruoyi-admin/          # 后端启动模块
├── ruoyi-common/         # 通用工具模块
├── ruoyi-framework/      # 框架核心模块
├── ruoyi-generator/      # 代码生成模块
├── ruoyi-quartz/         # 定时任务模块
├── ruoyi-system/         # 系统业务模块
├── ruoyi-ui/             # Vue 2 前端项目
├── sql/                  # 数据库初始化脚本
├── docker/               # Nginx 配置和运行数据目录
├── docker-compose.yml    # Docker Compose 编排文件
├── Dockerfile.backend    # 后端运行镜像
├── Dockerfile.frontend   # 前端 Nginx 镜像


## 快速启动：Docker

### 1. 克隆项目

```bash
git clone https://github.com/haikuoyu28/asset-management.git
cd asset-management
```

### 2. 准备构建产物

当前 Dockerfile 使用运行时镜像，要求本地已经存在：

```text
ruoyi-admin/target/ruoyi-admin.jar
ruoyi-ui/dist/
```

如果是全新克隆，先构建后端：

```bash
mvn clean package -DskipTests
```

再构建前端：

```bash
cd ruoyi-ui
npm install --registry=https://registry.npmmirror.com
npm run build:prod
cd ..
```

### 3. 启动服务

```bash
docker compose up -d --build
```

启动完成后访问：

```text
http://localhost
```

### 4. 查看状态

```bash
docker compose ps
```

查看后端日志：

```bash
docker compose logs -f backend
```

停止服务：

```bash
docker compose down
```

## 环境变量

仓库包含默认 `.env`：

```env
MYSQL_PORT=3307
```

可按需增加：

```env
FRONTEND_PORT=8081
BACKEND_PORT=8082
MYSQL_ROOT_PASSWORD=123456
MYSQL_DATABASE=ry_cloud
REDIS_PASSWORD=
```

修改端口后重新启动：

```bash
docker compose up -d
```

## 数据库初始化

首次启动 MySQL 容器时会自动创建数据库并执行：

```text
sql/ry_20260417.sql
sql/quartz.sql
sql/asset_monitor_tables.sql
sql/system_init.sql
```

首次初始化期间，后端可能短暂出现 MySQL `Connection refused` 日志。这通常是 MySQL 还在初始化，等待容器自动重启恢复即可。

数据目录：

| 目录 | 说明 |
| --- | --- |
| `docker/mysql/data/` | MySQL 数据 |
| `docker/redis/data/` | Redis 数据 |
| `docker/uploadPath/` | 后端上传文件 |

这些目录已被 `.gitignore` 排除，不会提交到仓库。

## Docker Hub 拉取失败处理

如果构建时出现：

```text
failed to fetch oauth token
failed to authorize
```

可以先通过镜像代理拉取基础镜像：

```bash
docker pull m.daocloud.io/docker.io/library/nginx:1.25-alpine
docker pull m.daocloud.io/docker.io/library/eclipse-temurin:17-jre

docker tag m.daocloud.io/docker.io/library/nginx:1.25-alpine nginx:1.25-alpine
docker tag m.daocloud.io/docker.io/library/eclipse-temurin:17-jre eclipse-temurin:17-jre
```

然后重新启动：

```bash
docker compose up -d --build
```

## 本地开发启动

### 后端

确保本机已启动 MySQL 和 Redis，并导入 `sql/` 下的初始化脚本。

默认配置支持环境变量覆盖：

```bash
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DATABASE=ry_cloud
MYSQL_USERNAME=root
MYSQL_PASSWORD=123456
REDIS_HOST=localhost
REDIS_PORT=6379
```

启动后端：

```bash
mvn clean package -DskipTests
java -jar ruoyi-admin/target/ruoyi-admin.jar
```

### 前端

```bash
cd ruoyi-ui
npm install --registry=https://registry.npmmirror.com
npm run dev
```

生产构建：

```bash
npm run build:prod
```

## 常见问题

### 端口被占用

Windows 下可检查常用端口：

```powershell
Get-NetTCPConnection -State Listen |
  Where-Object { $_.LocalPort -in 80,8080,3306,3307,6379 } |
  Select-Object LocalAddress,LocalPort,OwningProcess
```

如果 `80` 被占用，在 `.env` 中设置：

```env
FRONTEND_PORT=8081
```

如果 `8080` 被占用，在 `.env` 中设置：

```env
BACKEND_PORT=8082
```

### 重新初始化数据库

先停止服务：

```bash
docker compose down
```

再清理 `docker/mysql/data/` 后重新启动。清理前请确认没有需要保留的数据。

### 后端启动后无法连接数据库

检查 MySQL 容器状态：

```bash
docker compose ps
docker compose logs -f mysql
```

确认日志中出现 MySQL ready 后，再查看后端：

```bash
docker compose logs -f backend
```

## 默认账号

| 用户名 | 密码 | 说明 |
| --- | --- | --- |
| `admin` | `admin123` | 超级管理员 |

