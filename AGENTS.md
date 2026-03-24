# AGENTS 指南（clothesRecycle Monorepo）

本文件适用于在 `D:\23809\Desktop\ai\clothesRecycle` 目录执行任务的代理式编码助手。
目标：快速定位子项目、按真实可执行命令验证、以最小改动交付稳定结果。

## 1. 仓库结构与技术栈
- `clothesRecycle-client/`：用户端前端（Vue 3 + Vite + Pinia + Vue Router + Axios）。
- `clothesRecycle-admin/`：管理端前端（Vue 3 + Vite + Pinia + Vue Router + Axios + ECharts）。
- `clothesRecycle-server/`：后端（Spring Boot 3.2.5 + MyBatis-Plus + Sa-Token + Redis + MinIO）。
- 根目录是聚合目录，不提供统一构建脚本；命令需在子目录执行或使用前缀/`-f`。

## 2. Cursor / Copilot 规则状态（已核对）
已检查以下位置：
- `.cursor/rules/`
- `.cursorrules`
- `.github/copilot-instructions.md`

当前仓库未发现上述规则文件。
若后续新增，必须优先遵循，并同步更新本文件。

## 3. 通用执行原则
- 最小改动：只修改与当前需求直接相关的文件。
- 先读后改：先阅读目标模块及其直接依赖，再实施变更。
- 不混入无关重构，不引入无关依赖，不提交任何敏感信息。

## 4. Build / Lint / Test 命令（重点含单测）
默认建议先进入对应子项目目录执行。
若在根目录执行，请使用：
- 前端：`npm --prefix <subdir> run <script>`
- 后端：`mvn -f clothesRecycle-server/pom.xml <goal>`

### 4.1 clothesRecycle-client（用户端）
- 安装依赖：`npm install`
- 本地开发：`npm run dev`
- 生产构建：`npm run build`
- 预览构建：`npm run preview`
- 代码格式化：`npm run format`

Lint/Test 现状：
- 无 `lint`/`test` 脚本，未发现前端测试文件，当前无法运行单测。

### 4.2 clothesRecycle-admin（管理端）
- 安装依赖：`npm install`
- 本地开发：`npm run dev`
- 生产构建：`npm run build`
- 预览构建：`npm run preview`
- 代码格式化：`npm run format`

Lint/Test 现状：
- 无 `lint`/`test` 脚本，未发现前端测试文件，当前无法运行单测。

### 4.3 clothesRecycle-server（后端）
- 启动服务：`mvn spring-boot:run`
- 编译：`mvn clean compile`
- 打包（跳过测试）：`mvn clean package -DskipTests`
- 提交前推荐：`mvn clean verify`

测试命令（当前可执行）：
- 全量测试：`mvn test`
- 单个测试类：`mvn -Dtest=GlobalExceptionHandlerTest test`
- 单个测试方法：`mvn -Dtest=GlobalExceptionHandlerTest#should_return_400_when_validation_failed test`
- 同类多方法（通配）：`mvn -Dtest=ApiPrefixAndRoleTest#should_reject_* test`
- 多测试类：`mvn -Dtest=GlobalExceptionHandlerTest,ApiPrefixAndRoleTest test`

已发现后端测试目录：
- `src/test/java/com/example/project/exception`
- `src/test/java/com/example/project/security`
- `src/test/java/com/example/project/controller`
- `src/test/java/com/example/project/service`

Lint/静态检查现状：
- `pom.xml` 未配置 Checkstyle / SpotBugs / PMD / Spotless。
- 当前质量门禁以 `mvn clean verify` + 代码评审为主。

### 4.4 根目录等价命令示例
- 构建用户端：`npm --prefix clothesRecycle-client run build`
- 构建管理端：`npm --prefix clothesRecycle-admin run build`
- 后端全测：`mvn -f clothesRecycle-server/pom.xml test`
- 后端单测类：`mvn -f clothesRecycle-server/pom.xml -Dtest=UserAdminControllerTest test`
- 后端单测方法：`mvn -f clothesRecycle-server/pom.xml -Dtest=UserAdminControllerTest#should_* test`

## 5. 前端代码风格（client/admin 通用）
基础规范：
- 语言为 JavaScript（当前未启用 TypeScript），默认沿用 JS。
- Vue SFC 优先使用 `<script setup>`，并保持目标文件既有块顺序。
- 路径别名 `@` 指向 `src`，跨目录依赖优先 `@/`。
- Prettier 规则：`semi: false`、`singleQuote: true`、`printWidth: 100`。

Imports 规范：
- 顺序：第三方依赖 -> `@/` 模块 -> 相对路径模块。
- 合并同源导入，删除未使用导入。
- 不做无语义的全文件 import 重排。

命名规范：
- 组件文件：`PascalCase.vue`，页面组件建议 `*View.vue`。
- Store：`useXxxStore`。
- 变量/函数用 `camelCase`，布尔使用 `is/has/can` 前缀，常量按需用 `UPPER_SNAKE_CASE`。

类型与数据处理：
- 路由参数中的数值字段做显式转换（如 `Number(...)`）。
- 本地存储 JSON 读取必须处理空值和解析失败。
- 避免透传不透明大对象，优先传递明确字段。

API 与错误处理：
- 统一复用 `src/api/request.js`，禁止在业务页重复 `axios.create`。
- 用户端登录态键：`client_token` / `client_profile`。
- 管理端登录态键：`admin_token` / `admin_profile`。
- 响应约定：`payload.code !== 200` 时 reject，成功返回 `payload.data ?? payload`。
- 关键流程（提交/删除/审核/支付/状态切换）补充 `try/catch`，错误提示面向用户。

## 6. 后端代码风格（server）
基础规范：
- Java 17，4 空格缩进，禁止 Tab。
- 保持分层边界：`controller -> service -> mapper`，禁止 controller 直调 mapper。
- 统一返回 `Result<T>` + `ResultCode`，避免返回裸对象/裸 `Map`。
- 业务失败统一抛 `BusinessException`，由 `GlobalExceptionHandler` 转换响应。

Imports 与命名：
- 禁止 `import xxx.*`。
- 推荐分组：JDK -> 第三方 -> `com.example.project...` -> 静态导入。
- 类名按职责后缀：`*Controller`、`*Service`、`*ServiceImpl`、`*Mapper`、`*Dto`、`*Vo`。
- 方法名使用 `camelCase` 且动词开头，测试方法建议 `should_xxx_when_yyy`。

类型与建模：
- 严格区分 DTO / VO / PO，不跨层混用。
- DTO 使用 `jakarta.validation` 注解，Controller 可校验入参默认加 `@Valid`。
- 查询条件优先 `LambdaQueryWrapper`，提升类型安全与可维护性。

异常与日志：
- 不吞异常；可预期业务问题抛 `BusinessException`。
- 全局异常处理器统一输出标准响应结构。
- 日志避免泄露敏感数据，按需使用脱敏工具（如 `LogMaskUtil`）。

## 7. 变更与验证流程（推荐）
- 第一步：确认目标子项目与影响范围。
- 第二步：阅读对应子项目 `AGENTS.md` 与相关代码。
- 第三步：按最小改动实现，并自检 imports/命名/异常处理。
- 第四步：执行验证（前端至少 `npm run build`；后端至少运行受影响测试）。
- 第五步：交付说明写明改动内容、验证命令、限制与风险。

## 8. 提交前检查清单
- 只改了与需求相关的文件。
- 清理了未使用导入、调试代码、临时日志。
- 前端改动至少构建通过（`npm run build`）。
- 后端改动至少运行受影响单测（类或方法级）。
- 明确说明仓库当前 lint/test 的真实可用状态。

## 9. 与子项目 AGENTS.md 的优先级
- 本文件是仓库级总规范。
- 更具体规则优先，子项目文档高于本文件同类条目：
  - `clothesRecycle-client/AGENTS.md`
  - `clothesRecycle-admin/AGENTS.md`
  - `clothesRecycle-server/AGENTS.md`

---
维护建议：新增 lint/test 或新增 Cursor/Copilot 规则后，立即同步更新本文件。
