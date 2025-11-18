# 旅行规划后端系统

这是一个基于Spring Boot的旅行规划后端系统，提供用户注册、登录和JWT身份验证等功能。

## 快速开始

### 环境要求
- Java 21+
- Maven 3.6+
- PostgreSQL 17+

### 安装和运行

1. 克隆项目
```bash
git clone https://github.com/Zinnober02/TravelPlanner
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

**基础URL**: http://localhost:8080

### API

#### 认证 API

- 用户注册: POST http://localhost:8080/auth/register
- 用户登录: POST http://localhost:8080/auth/login

#### 旅行计划 API

- 创建旅行计划: POST http://localhost:8080/api/travel-plans
- 根据语音输入生成旅行计划: POST http://localhost:8080/api/travel-plans/generate
- 获取用户的旅行计划列表: GET http://localhost:8080/api/travel-plans
- 根据ID获取旅行计划详情: GET http://localhost:8080/api/travel-plans/{id}
- 根据目的地搜索旅行计划: GET http://localhost:8080/api/travel-plans/search?destination={destination}
- 更新旅行计划: PUT http://localhost:8080/api/travel-plans
- 删除旅行计划: DELETE http://localhost:8080/api/travel-plans/{id}

#### 行程详情 API

- 创建行程详情: POST http://localhost:8080/api/plan-details
- 获取旅行计划的所有行程详情: GET http://localhost:8080/api/plan-details/plan/{planId}
- 获取指定天数的行程详情: GET http://localhost:8080/api/plan-details/plan/{planId}/day/{dayNumber}
- 更新行程详情: PUT http://localhost:8080/api/plan-details
- 删除行程详情: DELETE http://localhost:8080/api/plan-details/{id}

#### 费用记录 API

- 创建费用记录: POST http://localhost:8080/api/expenses
- 获取旅行计划的所有费用记录: GET http://localhost:8080/api/expenses/plan/{planId}
- 按类型获取费用记录: GET http://localhost:8080/api/expenses/plan/{planId}/type/{type}
- 获取旅行计划的总费用: GET http://localhost:8080/api/expenses/plan/{planId}/total
- 更新费用记录: PUT http://localhost:8080/api/expenses
- 删除费用记录: DELETE http://localhost:8080/api/expenses/{id}

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

- Spring Boot 3.5.7
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (JSON Web Token)
- Jakarta Validation (Bean Validation)
- OpenAI API Integration