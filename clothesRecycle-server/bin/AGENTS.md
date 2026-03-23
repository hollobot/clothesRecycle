# AGENTS 指南（clothesRecycle-server）
本文面向在本仓库执行任务的 Agent（代码生成/修改/测试代理）。
目标：快速对齐构建命令、测试命令、代码风格与工程约束，减少返工。

## 1. 项目概览
- 技术栈：Spring Boot 3.2.5 + MyBatis-Plus + Sa-Token + Redis + MinIO + JUnit 5。
- 构建工具：Maven（无 `mvnw`，默认使用系统 `mvn`）。
- Java 版本：17（`pom.xml` 固定 `java.version=17`）。
- 包根路径：`src/main/java/com/example/project`。
- 统一响应：`Result<T>` + `ResultCode`。
- 业务异常：`BusinessException`，由 `GlobalExceptionHandler` 统一处理。

## 2. Build / Lint / Test 命令
以下命令默认在仓库根目录执行。

### 2.1 构建与运行
- 启动服务：`mvn spring-boot:run`
- 编译主代码：`mvn clean compile`
- 打包（跳过测试）：`mvn clean package -DskipTests`
- 全量校验：`mvn clean verify`

### 2.2 测试（重点：单测运行方式）
- 运行全部测试：`mvn test`
- 运行单个测试类：`mvn -Dtest=GlobalExceptionHandlerTest test`
- 运行单个测试方法：`mvn -Dtest=GlobalExceptionHandlerTest#should_return_400_when_validation_failed test`
- 运行同一类中的多个方法（通配）：`mvn -Dtest=ApiPrefixAndRoleTest#should_reject_* test`

### 2.3 Lint / 静态检查现状
- 当前 `pom.xml` 未配置 Checkstyle / SpotBugs / PMD / Spotless 等独立 lint 插件。
- 现阶段以以下流程替代 lint 闸门：
  - `mvn clean verify`（保证编译与测试）
  - IDE 检查（IntelliJ inspections）
  - 代码评审执行本文风格规则
- 若新增 lint 插件，请在 PR 说明规则来源、影响范围和迁移策略。

## 3. 测试策略与约定
- 测试框架：JUnit 5 + Spring Test + MockMvc + AssertJ。
- 现有测试目录：
  - `src/test/java/com/example/project/exception`
  - `src/test/java/com/example/project/security`
- 测试命名：`should_xxx_when_yyy`。
- 控制层/安全边界测试优先 `@WebMvcTest`，按需 `@Import`。
- 断言优先校验统一响应体 `code`/`msg`/`data`，而不是仅看 HTTP 状态码。
- 新增接口建议至少补齐：
  - 1 个成功路径测试
  - 1 个参数校验失败测试
  - 1 个鉴权/权限边界测试（若适用）

## 4. 分层与目录约定
保持现有分层，不引入跨层调用捷径：
- `controller`：请求编排、参数校验、调用 service、返回 `Result`。
- `service` / `service.impl`：业务规则与事务边界。
- `mapper`：MyBatis-Plus 数据访问接口。
- `model.dto`：入参对象。
- `model.vo`：出参对象。
- `model.po`：持久化实体。
- `exception`：业务异常与全局异常处理。
- `config`：框架与中间件配置。
- `utils`：无状态工具类。
禁止在 `controller` 中直接写复杂业务或直接操作 `mapper`。

## 5. 代码风格（必须遵守）
### 5.1 格式
- 4 空格缩进，不使用 Tab。
- 一行一个语句，避免超长链式调用。
- 字段块、方法块之间保留合理空行，保持与现有文件一致。
- 除非必要，不进行大规模“纯格式化”改动。

### 5.2 Imports
- 禁止通配符导入（`import xxx.*`）。
- 推荐分组顺序：
  1) Java 标准库
  2) 第三方库（Spring/MyBatis/Sa-Token 等）
  3) 项目内包
  4) 静态导入（单独分组，通常置底）
- 删除未使用 import。

### 5.3 类型与建模
- 严格区分 DTO / VO / PO，不混用。
- DTO 字段配套 `jakarta.validation` 注解，并使用中文错误提示。
- PO 与表结构一致，复用 `BaseEntity` 公共字段。
- 对外响应统一用 `Result<T>`，不要返回裸对象。
- 优先使用明确类型，避免 `Object` 与原始集合。

### 5.4 命名规范
- 类名：大驼峰 + 职责后缀（`*Controller`/`*Service`/`*ServiceImpl`/`*Mapper`/`*Dto`/`*Vo`）。
- 方法名：小驼峰，动词开头，语义完整。
- 常量名：全大写下划线（例：`REGISTER_SMS_KEY_PREFIX`）。
- 测试方法名：`should_xxx_when_yyy`。
- 包名：全小写，按业务语义分层。

### 5.5 控制层规则
- `@RequestBody` 参数默认加 `@Valid`（适用时）。
- 控制器只做编排，不写持久化细节。
- 路由前缀遵循：`/api/public/**`、`/api/user/**`、`/api/admin/**`。

### 5.6 Service 与 Mapper 规则
- 业务校验失败抛 `BusinessException`，不要返回魔法值。
- 查询条件优先 `LambdaQueryWrapper`，保持类型安全。
- 状态变更逻辑关注幂等与并发风险。
- Service 层不泄露控制层概念（如 `HttpServletRequest`）。

### 5.7 异常与日志
- 统一复用 `GlobalExceptionHandler`。
- 不吞异常；可预期业务错误抛 `BusinessException`。
- 日志级别建议：业务可恢复问题用 `warn`，系统异常用 `error`。
- 日志内容优先经过 `LogMaskUtil` 脱敏。

### 5.8 注释
- 注释解释“为什么”，不机械复述“做了什么”。
- 对外接口、复杂分支、边界条件保留简明 Javadoc。
- 删除无意义或过期注释。

## 6. 配置与安全约束
- 配置入口：`src/main/resources/application.yml`。
- 默认依赖本地 MySQL/Redis/MinIO，禁止提交个人环境硬编码。
- 严禁提交密钥、令牌、真实账号密码。
- 新增配置项时，同步更新 `README.md` 最小运行说明。

## 7. Agent 执行建议
- 修改前先读相邻层代码（controller/service/mapper/DTO/VO）。
- 优先最小改动，只改与需求直接相关的文件。
- 每次改动后至少运行相关测试；若无法运行需明确原因和替代验证。
- 新增接口时不要把“补测试”留到后续，至少提供最小可用测试。

## 8. Cursor / Copilot 规则同步状态
已检查：
- `.cursor/rules/`
- `.cursorrules`
- `.github/copilot-instructions.md`
当前仓库未发现以上规则文件。
因此本文即为默认 Agent 执行规范；若后续新增规则文件，以“更具体规则优先”。

## 9. 提交前最小检查清单
- 编译通过：`mvn clean compile`
- 相关测试通过：`mvn -Dtest=类名或类名#方法名 test`
- 无无用 import、无调试代码、无明文敏感信息
- 新增/变更行为有对应测试或给出明确说明
- 文档（README/注释）与实现保持一致
