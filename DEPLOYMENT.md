# 资产管理平台 - Docker 基础部署指南

## 方案概述

本项目默认使用最基础的 Docker Compose 部署方式：

- 在项目目录直接构建后端和前端镜像
- 同时启动 MySQL、Redis、后端服务和前端 Nginx
- 不依赖额外的镜像分发服务
- 不需要先发布镜像

## 前置要求

- Docker 已安装并运行
- Docker Compose v2 可用
- 部署机器可以访问 npm 和 Maven 依赖源

## 1. 准备环境变量

复制环境变量示例文件：

```bash
cp .env.example .env
```

编辑 `.env`，至少修改以下密钥和密码：

```env
MYSQL_ROOT_PASSWORD=change-me
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

## 2. 启动服务

在项目根目录执行：

```bash
docker compose up -d --build
```

该命令会：

- 构建后端镜像
- 构建前端镜像
- 启动 MySQL
- 启动 Redis
- 启动后端服务
- 启动前端 Nginx

首次启动时 MySQL 会自动导入 `sql/` 目录下的初始化脚本。

## 3. 查看状态和日志

```bash
docker compose ps
docker compose logs -f backend
docker compose logs -f frontend
```

默认访问地址：

```text
http://localhost
```

后端默认端口：

```text
http://localhost:8080
```

## 4. 停止服务

```bash
docker compose down
```

如果需要连同容器内网络一起清理但保留数据库文件，仍然使用上面的命令即可。数据库、Redis 和上传文件数据保存在：

```text
docker/mysql/data/
docker/redis/data/
docker/uploadPath/
```

## 5. 更新部署

代码更新后重新构建并启动：

```bash
docker compose up -d --build
```

如需查看构建日志，可以去掉 `-d`：

```bash
docker compose up --build
```

## 6. 重新初始化数据库

如果需要重新导入初始化 SQL，先停止服务并备份数据，再清理 MySQL 数据目录：

```bash
docker compose down
rm -rf docker/mysql/data/*
docker compose up -d --build
```

Windows PowerShell 可手动删除 `docker/mysql/data/` 下的数据文件后再重新启动。

## 7. 常见问题

### 端口被占用

修改 `.env` 中的端口：

```env
FRONTEND_PORT=8081
BACKEND_PORT=8082
MYSQL_PORT=3307
REDIS_PORT=6380
```

然后重新启动：

```bash
docker compose up -d --build
```

### 前端依赖下载慢

前端镜像在构建阶段执行 `npm install --legacy-peer-deps`。如果网络较慢，可以在 Dockerfile 中临时配置 npm 镜像源，或在网络更稳定的环境构建。

### 后端依赖下载慢

后端镜像在构建阶段执行 Maven 构建。可以根据部署环境配置 Maven 镜像源或挂载本地 Maven 缓存。
