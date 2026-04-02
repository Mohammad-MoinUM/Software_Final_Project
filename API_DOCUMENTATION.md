# API Documentation

## Base URL
```
http://localhost:8080
```

## Authentication
Most endpoints require authentication. Use HTTP Basic Auth or form-based login.

---

## User Endpoints

### Register User
```http
POST /api/users/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "fullName": "John Doe",
  "phoneNumber": "+8801234567890",
  "roles": ["BUYER"]
}
```

**Response:** `201 Created`
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "username": "johndoe",
    "email": "john@example.com",
    "fullName": "John Doe",
    "roles": ["BUYER"]
  }
}
```

---

## Product Endpoints

### Get All Products
```http
GET /api/products
```

### Get Product by ID
```http
GET /api/products/{id}
```

### Create Product (Seller/Admin)
```http
POST /api/products
Content-Type: application/json
Authorization: Required

{
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 75000.00,
  "stockQuantity": 10,
  "category": "Electronics",
  "imageUrl": "/uploads/products/laptop.jpg"
}
```

### Update Product (Seller/Admin)
```http
PUT /api/products/{id}
Content-Type: application/json
Authorization: Required
```

### Delete Product (Seller/Admin)
```http
DELETE /api/products/{id}
Authorization: Required
```

### Search Products
```http
GET /api/products/search?name={query}&category={category}&minPrice={min}&maxPrice={max}
```

---

## Cart Endpoints

### Get Cart
```http
GET /api/cart
Authorization: Required
```

**Response:** `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "productId": 5,
      "productName": "Laptop",
      "quantity": 2,
      "price": 75000.00,
      "subtotal": 150000.00
    }
  ]
}
```

### Add to Cart
```http
POST /api/cart
Content-Type: application/json
Authorization: Required

{
  "productId": 5,
  "quantity": 2
}
```

### Update Cart Item
```http
PUT /api/cart/{cartItemId}
Content-Type: application/json
Authorization: Required

{
  "quantity": 3
}
```

### Remove from Cart
```http
DELETE /api/cart/{cartItemId}
Authorization: Required
```

### Clear Cart
```http
DELETE /api/cart/clear
Authorization: Required
```

### Get Cart Total
```http
GET /api/cart/total
Authorization: Required
```

---

## Wishlist Endpoints

### Get Wishlist
```http
GET /api/wishlist
Authorization: Required
```

**Response:** `200 OK`
```json
{
  "success": true,
  "data": {
    "id": 1,
    "userId": 10,
    "products": [
      {
        "id": 5,
        "name": "Laptop",
        "price": 75000.00,
        "imageUrl": "/uploads/products/laptop.jpg"
      }
    ]
  }
}
```

### Add to Wishlist
```http
POST /api/wishlist/add/{productId}
Authorization: Required
```

### Remove from Wishlist
```http
DELETE /api/wishlist/remove/{productId}
Authorization: Required
```

---

## Review Endpoints

### Get Product Reviews
```http
GET /api/reviews/product/{productId}
```

**Response:** `200 OK`
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "productId": 5,
      "userId": 10,
      "userName": "John Doe",
      "rating": 5,
      "comment": "Excellent product!",
      "verifiedPurchase": true,
      "createdAt": "2024-01-15T10:30:00"
    }
  ]
}
```

### Create Review
```http
POST /api/reviews
Content-Type: application/json
Authorization: Required

{
  "productId": 5,
  "rating": 5,
  "comment": "Excellent product! Highly recommended.",
  "verifiedPurchase": true
}
```

### Update Review
```http
PUT /api/reviews/{reviewId}
Content-Type: application/json
Authorization: Required

{
  "rating": 4,
  "comment": "Updated review text"
}
```

### Delete Review
```http
DELETE /api/reviews/{reviewId}
Authorization: Required
```

### Get Average Rating
```http
GET /api/reviews/product/{productId}/average
```

---

## Order Endpoints

### Create Order
```http
POST /api/orders
Content-Type: application/json
Authorization: Required

{
  "productIds": [5, 7, 10],
  "shippingAddress": "123 Main St, Dhaka, Bangladesh"
}
```

### Get My Orders
```http
GET /api/orders/my-orders
Authorization: Required
```

### Get Order by ID
```http
GET /api/orders/{orderId}
Authorization: Required
```

### Update Order Status (Admin)
```http
PUT /api/orders/{orderId}/status?status=SHIPPED
Authorization: Required (Admin)
```

### Cancel Order
```http
DELETE /api/orders/{orderId}
Authorization: Required
```

---

## Payment Endpoints

### Create Payment
```http
POST /api/payments
Content-Type: application/json
Authorization: Required

{
  "orderId": 15,
  "paymentMethod": "BKASH",
  "amount": 150000.00
}
```

**Response:** `201 Created`
```json
{
  "success": true,
  "data": {
    "id": 1,
    "orderId": 15,
    "amount": 150000.00,
    "paymentMethod": "BKASH",
    "status": "PENDING",
    "transactionId": "TXN-1234567890",
    "paymentDate": null
  }
}
```

### Process Payment
```http
POST /api/payments/{paymentId}/process
Authorization: Required
```

### Get Payment by Order
```http
GET /api/payments/order/{orderId}
Authorization: Required
```

### Get Payment by Transaction ID
```http
GET /api/payments/transaction/{transactionId}
Authorization: Required
```

---

## File Upload Endpoints

### Upload Product Image
```http
POST /api/upload/product-image
Content-Type: multipart/form-data
Authorization: Required

file: (binary image file, max 5MB)
```

**Response:** `200 OK`
```json
{
  "success": true,
  "message": "File uploaded successfully",
  "data": "/uploads/products/12345-laptop.jpg"
}
```

### Delete Product Image
```http
DELETE /api/upload/product-image?imageUrl=/uploads/products/12345-laptop.jpg
Authorization: Required
```

---

## Response Format

All API responses follow this standard format:

### Success Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { }
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error description",
  "errors": ["Error detail 1", "Error detail 2"]
}
```

---

## HTTP Status Codes

- `200 OK` - Successful GET, PUT, DELETE
- `201 Created` - Successful POST (resource created)
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `409 Conflict` - Resource already exists
- `500 Internal Server Error` - Server error

---

## Payment Methods

Available payment methods (enum `PaymentMethod`):
- `CARD` - Credit/Debit card
- `BKASH` - bKash mobile payment
- `NAGAD` - Nagad mobile payment
- `ROCKET` - Rocket mobile payment
- `CASH_ON_DELIVERY` - Cash on delivery

## Payment Status

Payment status values (enum `PaymentStatus`):
- `PENDING` - Payment initiated
- `PROCESSING` - Payment in progress
- `COMPLETED` - Payment successful
- `FAILED` - Payment failed
- `REFUNDED` - Payment refunded

## Order Status

Order status values (enum `OrderStatus`):
- `PENDING` - Order placed
- `CONFIRMED` - Order confirmed
- `PROCESSING` - Order being prepared
- `SHIPPED` - Order shipped
- `DELIVERED` - Order delivered
- `CANCELLED` - Order cancelled

---

## Frontend Pages

### Public Pages
- `/` - Home page with features
- `/products` - Product listing with search/filters
- `/product-details?id={productId}` - Product details with reviews
- `/login` - Login page
- `/register` - Registration page

### Authenticated Pages
- `/dashboard` - User dashboard (Buyer)
- `/cart` - Shopping cart
- `/checkout` - Checkout page
- `/wishlist` - User wishlist
- `/order-history` - Order history
- `/seller-dashboard` - Seller dashboard (Seller/Admin)
- `/product-management` - Product creation/editing (Seller/Admin)
- `/admin-dashboard` - Admin dashboard (Admin only)

---

## Notes

- All timestamps are in ISO 8601 format
- Prices are in BDT (Bangladeshi Taka)
- File uploads limited to 5MB
- Supported image formats: JPG, JPEG, PNG, GIF
- Cart items are temporary and cleared after order placement
- Reviews can only be created by authenticated users
- Verified purchase badge requires actual order history
