# IT设备管理平台

面向企业 IT 设备资产、服务器监控和告警闭环的管理平台。项目基于 RuoYi 后端二次开发，当前前端已收敛为 Vue3 + Vite 工程，默认 Docker 部署直接使用 `ruoyi-ui-vue3`。

## 当前能力

| 模块 | 说明 |
| --- | --- |
| 首页工作台 | 资产、服务器、在线率、告警、资源趋势和风险服务器总览 |
| 资产中心 | 设备资产、资产变更、服务器类资产纳入监控 |
| 监控中心 | 服务器管理、监控数据、告警事件、告警规则 |
| Agent 链路 | Agent Token、心跳、指标上报、离线告警和恢复关闭 |
| 平台能力 | 登录、动态菜单、权限按钮、个人中心、明暗主题 |

## 技术栈

| 层级 | 技术 |
| --- | --- |
| 后端 | Java 17、Spring Boot、RuoYi、Spring Security、MyBatis、Quartz |
| 数据 | MySQL 8、Redis 7、Druid |
| 前端 | Vue3、Vite、Element Plus、Pinia、Vue Router 4、Axios、ECharts |
| 部署 | Docker Compose、Nginx、MySQL、Redis |

## 项目结构

```text
asset-management/
├─ ruoyi-admin/          # 后端启动模块和 Web 控制器
├─ ruoyi-common/         # 通用工具、注解、常量和基础实体
├─ ruoyi-framework/      # 安全认证、数据权限、Web 配置
├─ ruoyi-generator/      # RuoYi 代码生成模块
├─ ruoyi-quartz/         # 定时任务模块
├─ ruoyi-system/         # 系统、资产、监控业务模块
├─ ruoyi-ui-vue3/        # 默认 Vue3 + Vite 前端
├─ agent/                # Agent 相关脚本与说明
├─ scripts/              # 辅助脚本
├─ sql/                  # 数据库初始化脚本
├─ docker/               # Nginx、MySQL、Redis 运行配置
├─ docker-compose.yml    # Docker Compose 编排
├─ Dockerfile.backend    # 后端镜像构建
├─ Dockerfile.frontend   # Vue3 前端镜像构建
└─ README.md
```

## Docker 部署

1. 准备环境变量：

```bash
cp .env.example .env
```

2. 修改 `.env` 中的数据库密码、Token 密钥、Druid 账号密码等本机配置。

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

前端构建验证：

```powershell
cd ruoyi-ui-vue3
npm.cmd run build
```

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
- 不在配置模板中写入本机真实密码、Token 或密钥。
- 默认初始化账号只用于本地或首次部署体验，正式环境应立即修改。
- SSH 密码字段保留兼容旧结构，但业务上不再保存和展示 SSH 密码。

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
