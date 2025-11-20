# 项目解析：校园外卖管理系统

## 1. 项目简介

本项目是一个基于Java和Spring Boot的校园外卖管理系统后端，旨在提供一个功能完善的平台，用于管理用户、商家、菜品、订单和配送等核心外卖业务。项目采用经典的分层架构，并直接使用JDBC和原生SQL与MySQL兼容的数据库（如GaussDB for MySQL）进行交互，以满足特定的开发要求。

## 2. 技术栈与架构

- **核心框架**: Spring Boot 2.7.17
- **语言**: Java 17
- **数据库**: MySQL (或兼容的数据库如GaussDB for MySQL)
- **构建工具**: Apache Maven
- **架构模式**:
  - **分层架构**: 项目遵循经典的三层架构模式，将业务逻辑清晰地划分为不同的层次：
    - **Controller层**: 负责接收和处理HTTP请求，作为API的入口。
    - **Service层**: 包含核心业务逻辑，协调DAO层完成具体的数据操作。
    - **DAO层 (Data Access Object)**: 负责与数据库进行直接交互，执行SQL查询。
  - **RESTful API**: 通过HTTP协议提供RESTful风格的接口，便于前后端分离开发。

## 3. 项目结构解析

项目遵循标准的Maven目录结构，核心代码位于`src/main/java/com/campusdelivery`下：

- **`config`**:
  - `DataSourceConfig.java`: 配置数据源（DataSource），利用Spring Boot的自动配置机制，从`application.properties`文件中读取数据库连接信息（URL、用户名、密码等），并创建数据源Bean。

- **`controller`**:
  - 包含所有RESTful API的控制器，每个控制器对应一个核心实体（如`UserController`, `OrderController`等）。
  - 使用`@RestController`注解，并通过`@RequestMapping`定义URL路径。
  - 接收HTTP请求（如`@GetMapping`, `@PostMapping`），将请求体（`@RequestBody`）或路径参数（`@PathVariable`）传递给Service层进行处理，并返回`ResponseEntity`作为响应。

- **`dao`**:
  - 数据访问对象层，是项目与数据库交互的唯一入口。
  - 每个DAO（如`UserDao`, `OrderDao`）负责一个实体的数据持久化操作（增、删、改、查）。
  - **核心特点**: 所有数据库操作均通过`javax.sql.DataSource`获取数据库连接（`Connection`），并使用`PreparedStatement`执行原生SQL语句，以防止SQL注入。

- **`entity`**:
  - 包含所有数据实体的Java对象（POJO），每个实体类（如`User`, `Order`）的属性都与数据库表的字段一一对应。
  - 这些对象在各层之间传递数据。

- **`service`**:
  - 业务逻辑层，封装了项目的核心业务规则。
  - Service类（如`UserService`, `OrderService`）通过`@Autowired`注入对应的DAO，并调用DAO层的方法来完成对数据的操作。
  - 它隐藏了数据访问的复杂性，为Controller层提供清晰、简单的业务接口。

- **`resources`**:
  - `application.properties`: 存放应用的配置信息，最重要的是数据库连接参数。
  - `schema.sql`: 包含了所有数据表的创建语句（DDL）、触发器和存储过程的定义，是数据库的蓝图。

## 4. 数据库设计

数据库`campus_delivery`包含以下核心表：

- `users`: 存储用户信息。
- `addresses`: 存储用户的收货地址。
- `merchants`: 存储商家信息。
- `dishes`: 存储商家提供的菜品。
- `orders`: 存储订单信息，关联用户、商家和地址。
- `order_details`: 存储订单的具体菜品项。
- `riders`: 存储配送员信息。
- `delivery_records`: 记录订单的配送状态和配送员。
- `reviews`: 存储用户对订单的评价。

### 触发器与存储过程

- **触发器 (`update_merchant_rating_*`)**:
  - 在`reviews`表上定义了`AFTER INSERT`和`AFTER UPDATE`触发器。
  - 当用户提交或更新评价时，触发器会自动重新计算对应商家的平均评分，并更新`merchants`表中的`rating`字段。这保证了商家评分的实时性和准确性。

- **存储过程 (`get_user_orders_by_date_range`)**:
  - 接受用户ID和日期范围作为参数。
  - 查询指定用户在特定时间段内的所有订单及其关联的商家名称和配送地址。
  - 存储过程将复杂的查询逻辑封装在数据库层面，可以提高查询效率并简化应用端的代码。

## 5. API接口概览

项目提供了一套完整的RESTful API来管理外卖系统的各个方面：

- `POST /api/users`: 添加新用户
- `GET /api/users/{id}`: 根据ID获取用户信息
- `PUT /api/users`: 更新用户信息
- `DELETE /api/users/{id}`: 删除用户
- `POST /api/orders`: 创建新订单
- `GET /api/orders/{id}`: 根据ID获取订单信息
- `GET /api/orders/user/{userId}`: 获取特定用户的所有订单
- `PUT /api/orders/{orderId}/status/{status}`: 更新订单状态
- `POST /api/merchants`: 添加新商家
- `GET /api/merchants`: 获取所有商家列表
... 以及其他针对`addresses`, `dishes`, `riders`, `reviews`等实体的CRUD操作接口。

## 6. 数据流示例（创建订单）

1.  **客户端请求**: 前端应用发送一个`POST`请求到`/api/orders`，请求体中包含订单信息（如用户ID、商家ID、菜品列表等）。
2.  **Controller层**: `OrderController`的`createOrder`方法接收到请求。
3.  **Service层**: Controller调用`OrderService`的`createOrder`方法，并传递订单数据。
4.  **DAO层**: `OrderService`调用`OrderDao`的`createOrder`方法。
5.  **数据库交互**: `OrderDao`获取数据库连接，执行`INSERT INTO orders ...`语句，将订单数据存入数据库，并返回生成的订单ID。
6.  **返回响应**: 订单ID逐层返回，最终`OrderController`向客户端返回一个包含成功信息和订单ID的`ResponseEntity`。

## 7. 如何运行项目

1.  **数据库设置**:
    - 确保已安装MySQL或兼容的数据库。
    - 创建一个名为`campus_delivery`的数据库。
    - 在该数据库中执行`schema.sql`文件中的所有SQL语句，以创建表、触发器和存储过程。
2.  **配置数据库连接**:
    - 打开`src/main/resources/application.properties`文件。
    - 修改`spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password`以匹配您的数据库配置。
3.  **构建项目**:
    - 在项目根目录下打开终端，运行`mvn clean install`来编译代码并下载依赖。
4.  **运行应用**:
    - 运行命令`mvn spring-boot:run`。
    - 当控制台输出`Campus Delivery System is running!`时，表示后端服务已成功启动。
