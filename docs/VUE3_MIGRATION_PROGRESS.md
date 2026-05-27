# Vue3 Migration Progress

本文档记录 Vue3 迁移的实际落地进度。当前策略是并行建设新版前端，旧版 `ruoyi-ui` 继续承担生产入口，等新版主流程稳定后再切换 Docker 和默认访问入口。

## 当前阶段

- 新增并行前端目录：`ruoyi-ui-vue3`
- 技术栈：Vue3、Vite、Vue Router 4、Pinia、Element Plus、Axios、ECharts
- 已迁移页面：运维总览 `/index`
- 已复用接口：`/monitor/workbench/summary`
- 会话策略：复用当前 RuoYi 的 `Admin-Token` Cookie
- 兼容策略：资产、服务器、监控数据、告警页面暂时提供 Vue3 占位页，并可跳回现有 Vue2 页面

## 本阶段不做

- 不替换当前生产 Docker 前端
- 不删除 `ruoyi-ui`
- 不迁移登录、动态路由、菜单权限、字典组件和 CRUD 页面
- 不重构后端接口

## 下一批迁移建议

1. 迁移登录页、用户信息拉取、退出登录。
2. 接入后端菜单接口，建立 Vue3 动态路由和权限指令。
3. 迁移设备资产页，优先完成资产纳管和纳入监控流程。
4. 迁移服务器管理页，打通资产关联、Agent Token、连接状态展示。
5. 迁移告警事件页，完善处理、忽略、闭环留痕。
6. 新增独立 Dockerfile 和 Compose override，先作为预览服务运行。
7. 验收通过后再替换当前 `frontend` 服务。

## 本地验证

```powershell
cd ruoyi-ui-vue3
npm.cmd install
npm.cmd run build
npm.cmd run dev
```

默认开发地址为 `http://localhost:5173/index`，后端接口通过 `/dev-api` 代理到 `http://localhost:8080`。
