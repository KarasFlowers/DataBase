# Campus Food Delivery System - Backend

This document provides an overview of the project structure, key components, and setup instructions.

## 1. Introduction

This project is the backend for a campus food delivery management system, developed as per the assignment requirements. It is built with Java and is designed to connect to a MySQL-compatible database (such as GaussDB for MySQL). The core of the project involves using raw SQL queries via JDBC for all database interactions.

## 2. Project Structure

The project follows a standard Maven directory layout to ensure consistency and compatibility with common Java development tools.

```
campus-delivery-system/
│
├── pom.xml                     # Maven project configuration (dependencies and build settings)
├── schema.sql                  # SQL script with CREATE TABLE statements for the database
│
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── campusdelivery/
    │   │           ├── config/
    │   │           │   └── DataSourceConfig.java  # Handles database connection logic
    │   │           │
    │   │           ├── dao/
    │   │           │   ├── UserDao.java          # Data Access Object for the User entity
    │   │           │   └── ... (Other DAOs for each entity)
    │   │           │
    │   │           ├── model/
    │   │           │   ├── User.java             # Model (POJO) for the User entity
    │   │           │   └── ... (Other Models for each entity)
    │   │           │
    │   │           └── CampusDeliverySystemApplication.java # Main Spring Boot application class
    │   │
    │   └── resources/
    │       └── application.properties  # For configuration files or other non-java resources
    │
    └── test/
        └── java/                     # Contains unit tests for the project
```

### Key Files and Directories Explained

*   **`schema.sql`**: The blueprint for your database. Run this script to create all the necessary tables.
*   **`pom.xml`**: The Maven Project Object Model file. It manages project dependencies (like the MySQL JDBC driver) and defines how to build the project.
*   **`src/main/java/com/src.main.campusdelivery/model`**: This package contains the Model classes, also known as Plain Old Java Objects (POJOs). Each class (e.g., `User.java`, `Order.java`) is a simple data structure that represents a row in a database table.
*   **`src/main/java/com/src.main.campusdelivery/dao`**: This package holds the Data Access Object (DAO) classes. Each DAO (e.g., `UserDao.java`) is responsible for all database operations (Create, Read, Update, Delete) for its corresponding model. **This is where the raw SQL queries are written and executed.**
*   **`src/main/java/com/campusdelivery/config`**: This package is for configuration-related classes. `DataSourceConfig.java` is a key utility here, providing a standardized way to get a connection to the database, configured via `src/main/resources/application.properties`.

## 3. How to Set Up and Run

### Step 1: Database Setup
1.  Make sure you have access to a running MySQL-compatible database server.
2.  Create a new database (e.g., named `campus_delivery`).
3.  Execute the queries in the `schema.sql` script against your new database to create the required tables.

### Step 2: Configure Database Connection
1.  Navigate to `src/main/resources/application.properties`.
2.  Update the `spring.datasource.url`, `spring.datasource.username`, and `spring.datasource.password` properties with your specific database host, username, and password.

### Step 3: Build the Project
1.  Ensure you have Apache Maven installed on your system.
2.  Open a terminal and navigate to the root directory of this project (`campus-delivery-system`).
3.  Run the command `mvn clean install`. This will compile the source code and download the dependencies defined in `pom.xml`.

## 4. Next Steps

The project skeleton is now complete and all DAO methods have been implemented. You can now build and run the application.
To run the application:
1.  Ensure the database is set up and configured as per Step 1 and 2.
2.  Build the project using `mvn clean install` (Step 3).
3.  Run the Spring Boot application: `mvn spring-boot:run`.
