# AGENTS 指南（clothesRecycle Monorepo）
本文件面向在 `D:\23809\Desktop\ai\clothesRecycle` 工作的代理式编码助手。
目标：在多子项目仓库中以最小改动交付可构建、可验证、可维护的结果。

## 1. 仓库结构与技术栈
- `clothesRecycle-client/`：用户端前端，Vue 3 + Vite + Pinia + Vue Router + Axios。
- `clothesRecycle-admin/`：管理端前端，Vue 3 + Vite + Pinia + Vue Router + Axios + ECharts。
- `clothesRecycle-server/`：后端，Spring Boot 3 + MyBatis-Plus + Sa-Token + Redis + MinIO。
- 根目录是聚合目录，不承载统一构建脚本；所有命令需在子项目目录执行或显式指定前缀。

## 2. Cursor / Copilot 规则检查结果
基于仓库根目录已检查：
- `.cursor/rules/`
- `.cursorrules`
- `.github/copilot-instructions.md`

当前未发现以上规则文件。
若后续新增，需第一时间将关键约束同步到本文件，并在执行时优先遵循。

## 3. 代理执行总原则
- 最小改动：仅修改与需求直接相关的文件与逻辑。
- 先读后改：先阅读目标模块及其直接依赖（前端：api/store/router/view；后端：controller/service/mapper/dto/vo）。
- 不混入无关重构：命名清洗、样式重排、架构重整与当前任务分离。
- 不引入新依赖：除非需求明确要求并说明原因。
- 不提交敏感信息：密钥、token、账号密码、私密 `.env` 内容。

## 4. Build / Lint / Test 命令（重点：单测）
默认在对应子项目目录执行。
若从根目录执行：前端使用 `npm --prefix <dir> run <script>`，后端使用 `mvn -f clothesRecycle-server/pom.xml ...`。

### 4.1 clothesRecycle-client（用户端）
- 安装依赖：`npm install`
- 本地开发：`npm run dev`
- 生产构建：`npm run build`
- 预览构建：`npm run preview`
- 代码格式化：`npm run format`

Lint / Test 现状：
- `package.json` 无 `lint` 脚本，未发现 ESLint 配置。
- `package.json` 无 `test` 脚本，仓库未发现前端测试文件。
- 当前无法执行“单个前端测试”。

若后续接入 Vitest，建议约定：
- 全量：`npx vitest run`
- 单文件：`npx vitest run src/foo/bar.spec.js`
- 单用例：`npx vitest run src/foo/bar.spec.js -t "case name"`

### 4.2 clothesRecycle-admin（管理端）
- 安装依赖：`npm install`
- 本地开发：`npm run dev`
- 生产构建：`npm run build`
- 预览构建：`npm run preview`
- 代码格式化：`npm run format`

Lint / Test 现状：
- `package.json` 无 `lint` 脚本，未发现 ESLint 配置。
- `package.json` 无 `test` 脚本，仓库未发现前端测试文件。
- 当前无法执行“单个前端测试”。

若后续接入 Vitest，建议约定：
- 全量：`npx vitest run`
- 单文件：`npx vitest run src/foo/bar.test.js`
- 单用例：`npx vitest run src/foo/bar.test.js -t "关键词"`

### 4.3 clothesRecycle-server（后端）
- 启动服务：`mvn spring-boot:run`
- 编译：`mvn clean compile`
- 打包（跳过测试）：`mvn clean package -DskipTests`
- 提交前推荐：`mvn clean verify`

测试命令（当前可执行）：
- 全量测试：`mvn test`
- 单个测试类：`mvn -Dtest=GlobalExceptionHandlerTest test`
- 单个测试方法：`mvn -Dtest=GlobalExceptionHandlerTest#should_return_400_when_validation_failed test`
- 同类多方法：`mvn -Dtest=ApiPrefixAndRoleTest#should_reject_* test`
- 多测试类：`mvn -Dtest=GlobalExceptionHandlerTest,ApiPrefixAndRoleTest test`

当前测试目录示例：
- `src/test/java/com/example/project/exception`
- `src/test/java/com/example/project/security`
- `src/test/java/com/example/project/controller`
- `src/test/java/com/example/project/service`

Lint / 静态检查现状：
- `pom.xml` 未配置 Checkstyle / SpotBugs / PMD / Spotless。
- 当前质量门禁以 `mvn clean verify` + 代码评审为主。

## 5. 前端代码风格（client / admin 通用）
- 语言与范式：JavaScript + Vue SFC，优先 `<script setup>`。
- 路径别名：`@` 映射到 `src`，跨目录优先 `@/`。
- Prettier：`semi: false`、`singleQuote: true`、`printWidth: 100`。
- 修改策略：仅做必要格式化，避免大面积无语义 diff。

Imports 规范：
- 顺序：第三方依赖 -> `@/` 模块 -> 相对路径模块。
- 合并同源导入，删除未使用导入。
- 不为“风格偏好”重排全文件导入。

命名规范：
- 组件文件：`PascalCase.vue`，页面组件建议 `*View.vue`。
- Store：`useXxxStore`。
- 变量/函数：`camelCase`；布尔优先 `is/has/can`。
- 常量：仅在必要时使用 `UPPER_SNAKE_CASE`。

类型与数据处理：
- 当前非 TypeScript，默认继续使用 JS。
- 路由参数中的数值字段显式 `Number(...)` 转换。
- 读取本地存储 JSON 时处理空值与解析失败。
- 避免透传不透明大对象，优先传递明确字段。

API 与错误处理：
- 统一复用 `src/api/request.js`，禁止在业务页重复 `axios.create`。
- `client` 使用 `client_token` / `client_profile`。
- `admin` 使用 `admin_token` / `admin_profile`。
- 响应拦截器约定：`payload.code !== 200` 时 reject，成功返回 `payload.data ?? payload`。
- 关键异步流程（提交、审核、删除、支付、状态切换）补充 `try/catch`。
- 错误提示面向用户，不暴露堆栈或内部实现细节。

## 6. 后端代码风格（server）
- Java 17，4 空格缩进，保持与现有文件风格一致。
- 分层边界清晰：`controller -> service -> mapper`，禁止 controller 直调 mapper。
- 统一响应：`Result<T>` + `ResultCode`，避免返回裸对象或裸 `Map`。
- 业务失败抛 `BusinessException`，由 `GlobalExceptionHandler` 统一转换响应。

Imports 与命名：
- 禁止 `import xxx.*`。
- 推荐分组：JDK -> 第三方 -> `com.example.project...` -> 静态导入。
- 类名使用职责后缀：`*Controller`、`*Service`、`*ServiceImpl`、`*Mapper`、`*Dto`、`*Vo`。
- 测试方法建议：`should_xxx_when_yyy`。

DTO / VO / PO 与校验：
- 严格区分 DTO、VO、PO，避免跨层混用。
- DTO 使用 `jakarta.validation` 注解进行参数校验。
- Controller 入参可校验时默认加 `@Valid`。
- 查询条件优先 `LambdaQueryWrapper`，提高类型安全与可维护性。

异常与日志：
- 不吞异常；可预期业务问题统一抛 `BusinessException`。
- 全局异常处理器输出标准响应结构。
- 日志避免泄露敏感信息，按需使用脱敏工具（如 `LogMaskUtil`）。

## 7. 变更与验证流程（建议）
- 第一步：确认目标子项目与影响范围。
- 第二步：阅读相关代码与现有约定（优先看子项目 `AGENTS.md`）。
- 第三步：按最小改动实现并自检导入、命名、错误处理。
- 第四步：执行验证命令（前端至少 `npm run build`，后端至少运行受影响测试）。
- 第五步：交付说明写明改动内容、验证命令、测试限制与未覆盖风险。

## 8. 提交前检查清单
- 是否只改了与需求相关的文件？
- 是否遵循对应子项目构建命令与风格约定？
- 是否清理未使用导入、调试代码、临时日志？
- 前端是否至少构建通过（`npm run build`）？
- 后端是否运行了受影响测试（至少单测类/方法）？
- 是否明确说明当前仓库 lint/test 的真实可用状态？

## 9. 与子项目 AGENTS.md 的关系
- 本文件是仓库级总规范，适用于跨项目协作与通用流程。
- 若子项目有更细规则，优先遵循：
  - `clothesRecycle-client/AGENTS.md`
  - `clothesRecycle-admin/AGENTS.md`
  - `clothesRecycle-server/AGENTS.md`
- 若本文件与子项目文档冲突，以更具体、离代码更近的规则为准。

---
维护建议：仓库一旦新增 lint/test、Cursor 规则或 Copilot 规则，请立即更新本文件。
