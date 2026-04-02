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

### ReviewRepository
- `findByProduct(Product)`: Find reviews for a product
- `findByUser(User)`: Find reviews by user
- `findVerifiedPurchaseReviews(Product)`: Get verified purchase reviews
- `findByProductAndUser(Product, User)`: Check if user reviewed product
- `getAverageRating(Product)`: Calculate average rating

### WishlistRepository
- `findByUser(User)`: Find wishlist by user
- `existsByUserAndProduct(User, Product)`: Check if product in wishlist

### CartItemRepository
- `findByUser(User)`: Find cart items for user
- `findByUserAndProduct(User, Product)`: Find specific cart item
- `deleteByUser(User)`: Clear user's cart
- `countByUser(User)`: Get cart item count

### PaymentRepository
- `findByOrder(Order)`: Find payment for order
- `findByTransactionId(String)`: Find payment by transaction ID
- `findByStatus(PaymentStatus)`: Filter by payment status
- `findByPaymentMethod(PaymentMethod)`: Filter by payment method

---

## New Entities (Added in v2)

### 6. Review
**Table**: `reviews`  
**Description**: Product reviews and ratings by customers

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | Primary Key | Review ID |
| rating | Integer | NOT NULL, 1-5 | Star rating |
| comment | String(2000) | - | Review text |
| verifiedPurchase | Boolean | default=false | Verified buyer flag |
| createdAt | LocalDateTime | NOT NULL | Creation timestamp |
| updatedAt | LocalDateTime | NOT NULL | Update timestamp |

**Relationships**:
- ManyToOne with `Product` (field: `product`)
- ManyToOne with `User` (field: `user`)

**Validation**:
- Rating: must be between 1 and 5
- Comment: max 2000 characters

---

### 7. Wishlist
**Table**: `wishlists`  
**Description**: User wishlists for saving favorite products

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | Primary Key | Wishlist ID |
| createdAt | LocalDateTime | NOT NULL | Creation timestamp |
| updatedAt | LocalDateTime | NOT NULL | Update timestamp |

**Relationships**:
- OneToOne with `User` (field: `user`)
- ManyToMany with `Product` (join table: `wishlist_products`)

---

### 8. CartItem
**Table**: `cart_items`  
**Description**: Shopping cart items for users

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | Primary Key | Cart item ID |
| quantity | Integer | NOT NULL, >0 | Product quantity |
| createdAt | LocalDateTime | NOT NULL | Creation timestamp |
| updatedAt | LocalDateTime | NOT NULL | Update timestamp |

**Relationships**:
- ManyToOne with `User` (field: `user`)
- ManyToOne with `Product` (field: `product`)

**Validation**:
- Quantity: must be greater than 0

---

### 9. Payment
**Table**: `payments`  
**Description**: Payment transactions for orders

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | Long | Primary Key | Payment ID |
| amount | BigDecimal(10,2) | NOT NULL, >0 | Payment amount |
| paymentMethod | PaymentMethod | NOT NULL | Payment method used |
| status | PaymentStatus | NOT NULL, default=PENDING | Payment status |
| transactionId | String(100) | UNIQUE | Transaction reference |
| paymentDate | LocalDateTime | - | Payment completion time |
| createdAt | LocalDateTime | NOT NULL | Creation timestamp |
| updatedAt | LocalDateTime | NOT NULL | Update timestamp |

**Enum Values (PaymentMethod)**: `CARD`, `BKASH`, `NAGAD`, `ROCKET`, `CASH_ON_DELIVERY`

**Enum Values (PaymentStatus)**: `PENDING`, `PROCESSING`, `COMPLETED`, `FAILED`, `REFUNDED`

**Relationships**:
- OneToOne with `Order` (field: `order`)

**Validation**:
- Amount: must be greater than 0

---

## Updated Relationship Diagram

```
┌──────────────┐
│     User     │◄────────────────┐
├──────────────┤                 │
│ id           │◄───────┐        │ ManyToMany
│ username     │        │        │
│ ...          │        │   ┌────┴─────┐
└──┬───┬───┬───┘        │   │   Role   │
   │   │   │            │   └──────────┘
   │   │   │            │
   │   │   └─OneToOne──►│
   │   │                │
   │   │           ┌────┴─────┐
   │   │           │ Wishlist │
   │   │           └──┬───────┘
   │   │              │ ManyToMany
   │   │              ▼
   │   │         ┌────────────┐
   │   └────────►│  CartItem  │
   │   OneToMany ├────────────┤
   │             │ quantity   │
   │             └──┬─────────┘
   │                │ ManyToOne
   │                ▼
   │         ┌──────────────┐
   ├────────►│   Product    │◄────────┐
   │ OneToMany├──────────────┤         │
   │ (seller) │ name, price  │         │
   │          └──┬───────────┘         │
   │             │ OneToMany            │
   │             ▼                      │
   │         ┌──────────┐               │
   │         │  Review  │               │
   │         ├──────────┤               │
   │         │ rating   │               │
   │         │ comment  │               │
   │         └──────────┘               │
   │                                    │
   │ OneToMany                          │
   │                                    │
   └────────►┌──────────┐              │
             │  Order   │              │
             ├──────────┤              │
             │ total    │              │
             │ status   │              │
             └──┬───────┘              │
                │ OneToOne              │
                ▼                       │
             ┌──────────┐              │
             │ Payment  │              │
             ├──────────┤              │
             │ amount   │              │
             │ method   │              │
             └──────────┘              │
                                       │
                  ManyToMany           │
                  (order_products)─────┘
```

---

## New Join Tables

### wishlist_products
**Purpose**: Maps wishlists to products (many-to-many)

| Column | Type | References |
|--------|------|------------|
| wishlist_id | Long | wishlists.id |
| product_id | Long | products.id |

---

## Notes

- All entities extend `BaseEntity` for consistent timestamp management
- Lombok annotations (`@Getter`, `@Setter`, `@Builder`) reduce boilerplate
- Hibernate automatically creates/updates tables based on entity definitions
- All relationships use proper cascade types and fetch strategies
- Helper methods in entities maintain bidirectional relationship consistency
- Payment integration supports multiple local payment methods (bKash, Nagad, Rocket)
- Review system includes verified purchase flagging
- Cart items are temporary and cleared after order placement
- Wishlist provides persistent product saving across sessions
