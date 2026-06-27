# 🛵 Glovo Clone — Food Delivery API

A full-featured, production-ready food delivery REST API built with **Spring Boot 3** and **PostgreSQL**. Inspired by Glovo, this backend powers a complete delivery platform with multi-role authentication, cart management, order lifecycle tracking, real-time delivery progression, async notifications, and vendor ratings.

Built as a **modular monolith** — clean domain boundaries that map directly to microservices when you're ready to scale.

---

## 🚀 Features

### 👤 Authentication & Roles
- JWT-based stateless authentication
- Four roles: `CUSTOMER`, `VENDOR`, `COURIER`, `ADMIN`
- Role-restricted endpoints enforced via Spring Security
- BCrypt password hashing

### 🍽️ Vendor & Menu Management
- Vendors create and manage restaurant profiles
- Full menu item CRUD with pricing
- Open/closed status toggling
- One vendor profile per VENDOR user (enforced)

### 🛒 Cart
- Persistent cart tied to each customer
- Single-vendor lock (can't mix vendors in one cart)
- Quantity management, subtotals, and running total
- Auto-clears on successful checkout

### 📦 Orders
- Checkout converts cart to order with price snapshots
- Full order lifecycle:
  ```
  PLACED → ACCEPTED → PREPARING → READY_FOR_PICKUP
  → PICKED_UP → DELIVERING → DELIVERED
  (or CANCELLED at any valid point)
  ```
- Strict status transition validation — no skipping steps
- Vendor-controlled status updates
- Customer order history

### 🚴 Delivery
- Couriers browse available `READY_FOR_PICKUP` orders
- Self-assignment (first-come, first-served)
- Step-by-step delivery progression with timestamps
- Order status synced automatically with delivery status

### 🔔 Notifications
- Async Spring Events — fires on every order status change
- Console-logged stubs ready to swap for real email/SMS/push
- Non-blocking (`@Async`) — never delays the main request

### 🛡️ Admin Panel
- View all users, vendors, and orders
- Suspend/activate user accounts
- Toggle vendor open/closed status
- Protected by `ADMIN` role

### ⭐ Ratings & Reviews
- Customers rate vendors after order is `DELIVERED`
- One rating per order (duplicate prevention)
- Written review support
- Vendor average score calculation
- Public vendor rating summaries

---

## 🏗️ Architecture

```
org.appGlovo.glovoclone/
├── user/           # Auth, JWT, roles
├── vendor/         # Restaurant profiles, menus
├── cart/           # Cart management
├── order/          # Order lifecycle
├── delivery/       # Courier assignment & tracking
├── notification/   # Spring Events (async)
├── admin/          # Admin operations
├── rating/         # Reviews & ratings
├── security/       # JWT filter, auth service
├── config/         # Security config, OpenAPI
└── common/         # Global exception handler
```

Each package is a self-contained domain (entity → repository → service → controller) following the **modular monolith** pattern — ready to extract into separate microservices.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.x |
| Language | Java 21 |
| Database | PostgreSQL 15 |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| Validation | Jakarta Bean Validation |
| Events | Spring Application Events |
| Build Tool | Maven |
| Utilities | Lombok |

---

## 📋 Prerequisites

- Java 21+
- PostgreSQL 15+
- Maven 3.8+

---

## ⚙️ Setup & Installation

### 1. Clone the repository

```bash
git clone https://github.com/YOUR_USERNAME/glovo-clone.git
cd glovo-clone
```

### 2. Create the database

```sql
CREATE DATABASE glovodb;
```

### 3. Configure environment

Copy or edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/glovodb
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

app.jwt.secret=YOUR_BASE64_SECRET
app.jwt.expiration-ms=86400000
```

> For production, use environment variables instead of hardcoding values.

### 4. Run the application

```bash
mvn spring-boot:run
```

The API will start at `http://localhost:8080`.

---

## 📡 API Reference

### Auth
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register a new user |
| POST | `/api/auth/login` | Public | Login and receive JWT |

### Vendors
| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/vendors` | Public | List all open vendors |
| GET | `/api/vendors/{id}` | Public | Get vendor with menu |
| POST | `/api/vendors` | VENDOR | Create vendor profile |
| POST | `/api/vendors/{id}/menu-items` | VENDOR | Add menu item |

### Cart
| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/cart` | Authenticated | View current cart |
| POST | `/api/cart/items` | Authenticated | Add item to cart |
| PUT | `/api/cart/items/{id}` | Authenticated | Update item quantity |
| DELETE | `/api/cart/items/{id}` | Authenticated | Remove item |
| DELETE | `/api/cart` | Authenticated | Clear cart |

### Orders
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/orders` | Authenticated | Place order from cart |
| GET | `/api/orders/my-orders` | Authenticated | Customer order history |
| GET | `/api/orders/vendor-orders` | VENDOR | Incoming vendor orders |
| PATCH | `/api/orders/{id}/status` | VENDOR | Update order status |

### Deliveries
| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/deliveries/available` | Authenticated | Browse available orders |
| POST | `/api/deliveries/claim/{orderId}` | COURIER | Claim an order |
| PATCH | `/api/deliveries/{id}/status` | COURIER | Update delivery status |
| GET | `/api/deliveries/my-deliveries` | COURIER | Courier delivery history |

### Ratings
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/ratings` | Authenticated | Submit rating (delivered orders only) |
| GET | `/api/ratings/vendor/{id}` | Public | Vendor rating summary |
| GET | `/api/ratings/my-ratings` | Authenticated | Customer's submitted ratings |

### Admin
| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/admin/users` | ADMIN | All users |
| GET | `/api/admin/vendors` | ADMIN | All vendors |
| GET | `/api/admin/orders` | ADMIN | All orders |
| PATCH | `/api/admin/users/{id}/toggle-active` | ADMIN | Suspend/activate user |
| PATCH | `/api/admin/vendors/{id}/toggle-open` | ADMIN | Toggle vendor open status |

---

## 🔐 Authentication

All protected endpoints require a Bearer token in the `Authorization` header:

```
Authorization: Bearer <your_jwt_token>
```

Get your token from `/api/auth/login` or `/api/auth/register`.

---

## 🔄 Order Lifecycle

```
Customer places order
        ↓
    PLACED
        ↓ (Vendor accepts)
   ACCEPTED
        ↓ (Vendor starts cooking)
   PREPARING
        ↓ (Vendor marks ready)
READY_FOR_PICKUP ←── Courier sees this and claims it
        ↓ (Courier picks up)
   PICKED_UP
        ↓ (Courier en route)
  DELIVERING
        ↓ (Courier delivers)
   DELIVERED ←── Customer can now rate the vendor
```

Invalid transitions are rejected with a clear error message.

---

## 🗂️ Database Schema

Core tables: `users`, `vendors`, `menu_items`, `carts`, `cart_items`, `orders`, `order_items`, `deliveries`, `ratings`

Tables are auto-created by Hibernate on first run (`ddl-auto=update`).

---

## 🚧 Roadmap

- [ ] Frontend (React)
- [ ] Swagger / OpenAPI documentation
- [ ] Real email notifications (Spring Mail)
- [ ] Payment integration (Paystack / Stripe)
- [ ] Live order tracking (WebSockets)
- [ ] Microservices extraction
- [ ] Docker Compose setup
- [ ] CI/CD pipeline

---

## 👨‍💻 Author

**Lyon Ade**
NYSC Corps Member | Software Developer
Delta State, Nigeria

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
