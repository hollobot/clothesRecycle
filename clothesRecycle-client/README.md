# clothesRecycle-client

校园二手衣物回收系统用户端（Vue3 + Pinia + Vue Router + Axios）。

## 启动

```bash
npm install
npm run dev
```

## 构建

```bash
npm run build
```

## 环境变量

`.env.development`

```env
# 本地开发后端地址
VITE_API_BASE_URL=http://localhost:8080
```

`.env.production`

```env
# 生产环境必须配置真实后端地址
VITE_API_BASE_URL=https://api.your-domain.com
```

说明：

- 开发环境未配置 `VITE_API_BASE_URL` 时，会回退 `http://localhost:8080`。
- 生产环境未配置 `VITE_API_BASE_URL` 时，前端会直接抛出配置错误，避免请求误打到 `localhost`。

## UI 说明

- 整体配色、圆角、卡片层次参考根目录 `prototype.html`。
- 当前页面包含：登录、注册、物品广场、物品详情、发布、订单、积分、签到、排行、个人中心。
