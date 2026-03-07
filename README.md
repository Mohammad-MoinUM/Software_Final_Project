# Mini Marketplace

A Spring Boot web application for a mini marketplace platform.

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.3**
- **Maven** - Build tool
- **Thymeleaf** - Template engine
- **Spring Web** - REST API & MVC
- **Spring Data JPA** - Data persistence
- **Spring Security** - Authentication & Authorization
- **PostgreSQL** - Database
- **Lombok** - Boilerplate reduction
- **Spring Validation** - Input validation

## Project Structure

```
src/main/java/com/marketplace/
├── controller/      # HTTP request handlers
├── service/         # Business logic layer
├── repository/      # Data access layer
├── entity/          # JPA entities
├── dto/             # Data Transfer Objects
├── config/          # Configuration classes
└── security/        # Security configuration
```

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+

## Database Schema

### Entities

The application uses the following JPA entities with PostgreSQL:

#### User
- **Table**: `users`
- **Fields**: id, username, email, password, fullName, phoneNumber, enabled, createdAt, updatedAt
- **Relationships**: 
  - ManyToMany with Role
  - OneToMany with Product (as seller)
  - OneToMany with Order (as buyer)

#### Role
- **Table**: `roles`
- **Fields**: id, name (RoleType enum), description, createdAt, updatedAt
- **Enum Values**: ADMIN, SELLER, BUYER
- **Relationships**: ManyToMany with User

#### Product
- **Table**: `products`
- **Fields**: id, name, description, price, stockQuantity, category, imageUrl, available, createdAt, updatedAt
- **Relationships**: 
  - ManyToOne with User (seller)
  - ManyToMany with Order

#### Order
- **Table**: `orders`
- **Fields**: id, orderNumber, totalAmount, status (OrderStatus enum), shippingAddress, notes, createdAt, updatedAt
- **Enum Values**: PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED
- **Relationships**: 
  - ManyToOne with User (buyer)
  - ManyToMany with Product

### Entity Relationships Diagram

```
User (users)
├── ManyToMany ──> Role (roles) [user_roles]
├── OneToMany ──> Product (products)
└── OneToMany ──> Order (orders)

Order (orders)
└── ManyToMany ──> Product (products) [order_products]
```

### Repositories

All entities have corresponding Spring Data JPA repositories with custom query methods:
- **UserRepository**: Find by username/email, check existence, find enabled users
- **RoleRepository**: Find by role type, check existence
- **ProductRepository**: Find by seller, category, price range, availability, search by name
- **OrderRepository**: Find by order number, buyer, status, date range

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+

## Database Setup

1. Create a PostgreSQL database:
```sql
CREATE DATABASE mini_marketplace;
```

Alternatively, you can use the provided initialization script:
```bash
psql -U postgres -f database/init.sql
```

2. Update database credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mini_marketplace
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. The database tables will be automatically created by Hibernate on first run (JPA DDL set to `update`).

## Running the Application

1. Build the project:
```bash
mvn clean install
```

2. Run the application:
```bash
mvn spring-boot:run
```

3. Access the application at: `http://localhost:8080`

## Default Configuration

- **Server Port**: 8080
- **Context Path**: /
- **Database**: PostgreSQL (localhost:5432)
- **JPA DDL**: update (auto-create/update tables)

## Features

- Clean layered architecture
- Spring Security integration
- Thymeleaf templating
- PostgreSQL database
- Lombok annotations
- Bean validation

## Development

The application uses Spring Boot DevTools for hot-reload during development. Any changes to Java files will automatically restart the application.

---
Created with Spring Boot following best practices and clean architecture principles.