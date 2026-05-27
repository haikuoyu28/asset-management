# 资产管理平台 - Docker 多服务器部署指南

## 方案概述

**本机构建 + 本机私有仓库 + 服务器拉取部署**

- **构建机**（你的开发电脑）：负责构建 Docker 镜像，运行私有仓库
- **服务器**（目标部署机器）：无需 Java/Maven，只需 Docker，从构建机拉取镜像运行

```
┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
│    构建机        │  push   │   本机私有仓库    │  pull   │    服务器 1      │
│  (你的电脑)      │ ──────▶ │  localhost:5000 │ ──────▶ │  Docker 运行    │
│  Java + Maven   │         │                 │         │                 │
│  Docker Desktop │         │                 │  pull   │    服务器 2      │
│                 │         │                 │ ──────▶ │  Docker 运行    │
└─────────────────┘         └─────────────────┘         └─────────────────┘
```

---

## 一、构建机配置（你的电脑）

### 1. 前置要求

- Docker Desktop 已安装并运行
- Java 17 和 Maven 3.9+（用于构建项目）
- PowerShell（Windows）

### 2. 启动私有仓库

```powershell
# 在项目根目录执行
docker-compose -f docker-compose.registry.yml up -d
```

私有仓库将运行在 `http://localhost:5000`

### 3. 配置 Docker 允许非安全仓库

**Windows:**
打开 Docker Desktop → Settings → Docker Engine，添加：
```json
{
  "insecure-registries": ["localhost:5000", "你的内网IP:5000"]
}
```

**注意：** 如果服务器要通过内网 IP 访问，需要把内网 IP 也加进去，例如 `"192.168.1.100:5000"`

### 4. 构建并推送镜像

```powershell
# 使用脚本自动构建并推送
.\build-and-push.ps1

# 或者指定版本号
.\build-and-push.ps1 -Version "v1.0.0"

# 或者指定仓库地址（如果服务器通过内网IP访问）
.\build-and-push.ps1 -Registry "192.168.1.100:5000" -Version "v1.0.0"
```

---

## 二、服务器配置（目标机器）

### 1. 前置要求

- Docker 已安装
- Docker Compose 已安装
- 能通过网络访问构建机的 5000 端口

### 2. 配置 Docker 允许访问私有仓库

```bash
# 创建/编辑 Docker 配置文件
sudo tee /etc/docker/daemon.json <<EOF
{
  "insecure-registries": ["构建机IP:5000"]
}
EOF

# 重启 Docker
sudo systemctl restart docker
```

### 3. 复制部署文件到服务器

从构建机复制以下文件到服务器：
```
docker-compose.yml
.env
deploy.sh
sql/ 目录（首次部署需要初始化数据库）
```

### 4. 修改 .env 文件

```bash
# 编辑 .env 文件，将 REGISTRY 改为构建机的实际 IP
REGISTRY=192.168.1.100:5000  # 改为构建机的内网 IP
VERSION=latest               # 或者 v1.0.0 等版本号
```

### 5. 执行部署脚本

```bash
# 给脚本执行权限
chmod +x deploy.sh

# 执行部署
./deploy.sh
```

或者手动执行：
```bash
# 创建目录
mkdir -p docker/mysql/data docker/redis/data docker/uploadPath

# 拉取镜像
docker pull 192.168.1.100:5000/asset-management-backend:latest

# 启动服务
docker-compose up -d
```

---

## 三、部署多台服务器

部署多台服务器非常简单，只需重复**服务器配置**步骤：

```bash
# 服务器 1
scp docker-compose.yml .env deploy.sh root@服务器1IP:/opt/asset-management/
ssh root@服务器1IP "cd /opt/asset-management && chmod +x deploy.sh && ./deploy.sh"

# 服务器 2
scp docker-compose.yml .env deploy.sh root@服务器2IP:/opt/asset-management/
ssh root@服务器2IP "cd /opt/asset-management && chmod +x deploy.sh && ./deploy.sh"

# 服务器 3...
```

---

## 四、更新部署

当代码更新后，重新部署的流程：

### 构建机（执行一次）
```powershell
# 重新构建并推送新版本
.\build-and-push.ps1 -Registry "192.168.1.100:5000" -Version "v1.0.1"
```

### 各服务器（每台执行）
```bash
# 修改 .env 中的版本号
sed -i 's/VERSION=.*/VERSION=v1.0.1/' .env

# 拉取新镜像并重启
docker-compose pull
docker-compose up -d

# 或者使用脚本
./deploy.sh
```

---

## 五、常见问题

### Q1: 服务器无法连接到构建机的私有仓库

**检查项：**
1. 构建机的防火墙是否放行了 5000 端口
2. 服务器是否能 ping 通构建机
3. 服务器的 Docker 是否配置了 insecure-registries

**Windows 防火墙放行 5000 端口：**
```powershell
# 以管理员身份运行 PowerShell
New-NetFirewallRule -DisplayName "Docker Registry" -Direction Inbound -LocalPort 5000 -Protocol TCP -Action Allow
```

### Q2: 镜像推送失败

**可能原因：**
- 私有仓库未启动：`docker-compose -f docker-compose.registry.yml up -d`
- Docker 未配置 insecure-registries

### Q3: 数据库初始化失败

**解决：**
```bash
# 删除 MySQL 数据卷重新初始化
docker-compose down
docker volume rm asset-management_mysql_data  # 如果有命名卷
rm -rf docker/mysql/data/*
docker-compose up -d mysql
```

### Q4: 如何查看日志

```bash
# 查看所有服务日志
docker-compose logs -f

# 只看后端日志
docker-compose logs -f backend

# 只看 MySQL 日志
docker-compose logs -f mysql
```

---

## 六、文件说明

| 文件 | 用途 | 所在位置 |
|------|------|----------|
| Dockerfile.backend | 后端多阶段构建文件 | 构建机 |
| docker-compose.registry.yml | 私有仓库配置 | 构建机 |
| docker-compose.yml | 应用服务编排 | 服务器 |
| build-and-push.ps1 | 构建推送脚本 | 构建机 |
| deploy.sh | 服务器部署脚本 | 服务器 |
| .env | 环境变量配置 | 服务器 |

---

## 七、安全建议

1. **生产环境建议配置 HTTPS**：为私有仓库配置 TLS 证书
2. **限制仓库访问**：通过防火墙限制只有内网 IP 能访问 5000 端口
3. **定期备份数据**：`docker/mysql/data` 和 `docker/redis/data`
4. **使用强密码**：修改 `.env` 中的默认密码
