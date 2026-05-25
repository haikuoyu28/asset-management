# Docker 后续使用文档

本文档记录本项目在当前 Windows + Docker Desktop 环境下的常用启动、停止、排查方式。

## 1. 当前运行方式

项目根目录：

```powershell
D:\Ruoyiproject\asset-management
```

启动命令：

```powershell
docker compose up -d --build
```

当前服务访问地址：

| 服务 | 地址 |
| --- | --- |
| 前端页面 | http://localhost |
| 后端接口 | http://localhost:8080 |
| MySQL | localhost:3307 |
| Redis | localhost:6379 |

说明：MySQL 容器内部仍然使用 `3306`，只是映射到宿主机的 `3307`，用于避开本机已有的 `3306` 端口占用。

## 2. 环境变量

项目根目录已新增 `.env`：

```env
MYSQL_PORT=3307
```

如果以后本机 `3307` 也被占用，可以改成其他端口，例如：

```env
MYSQL_PORT=3308
```

修改后重新启动：

```powershell
docker compose up -d
```

## 3. 常用命令

查看容器状态：

```powershell
docker compose ps
```

查看全部日志：

```powershell
docker compose logs -f
```

查看后端日志：

```powershell
docker compose logs -f backend
```

查看 MySQL 日志：

```powershell
docker compose logs -f mysql
```

停止服务：

```powershell
docker compose down
```

停止并重新构建：

```powershell
docker compose up -d --build
```

## 4. 首次启动说明

首次启动 MySQL 时会自动初始化数据库，并执行以下 SQL：

```text
sql/ry_20260417.sql
sql/quartz.sql
sql/asset_monitor_tables.sql
sql/system_init.sql
```

首次初始化期间，后端可能短暂出现 MySQL `Connection refused` 日志，这是因为 `depends_on` 只保证容器启动顺序，不保证 MySQL 已经完全可连接。等待 MySQL 初始化完成后，后端会通过 `restart: always` 自动恢复。

确认后端成功启动可看日志：

```powershell
docker compose logs --tail=120 backend
```

正常情况下会出现：

```text
企业IT设备与服务器资产管理平台 - 启动成功！
访问地址: http://localhost:80
```

## 5. Docker Hub 拉取失败处理

如果构建时出现类似错误：

```text
failed to fetch oauth token
failed to authorize
```

说明当前网络无法正常访问 Docker Hub。可以使用镜像代理先拉取基础镜像，再打成本项目 Dockerfile 需要的标签：

```powershell
docker pull m.daocloud.io/docker.io/library/nginx:1.25-alpine
docker pull m.daocloud.io/docker.io/library/eclipse-temurin:17-jre

docker tag m.daocloud.io/docker.io/library/nginx:1.25-alpine nginx:1.25-alpine
docker tag m.daocloud.io/docker.io/library/eclipse-temurin:17-jre eclipse-temurin:17-jre
```

然后重新构建：

```powershell
docker compose up -d --build
```

## 6. 端口占用排查

检查常用端口占用：

```powershell
Get-NetTCPConnection -State Listen |
  Where-Object { $_.LocalPort -in 80,8080,3306,3307,6379 } |
  Select-Object LocalAddress,LocalPort,OwningProcess
```

如果 `80` 被占用，可以在 `.env` 里增加：

```env
FRONTEND_PORT=8081
```

然后访问：

```text
http://localhost:8081
```

如果 `8080` 被占用，可以在 `.env` 里增加：

```env
BACKEND_PORT=8082
```

修改后重新启动：

```powershell
docker compose up -d
```

## 7. 数据目录

Docker 数据会保存在项目目录下：

| 目录 | 说明 |
| --- | --- |
| `docker/mysql/data` | MySQL 数据 |
| `docker/redis/data` | Redis 数据 |
| `docker/uploadPath` | 后端上传文件 |

执行 `docker compose down` 不会删除这些数据目录。

如果需要重新初始化数据库，需要先停止容器，再确认后手动清理 `docker/mysql/data`。清理前请确认没有需要保留的数据。

## 8. 当前已验证状态

本次已完成验证：

```text
asset-mysql     3307->3306
asset-redis     6379->6379
asset-backend   8080->8080
asset-frontend  80->80
```

前端健康检查：

```powershell
Invoke-WebRequest -UseBasicParsing -Uri http://localhost -TimeoutSec 10
```

返回：

```text
200 OK
```
