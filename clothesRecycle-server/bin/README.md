# clothesRecycle-server

校园二手衣物回收系统后端（Spring Boot 3 + MyBatis-Plus + Sa-Token + Redis + MinIO）。

## 运行环境

- JDK 17
- Maven 3.9+
- MySQL 8
- Redis 6+
- MinIO（开发可选）

## 启动步骤

1. 创建数据库：`clothes_recycle`
2. 执行建表脚本：`src/main/resources/sql/schema.sql`
3. 执行初始化脚本：`src/main/resources/sql/seed.sql`
4. 修改配置：`src/main/resources/application.yml`
5. 启动项目：

```bash
mvn spring-boot:run
```

## 说明

- 接口前缀：
  - 用户端：`/api/user/**`
  - 管理端：`/api/admin/**`
  - 公开端：`/api/public/**`
- 开发阶段短信验证码使用模拟发送器（日志输出）。
- 管理端默认初始化账号见 `seed.sql`。
