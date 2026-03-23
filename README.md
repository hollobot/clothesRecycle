# clothesRecycle

校园旧衣回收与流转平台（Monorepo）。

## 项目结构

- `clothesRecycle-client/`：用户端（Vue 3 + Vite + Pinia + Vue Router + Axios + Element Plus）
- `clothesRecycle-admin/`：管理端（Vue 3 + Vite + Pinia + Vue Router + Axios + ECharts + Element Plus）
- `clothesRecycle-server/`：后端（Spring Boot 3 + MyBatis-Plus + Sa-Token + Redis + MinIO）

## 快速开始

### 1) 后端启动

```bash
cd clothesRecycle-server
mvn spring-boot:run
```

默认端口：`8080`

### 2) 用户端启动

```bash
cd clothesRecycle-client
npm install
npm run dev
```

默认端口：`5173`

### 3) 管理端启动

```bash
cd clothesRecycle-admin
npm install
npm run dev
```

默认端口：`5174`（若 5173 被占用）

## 环境变量

### 用户端（`clothesRecycle-client`）

- 开发环境：`.env.development`

```env
VITE_API_BASE_URL=http://localhost:8080
```

- 生产环境：`.env.production`

```env
VITE_API_BASE_URL=https://api.your-domain.com
```

说明：

- 开发环境未配置时会回退到 `http://localhost:8080`。
- 生产环境必须配置 `VITE_API_BASE_URL`，否则会直接报错，避免请求误打到 `localhost`。

## 常用命令

### 用户端

```bash
cd clothesRecycle-client
npm run dev
npm run build
npm run format
```

### 管理端

```bash
cd clothesRecycle-admin
npm run dev
npm run build
npm run format
```

### 后端

```bash
cd clothesRecycle-server
mvn clean compile
mvn test
mvn clean verify
```

## 近期功能说明

- 完善订单流程（认领、确认、取消、完成、超时恢复）
- 完善用户资料模块（基础信息编辑、旧密码修改、短信验证码重置）
- 完善消息、我的发布、排行榜等模块的交互与状态展示
- 接入 Sa-Token + Redis 会话持久化

## 开发约定

- 前端 UI 优先使用 `element-plus`
- 业务状态在页面上必须体现可交互/不可交互
- 方法与关键字段需要保留注释
- 统一错误提示使用中文，避免直接暴露枚举值给用户

## 参考文档

- 原型图：`prototype.html`
- 设计文档：`设计文档.md`
- 代理协作规范：`AGENTS.md`
