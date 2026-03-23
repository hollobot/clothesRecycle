# AGENTS 指南（clothesRecycle-server）

本文面向在本仓库执行任务的 Agent（代码生成/修改/测试代理）。
目标：统一构建/测试命令、代码风格与工程约束，降低沟通成本和返工概率。

## 1. 仓库概览

- 技术栈：Spring Boot 3.2.5 + MyBatis-Plus + Sa-Token + Redis + MinIO。
- 构建工具：Maven（仓库无 `mvnw`，默认使用系统 `mvn`）。
- Java 版本：17（`pom.xml` 中 `java.version=17`）。
- 主包名：`com.example.project`。
- 统一响应：`Result<T>` / `ResultCode`。
- 统一业务异常：`BusinessException`，由 `GlobalExceptionHandler` 转换响应。
- 推荐工作方式：小步修改、边改边测、保持分层边界。

## 2. 目录与分层约定

关键目录（以 `src/main/java/com/example/project` 为根）：

- `controller/admin`：管理端接口。
- `controller/client`：用户端/公开端接口。
- `service` / `service/impl`：业务接口与实现。
- `mapper`：MyBatis-Plus 数据访问层。
- `model/dto`：入参模型（校验注解放这里）。
- `model/vo`：出参模型。
- `model/po`：持久化对象（与表结构对应）。
- `exception`：业务异常与全局异常处理。
- `config`：框架配置（Sa-Token、MyBatis、Redis 等）。
- `utils`：无状态工具（如日志脱敏工具）。

硬性要求：

- `controller` 仅做请求编排、参数校验、调用 service、返回 `Result`。
- 禁止在 `controller` 中写复杂业务，禁止直接调用 `mapper`。
- `service` 不暴露 Web 层概念（如 `HttpServletRequest`）。

## 3. Build / Test / Lint 命令

以下命令默认在仓库根目录执行。

### 3.1 构建与运行

- 启动服务：`mvn spring-boot:run`
- 清理并编译：`mvn clean compile`
- 打包（跳过测试）：`mvn clean package -DskipTests`
- 全量校验（推荐提交前执行）：`mvn clean verify`

### 3.2 测试（重点：单测执行）

- 运行全部测试：`mvn test`
- 运行单个测试类：`mvn -Dtest=GlobalExceptionHandlerTest test`
- 运行单个测试方法：`mvn -Dtest=GlobalExceptionHandlerTest#should_return_400_when_validation_failed test`
- 运行同类多个方法（通配）：`mvn -Dtest=ApiPrefixAndRoleTest#should_reject_* test`
- 运行多个测试类（逗号分隔）：`mvn -Dtest=GlobalExceptionHandlerTest,ApiPrefixAndRoleTest test`

补充说明：

- 当前测试主要位于：`src/test/java/com/example/project/exception`、`src/test/java/com/example/project/security`。
- 安全/接口边界测试大量使用 `@WebMvcTest + MockMvc`。
- 断言优先校验统一响应体（`code`/`msg`/`data`），不要只看 HTTP 状态码。

### 3.3 Lint / 静态检查现状

- 当前 `pom.xml` 未配置 Checkstyle / SpotBugs / PMD / Spotless。
- 现阶段质量闸门以 `mvn clean verify` + 代码评审为主。
- 若新增 lint 插件，请在变更说明中写清：规则来源、影响范围、迁移策略。

## 4. 代码风格规范（必须遵守）

### 4.1 格式与排版

- 4 空格缩进，不使用 Tab。
- 一行一个语句，避免过长方法链。
- 保持与现有文件一致的空行和括号风格。
- 非必要不做大规模“纯格式化”改动。

### 4.2 Imports 规范

- 禁止通配符导入（`import xxx.*`）。
- 按分组排序：
  1) Java 标准库
  2) 第三方库（Spring/MyBatis/Sa-Token/Jakarta 等）
  3) 项目内包（`com.example.project...`）
  4) 静态导入（置底）
- 删除未使用导入。

### 4.3 类型、模型与数据边界

- 严格区分 DTO / VO / PO，不混用。
- DTO 字段使用 `jakarta.validation` 注解，错误提示使用中文。
- PO 尽量与表结构一致，公共字段复用 `BaseEntity`。
- 对外返回统一使用 `Result<T>`，不要返回裸对象或裸 `Map`。
- 优先明确类型，避免 `Object`、原始集合和隐式转换。

### 4.4 命名规范

- 类名：大驼峰 + 职责后缀（`*Controller`/`*Service`/`*ServiceImpl`/`*Mapper`/`*Dto`/`*Vo`）。
- 方法名：小驼峰，动词开头，语义完整。
- 常量名：全大写下划线（如 `REGISTER_SMS_KEY_PREFIX`）。
- 包名：全小写，按业务语义分层。
- 测试方法命名：`should_xxx_when_yyy`。

### 4.5 Controller 约束

- `@RequestBody` 参数默认加 `@Valid`（可校验时）。
- 分页/筛选参数通过 `@RequestParam` 明确声明。
- 路由前缀保持一致：
  - ` /api/public/**`
  - ` /api/user/**`
  - ` /api/admin/**`

### 4.6 Service / Mapper 约束

- 业务校验失败统一抛 `BusinessException`，不要返回魔法值表示失败。
- 查询条件优先使用 `LambdaQueryWrapper` 保持类型安全。
- 更新/状态变更逻辑需要考虑幂等性与并发场景。
- 数据访问留在 mapper，service 负责业务编排与规则。

### 4.7 异常处理与日志

- 统一使用 `GlobalExceptionHandler` 输出标准响应。
- 禁止吞异常；可预期业务错误抛 `BusinessException`。
- 日志级别建议：业务可恢复问题 `warn`，系统异常 `error`。
- 日志内容优先通过 `LogMaskUtil` 做脱敏。

### 4.8 注释与文档

- 注释解释“为什么”，避免复述代码表面行为。
- 对外接口、复杂分支、边界条件建议保留简明 Javadoc。
- 删除过期注释，避免误导。

## 5. 测试策略

- 测试框架：JUnit 5 + Spring Test + MockMvc + AssertJ。
- 控制层测试优先 `@WebMvcTest`，必要时用 `@Import` 补充配置。
- 新增接口至少覆盖：
  - 1 条成功路径
  - 1 条参数校验失败路径
  - 1 条鉴权/权限边界路径（如适用）

## 6. 配置与安全约束

- 主配置文件：`src/main/resources/application.yml`。
- 本地依赖通常包括 MySQL / Redis / MinIO；禁止提交个人环境硬编码。
- 严禁提交密钥、令牌、真实账号密码等敏感信息。
- 新增配置项时，同步更新 `README.md` 的最小运行说明。

## 7. Cursor / Copilot 规则同步

已检查以下路径：

- `.cursor/rules/`
- `.cursorrules`
- `.github/copilot-instructions.md`

当前仓库未发现上述规则文件。
因此本文即为默认 Agent 执行规范；若后续新增规则文件，以更具体规则优先。

## 8. Agent 执行建议

- 先读相邻层实现（controller/service/mapper/DTO/VO）再下手改动。
- 优先最小改动原则，只修改与需求直接相关的文件。
- 每次改动后至少运行相关测试；无法运行时说明原因与替代验证。
- 不要顺手重构无关模块；如确需重构，分开提交并说明收益。

## 9. 提交前最小检查清单

- 能编译：`mvn clean compile`
- 相关测试通过：`mvn -Dtest=类名或类名#方法名 test`
- 无无用 import、无调试输出、无明文敏感信息
- 新增/变更行为有测试覆盖，或明确说明未覆盖原因
- 文档与实现一致（README、注释、配置说明）
