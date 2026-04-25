# AGENTS 指南（clothesRecycle Monorepo）

适用范围：`D:\23809\Desktop\ai\clothesRecycle`
目标：帮助代理式编码助手快速定位子项目、遵循现有约定、用真实可执行命令完成验证。

## 1. 仓库结构
- `clothesRecycle-client/`：用户端前端，Vue 3 + Vite + Pinia + Vue Router + Axios + Element Plus。
- `clothesRecycle-admin/`：管理端前端，Vue 3 + Vite + Pinia + Vue Router + Axios + Element Plus + ECharts。
- `clothesRecycle-server/`：后端，Spring Boot 3.2.5 + MyBatis-Plus + Sa-Token + Redis + MinIO。
- 根目录是聚合目录，没有统一的 `package.json` 或根 `pom.xml` 作为主入口。
- `clothesRecycle-server/bin/` 下存在独立文件，但默认不作为主要开发入口；优先修改正式源码目录。

## 2. 规则文件扫描结果
已检查以下位置：
- `.cursor/rules/`
- `.cursorrules`
- `.github/copilot-instructions.md`

当前仓库未发现上述 Cursor / Copilot 规则文件。
若后续新增，必须优先遵循，并同步更新本文件。

## 3. 总体执行原则
- 先确认目标子项目，再进入对应目录执行命令。
- 先读相关代码和子项目 `AGENTS.md`，再做最小必要改动。
- 不混入无关重构，不随意改目录结构，不引入无关依赖。
- 不提交密钥、令牌、真实账号密码、`.env` 敏感配置。
- 前端改动至少做构建验证；后端改动至少运行受影响测试。

## 4. 根目录等价命令
建议直接在子项目目录执行；若必须在根目录执行，可使用：
- 用户端安装依赖：`npm --prefix clothesRecycle-client install`
- 用户端构建：`npm --prefix clothesRecycle-client run build`
- 管理端安装依赖：`npm --prefix clothesRecycle-admin install`
- 管理端构建：`npm --prefix clothesRecycle-admin run build`
- 后端编译：`mvn -f clothesRecycle-server/pom.xml clean compile`
- 后端全量测试：`mvn -f clothesRecycle-server/pom.xml test`
- 后端单个测试类：`mvn -f clothesRecycle-server/pom.xml -Dtest=UserAdminControllerTest test`
- 后端单个测试方法：`mvn -f clothesRecycle-server/pom.xml -Dtest=UserAdminControllerTest#should_create_user_when_request_valid test`

## 5. clothesRecycle-client（用户端）
### 5.1 构建与开发命令
在 `clothesRecycle-client/` 目录执行：
- 安装依赖：`npm install`
- 本地开发：`npm run dev`
- 生产构建：`npm run build`
- 预览构建：`npm run preview`
- 代码格式化：`npm run format`

### 5.2 Lint / Test 现状
- `package.json` 仅包含 `dev`、`build`、`preview`、`format` 脚本。
- 当前没有 `lint` 脚本，也未发现 ESLint 配置。
- 当前没有 `test` 脚本，也未发现 `src/**/*.test.*` 或 `src/**/*.spec.*` 测试文件。
- 因此当前用户端无法运行全量测试或单个测试。

### 5.3 用户端代码风格
- 语言保持 JavaScript，不擅自迁移 TypeScript。
- 优先使用 Vue SFC + `<script setup>`，并保持目标文件原有块顺序。
- 路径别名 `@` 指向 `src`，跨目录引用优先 `@/`。
- 格式化遵循 `.prettierrc.json`：`semi: false`、`singleQuote: true`、`printWidth: 100`。
- import 顺序：第三方依赖 -> `@/` 模块 -> 相对路径模块。
- 合并同源导入，删除未使用导入，避免无语义的大范围重排。
- 组件文件使用 `PascalCase.vue`，页面组件优先 `*View.vue`。
- store 命名使用 `useXxxStore`，变量和函数使用 `camelCase`。
- 布尔命名优先 `is/has/can` 前缀，常量按需使用 `UPPER_SNAKE_CASE`。
- 路由参数中的数值字段显式转成 `Number(...)`。
- 读取本地存储 JSON 时必须处理空值与解析失败。
- 统一复用 `src/api/request.js`，禁止在业务页重复 `axios.create`。
- 登录态键名沿用 `client_token`、`client_profile`，不要擅自改名。
- 请求成功返回 `payload.data ?? payload`；`payload.code !== 200` 时按现有拦截器逻辑 reject。
- 提交、删除、支付、状态切换等关键异步流程优先补 `try/catch`，错误信息面向用户。

## 6. clothesRecycle-admin（管理端）
### 6.1 构建与开发命令
在 `clothesRecycle-admin/` 目录执行：
- 安装依赖：`npm install`
- 本地开发：`npm run dev`
- 生产构建：`npm run build`
- 预览构建：`npm run preview`
- 代码格式化：`npm run format`

### 6.2 Lint / Test 现状
- `package.json` 仅包含 `dev`、`build`、`preview`、`format` 脚本。
- 当前没有 `lint` 脚本，也未发现 ESLint 配置。
- 当前没有 `test` 脚本，也未发现 `src/**/*.test.*` 或 `src/**/*.spec.*` 测试文件。
- 因此当前管理端无法运行全量测试或单个测试。

### 6.3 管理端代码风格
- 与用户端一致，保持 JavaScript + Vue 3 现有写法。
- 默认使用 `<script setup>`，路由组件优先延续现有懒加载模式。
- 路径别名、Prettier 规则、import 顺序与用户端保持一致。
- 组件命名使用 `PascalCase`，路由路径使用 kebab-case。
- 统一走 `src/api/request.js`，不要在页面中直接写裸 `axios` 请求。
- 登录态键名沿用 `admin_token`、`admin_profile`。
- 本地存储 JSON 读取要兜底，避免脏数据导致页面异常。
- 对后端非 200 业务码沿用拦截器 reject 机制，不要静默吞错。
- 涉及鉴权、登录态失效、角色判断的逻辑，要与全局路由守卫和 `admin` store 保持一致。

## 7. clothesRecycle-server（后端）
### 7.1 构建与运行命令
在 `clothesRecycle-server/` 目录执行：
- 启动服务：`mvn spring-boot:run`
- 编译：`mvn clean compile`
- 打包（跳过测试）：`mvn clean package -DskipTests`
- 提交前推荐：`mvn clean verify`

### 7.2 测试命令（重点）
- 全量测试：`mvn test`
- 单个测试类：`mvn -Dtest=GlobalExceptionHandlerTest test`
- 单个测试方法：`mvn -Dtest=GlobalExceptionHandlerTest#should_return_400_when_validation_failed test`
- 同类多方法：`mvn -Dtest=ApiPrefixAndRoleTest#should_reject_* test`
- 多测试类：`mvn -Dtest=GlobalExceptionHandlerTest,ApiPrefixAndRoleTest test`

### 7.3 后端测试现状
当前已发现测试位于：
- `src/test/java/com/example/project/exception`
- `src/test/java/com/example/project/security`
- `src/test/java/com/example/project/controller/admin`
- `src/test/java/com/example/project/service`

当前 `pom.xml` 未配置 Checkstyle、SpotBugs、PMD、Spotless 等静态检查插件。
后端当前质量门禁以 `mvn clean verify` + 代码评审为主。

### 7.4 后端代码风格
- Java 17，4 空格缩进，禁止 Tab。
- 保持分层边界：`controller -> service -> mapper`，禁止 controller 直调 mapper。
- 统一返回 `Result<T>` + `ResultCode`，不要返回裸对象或裸 `Map`。
- 业务失败统一抛 `BusinessException`，由 `GlobalExceptionHandler` 转成标准响应。
- import 禁止使用通配符；推荐分组为 JDK -> 第三方 -> `com.example.project...` -> 静态导入。
- 删除未使用 import，不做无意义的全文件整理。
- 类名按职责使用 `*Controller`、`*Service`、`*ServiceImpl`、`*Mapper`、`*Dto`、`*Vo`。
- 方法名使用 `camelCase` 且动词开头，测试方法优先 `should_xxx_when_yyy`。
- 严格区分 DTO / VO / PO，不跨层混用。
- DTO 字段优先使用 `jakarta.validation` 注解，`@RequestBody` 入参可校验时默认加 `@Valid`。
- 查询条件优先 `LambdaQueryWrapper`，避免硬编码字段字符串。
- 不吞异常；业务可预期错误抛 `BusinessException`，系统异常交给全局异常处理。
- 日志避免泄露敏感信息，延续 `LogMaskUtil` 脱敏约定。
- 控制层测试优先延续 `@WebMvcTest + MockMvc` 现有模式，断言优先校验统一响应体中的 `code`、`msg`、`data`。

## 8. 变更与验证流程
- 第一步：确认需求落在 client、admin、server 中的哪一个子项目。
- 第二步：优先阅读目标子项目自己的 `AGENTS.md` 与相邻模块实现。
- 第三步：按最小改动实现，避免把重构和功能修改混在一起。
- 第四步：前端至少运行 `npm run build`；后端至少运行受影响测试，必要时补 `mvn clean verify`。
- 第五步：交付说明写清改动文件、验证命令、结果和当前仓库限制。

## 9. 提交前检查清单
- 只修改了与当前任务直接相关的文件。
- 清理未使用导入、调试输出、临时日志、无效注释。
- 前端改动已完成构建验证。
- 后端改动已运行受影响单测或说明无法运行的原因。
- 回复中明确说明当前仓库真实的 lint/test 可用状态。

## 10. 优先级说明
- 根目录 `AGENTS.md` 提供全仓库通用规范。
- 更具体的子项目文档优先于本文件同类条目：
  - `clothesRecycle-client/AGENTS.md`
  - `clothesRecycle-admin/AGENTS.md`
  - `clothesRecycle-server/AGENTS.md`
- 若未来出现 Cursor / Copilot 规则文件，更具体规则优先于 AGENTS。

---
维护建议：当脚本、测试框架、静态检查或规则文件发生变化时，立即同步更新本文件。
