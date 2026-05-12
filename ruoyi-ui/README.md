# 企业IT设备与服务器资产管理平台 - 前端项目

## 📋 项目简介

本项目是基于 **Vue 3 + Element Plus** 开发的企业级IT资产管理平台前端，配合后端 Spring Boot 服务提供完整的设备管理与运维监控功能。

## 🚀 快速开始

### 环境要求

- **Node.js**: 22.22.1+
- **npm**: 10.0+
- **Vue**: 3.4+
- **Element Plus**: 2.5+

### 开发步骤

```bash
# 1. 进入前端目录
cd ruoyi-ui

# 2. 安装依赖
npm install --registry=https://registry.npmmirror.com

# 3. 启动开发服务器
npm run dev

# 4. 浏览器访问
# 自动打开 http://localhost:80
```

### 生产环境构建

```bash
# 构建测试环境
npm run build:stage

# 构建生产环境
npm run build:prod
```

---

## 📂 项目结构

```
ruoyi-ui/
├── public/              # 静态资源
│   ├── index.html      # HTML模板
│   └── robots.txt     # 爬虫配置
├── src/                # 源代码
│   ├── api/           # API接口
│   │   ├── asset/     # 资产管理API
│   │   ├── monitor/   # 运维监控API
│   │   └── system/    # 系统管理API
│   ├── assets/        # 主题、字体等静态资源
│   ├── components/     # 全局公用组件
│   ├── directive/      # 全局指令
│   ├── layout/        # 布局组件
│   ├── router/        # 路由配置
│   ├── store/         # 状态管理(Pinia)
│   ├── utils/         # 全局工具类
│   ├── views/         # 页面视图组件
│   │   ├── asset/     # 资产管理模块
│   │   ├── monitor/   # 运维监控模块
│   │   └── system/    # 系统管理模块
│   ├── App.vue        # 入口页面
│   └── main.js        # 入口文件
├── .env.development   # 开发环境变量
├── .env.production    # 生产环境变量
├── package.json       # 项目依赖
└── vue.config.js      # Vue配置
```

---

## 🔧 配置说明

### 开发环境配置（.env.development）

```properties
# 开发环境配置
ENV = 'development'

# 开发环境 API 地址
VUE_APP_BASE_API = '/dev-api'

# 路由懒加载
VUE_CLI_BABEL_TRANSPILE_MODULES = true
```

### 生产环境配置（.env.production）

```properties
# 生产环境配置
ENV = 'production'

# 生产环境 API 地址
VUE_APP_BASE_API = '/prod-api'
```

### 代理配置（vue.config.js）

```javascript
module.exports = {
  devServer: {
    host: '0.0.0.0',
    port: 80,
    proxy: {
      '/dev-api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: {
          '^/dev-api': ''
        }
      }
    }
  }
}
```

---

## 📦 依赖说明

### 核心依赖

| 依赖 | 版本 | 说明 |
|------|------|------|
| vue | ^3.4.0 | 前端框架 |
| vue-router | ^4.2.0 | 路由管理 |
| pinia | ^2.1.0 | 状态管理 |
| element-plus | ^2.5.0 | UI组件库 |
| axios | ^1.6.0 | HTTP客户端 |
| vite | ^5.0.0 | 构建工具 |

### 功能依赖

| 依赖 | 版本 | 说明 |
|------|------|------|
| echarts | ^5.4.0 | 图表组件 |
| dayjs | ^1.11.0 | 日期处理 |
| file-saver | ^2.0.0 | 文件下载 |
| xlsx | ^0.18.0 | Excel导出 |
| js-cookie | ^3.0.0 | Cookie处理 |

---

## 🎯 功能模块

### 1️⃣ 资产管理模块

- **设备信息管理**：设备列表、新增、编辑、删除
- **资产流程管理**：领用、归还、维修、报废流程
- **资产统计报表**：按类型、状态、部门统计

### 2️⃣ 运维监控模块

- **服务器管理**：服务器列表、状态监控
- **监控数据**：CPU、内存、磁盘使用率图表
- **告警管理**：告警规则、告警记录、告警处理

### 3️⃣ 系统管理模块

- **用户管理**：用户增删改查、角色分配
- **角色管理**：角色权限分配
- **菜单管理**：菜单配置、权限标识
- **部门管理**：部门树形结构

---

## 🛠️ 开发指南

### 新增页面步骤

1. **创建API文件**

```javascript
// src/api/asset/info.js
import request from '@/utils/request'

// 查询设备列表
export function listInfo(query) {
  return request({
    url: '/asset/info/list',
    method: 'get',
    params: query
  })
}
```

2. **创建页面组件**

```vue
<!-- src/views/asset/info/index.vue -->
<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" :inline="true">
      <el-form-item label="设备名称" prop="assetName">
        <el-input v-model="queryParams.assetName" placeholder="请输入设备名称" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">搜索</el-button>
      </el-form-item>
    </el-form>
    
    <!-- 数据表格 -->
    <el-table :data="infoList">
      <el-table-column label="设备ID" prop="assetId" />
      <el-table-column label="设备名称" prop="assetName" />
    </el-table>
  </div>
</template>

<script setup>
import { listInfo } from '@/api/asset/info'
import { ref, onMounted } from 'vue'

const infoList = ref([])
const queryParams = ref({
  assetName: undefined
})

function getList() {
  listInfo(queryParams.value).then(response => {
    infoList.value = response.data
  })
}

function handleQuery() {
  getList()
}

onMounted(() => {
  getList()
})
</script>
```

3. **配置路由**

```javascript
// src/router/modules/asset.js
export default {
  path: '/asset',
  component: Layout,
  hidden: false,
  redirect: '/asset/info',
  meta: { title: '资产管理', icon: 'computer' },
  children: [
    {
      path: 'info',
      component: () => import('@/views/asset/info/index'),
      name: 'AssetInfo',
      meta: { title: '设备管理', icon: 'computer' }
    }
  ]
}
```

---

## 📝 打包部署

### 构建生产版本

```bash
# 1. 安装依赖
npm install

# 2. 构建生产环境
npm run build:prod

# 3. 构建后的文件在 dist/ 目录
ls dist/
```

### Nginx 部署配置

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    root /var/www/html/asset-management;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    location /prod-api/ {
        proxy_pass http://localhost:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

## 📞 联系方式

- **开发者**: 黄为安
- **学校**: 广西某高校（2027届物联网工程）
- **求职意向**: 计算机运维 / 嵌入式 / 信息管理
- **邮箱**: [你的邮箱]
- **GitHub**: [你的GitHub链接]

---

## 📄 许可证

本项目基于 [RuoYi-Vue](https://gitee.com/y_project/RuoYi-Vue) 框架开发，遵循 MIT 许可证。

Copyright (c) 2018 RuoYi  
Copyright (c) 2024 黄为安

---

**⚡ 如果觉得这个项目对你有帮助，请给我一个 Star！ ⚡**
