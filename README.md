# 餐厅点餐系统 (Restaurant Ordering System)

一个基于Spring Boot的多模块餐厅点餐系统，支持Web管理端和小程序端。

## 项目结构

```
restaurant-ordering-system/
├── restaurant-common/          # 公共模块 - 通用工具类、配置、异常处理
├── restaurant-auth/           # 认证模块 - 用户认证、JWT处理
├── restaurant-menu/           # 菜单模块 - 菜品管理、分类管理
├── restaurant-order/          # 订单模块 - 订单处理、状态管理
├── restaurant-payment/        # 支付模块 - 支付处理、退款
├── restaurant-notification/   # 通知模块 - WebSocket实时通知
├── restaurant-admin/          # 管理端模块 - Web管理界面
└── restaurant-api/           # API网关 - 统一入口、路由分发
```

## 技术栈

- **框架**: Spring Boot 2.7.14
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **ORM**: MyBatis-Plus 3.5.3.2
- **认证**: JWT
- **构建工具**: Maven
- **Java版本**: 8

## 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE restaurant_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改配置文件 `restaurant-api/src/main/resources/application-dev.yml` 中的数据库连接信息

### 启动应用

1. 编译项目：
```bash
mvn clean compile
```

2. 启动应用：
```bash
cd restaurant-api
mvn spring-boot:run
```

3. 访问健康检查接口：
```
GET http://localhost:8080/api/health
```

## 模块说明

### restaurant-common
- 统一响应结果封装 (Result)
- 全局异常处理 (GlobalExceptionHandler)
- 错误码定义 (ErrorCode)
- Redis配置
- 应用配置属性

### restaurant-api
- 应用启动类
- 健康检查接口
- 统一配置文件
- 日志配置

## 默认用户

系统初始化时会创建以下默认用户：

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin  | 123456 | ADMIN | 系统管理员 |
| staff  | 123456 | STAFF | 服务员 |
| chef   | 123456 | CHEF  | 厨师 |

## API文档

启动应用后，可以通过以下接口进行测试：

### 系统接口
- 健康检查: `GET /api/health`
- 详细健康检查: `GET /api/health/detail`

### 系统用户管理接口 (员工管理)
- 获取所有系统用户: `GET /api/staff-users`
- 分页查询系统用户: `GET /api/staff-users/page?current=1&size=10`
- 根据ID获取系统用户: `GET /api/staff-users/{id}`
- 根据用户名获取系统用户: `GET /api/staff-users/username/{username}`
- 创建系统用户: `POST /api/staff-users`
- 更新系统用户: `PUT /api/staff-users/{id}`
- 删除系统用户: `DELETE /api/staff-users/{id}` (逻辑删除)

### 顾客管理接口
- 获取所有顾客: `GET /api/customers`
- 分页查询顾客: `GET /api/customers/page?current=1&size=10`
- 根据ID获取顾客: `GET /api/customers/{id}`
- 根据手机号获取顾客: `GET /api/customers/phone/{phone}`
- 根据微信OpenID获取顾客: `GET /api/customers/wechat/{openid}`
- 创建顾客: `POST /api/customers`
- 创建或获取顾客: `POST /api/customers/create-or-get?phone={phone}&name={name}`
- 更新顾客: `PUT /api/customers/{id}`
- 删除顾客: `DELETE /api/customers/{id}` (逻辑删除)

## 开发计划

- [x] 项目基础设施搭建
- [ ] 用户认证模块开发
- [ ] 菜单管理模块开发
- [ ] 订单处理模块开发
- [ ] 支付集成模块开发
- [ ] 实时通知模块开发
- [ ] Web管理界面开发
- [ ] 小程序API开发

## 贡献指南

1. Fork 项目
2. 创建特性分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 许可证

MIT License