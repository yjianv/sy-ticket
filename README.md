# 研发工单系统 (SY-Ticket)

一个基于Spring Boot 3的现代化工单管理系统，专为研发团队内部使用而设计。

## 功能特性

### 🎯 核心功能
- **工单管理**: 创建、编辑、查看、分配工单
- **工作空间**: 支持测试环境和生产环境工作空间切换
- **富文本编辑**: 集成Editor.js，支持图片上传、拖拽等
- **文件管理**: 基于MinIO的文件上传和管理
- **工单流转**: 工单状态流转、移交、评论功能
- **企业微信推送**: 工单创建和解决时自动推送通知

### 🔐 权限管理
- **Spring Security**: 用户认证和授权
- **统一权限**: 所有用户拥有相同的操作权限
- **会话管理**: 安全的用户会话控制

### 🎨 用户界面
- **响应式设计**: 基于Tailwind CSS的现代UI
- **图标系统**: FontAwesome图标库
- **交互体验**: jQuery增强的用户交互

### 📊 数据管理
- **MySQL数据库**: 可靠的关系型数据存储
- **MyBatis**: 灵活的ORM映射框架
- **分页查询**: MyBatis PageHelper分页支持
- **数据库版本控制**: Flyway数据库迁移管理

## 技术栈

### 后端技术
- **Java 17**: 现代Java开发
- **Spring Boot 3.2.0**: 企业级应用框架
- **Spring Security**: 安全框架
- **MyBatis 3.0.3**: 持久层框架
- **PageHelper 2.1.0**: 分页插件
- **MySQL 8.0**: 关系型数据库
- **Flyway**: 数据库版本管理
- **MinIO**: 对象存储服务
- **FastJSON2**: JSON处理库

### 前端技术
- **Thymeleaf**: 服务端模板引擎
- **Tailwind CSS**: 实用优先的CSS框架
- **FontAwesome 6.4.0**: 图标库
- **jQuery 3.7.1**: JavaScript库
- **Editor.js**: 富文本编辑器

## 快速开始

### 环境要求
- Java 17+
- MySQL 8.0+
- MinIO服务
- Maven 3.6+

### 1. 克隆项目
```bash
git clone <repository-url>
cd sy-ticket
```

### 2. 数据库配置
创建MySQL数据库：
```sql
CREATE DATABASE sy_ticket CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 配置文件
修改 `src/main/resources/application.yml` 中的配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/sy_ticket?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root
    password: your_password

# MinIO配置
minio:
  endpoint: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin
  bucket-name: sy-ticket

# 企业微信配置
wechat:
  webhook-url: https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=your_webhook_key
```

### 4. 启动MinIO服务
```bash
# Docker方式启动MinIO
docker run -p 9000:9000 -p 9001:9001 \
  --name minio \
  -e "MINIO_ACCESS_KEY=minioadmin" \
  -e "MINIO_SECRET_KEY=minioadmin" \
  minio/minio server /data --console-address ":9001"
```

### 5. 运行应用
```bash
mvn spring-boot:run
```

### 6. 访问系统
- 应用地址: http://localhost:8080
- 默认账户: admin / admin123
- MinIO控制台: http://localhost:9001

## 项目结构

```
sy-ticket/
├── src/main/java/com/syticket/
│   ├── config/          # 配置类
│   ├── controller/      # 控制器层
│   ├── entity/          # 实体类
│   ├── mapper/          # 数据访问层
│   ├── security/        # 安全配置
│   ├── service/         # 业务逻辑层
│   ├── util/           # 工具类
│   └── TicketApplication.java
├── src/main/resources/
│   ├── db/migration/    # Flyway数据库迁移脚本
│   ├── mapper/          # MyBatis XML映射文件
│   ├── templates/       # Thymeleaf模板
│   └── application.yml  # 应用配置
└── pom.xml             # Maven依赖配置
```

## 数据库表结构

### 主要表
- `users`: 用户表
- `workspaces`: 工作空间表
- `tickets`: 工单表
- `ticket_comments`: 工单评论表
- `ticket_flows`: 工单流转记录表
- `files`: 文件表

### 初始数据
系统会自动创建：
- 默认工作空间：测试环境(TEST)、生产环境(PROD)
- 默认管理员：admin / admin123

## API接口

### 文件管理API
- `POST /api/files/upload` - 上传文件
- `GET /api/files/{id}/download` - 下载文件
- `GET /api/files/{id}` - 获取文件信息
- `DELETE /api/files/{id}` - 删除文件
- `POST /api/files/upload/image` - Editor.js图片上传

### 页面路由
- `/` - 仪表盘
- `/login` - 登录页面
- `/tickets` - 工单列表
- `/tickets/create` - 创建工单
- `/tickets/{id}` - 工单详情
- `/tickets/{id}/edit` - 编辑工单

## 企业微信推送

系统支持以下场景的企业微信推送：
- 工单创建通知
- 工单解决通知
- 工单指派通知
- 工单状态变更通知

### 配置WebHook
1. 在企业微信中创建群聊机器人
2. 获取WebHook地址
3. 在`application.yml`中配置WebHook URL

## 开发指南

### 添加新功能
1. 在`entity`包中定义实体类
2. 在`mapper`包中创建数据访问接口和XML映射
3. 在`service`包中实现业务逻辑
4. 在`controller`包中创建控制器
5. 在`templates`目录中创建页面模板

### 数据库迁移
在`src/main/resources/db/migration/`目录中创建新的迁移脚本：
```sql
-- V2__Add_new_feature.sql
CREATE TABLE new_table (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL
);
```

### 自定义样式
在模板文件的`custom-head`片段中添加CSS：
```html
<th:block layout:fragment="custom-head">
    <style>
        .custom-class {
            /* 自定义样式 */
        }
    </style>
</th:block>
```

## 部署说明

### 生产环境配置
1. 修改数据库连接配置
2. 配置生产环境的MinIO服务
3. 设置企业微信WebHook
4. 建议使用外部配置文件覆盖默认配置

### Docker部署
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/sy-ticket-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 常见问题

### Q: 无法上传文件
A: 检查MinIO服务是否正常运行，确认配置信息正确。

### Q: 企业微信推送不工作
A: 验证WebHook URL是否正确，检查网络连接。

### Q: 数据库连接失败
A: 确认MySQL服务运行正常，检查连接配置和权限。

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 许可证

MIT License

## 支持

如有问题或建议，请提交Issue或联系开发团队。
