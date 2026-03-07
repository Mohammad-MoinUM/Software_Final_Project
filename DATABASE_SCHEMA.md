# Database Entity Documentation

## Entity Overview

This document describes the database entities and their relationships in the Mini Marketplace application.

## Entities

### 1. BaseEntity (Abstract)
**Purpose**: Base class for all entities with common fields

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary key (auto-generated) |
| createdAt | LocalDateTime | Timestamp when record was created |
| updatedAt | LocalDateTime | Timestamp when record was last updated |

---

### 2. User
**Table**: `users`  
**Description**: Represents users in the marketplace (buyers, sellers, admins)

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | Primary Key | User ID |
| username | String(50) | NOT NULL, UNIQUE | Unique username |
| email | String(100) | NOT NULL, UNIQUE | User email |
| password | String(255) | NOT NULL | Encrypted password |
| fullName | String(100) | - | User's full name |
| phoneNumber | String(20) | - | Contact number |
| enabled | Boolean | NOT NULL, default=true | Account status |
| createdAt | LocalDateTime | NOT NULL | Creation timestamp |
| updatedAt | LocalDateTime | NOT NULL | Update timestamp |

**Relationships**:
- ManyToMany with `Role` (join table: `user_roles`)
- OneToMany with `Product` (as seller, field: `products`)
- OneToMany with `Order` (as buyer, field: `orders`)

**Validation**:
- Username: min 3, max 50 characters
- Email: valid email format
- Password: min 6 characters

---

### 3. Role
**Table**: `roles`  
**Description**: User roles for authorization

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | Primary Key | Role ID |
| name | RoleType (Enum) | NOT NULL, UNIQUE | Role name |
| description | String(255) | - | Role description |
| createdAt | LocalDateTime | NOT NULL | Creation timestamp |
| updatedAt | LocalDateTime | NOT NULL | Update timestamp |

**Enum Values**: `ADMIN`, `SELLER`, `BUYER`

**Relationships**:
- ManyToMany with `User` (mappedBy: `roles`)

---

### 4. Product
**Table**: `products`  
**Description**: Products available in the marketplace

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | Primary Key | Product ID |
| name | String(100) | NOT NULL | Product name |
| description | String(1000) | - | Product description |
| price | BigDecimal(10,2) | NOT NULL, >0 | Product price |
| stockQuantity | Integer | NOT NULL, default=0 | Available quantity |
| category | String(50) | - | Product category |
| imageUrl | String(500) | - | Product image URL |
| available | Boolean | NOT NULL, default=true | Availability status |
| createdAt | LocalDateTime | NOT NULL | Creation timestamp |
| updatedAt | LocalDateTime | NOT NULL | Update timestamp |

**Relationships**:
- ManyToOne with `User` (field: `seller`)
- ManyToMany with `Order` (mappedBy: `products`)

**Validation**:
- Name: min 3, max 100 characters
- Price: must be greater than 0

---

### 5. Order
**Table**: `orders`  
**Description**: Customer orders

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | Primary Key | Order ID |
| orderNumber | String(50) | NOT NULL, UNIQUE | Unique order number |
| totalAmount | BigDecimal(10,2) | NOT NULL, >=0 | Total order amount |
| status | OrderStatus (Enum) | NOT NULL, default=PENDING | Order status |
| shippingAddress | String(500) | - | Delivery address |
| notes | String(1000) | - | Order notes |
| createdAt | LocalDateTime | NOT NULL | Creation timestamp |
| updatedAt | LocalDateTime | NOT NULL | Update timestamp |

**Enum Values**: `PENDING`, `CONFIRMED`, `PROCESSING`, `SHIPPED`, `DELIVERED`, `CANCELLED`

**Relationships**:
- ManyToOne with `User` (field: `buyer`)
- ManyToMany with `Product` (join table: `order_products`)

---

## Relationship Details

### user_roles (Join Table)
**Purpose**: Maps users to their roles (many-to-many)

| Column | Type | References |
|--------|------|------------|
| user_id | Long | users.id |
| role_id | Long | roles.id |

### order_products (Join Table)
**Purpose**: Maps orders to products (many-to-many)

| Column | Type | References |
|--------|------|------------|
| order_id | Long | orders.id |
| product_id | Long | products.id |

---

## Entity Relationship Diagram

```
┌──────────────┐
│     User     │
├──────────────┤
│ id           │◄─────────┐
│ username     │          │
│ email        │          │ ManyToMany
│ password     │          │
│ ...          │     ┌────┴─────┐
└──────┬───────┘     │   Role   │
       │             ├──────────┤
       │ OneToMany   │ id       │
       │             │ name     │
       ├────────────►│ ...      │
       │             └──────────┘
       │
       │ OneToMany
       │
       ├────────────►┌────────────┐
       │             │  Product   │
       │             ├────────────┤
       │             │ id         │
       │             │ name       │
       │             │ price      │
       │             │ ...        │
       │             └──────┬─────┘
       │                    │
       │ OneToMany          │ ManyToMany
       │                    │
       └────────────►┌──────▼─────┐
                     │   Order    │
                     ├────────────┤
                     │ id         │
                     │ orderNumber│
                     │ totalAmount│
                     │ ...        │
                     └────────────┘
```

---

## Repository Methods Summary

### UserRepository
- `findByUsername(String)`: Find user by username
- `findByEmail(String)`: Find user by email
- `existsByUsername(String)`: Check username existence
- `existsByEmail(String)`: Check email existence
- `findAllEnabledUsers()`: Get all active users

### RoleRepository
- `findByName(RoleType)`: Find role by type
- `existsByName(RoleType)`: Check role existence

### ProductRepository
- `findBySeller(User)`: Find products by seller
- `findByAvailableTrue()`: Find available products
- `findByCategory(String)`: Find by category
- `findByNameContainingIgnoreCase(String)`: Search products
- `findByPriceRange(BigDecimal, BigDecimal)`: Filter by price
- `findInStockProducts(Integer)`: Find products with stock

### OrderRepository
- `findByOrderNumber(String)`: Find by order number
- `findByBuyer(User)`: Find orders by buyer
- `findByStatus(OrderStatus)`: Filter by status
- `findOrdersBetweenDates(LocalDateTime, LocalDateTime)`: Date range query
- `countByStatus(OrderStatus)`: Count orders by status

---

## Notes

- All entities extend `BaseEntity` for consistent timestamp management
- Lombok annotations (`@Getter`, `@Setter`, `@Builder`) reduce boilerplate
- Hibernate automatically creates/updates tables based on entity definitions
- All relationships use proper cascade types and fetch strategies
- Helper methods in entities maintain bidirectional relationship consistency
