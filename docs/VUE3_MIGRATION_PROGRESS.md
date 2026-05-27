# Vue3 Migration Progress

本文档记录 Vue3 迁移的实际落地进度。当前策略是并行建设新版前端，旧版 `ruoyi-ui` 继续承担生产入口，等新版主流程稳定后再切换 Docker 和默认访问入口。

## 当前阶段

- 新增并行前端目录：`ruoyi-ui-vue3`
- 技术栈：Vue3、Vite、Vue Router 4、Pinia、Element Plus、Axios、ECharts
- 已迁移页面：
  - 登录 `/login`
  - 运维总览 `/index`
  - 设备资产 `/asset/info`
  - 服务器管理 `/ops-monitor/server`
  - 监控数据 `/ops-monitor/data`
  - 告警事件 `/ops-monitor/alarm`
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
- 会话策略：沿用当前 RuoYi 的 `Admin-Token` Cookie
- 兼容策略：当前 Vue2 前端继续作为生产入口，Vue3 前端作为迁移预览入口

## 本阶段不做

- 不替换当前生产 Docker 前端
- 不删除 `ruoyi-ui`
- 不迁移全部 RuoYi 系统管理页面
- 不接入后端动态菜单和细粒度权限指令
- 不切换 Docker 默认前端
- 不重构后端接口

## 下一批迁移建议

1. 接入后端菜单接口，建立 Vue3 动态路由和权限指令。
2. 抽象字典、分页、表单弹窗、导出下载等公共能力。
3. 迁移资产变更页面，补齐生命周期操作记录。
4. 迁移告警规则页面，形成阈值配置入口。
5. 新增独立 Dockerfile 和 Compose override，先作为预览服务运行。
6. 验收通过后再替换当前 `frontend` 服务。
7. 开始 AIOps 数据建模、异常检测和智能告警降噪。

## 本地验证

```powershell
cd ruoyi-ui-vue3
npm.cmd install
npm.cmd run build
npm.cmd run dev
```

默认开发地址为 `http://localhost:5173/index`，后端接口通过 `/dev-api` 代理到 `http://localhost:8080`。
