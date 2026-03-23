# AGENTS 指南（clothesRecycle-client）

本文档面向在本仓库工作的智能编码代理。
目标：在不破坏现有结构的前提下，高质量完成开发任务。

## 1. 项目概览
- 技术栈：Vue 3（`<script setup>`）+ Pinia + Vue Router + Axios + Vite。
- 包管理器：`npm`（有 `package-lock.json`，默认使用 npm）。
- Node 版本：`^20.19.0 || >=22.12.0`。
- 路径别名：`@` -> `src`（见 `vite.config.js`、`jsconfig.json`）。
- 关键目录：
  - `src/api`：接口封装。
  - `src/stores`：Pinia 状态。
  - `src/views`：页面组件。
  - `src/router/index.js`：路由和登录守卫。
  - `src/assets`：全局样式和设计变量。

## 2. Cursor / Copilot 规则状态
已检查：
- `.cursor/rules/`
- `.cursorrules`
- `.github/copilot-instructions.md`

当前仓库未发现上述规则文件。
若后续新增，请将规则同步到本文档并优先遵循。

## 3. 构建 / 检查 / 测试命令
### 3.1 安装与本地开发
```bash
npm install
npm run dev
```

### 3.2 构建与预览
```bash
npm run build
npm run preview
```

### 3.3 代码格式化
```bash
npm run format
```
说明：
- `format` 实际命令：`prettier --write --experimental-cli src/`。
- 仅格式化 `src/`，不会处理根目录其它文件。

### 3.4 Lint 现状
- 没有 `npm run lint`。
- 未发现 ESLint 配置文件。
- 如任务要求 lint，先说明仓库未配置，再决定是否补充。

### 3.5 Test 现状（含单测）
- 没有 `npm run test`。
- 未发现项目测试文件（`src/**/*.test.*`、`src/**/*.spec.*`）。
- 当前仓库无法运行“单个测试”或“全量测试”。

若后续接入 Vitest，可约定：
```bash
# 全量
npx vitest run
# 单文件
npx vitest run src/foo/bar.spec.js
# 单用例名
npx vitest run src/foo/bar.spec.js -t "case name"
```

## 4. 代码风格总原则
- 优先延续现有写法，再考虑个性化优化。
- 改动最小化：只改与任务直接相关内容。
- 避免“顺手重构”导致无关 diff。
- 不新增架构层，除非需求明确要求。

## 5. Imports 规范
- 先第三方依赖，再项目内模块。
- 相同来源导入尽量合并，避免重复导入。
- `src` 内跨目录引用优先使用 `@/`。
- 删除未使用 import。
- 命名保持语义化，不使用不明缩写。

## 6. 格式化规范
基于 `.prettierrc.json`：
- `semi: false`
- `singleQuote: true`
- `printWidth: 100`

补充：
- 不手动制造大面积格式化噪音。
- 模板长属性可换行，但应保持对齐和可读性。
- 与周边文件保持一致的空行和缩进节奏。

## 7. 类型与数据约定
- 项目当前为 JavaScript，默认继续使用 JS。
- 变量名体现语义：`itemId`、`campusId`、`pointPrice`。
- 路由参数中的数值字段做显式转换（如 `Number(...)`）。
- 布尔命名使用 `is/has/can` 前缀。
- 避免不透明“大对象”透传，优先明确字段。

## 8. 命名规范
- 页面组件：`PascalCase` + `View` 后缀（如 `LoginView.vue`）。
- API 函数：动词开头（`getXxx`、`createXxx`、`listXxx`）。
- Store：`useXxxStore`。
- 变量/函数：`camelCase`。
- 常量：仅在必要时用 `UPPER_SNAKE_CASE`。

## 9. API 与错误处理
`src/api/request.js` 现有约定：
- 请求拦截器从 `localStorage.client_token` 写入 `Authorization`。
- 响应 `payload.code !== 200` 时统一 `reject(Error)`。
- 成功返回 `payload.data ?? payload`。

新增 API 代码要求：
- 复用统一 `request` 实例，不在业务文件 `axios.create`。
- 按业务域拆分 API 文件，避免超级大文件。
- 参数命名尽量与后端字段对齐。

页面交互建议：
- 当前页面普遍直接 `await`，缺少 `try/catch`。
- 新增关键流程（提交、取消、支付、删除）建议补充 `try/catch`。
- 错误提示应面向用户，不暴露堆栈。

## 10. Vue / 路由 / 状态约定
- 默认使用 `<script setup>`。
- 列表与详情数据通常用 `ref/reactive + onMounted` 拉取。
- 需要鉴权的页面通过 `meta.auth` + 全局守卫控制。
- 用户态沿用 `useUserStore` 与本地存储键：`client_token`、`client_profile`。

## 11. CSS 与 UI 约定
- 优先复用 `src/assets/base.css` 中变量。
- 优先复用已有结构类：`.page`、`.topbar`、`.section`、`.btn`、`.input`。
- 非必要不引入新 UI 框架。
- 内联样式若重复出现，应抽取成类。

## 12. 代理执行建议
1. 先读相关文件再改动，避免盲改。
2. 改动完成后至少执行 `npm run build`。
3. 涉及较多 `src/` 改动时执行 `npm run format`。
4. 结果说明应包含：改动内容、原因、验证命令、测试限制。

## 13. 禁止事项
- 不随意改动构建配置和目录结构。
- 不将无关重构混入当前任务。
- 不在未说明情况下引入新依赖。
- 不随意变更 `client_token` / `client_profile` 键名。

## 14. 交付前检查清单
- 是否符合 Prettier 规则（单引号、无分号、100 列）？
- 是否复用 `@/` 别名与统一 `request`？
- 是否清理未使用导入和死代码？
- 是否执行并通过 `npm run build`？
- 是否明确说明“当前无 lint/test 脚本”？

---
维护建议：当仓库接入 lint/test 或新增 Cursor/Copilot 规则后，第一时间更新本文档。
