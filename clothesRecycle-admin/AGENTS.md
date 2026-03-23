# AGENTS 指南（clothesRecycle-admin）

## 1. 目标与适用范围
- 本文件用于指导代理式编码助手在本仓库内进行一致、安全、可验证的改动。
- 适用于开发、重构、修复、文档维护、脚本维护等全部日常工程任务。
- 默认遵循“先理解现状，再最小改动，再验证结果”的执行顺序。
- 本仓库为 Vue 3 + Vite + Pinia + Vue Router + Axios 的前端管理后台。

## 2. 仓库现状（必须先读）
- 包管理器：npm（存在 `package-lock.json`）。
- Node 版本约束：`^20.19.0 || >=22.12.0`（见 `package.json`）。
- 语言：JavaScript（当前未启用 TypeScript）。
- 格式化工具：Prettier（存在 `.prettierrc.json`）。
- 当前未发现 ESLint 配置文件与 lint 脚本。
- 当前未发现测试框架依赖与测试脚本。
- 路径别名：`@` -> `src`（见 `vite.config.js` 与 `jsconfig.json`）。

## 3. Cursor / Copilot 规则扫描结果
- 未发现 `.cursorrules`。
- 未发现 `.cursor/rules/` 目录。
- 未发现 `.github/copilot-instructions.md`。
- 若未来新增上述规则文件，需将其要求合并进本文件并优先执行。

## 4. 常用命令（构建 / 格式化 / 测试）

### 4.1 安装与启动
- 安装依赖：`npm install`
- 本地开发：`npm run dev`
- 生产构建：`npm run build`
- 本地预览构建产物：`npm run preview`

### 4.2 代码格式化（当前唯一“规范检查”命令）
- 全量格式化（src）：`npm run format`
- 等价命令：`npx prettier --write --experimental-cli src/`
- 说明：仓库目前无真正 lint 脚本，不能把 `format` 等同于语义级 lint。

### 4.3 测试命令现状（重点）
- 当前 `package.json` 中没有 `test` 脚本。
- 当前仓库未安装 Vitest/Jest，也未发现项目测试文件（忽略 `node_modules`）。
- 因此当前**无法直接运行单测或单个测试用例**。

### 4.4 若后续引入 Vitest，建议约定命令
- 全量测试：`npx vitest run`
- 监听模式：`npx vitest`
- 运行单个测试文件：`npx vitest run src/foo/bar.test.js`
- 运行单个测试名称：`npx vitest run src/foo/bar.test.js -t "用例名关键词"`
- 仅运行匹配路径：`npx vitest run --include "src/**/xxx.test.js"`
- 说明：在未引入 Vitest 前，这些命令属于“目标约定”，非当前可执行事实。

## 5. 目录与职责约定
- `src/api/`：按领域拆分 API 模块，统一复用 `src/api/request.js`。
- `src/router/`：路由定义、守卫、路由级懒加载。
- `src/stores/`：Pinia 状态管理。
- `src/views/`：页面级组件（按业务目录分组）。
- `src/components/`：可复用通用组件。
- `src/assets/`：样式与静态资源。

## 6. 代码风格总则
- 使用 ES Module 语法。
- 遵循 Prettier 配置：
  - `semi: false`（不加分号）
  - `singleQuote: true`（单引号）
  - `printWidth: 100`
- 除非明确需要，不引入新的格式化工具或自定义风格分歧。
- 修改时保持“局部一致性”：优先沿用目标文件已有结构和书写风格。
- 非必要不做大面积无语义改动（如全文件重排、无关重命名）。

## 7. Imports 规范
- 三方依赖优先，本地模块其次。
- 使用 `@/` 引用 `src` 内模块，避免深层相对路径地狱。
- 同一文件内 import 分组建议：
  1) 框架/三方包
  2) 别名模块（`@/`）
  3) 相对路径模块
- 保持 import 精简：删除未使用导入。
- Vue Router 组件（如 `RouterLink`）按文件既有习惯处理，避免无意义改风格。

## 8. Vue 组件规范
- 优先使用 `<script setup>` 组合式 API（仓库主流写法）。
- 文件块顺序在仓库中并非完全统一；修改已有文件时保持原顺序。
- 页面组件命名使用 PascalCase，例如 `AdminLoginView.vue`。
- 业务目录可使用 kebab-case（如 `drop-point`），与现有结构一致。
- 路由组件优先使用懒加载：`() => import('...')`。
- 模板内避免复杂表达式，复杂逻辑下沉到 script 中。
- 非必要不在模板中堆叠过长内联样式，优先抽取 class。

## 9. 命名规范
- 变量/函数：`camelCase`。
- 组件文件：`PascalCase.vue`。
- Store：`useXxxStore` 命名。
- API 方法：动词开头，语义明确（如 `getUserList`、`adminLogin`）。
- 常量：`UPPER_SNAKE_CASE`（仅真正常量使用）。
- 路由路径：kebab-case，保持 URL 可读性。

## 10. 类型与数据约束（JS 项目）
- 当前项目为 JavaScript，不强制 TypeScript。
- 新增复杂函数时可用 JSDoc 描述参数与返回值。
- 与后端交互的数据结构要有最小字段校验与兜底值。
- 避免依赖“隐式 truthy/falsy”表达核心业务含义，必要时显式判断。
- 本地存储读取 JSON 时要考虑解析失败和空值场景。

## 11. API 与网络层规范
- 所有 HTTP 请求统一走 `src/api/request.js` 实例。
- 认证信息通过请求拦截器注入（当前为 `admin_token`）。
- 响应拦截器按后端约定处理 `code` / `msg` / `data`。
- 新 API 模块放入 `src/api/`，保持“一个领域一个文件”的清晰边界。
- 不在页面组件中直接写裸 axios 调用。

## 12. 错误处理规范
- 异步操作默认使用 `try/catch` 或显式 Promise 错误分支。
- 错误信息对用户可读，对开发可定位（必要时保留原始 error）。
- 不要静默吞错；至少记录日志或抛出可追踪错误。
- 登录态失效、鉴权失败等场景要与路由守卫逻辑一致。
- 对后端非 200 业务码，沿用当前拦截器 reject 机制。

## 13. 状态管理与路由守卫
- 登录态以 Pinia `admin` store 为单一事实来源。
- localStorage 与 store 状态保持同步更新。
- 路由鉴权统一在全局守卫处理，避免散落在页面内重复判断。
- 涉及权限扩展时，优先在路由 `meta` 中表达策略。

## 14. 变更策略（给代理的执行原则）
- 先读相关模块（API、store、router、view）再改。
- 优先最小可行改动，避免“顺手重构”扩大影响面。
- 不改动无关文件，不引入与任务无关依赖。
- 不提交密钥、令牌、环境敏感信息（如 `.env.*`）。
- 若发现现有行为与需求冲突，先在提交说明中写清取舍。

## 15. 验证清单（提交前）
- 至少执行：`npm run build`（确保可构建）。
- 建议执行：`npm run format`（保持风格一致）。
- 若只改样式/文案，也建议跑一次构建防止意外语法错误。
- 若未来引入测试框架：
  - 先跑受影响单测文件。
  - 再跑全量测试。
- 在回复或提交说明中明确列出已执行命令与结果。

## 16. 给后续维护者的补充
- 如新增 lint/test，请同步更新本文件第 4 节命令。
- 如新增 Cursor/Copilot 规则，请同步更新第 3 节并提炼关键约束。
- 如迁移到 TypeScript，请新增“类型策略”小节并替换第 10 节内容。
- 本文件目标是“让代理快速做对事”，因此要保持简洁、真实、可执行。
