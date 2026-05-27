# 企业 IT 资产与运维监控平台

这是一个面向企业 IT 运维场景的资产纳管与监控告警平台。项目基于 RuoYi-Vue 二次开发，当前目标不是继续保留通用后台模板感，而是逐步收敛为“资产中心 + 监控中心 + 告警闭环 + 运维工作台”的产品骨架，为后续 AIOps 能力打底。

当前生产入口仍是稳定的 Vue2 前端 `ruoyi-ui`，同时已启动并行 Vue3 前端 `ruoyi-ui-vue3`。Vue3 版本用于承接新的产品化界面、交互和后续 AIOps 体验，等主流程稳定后再替换默认 Docker 前端。

## 当前阶段

已完成的产品骨架：

| 模块 | 状态 | 说明 |
| --- | --- | --- |
| 运维工作台 | 已完成第一版 | 展示资产、服务器、在线率、告警、资源趋势、风险服务器、最近变更 |
| 设备资产 | 已完成主流程 | 资产列表、新增、编辑、删除、服务器类资产纳入监控 |
| 服务器管理 | 已完成主流程 | 资产关联、连接状态、监控状态、Agent Token、服务器规格维护 |
| 监控数据 | 已完成主流程 | CPU、内存、磁盘、网络、负载、进程、采集时间列表 |
| 告警事件 | 已完成主流程 | 告警筛选、处理、忽略、闭环记录 |
| 告警规则 | 已完成主流程 | 阈值规则、范围、等级、沉默时间、启停状态 |
| Agent 上报 | 已具备基础链路 | Token、心跳、指标上报、离线告警、恢复自动关闭 |
| Vue3 迁移 | 进行中 | 已有登录、核心运维页面和 Docker 预览入口，暂未切换生产入口 |

## 技术栈

| 层级 | 技术 |
| --- | --- |
| 后端 | Java 17、Spring Boot、RuoYi、Spring Security、MyBatis、Quartz |
| 数据 | MySQL 8、Redis 7、Druid |
| 当前生产前端 | Vue2、Vue CLI、Element UI、Vuex、Vue Router 3、ECharts |
| 新版前端 | Vue3、Vite、Element Plus、Pinia、Vue Router 4、Axios、ECharts |
| 部署 | Docker Compose、Nginx、MySQL、Redis |

## 项目结构

```text
asset-management/
├─ ruoyi-admin/              # 后端启动模块和 Web 控制器
├─ ruoyi-common/             # 通用工具、注解、常量和基础实体
├─ ruoyi-framework/          # 安全认证、数据权限、Web 配置
├─ ruoyi-generator/          # RuoYi 代码生成模块
├─ ruoyi-quartz/             # 定时任务模块
├─ ruoyi-system/             # 系统、资产、监控业务模块
├─ ruoyi-ui/                 # 当前 Vue2 + Element UI 生产前端
├─ ruoyi-ui-vue3/            # 新版 Vue3 + Vite 并行前端
├─ agent/                    # Agent 相关脚本与说明
├─ docs/                     # 阶段文档和迁移记录
├─ sql/                      # 数据库初始化脚本
├─ docker/                   # Nginx、MySQL、Redis 运行配置
├─ docker-compose.yml        # Docker Compose 编排
├─ Dockerfile.backend        # 后端镜像构建文件
├─ Dockerfile.frontend       # 当前 Vue2 前端镜像构建文件
└─ README.md
```

## Docker 部署

1. 准备环境变量：

```bash
cp .env.example .env
```

2. 修改 `.env` 中的数据库密码、Token 密钥、Druid 密码等本机配置。

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

Vue3 预览前端可以通过 Compose override 单独启动：

```bash
docker compose -f docker-compose.yml -f docker-compose.vue3.yml up -d --build frontend-vue3
```

默认访问：

```text
Vue3 预览：http://localhost:5173
```

`.env`、`docker/mysql/data/`、`docker/redis/data/`、`node_modules/`、前端构建产物均不会进入 Git。

## 本地开发

### 后端

```bash
mvn -DskipTests package
java -jar ruoyi-admin/target/ruoyi-admin.jar
```

后端默认地址：

```text
http://localhost:8080
```

### 当前 Vue2 前端

```powershell
cd ruoyi-ui
npm.cmd install
npm.cmd run dev
```

### 新版 Vue3 前端

```powershell
cd ruoyi-ui-vue3
npm.cmd install
npm.cmd run dev
```

Vue3 开发地址：

```text
http://localhost:5173/index
```

Vue3 构建验证：

```powershell
cd ruoyi-ui-vue3
npm.cmd run build
```

当前环境中 Vite 可能需要写入 `node_modules/.vite-temp`，如果沙箱拦截，需要用更高权限执行构建。

## 数据库初始化

新库按顺序导入：

```text
sql/ry_20260417.sql
sql/quartz.sql
sql/asset_monitor_tables.sql
sql/system_init.sql
```

Docker MySQL 已挂载 UTF-8 配置，初始化 SQL 顶部也设置了 `SET NAMES utf8mb4;`，用于避免中文菜单和业务数据乱码。

## Vue3 迁移策略

迁移采用并行策略：

1. 保留 `ruoyi-ui` 作为当前稳定生产入口。
2. 在 `ruoyi-ui-vue3` 中迁移登录、工作台、资产、服务器、监控、告警主流程。
3. 接入动态菜单、权限指令、字典组件和公共 CRUD 能力。
4. 为 Vue3 前端新增独立 Dockerfile 和 Compose preview 服务。
5. 验收通过后再替换当前 `frontend` 服务。
6. 最后移除 Vue2 工程或保留为历史分支。

更多记录见：

```text
docs/VUE3_MIGRATION_READINESS.md
docs/VUE3_MIGRATION_PROGRESS.md
```

## AIOps 前置路线

进入 AIOps 开发前，项目需要先稳定这些底座：

| 阶段 | 目标 |
| --- | --- |
| 资产对象标准化 | 统一资产、服务器、业务系统、负责人、位置、标签和生命周期字段 |
| 指标数据标准化 | 统一 CPU、内存、磁盘、网络、负载、进程、采集时间和数据质量 |
| 告警闭环标准化 | 形成未处理、处理中、已处理、已忽略的处置记录和结果样本 |
| Agent 链路稳定 | 稳定注册、Token、心跳、指标上报、离线判断和恢复逻辑 |
| Vue3 主流程稳定 | 完成核心页面、权限、菜单、字典、Docker preview 和生产切换 |
| AIOps 数据准备 | 为异常检测、告警降噪、根因分析、容量预测准备可用历史数据 |

后续 AIOps 可以逐步引入：

- 智能告警降噪
- 异常检测和趋势预测
- 根因分析拓扑
- 自然语言运维助手
- 自动化处置建议
- 巡检报告生成
- 资产风险画像

## UI 方向

新版 Vue3 前端会逐步摆脱 RuoYi 默认后台视觉，方向参考现代 AIOps 控制台和用户提供的 [Wuhr-AI-ops](https://github.com/st-lzh/Wuhr-AI-ops) 项目：深色导航、运维驾驶舱、数据密度更高的表格、清晰的状态标签和更有产品感的工作台。

## 安全说明

- 不提交 `.env`。
- 不提交数据库、Redis、上传目录等运行数据。
- 不在配置模板中写入本机真实密码、Token、密钥。
- 默认初始化账号只用于本地或首次部署体验，正式环境必须立即修改。
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

### Git 推送失败

如果出现 `Failed to connect to github.com port 443` 或 `Connection was reset`，说明当前网络无法连接 GitHub。代码可以先提交在本地，网络恢复后执行：

```bash
git push origin main
```

## License

本项目基于 RuoYi 二次开发，遵循仓库中的 [LICENSE](LICENSE)。
