# 旅行规划后端系统

这是一个基于Spring Boot的旅行规划后端系统，提供用户注册、登录和JWT身份验证等功能。

## 快速开始

### 环境要求
- Java 17+
- Maven 3.6+
- PostgreSQL 12+

### 安装和运行

1. 克隆项目
```bash
git clone <项目仓库地址>
cd travel-planner
```

2. 配置数据库
确保PostgreSQL数据库已启动，并在`application.yml`中配置正确的数据库连接信息。

3. 构建项目
```bash
mvn clean package
```

4. 运行应用
```bash
java -jar target/travel-planner-1.0.0.jar
```

### 访问地址
应用启动后，可以通过以下地址访问：

**基础URL**: http://localhost:8080/api

### API端点示例

- 用户注册: POST http://localhost:8080/api/auth/register
- 用户登录: POST http://localhost:8080/api/auth/login
- 获取用户信息: GET http://localhost:8080/api/users/me (需要JWT认证)

## 项目结构

```
src/
├── main/
│   ├── java/com/travelplanner/         # 源代码目录
│   └── resources/                      # 资源文件目录
│       ├── application.yml             # 应用配置文件
│       └── schema.sql                  # 数据库表结构脚本
└── test/                               # 测试代码目录
```

## 技术栈

- Spring Boot 3.x
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (JSON Web Token)