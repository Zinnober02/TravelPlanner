# 旅行规划系统

这是一个基于Spring Boot的旅行规划后端系统，提供用户注册、登录和JWT身份验证等功能。

## Docker部署

### 环境要求
- Docker 20.10+ 
- 确保ubuntu麦克风可用，若不可用，可在主机编译运行。

### 拉取镜像
```bash
docker pull zinnober02/travel-planner:latest
```

### 运行容器
```bash
docker run -d -p 8080:8080 zinnober02/travel-planner:latest
```

### 访问地址
容器运行成功后，可以通过以下地址访问：

**基础URL**: http://localhost:8080

已经注册了用户名：zinnober，密码：123456，并有6个旅行计划。

也可注册新用户，首次登录后，会弹出弹窗要求填写阿里云百炼的API Key，也可以在首页右上角设置中修改API key。

语音输入功能：使用讯飞语音听写webapi，当然也可以手动输入文字作为识别结果（语音输入后，用户也可以编辑）。

当前已经实现了注册登录功能、旅行计划的创建、根据语音输入生成旅行计划、获取用户的旅行计划列表、根据ID获取旅行计划详情、更新旅行计划、删除旅行计划等功能。


## 快速开始

### 环境要求
- Java 21+
- Maven 3.6+
- PostgreSQL 17+

### 安装和运行

#### 分步执行

1. 克隆项目
```bash
git clone https://github.com/Zinnober02/TravelPlanner
cd TravelPlanner
```

2. 构建项目
```bash
mvn clean package
```

3. 运行应用
```bash
java -jar target/travel-planner-1.0.0.jar
```

#### 一键复制版本

**Bash版本**（适用于Linux/macOS）：
```bash
git clone https://github.com/Zinnober02/TravelPlanner && cd TravelPlanner && mvn clean package && java -jar target/travel-planner-1.0.0.jar
```

**PowerShell版本**（适用于Windows）：
```powershell
git clone https://github.com/Zinnober02/TravelPlanner; cd TravelPlanner; mvn clean package; java -jar target/travel-planner-1.0.0.jar
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
- 获取用户的旅行计划列表: GET http://localhost:8080/api/travel-plans
- 根据ID获取旅行计划详情: GET http://localhost:8080/api/travel-plans/{id}
- 根据目的地搜索旅行计划: GET http://localhost:8080/api/travel-plans/search?destination={destination}
- 更新旅行计划: PUT http://localhost:8080/api/travel-plans
- 删除旅行计划: DELETE http://localhost:8080/api/travel-plans/{id}
- 语音识别WebSocket: ws://localhost:8080/ws/speech?token={JWT_TOKEN}

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