# Vue3 Migration Readiness

本文档记录当前进入 Vue3 升级前需要冻结和确认的边界。目标是先稳定业务骨架，再迁移前端技术栈，避免业务重构和框架升级互相放大风险。

## 当前业务底座

- 工作台：资产、服务器、在线率、告警、资产变更、资源趋势、风险服务器和告警状态总览。
- 资产中心：设备资产、资产变更，支持服务器类资产纳入监控。
- 监控中心：服务器管理、监控数据、告警事件、告警规则。
- Agent 链路：生成 Agent Token、Agent 上报、心跳检查、离线告警、指标恢复告警自动关闭。
- 平台管理：用户、角色、菜单、操作日志、登录日志。

## Vue3 升级前冻结项

- 不再扩大 Vue2 页面结构，只做 bug fix 和必要文案修正。
- 后端接口路径保持稳定，迁移时优先复用现有 `/asset/*`、`/monitor/*`、`/system/*` API。
- 菜单权限标识保持稳定，迁移时继续复用 `sys_menu.perms`。
- 字典值保持稳定，迁移时继续复用当前字典：
  - `asset_type`
  - `asset_status`
  - `monitor_status`
  - `connection_status`
  - `alarm_type`
  - `alarm_level`
  - `alarm_status`
- Agent Token、数据库密码、`.env` 和 Docker 数据目录继续禁止进入 Git。

## 推荐迁移顺序

1. 新建 Vue3 + Vite 前端工程骨架。
2. 迁移登录、权限、动态路由、菜单加载和请求封装。
3. 迁移工作台，作为视觉和交互基准页。
4. 迁移资产中心：设备资产、资产变更。
5. 迁移监控中心：服务器管理、监控数据、告警事件、告警规则。
6. 迁移平台管理中仍保留的用户、角色、菜单和日志页面。
7. 替换生产 Docker 前端构建流程。
8. 移除旧 Vue2 工程或保留为历史分支。

## 验收口径

- 登录后菜单结构和权限与当前 Vue2 版本一致。
- 工作台数据与当前 `/monitor/workbench/summary` 返回一致。
- 资产纳管、Agent Token、监控上报、告警处理主流程可用。
- `npm run build`、后端 Maven 构建、Docker Compose 构建均通过。
- 浏览器强刷 `/index`、`/asset/info`、`/ops-monitor/server`、`/ops-monitor/alarm` 后页面正常。
