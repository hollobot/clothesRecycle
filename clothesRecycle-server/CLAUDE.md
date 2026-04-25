# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目简介

校园旧衣回收与流转平台后端服务。Spring Boot 3.2.5 + Java 17，提供用户端（client）和管理端（admin）两套 REST API。

## 常用命令

```bash
# 启动（开发环境，端口 8080）
mvn spring-boot:run

# 编译
mvn clean compile

# 打包
mvn clean package -DskipTests
```

## 架构概览

### 分层结构

```
controller/
  admin/    # 管理端接口（用户管理、物品审核、校区/回收点管理、礼物管理）
  client/   # 用户端接口（认证、物品、订单、消息、积分、排行榜、签到等）
service/    # 业务逻辑（接口 + impl 实现）
mapper/     # MyBatis-Plus Mapper（直接继承 BaseMapper，复杂 SQL 在 resources/mapper/*.xml）
model/
  po/       # 数据库实体，全部继承 BaseEntity（id/createTime/updateTime/deleted）
  dto/      # 请求入参
  vo/       # 响应出参
config/     # 配置类（CORS、MinIO、Redis、Sa-Token、MyBatis-Plus）
exception/  # BusinessException + GlobalExceptionHandler
job/        # 定时任务（OrderTimeoutJob：超时订单自动关闭）
handler/
  aspect/   # LogAspect（接口日志 AOP）
  filter/   # CorsFilter
common/
  result/   # 统一响应 Result<T> + ResultCode
  constant/ # SystemConstant
```

### 关键设计约定

- **统一响应**：所有接口返回 `Result<T>`，用 `Result.ok(data)` / `Result.fail(msg)`
- **认证**：Sa-Token，Token 放请求头 `Authorization`，用户和管理员分两个登录体系
- **逻辑删除**：MyBatis-Plus 全局配置，`deleted` 字段（0=正常，1=删除），勿手动过滤
- **自动填充**：`createTime` / `updateTime` 由 `AutoFillMetaObjectHandler` 自动写入
- **文件存储**：MinIO，通过 `FileClientController` / `FileAdminController` 上传，返回文件 URL

### 外部依赖（本地开发需自行启动）

| 服务 | 地址 | 说明 |
|------|------|------|
| MySQL 8 | `localhost:3306/clothes_recycle` | 用户名/密码：root/root |
| Redis | `localhost:6379` | 密码：123456，Sa-Token Session 存于此 |
| MinIO | `192.168.30.129:9000` | Bucket：clothes-recycle |

### 环境配置

- `application.yml` 只做 `spring.profiles.active=dev` 路由
- 开发配置在 `application-dev.yml`，生产配置在 `application-prod.yml`
- 切换环境：修改 `application.yml` 中的 `active` 值，或启动时传 `--spring.profiles.active=prod`
