# Vue3 Migration Progress

本文档记录 Vue3 迁移的实际落地进度。当前策略是并行建设新版前端，旧版 `ruoyi-ui` 继续承担生产入口，等新版主流程稳定后再切换 Docker 和默认访问入口。

## 当前阶段

- 新增并行前端目录：`ruoyi-ui-vue3`
- 技术栈：Vue3、Vite、Vue Router 4、Pinia、Element Plus、Axios、ECharts
- 已迁移页面：
  - 登录 `/login`
  - 运维总览 `/index`
  - 设备资产 `/asset/info`
  - 资产变更 `/asset/flow`
  - 服务器管理 `/ops-monitor/server`
  - 监控数据 `/ops-monitor/data`
  - 告警事件 `/ops-monitor/alarm`
  - 告警规则 `/ops-monitor/rule`
- 已复用接口：
  - `/login`
  - `/captchaImage`
  - `/getInfo`
  - `/monitor/workbench/summary`
  - `/asset/info/list`
  - `/asset/info`
  - `/monitor/server/list`
  - `/monitor/server`
  - `/monitor/server/{id}/agent-token`
  - `/monitor/data/list`
  - `/monitor/alarm/list`
  - `/monitor/alarm/handle/{id}`
  - `/monitor/alarm/ignore/{id}`
  - `/monitor/rule/list`
  - `/monitor/rule`
- 会话策略：沿用当前 RuoYi 的 `Admin-Token` Cookie
- 权限策略：已接入 `/getRouters` 生成 Vue3 侧栏菜单，并通过 `v-hasPermi` 控制页面按钮权限
- 兼容策略：当前 Vue2 前端继续作为生产入口，Vue3 前端作为迁移预览入口
- 部署策略：新增 `Dockerfile.frontend-vue3` 和 `docker-compose.vue3.yml`，可独立启动 Vue3 预览服务
- UI 策略：参考 Wuhr-AI-ops 的浅色 AIOps 控制台风格，使用白色侧栏、顶部工具栏、浅蓝灰背景和卡片式业务面板

## 本阶段不做

- 不替换当前生产 Docker 前端
- 不删除 `ruoyi-ui`
- 不迁移全部 RuoYi 系统管理页面
- 不切换 Docker 默认前端
- 不重构后端接口

## 下一批迁移建议

1. 抽象字典、分页、表单弹窗、导出下载等公共能力。
2. 迁移系统管理必要页面：用户、角色、菜单、操作日志、登录日志。
3. 强化资产选择器、服务器选择器和表单校验。
4. 补齐未实现菜单的 Vue3 页面或隐藏不可用入口。
5. 验收通过后再替换当前 `frontend` 服务。
6. 开始 AIOps 数据建模、异常检测和智能告警降噪。

## 本地验证

```powershell
cd ruoyi-ui-vue3
npm.cmd install
npm.cmd run build
npm.cmd run dev
```

默认开发地址为 `http://localhost:5173/index`，后端接口通过 `/dev-api` 代理到 `http://localhost:8080`。
