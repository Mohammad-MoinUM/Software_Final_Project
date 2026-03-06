# Mini Marketplace - Spring Boot Application

A clean Spring Boot web application with layered architecture for a Mini Marketplace platform.

## 🎯 Project Overview

This is a skeleton Spring Boot project implementing best practices with a clean layered architecture pattern.

## 🛠️ Technology Stack

- **Java 17**
- **Spring Boot 3.2.3**
- **Maven** - Build & Dependency Management
- **Thymeleaf** - Template Engine
- **Spring Web** - REST & MVC
- **Spring Data JPA** - Data Access Layer
- **Spring Security** - Authentication & Authorization
- **PostgreSQL** - Database
- **Lombok** - Boilerplate Code Reduction
- **Spring Validation** - Input Validation

## 📁 Project Structure

```
mini-marketplace/
├── src/
│   ├── main/
│   │   ├── java/com/marketplace/
│   │   │   ├── MiniMarketplaceApplication.java    # Main Application Entry Point
│   │   │   ├── controller/                        # Web Controllers
│   │   │   │   └── HomeController.java
│   │   │   ├── service/                           # Business Logic Layer
│   │   │   ├── repository/                        # Data Access Layer (JPA Repositories)
│   │   │   ├── entity/                            # JPA Entities
│   │   │   ├── dto/                               # Data Transfer Objects
│   │   │   ├── config/                            # Application Configuration
│   │   │   │   └── WebConfig.java
│   │   │   └── security/                          # Security Configuration
│   │   │       └── SecurityConfig.java
│   │   └── resources/
│   │       ├── application.yml                    # Application Configuration
│   │       └── templates/                         # Thymeleaf Templates
│   │           ├── home.html
│   │           └── login.html
│   └── test/
│       └── java/com/marketplace/
│           └── MiniMarketplaceApplicationTests.java
├── pom.xml                                        # Maven Dependencies
└── README.md
```

## 🏗️ Architecture

The application follows a **layered architecture** pattern:

1. **Controller Layer** - Handles HTTP requests and responses
2. **Service Layer** - Contains business logic
3. **Repository Layer** - Data access using Spring Data JPA
4. **Entity Layer** - JPA entities representing database tables
5. **DTO Layer** - Data transfer objects for API communication
6. **Config Layer** - Application configurations
7. **Security Layer** - Security and authentication setup

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Database Setup

1. Install PostgreSQL
2. Create a database:
```sql
CREATE DATABASE marketplace_db;
```

3. Update credentials in `src/main/resources/application.yml` if needed:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/marketplace_db
    username: postgres
    password: postgres
```

### Running the Application

1. Clone the repository
2. Navigate to project directory
3. Run using Maven:

```bash
mvn spring-boot:run
```

Or build and run the JAR:

```bash
mvn clean package
java -jar target/mini-marketplace-1.0.0.jar
```

4. Access the application:
   - Home Page: http://localhost:8080
   - Login Page: http://localhost:8080/login

## 🔒 Security Configuration

Spring Security is configured with:
- Public access to home page and static resources
- Form-based authentication for protected endpoints
- BCrypt password encoding
- Default login/logout endpoints

## 📝 Current Implementation

### HomeController
- **GET /** or **/home** - Returns the home page with welcome message
- Demonstrates basic Thymeleaf integration

### Security
- Public endpoints: `/`, `/home`, `/css/**`, `/js/**`, `/images/**`
- All other endpoints require authentication
- Custom login page at `/login`

## 🔧 Configuration

### Application Properties (application.yml)

Key configurations:
- Server runs on port **8080**
- JPA auto-DDL set to **update** (creates/updates tables automatically)
- SQL logging enabled for development
- Thymeleaf caching disabled for development

## 🧪 Testing

Run tests using Maven:

```bash
mvn test
```

## 📦 Building for Production

```bash
mvn clean package -DskipTests
```

The executable JAR will be in `target/mini-marketplace-1.0.0.jar`

## 🎯 Next Steps

This is a skeleton project ready for:
- Implementing business entities (User, Product, Order, etc.)
- Creating service layer implementation
- Adding repository interfaces
- Building REST APIs or additional MVC controllers
- Implementing authentication with UserDetailsService
- Adding business logic and features

## 📖 Additional Notes

- **Lombok**: Annotate entities with `@Data`, `@Entity`, etc.
- **Validation**: Use `@Valid` and constraint annotations in DTOs
- **JPA**: Extend `JpaRepository<Entity, ID>` for repositories
- **Services**: Use `@Service` annotation and inject repositories

## 👨‍💻 Development

### Hot Reload
Spring Boot DevTools is included for automatic restarts during development.

### IDE Setup
- Import as Maven project
- Enable annotation processing for Lombok
- Ensure Java 17 is configured

## 📄 License

This project is open source and available for educational purposes.

---

**Status**: ✅ Project Skeleton Ready - No business logic implemented yet