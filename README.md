# 企业IT设备与服务器资产管理平台

<div align="center">

**基于若依框架开发的企业后台管理系统**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.4-blue.svg)](https://vuejs.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-orange.svg)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7.0-red.svg)](https://redis.io/)

</div>

---

## 📋 项目简介

本系统是基于 **RuoYi-Vue** 框架开发的企业级IT设备与服务器资产管理平台，旨在解决企业IT资产管理混乱、审批流程不规范、设备维护不及时等痛点。

系统涵盖 **6大核心业务模块**，实现从设备采购入库、领用审批、日常维护到报废处置的全生命周期管理。

### 适用场景
- 中小企业IT资产管理工作
- 计算机运维部门设备台账管理
- 办公用品申领与审批流程
- 运维工单管理与跟踪

---

## 🏗️ 技术架构

### 后端技术栈
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.0 | 核心框架 |
| MyBatis-Plus | 3.5.5 | ORM框架 |
| MySQL | 8.0+ | 数据库 |
| Redis | 7.0+ | 缓存数据库 |
| Spring Security | 6.2.0 | 安全框架 |
| JJWT | 0.12.3 | Token认证 |
| Druid | 1.2.20 | 数据库连接池 |
| Swagger | 3.0.0 | API文档 |

### 前端技术栈
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4+ | 前端框架 |
| Element Plus | 2.5+ | UI组件库 |
| Vue Router | 4.2+ | 路由管理 |
| Pinia | 2.1+ | 状态管理 |
| Axios | 1.6+ | HTTP客户端 |
| Vite | 5.0+ | 构建工具 |

### 开发环境
- **JDK**: 21
- **Maven**: 3.9.15
- **Node.js**: 22.22.1
- **IDE**: IntelliJ IDEA 2024.1
- **OS**: Windows 10 / Linux

---

## 🎯 功能模块

### 1️⃣ 材料审批模块
- 采购申请提交与审批
- 多级审批流程（申请人 → 部门主管 → 总经理）
- 审批记录查询与统计
- 审批意见与驳回原因记录

### 2️⃣ 资产管理模块
- IT设备信息录入（电脑、服务器、网络设备等）
- 设备状态管理（正常 / 维修中 / 报废）
- 资产调拨与归还
- 资产盘点与统计报表

### 3️⃣ 办公用品申领模块
- 办公用品库存管理
- 领用申请与审批
- 领用记录查询
- 库存预警提醒

### 4️⃣ 公告管理模块
- 公告发布与编辑
- 公告分类（通知 / 新闻 / 制度）
- 公告阅读状态跟踪
- 附件上传与下载

### 5️⃣ 运维工单模块
- 故障报修工单创建
- 工单分配与处理
- 工单进度跟踪
- 工单满意度评价

### 6️⃣ 设备管理模块
- 服务器信息管理
- 设备监控数据采集
- 维护计划与记录
- 设备生命周期管理

---

## 🚀 快速开始

### 环境准备

1. **安装依赖环境**
   ```bash
   # 检查 Java 版本
   java -version  # 需要 JDK 21+
   
   # 检查 Maven
   mvn -version   # 需要 3.9+
   
   # 检查 Node.js
   node -v        # 需要 22.22.1+
   ```

2. **初始化数据库**
   ```bash
   # 创建数据库
   mysql -u root -p
   CREATE DATABASE `asset-management` DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;
   
   # 导入表结构与数据
   use `asset-management`;
   source D:/Ruoyiproject/RuoYi-Vue-master/sql/ry_2024xxxx.sql
   ```

3. **配置 Redis**
   ```bash
   # Windows 启动 Redis
   cd D:/Redis
   redis-server redis.windows.conf
   
   # 测试连接
   redis-cli ping  # 应返回 PONG
   ```

---

### 后端启动

```bash
# 1. 进入项目目录
cd D:/Ruoyiproject/RuoYi-Vue-master

# 2. 修改配置文件
# 编辑 ruoyi-admin/src/main/resources/application.yml
# 修改数据库连接信息

# 3. 编译项目
mvn clean install -DskipTests

# 4. 启动后端服务
cd ruoyi-admin
mvn spring-boot:run

# 或者运行打包后的 jar
java -jar target/ruoyi-admin.jar
```

**验证后端启动成功：**
- 访问 http://localhost:8080
- 看到若依登录页面即成功

---

### 前端启动

```bash
# 1. 进入前端目录
cd D:/Ruoyiproject/RuoYi-Vue-master/ruoyi-ui

# 2. 安装依赖
npm install --registry=https://registry.npmmirror.com

# 3. 启动开发服务器
npm run dev

# 4. 构建生产环境
npm run build:prod
```

**验证前端启动成功：**
- 自动打开浏览器 http://localhost:80
- 看到登录页面即成功

---

## 📂 项目结构

```
RuoYi-Vue-master/
├── ruoyi-admin/          # 后台服务模块（启动类所在）
├── ruoyi-common/         # 通用工具模块
├── ruoyi-framework/      # 框架核心模块
├── ruoyi-system/         # 系统管理模块
├── ruoyi-ui/             # 前端Vue项目
├── sql/                  # 数据库脚本
├── doc/                  # 项目文档
└── README.md             # 项目说明文档
```

---

## 🔧 配置说明

### 后端配置（application.yml）

```yaml
# 数据库配置
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/asset-management?useUnicode=true&characterEncoding=utf8
    username: root
    password: 123456

# Redis配置
  redis:
    host: 127.0.0.1
    port: 6379
    password:
    database: 0

# 端口配置
server:
  port: 8080
```

### 前端配置（vue.config.js）

```javascript
module.exports = {
  devServer: {
    host: '0.0.0.0',
    port: 80,
    proxy: {
      '/dev-api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: { '^/dev-api': '' }
      }
    }
  }
}
```

---

## 📊 核心功能展示

### 资产管理流程

```
┌─────────────┐
│  设备采购入库  │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  资产信息录入  │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  分配与领用   │ ← 需要审批
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  日常维护    │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│  报废处置    │ ← 需要审批
└─────────────┘
```

---

## 🛠️ 开发指南

### 新增业务模块步骤

1. **设计数据库表**
   ```sql
   CREATE TABLE asset (
     asset_id BIGINT PRIMARY KEY AUTO_INCREMENT,
     asset_name VARCHAR(100) NOT NULL,
     ...
   );
   ```

2. **使用代码生成器**
   - 进入系统：`系统工具 → 代码生成`
   - 导入数据表
   - 编辑字段信息
   - 生成前后端代码

3. **导入生成代码**
   - 将生成的Java代码放入 `ruoyi-system` 模块
   - 将生成的前端代码放入 `ruoyi-ui/src/views`

4. **配置菜单权限**
   - 进入系统：`系统管理 → 菜单管理`
   - 新增菜单项
   - 分配权限给角色

---

## 📝 部署指南

### 后端部署（jar包方式）

```bash
# 1. 打包
cd D:/Ruoyiproject/RuoYi-Vue-master
mvn clean package -DskipTests

# 2. 上传 jar 包到服务器
scp ruoyi-admin/target/ruoyi-admin.jar user@server:/app/

# 3. 后台运行
nohup java -jar ruoyi-admin.jar > app.log 2>&1 &
```

### 前端部署（Nginx）

```bash
# 1. 构建生产版本
cd ruoyi-ui
npm run build:prod

# 2. 上传 dist 目录到服务器
scp -r dist/* user@server:/var/www/html/

# 3. Nginx 配置
server {
    listen 80;
    server_name your-domain.com;
    root /var/www/html;
    index index.html;
}
```

---

## 👤 默认账号

| 用户名 | 密码 | 权限 |
|--------|------|------|
| admin | admin123 | 超级管理员 |
| ry | admin123 | 普通用户 |

---

## 📞 联系方式

- **开发者**: 黄为安
- **学校**: 广西某高校（2027届物联网工程）
- **求职意向**: 计算机运维 / 嵌入式 / 信息管理
- **邮箱**: [你的邮箱]
- **GitHub**: [你的GitHub链接]

---

## 📄 许可证

本项目仅供学习与求职展示使用，禁止商业用途。

---

## 🙏 致谢

本项目基于 [RuoYi-Vue](https://gitee.com/y_project/RuoYi-Vue) 框架开发，感谢若依团队的优秀开源项目！

---

<div align="center">
  
**⚡ 如果觉得这个项目对你有帮助，请给我一个 Star！ ⚡**

</div>
