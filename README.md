# Mini Marketplace

A full-stack web application built with Spring Boot, Thymeleaf, PostgreSQL, and Docker, featuring role-based access control, RESTful APIs, and a complete CI/CD pipeline.

## Table of Contents

- [Project Overview](#project-overview)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Database Schema](#database-schema)
- [API Endpoints](#api-endpoints)
- [Getting Started](#getting-started)
- [Running with Docker](#running-with-docker)
- [Testing](#testing)
- [CI/CD Pipeline](#cicd-pipeline)
- [Deployment](#deployment)
- [Security](#security)
- [Team Members](#team-members)

## Project Overview

Mini Marketplace is a complete e-commerce platform that demonstrates professional software development practices. The system supports three user roles (Admin, Seller, and Buyer) with appropriate access controls and business logic.

**Key Features:**

### Core Functionality
- User registration and authentication with BCrypt password encryption
- Role-based authorization (ADMIN, SELLER, BUYER)
- Product management (CRUD operations) with image upload
- Order processing and tracking
- Advanced shopping cart with real-time calculations
- Wishlist functionality for saving favorite products
- Product reviews and ratings system with verified purchase badges
- Multiple payment methods (Card, bKash, Nagad, Rocket, Cash on Delivery)
- Email notifications for order confirmations and updates

### User Dashboards
- **Buyer Dashboard**: Order history, profile management, quick stats
- **Seller Dashboard**: Product management, sales analytics, inventory tracking
- **Admin Dashboard**: User management, product oversight, order management, platform statistics

### Technical Features
- RESTful API design with proper HTTP methods and status codes
- Global exception handling
- Comprehensive test coverage (Unit + Integration tests)
- Dockerized application with PostgreSQL
- Automated CI/CD pipeline with GitHub Actions
- Cloud deployment ready (Render)
- Responsive modern UI with gradient designs
- Async fetch API for seamless user experience
- Advanced search and filtering capabilities
- File upload service with validation (max 5MB, image types)

## 🛠 Technology Stack

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.2.3** - Application framework
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - Data persistence
- **Hibernate** - ORM framework
- **Maven** - Build tool

### Frontend
- **Thymeleaf** - Template engine
- **HTML/CSS/JavaScript** - UI components

### Database
- **PostgreSQL 15** - Relational database

### Testing
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework
- **Spring Boot Test** - Integration testing
- **MockMvc** - API testing

### DevOps
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration
- **GitHub Actions** - CI/CD pipeline
- **Render** - Cloud deployment platform

## 🏗 Architecture

### System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Client Layer                          │
│                   (Browser/Thymeleaf UI)                     │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTP/HTTPS
┌────────────────────────▼────────────────────────────────────┐
│                     Controller Layer                         │
│   ┌──────────────┐ ┌──────────────┐ ┌──────────────┐      │
│   │    User      │ │   Product    │ │    Order     │      │
│   │ Controller   │ │  Controller  │ │  Controller  │      │
│   └──────┬───────┘ └──────┬───────┘ └──────┬───────┘      │
└──────────┼────────────────┼────────────────┼──────────────┘
           │                │                │
┌──────────▼────────────────▼────────────────▼──────────────┐
│                      Service Layer                          │
│   ┌──────────────┐ ┌──────────────┐ ┌──────────────┐      │
│   │    User      │ │   Product    │ │    Order     │      │
│   │   Service    │ │   Service    │ │   Service    │      │
│   └──────┬───────┘ └──────┬───────┘ └──────┬───────┘      │
└──────────┼────────────────┼────────────────┼──────────────┘
           │                │                │
┌──────────▼────────────────▼────────────────▼──────────────┐
│                   Repository Layer (JPA)                    │
│   ┌──────────────┐ ┌──────────────┐ ┌──────────────┐      │
│   │    User      │ │   Product    │ │    Order     │      │
│   │  Repository  │ │  Repository  │ │  Repository  │      │
│   └──────┬───────┘ └──────┬───────┘ └──────┬───────┘      │
└──────────┼────────────────┼────────────────┼──────────────┘
           │                │                │
┌──────────▼────────────────▼────────────────▼──────────────┐
│                    PostgreSQL Database                      │
│              (users, roles, products, orders)               │
└─────────────────────────────────────────────────────────────┘
```

### Layered Architecture

1. **Controller Layer** - Handles HTTP requests, validates input, returns responses
2. **Service Layer** - Contains business logic, transaction management
3. **Repository Layer** - Data access operations using Spring Data JPA
4. **Entity Layer** - JPA entities representing database tables
5. **Security Layer** - Authentication, authorization, password encryption

## 🗄 Database Schema

### Entity Relationship Diagram

```
┌─────────────────┐           ┌─────────────────┐
│     USERS       │           │     ROLES       │
├─────────────────┤           ├─────────────────┤
│ id (PK)         │◄─────────►│ id (PK)         │
│ username (U)    │   M:M     │ name (ENUM)     │
│ email (U)       │user_roles │ description     │
│ password        │           │ created_at      │
│ full_name       │           │ updated_at      │
│ phone_number    │           └─────────────────┘
│ enabled         │
│ created_at      │
│ updated_at      │
└────────┬────────┘
         │ 1
         │
         │ M                  ┌─────────────────┐
         └───────────────────►│    PRODUCTS     │
           seller_id          ├─────────────────┤
                              │ id (PK)         │
         ┌────────────────────┤ name            │
         │                    │ description     │
         │ 1                  │ price           │
         │                    │ stock_quantity  │
         │                    │ category        │
┌────────▼────────┐           │ image_url       │
│     ORDERS      │◄─────────►│ available       │
├─────────────────┤   M:M     │ seller_id (FK)  │
│ id (PK)         │order_      │ created_at      │
│ order_number    │products   │ updated_at      │
│ buyer_id (FK)   │           └─────────────────┘
│ total_amount    │
│ status (ENUM)   │
│ created_at      │
│ updated_at      │
└─────────────────┘
```

### Tables

1. **users** - Stores user information
   - Primary Key: id
   - Unique: username, email
   - Relationships: M:M with roles, 1:M with products (as seller), 1:M with orders (as buyer)

2. **roles** - Defines user roles (ADMIN, SELLER, BUYER)
   - Primary Key: id
   - Unique: name
   - Relationships: M:M with users

3. **products** - Product catalog
   - Primary Key: id
   - Foreign Key: seller_id → users.id
   - Relationships: M:1 with users (seller), M:M with orders

4. **orders** - Purchase orders
   - Primary Key: id
   - Foreign Key: buyer_id → users.id
   - Relationships: M:1 with users (buyer), M:M with products

5. **user_roles** - Join table for users and roles

6. **order_products** - Join table for orders and products

## 🔌 API Endpoints

### User Management

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/users/register` | Register new user | Public |
| POST | `/api/users` | Create user (admin) | ADMIN |
| GET | `/api/users` | Get all users | ADMIN |
| GET | `/api/users/{id}` | Get user by ID | ADMIN, Owner |
| GET | `/api/users/username/{username}` | Get user by username | Authenticated |
| PUT | `/api/users/{id}` | Update user | ADMIN, Owner |
| DELETE | `/api/users/{id}` | Delete user | ADMIN |
| PATCH | `/api/users/{id}/toggle-status` | Toggle user status | ADMIN |
| GET | `/api/users/role/{role}` | Get users by role | ADMIN |

### Product Management

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/products` | Get all products | Public |
| GET | `/api/products/available` | Get available products | Public |
| GET | `/api/products/{id}` | Get product by ID | Public |
| GET | `/api/products/category/{category}` | Get products by category | Public |
| GET | `/api/products/seller/{sellerId}` | Get products by seller | Public |
| GET | `/api/products/search?name=` | Search products | Public |
| GET | `/api/products/price-range?min=&max=` | Filter by price | Public |
| POST | `/api/products` | Create product | SELLER, ADMIN |
| PUT | `/api/products/{id}` | Update product | Owner, ADMIN |
| DELETE | `/api/products/{id}` | Delete product | Owner, ADMIN |
| PATCH | `/api/products/{id}/stock` | Update stock | Owner, ADMIN |
| PATCH | `/api/products/{id}/toggle-availability` | Toggle availability | Owner, ADMIN |

### Order Management

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/orders` | Get all orders | ADMIN |
| GET | `/api/orders/{id}` | Get order by ID | ADMIN, Owner |
| GET | `/api/orders/buyer/{buyerId}` | Get orders by buyer | ADMIN, Owner |
| GET | `/api/orders/seller/{sellerId}` | Get orders by seller | ADMIN, Owner |
| GET | `/api/orders/status/{status}` | Get orders by status | ADMIN |
| POST | `/api/orders` | Create order | Authenticated |
| PATCH | `/api/orders/{id}/status` | Update order status | ADMIN, SELLER |
| PATCH | `/api/orders/{id}/cancel` | Cancel order | ADMIN, Owner |
| DELETE | `/api/orders/{id}` | Delete order | ADMIN |

### API Response Format

**Success Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

**Error Response:**
```json
{
  "timestamp": "2026-03-10T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/users/register",
  "validationErrors": {
    "email": "Email must be valid",
    "password": "Password must be at least 6 characters"
  }
}
```

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+ (or use Docker)
- Git

### Local Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/Mohammad-MoinUM/Software_Final_Project.git
   cd Software_Final_Project
   ```

2. **Configure Database**
   
   Create a PostgreSQL database:
   ```sql
   CREATE DATABASE mini_marketplace;
   ```

   Update `src/main/resources/application-dev.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/mini_marketplace
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - Web UI: http://localhost:8080
   - API Base URL: http://localhost:8080/api
   - Health Check: http://localhost:8080/actuator/health

## 🐳 Running with Docker

### Using Docker Compose (Recommended)

1. **Copy environment file**
   ```bash
   cp .env.example .env
   ```

2. **Build and run**
   ```bash
   docker compose up --build
   ```

3. **Stop the application**
   ```bash
   docker compose down
   ```

### Using Dockerfile Only

1. **Build the image**
   ```bash
   docker build -t mini-marketplace .
   ```

2. **Run the container**
   ```bash
   docker run -p 8080:8080 \
     -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/mini_marketplace \
     -e SPRING_DATASOURCE_USERNAME=postgres \
     -e SPRING_DATASOURCE_PASSWORD=postgres \
     mini-marketplace
   ```

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=UserServiceTest
```

### Test Coverage

The project includes:
- **15+ Unit Tests** - Service layer testing with Mockito
- **3+ Integration Tests** - Controller testing with MockMvc
- **Test Categories:**
  - UserServiceTest (15 tests)
  - ProductServiceTest (12 tests)
  - OrderServiceTest (11 tests)
  - UserControllerIntegrationTest (8 tests)
  - ProductControllerIntegrationTest (10 tests)
  - OrderControllerIntegrationTest (10 tests)

## 🔄 CI/CD Pipeline

### GitHub Actions Workflow

The project uses GitHub Actions for automated CI/CD with the following stages:

#### 1. Build and Test
- Checkout code
- Setup JDK 17
- Build with Maven
- Run all tests with PostgreSQL service
- Package application
- Upload artifact

#### 2. Docker Build
- Build Docker image
- Test Docker image

#### 3. Deploy
- Trigger deployment to Render

#### 4. Code Quality
- Run Maven verify

### Workflow Triggers

- **Push to `main`** - Full pipeline (build, test, deploy)
- **Push to `develop`** - Build and test only
- **Pull Request to `main`** - Build and test only

### Branch Protection

- **main** branch is protected
- Requires pull request with at least 1 approval
- No direct pushes allowed
- All checks must pass before merging

### Setting Up CI/CD

1. **Add GitHub Secrets** (Settings → Secrets):
   - `RENDER_DEPLOY_HOOK` - Render deployment webhook URL

2. **Configure Branch Protection** (Settings → Branches):
   - Protect `main` branch
   - Require pull request reviews
   - Require status checks to pass

## 🌐 Deployment

### Deploying to Render

1. **Create a Render account** at https://render.com

2. **Create PostgreSQL Database**
   - Go to Dashboard → New → PostgreSQL
   - Note the internal database URL

3. **Create Web Service**
   - Go to Dashboard → New → Web Service
   - Connect your GitHub repository
   - Configure:
     - **Build Command:** `mvn clean package -DskipTests`
     - **Start Command:** `java -jar target/mini-marketplace-1.0.0.jar`
     - **Environment Variables:**
       ```
       DATABASE_URL=<internal-database-url>
       DDL_AUTO=update
       SPRING_PROFILES_ACTIVE=prod
       ```

4. **Enable Auto-Deploy**
   - Enable auto-deploy from `main` branch
   - Copy the Deploy Hook URL
   - Add to GitHub Secrets as `RENDER_DEPLOY_HOOK`

### Environment Variables for Production

```bash
DATABASE_URL=jdbc:postgresql://<host>:<port>/<database>
DB_USERNAME=<username>
DB_PASSWORD=<password>
DDL_AUTO=validate
SPRING_PROFILES_ACTIVE=prod
LOG_LEVEL=INFO
SHOW_SQL=false
```

## 🔐 Security

### Authentication & Authorization

- **Password Encryption:** BCrypt with strength 10
- **Session Management:** Spring Security session
- **CSRF Protection:** Enabled for web forms, disabled for API endpoints
- **Method-Level Security:** `@PreAuthorize` annotations

### Roles & Permissions

| Role | Permissions |
|------|-------------|
| **ADMIN** | Full system access, user management, all CRUD operations |
| **SELLER** | Create/manage own products, view orders containing their products |
| **BUYER** | Create orders, view own orders, view products |

### API Security

- **HTTP Basic Auth:** Enabled for API testing
- **Form Login:** Available for web interface
- **Role-Based Access Control:** Enforced at controller and method level
- **Resource Ownership:** Users can only access their own resources

## 📚 Project Structure

```
Software_Final_Project/
├── .github/
│   └── workflows/
│       └── ci-cd.yml           # GitHub Actions workflow
├── database/
│   └── init.sql                # Database initialization script
├── src/
│   ├── main/
│   │   ├── java/com/marketplace/
│   │   │   ├── controller/     # REST controllers
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   ├── entity/         # JPA entities
│   │   │   ├── exception/      # Exception handling
│   │   │   ├── repository/     # Data repositories
│   │   │   ├── security/       # Security configuration
│   │   │   ├── service/        # Business logic
│   │   │   └── util/           # Utility classes
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── templates/      # Thymeleaf templates
│   └── test/
│       └── java/com/marketplace/
│           ├── controller/     # Integration tests
│           └── service/        # Unit tests
├── .dockerignore
├── .env.example
├── docker-compose.yml
├── Dockerfile
├── pom.xml
└── README.md
```


## 👥 Team Members

- **Member 1:** Nure Alam Siddiki Prince (prince2107110@stud.kuet.ac.bd)
- **Member 2:** Moinuddin Moin (moin2107111@stud.kuet.ac.bd)
## 📄 License

This project is developed as part of CSE 3220 Software Engineering Lab.

## 🤝 Contributing

1. Create a feature branch from `develop`
2. Make your changes
3. Write/update tests
4. Submit a pull request to `develop`
5. After review and approval, merge to `main` for deployment

## 📞 Support

For issues or questions, please open an issue in the GitHub repository.

---


**Deployment Status:** [![CI/CD Pipeline](https://github.com/Mohammad-MoinUM/Software_Final_Project/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/Mohammad-MoinUM/Software_Final_Project/actions/workflows/ci-cd.yml)

**Live Demo:** Add your Render URL here after deployment