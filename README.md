# 🛒 Grocery Store Backend

A full-featured **REST API backend** for an online Grocery Store application, built with **Spring Boot 3.5** and **Java 17**. This backend powers both the **Customer-facing storefront** and the **Admin dashboard**, providing authentication, product catalog, order management, reporting, and real-time notifications.

---

## 📋 Table of Contents

- [Tech Stack](#-tech-stack)
- [Features](#-features)
- [Project Structure](#-project-structure)
- [Database Schema](#-database-schema)
- [API Reference](#-api-reference)
  - [Authentication](#authentication)
  - [Products](#products-customer)
  - [Categories](#categories-customer)
  - [Orders](#orders-customer)
  - [User Profile & Address](#user-profile--address)
  - [Wishlist](#wishlist)
  - [Admin - Users](#admin---users)
  - [Admin - Products](#admin---products)
  - [Admin - Categories](#admin---categories)
  - [Admin - Orders](#admin---orders)
  - [Admin - Dashboard & Reports](#admin---dashboard--reports)
  - [Admin - Settings](#admin---settings)
- [Getting Started](#-getting-started)
- [Environment Configuration](#-environment-configuration)
- [Docker Support](#-docker-support)
- [Frontend Integration](#-frontend-integration)

---

## 🛠 Tech Stack

| Layer | Technology |
|:------|:-----------|
| **Framework** | Spring Boot 3.5.6 |
| **Language** | Java 17 |
| **Database** | PostgreSQL |
| **ORM** | Spring Data JPA (Hibernate) |
| **Authentication** | JWT (jjwt 0.11.5) + Spring Security |
| **Password Hashing** | BCrypt (Spring Security Crypto) |
| **Validation** | Spring Boot Starter Validation (Jakarta) |
| **Migration** | Flyway |
| **File Storage** | Cloudinary (cloud-based image uploads) |
| **Real-time** | WebSocket (STOMP protocol) |
| **PDF Reports** | OpenPDF + JFreeChart |
| **Build Tool** | Maven |
| **Containerization** | Docker (multi-stage build) |
| **Boilerplate Reduction** | Lombok |

---

## ✨ Features

### Customer Features
- 🔐 **User Registration & Login** – JWT-based authentication with token refresh
- 🏠 **Home Page** – Featured products and product listing
- 📦 **Product Browsing** – Search, filter by category, view product details with variants
- ❤️ **Wishlist** – Add/remove products, toggle wishlist
- 🛍 **Order Placement** – Place orders with address selection, COD/Online payment
- 📜 **Order History** – Track past orders and their statuses
- 👤 **Profile Management** – Update name, mobile, address
- 📍 **Address Management** – Multiple addresses with default address support

### Admin Features
- 👥 **User Management** – View, create, update, delete users; reset passwords; assign roles
- 📦 **Product Management** – Full CRUD with image upload (Cloudinary), product variants, featured flag
- 🏷️ **Category Management** – Create, update, soft-delete categories
- 📋 **Order Management** – View all orders, update status (single & bulk), filter/search
- 📊 **Dashboard** – Revenue, total orders, pending orders, items sold, sales trend charts, best sellers
- 📈 **Reports** – Export full reports as PDF with charts
- ⚙️ **Settings** – Configurable admin settings
- 🔔 **Real-time Notifications** – WebSocket-based live order updates

### Security & Architecture
- 🔑 **Role-based Access** – `ROLE_CUSTOMER` and `ROLE_ADMIN`
- 🛡️ **JWT Authentication Filter** – Custom filter for token validation on every request
- 🚫 **Account Status Management** – ACTIVE, INACTIVE, SUSPENDED, DELETED
- ⚠️ **Global Exception Handling** – Centralized error responses
- 📝 **Standardized API Response** – Consistent `{ success, message, data, timestamp }` wrapper
- 🌐 **CORS Configuration** – Configurable allowed origins
- 🌱 **Admin Data Seeder** – Auto-creates default admin account on first run

---

## 📁 Project Structure

```
src/main/java/com/grocery/
│
├── GroceryStoreBackendApplication.java     # Main entry point
│
├── auth/                                    # Authentication module
│   ├── AccountStatus.java                   # Enum: ACTIVE, INACTIVE, SUSPENDED, DELETED
│   ├── AuthController.java                  # Login, Register, Token Check, Token Refresh
│   ├── AuthResponse.java                    # JWT token response DTO
│   ├── LoginRequest.java                    # Login request DTO
│   ├── RegistrationRequest.java             # Registration request DTO (with validation)
│   ├── Role.java                            # Enum: ROLE_CUSTOMER, ROLE_ADMIN
│   ├── User.java                            # User entity (implements UserDetails)
│   ├── UserRepository.java                  # JPA Repository with search query
│   └── UserService.java                     # User business logic
│
├── category/                                # Category module
│   ├── Category.java                        # Category entity
│   ├── CategoryAdminController.java         # Admin CRUD for categories
│   ├── CategoryController.java              # Public: list active categories
│   ├── CategoryRepository.java              # JPA Repository with search + filter
│   ├── CategoryService.java                 # Category business logic
│   └── dto/
│       ├── CategoryCreateRequest.java
│       └── CategoryUpdateRequest.java
│
├── product/                                 # Product module
│   ├── Product.java                         # Product entity
│   ├── ProductVariant.java                  # Product variant entity (size/weight/price)
│   ├── controller/
│   │   ├── AdminProductController.java      # Admin CRUD + image upload
│   │   └── ProductController.java           # Public: featured, list, get by ID
│   ├── dto/
│   │   ├── ProductCreateRequest.java
│   │   ├── ProductUpdateRequest.java
│   │   ├── ProductResponse.java
│   │   └── VariantDto.java
│   ├── repository/
│   │   ├── ProductRepository.java
│   │   └── ProductVariantRepository.java
│   └── service/
│       ├── ProductService.java              # Product business logic
│       └── FileStorageService.java          # Cloudinary image upload service
│
├── order/                                   # Order module
│   ├── Order.java                           # Order entity
│   ├── OrderItem.java                       # Order item entity
│   ├── OrderStatus.java                     # Enum: PENDING → DELIVERED / CANCELLED
│   ├── controller/
│   │   ├── AdminOrderController.java        # Admin: list all, update status, bulk update
│   │   └── OrderController.java             # Customer: place order, view my orders
│   ├── dto/
│   │   ├── OrderRequest.java                # Order placement request
│   │   └── BulkStatusRequest.java           # Bulk status update request
│   ├── repository/
│   │   ├── OrderRepository.java
│   │   └── OrderItemRepository.java
│   └── service/
│       └── OrderService.java                # Order business logic
│
├── user/                                    # User profile & address module
│   ├── AddressRepository.java
│   ├── WishlistRepository.java
│   ├── controller/
│   │   ├── AddressController.java           # Address CRUD + set default
│   │   ├── AdminUserController.java         # Admin: user CRUD + password reset
│   │   ├── ProfileController.java           # Get/update own profile
│   │   └── WishlistController.java          # Add/toggle wishlist, get my wishlist
│   ├── dto/
│   │   ├── AdminCreateRequest.java
│   │   └── UpdateUserRequest.java
│   └── entity/
│       ├── Address.java                     # Address entity
│       └── Wishlist.java                    # Wishlist entity
│
├── reports/                                 # Dashboard & reports module
│   ├── DashBoardController.java             # Stats + PDF export
│   ├── DashBoardDTO.java                    # Stats DTOs (charts, best sellers, low stock)
│   └── DashBoardService.java                # Report generation logic
│
├── settings/                                # Admin settings module
│   ├── AdminSettings.java                   # Settings entity
│   ├── AdminSettingsRepo.java               # Settings repository
│   └── SettingsController.java              # Get/update settings
│
├── common/                                  # Shared utilities
│   ├── ApiResponse.java                     # Standard API response wrapper
│   ├── JwtUtility.java                      # JWT token generation & validation
│   └── Status.java                          # Enum: ACTIVE, INACTIVE, DELETED
│
├── config/                                  # Configuration
│   ├── AdminDataSeeder.java                 # Seeds default admin user
│   ├── AuthEntryPoint.java                  # Custom 401 handler
│   ├── GlobalExceptionHandler.java          # Centralized exception handling
│   ├── JwtAuthenticationFilter.java         # JWT filter for Spring Security
│   ├── SecurityConfig.java                  # Security filter chain configuration
│   ├── WebConfig.java                       # CORS configuration
│   └── WebSocketConfig.java                 # WebSocket/STOMP configuration
│
└── exception/                               # Custom exceptions
    ├── CategoryAlreadyExistsException.java
    ├── FileStorageException.java
    ├── InsufficientStockException.java
    ├── ReportGenerationException.java
    ├── ResourceNotFoundException.java
    ├── UnauthorizedAccessException.java
    └── UserAlreadyExistsException.java
```

---

## 🗄 Database Schema

The application uses **7 database tables** + 1 join table:

| Table | Entity | Description |
|:------|:-------|:------------|
| `users` | `User` | User accounts with email, password hash, roles, status |
| `user_roles` | *(join table)* | Maps users to their roles (ROLE_CUSTOMER, ROLE_ADMIN) |
| `categories` | `Category` | Product categories with name, description, status |
| `products` | `Product` | Products linked to categories, with stock, price, image, featured flag |
| `product_variants` | `ProductVariant` | Product variants (size/weight options with individual pricing) |
| `orders` | `Order` | Customer orders with status tracking, shipping address, payment method |
| `order_items` | `OrderItem` | Individual items within an order |
| `wishlist` | `Wishlist` | User's wishlisted products |

### Entity Relationships

```
User (1) ──────── (N) Address          (One user has many addresses)
User (1) ──────── (N) Order            (One user has many orders)
User (1) ──────── (N) Wishlist         (One user has many wishlist items)
Category (1) ──── (N) Product          (One category has many products)
Product (1) ───── (N) ProductVariant   (One product has many variants)
Product (1) ───── (N) Wishlist         (One product can be in many wishlists)
Order (1) ─────── (N) OrderItem        (One order has many items)
```

---

## 📡 API Reference

> **Base URL:** `http://localhost:8080/api`
>
> **Auth Header:** `user-payload: <JWT_TOKEN>` (for protected endpoints)

### Authentication

| Method | Endpoint | Description | Auth |
|:-------|:---------|:------------|:-----|
| `POST` | `/auth/login` | Login with email & password | ❌ |
| `POST` | `/auth/register` | Register a new customer account | ❌ |
| `GET` | `/auth/check` | Verify if current JWT token is valid | ✅ |
| `POST` | `/auth/refresh` | Refresh JWT token | ✅ |

### Products (Customer)

| Method | Endpoint | Description | Auth |
|:-------|:---------|:------------|:-----|
| `GET` | `/products/featured` | Get featured products | ✅ |
| `GET` | `/products/getProducts` | List products (search via `?q=`) | ✅ |
| `GET` | `/products/getProducts/{id}` | Get product details by ID | ✅ |

### Categories (Customer)

| Method | Endpoint | Description | Auth |
|:-------|:---------|:------------|:-----|
| `GET` | `/categories` | List all active categories | ✅ |

### Orders (Customer)

| Method | Endpoint | Description | Auth |
|:-------|:---------|:------------|:-----|
| `POST` | `/orders/place` | Place a new order | ✅ |
| `GET` | `/orders/my-orders` | Get current user's order history | ✅ |

### User Profile & Address

| Method | Endpoint | Description | Auth |
|:-------|:---------|:------------|:-----|
| `GET` | `/users/me` | Get current user's profile | ✅ |
| `PUT` | `/users/update-profile` | Update own profile | ✅ |
| `GET` | `/address` | Get user's addresses | ✅ |
| `POST` | `/address` | Add a new address | ✅ |
| `PATCH` | `/address/{addressId}/set-default` | Set an address as default | ✅ |

### Wishlist

| Method | Endpoint | Description | Auth |
|:-------|:---------|:------------|:-----|
| `POST` | `/wishlist/addWishList/{productId}` | Add/toggle product in wishlist | ✅ |
| `GET` | `/wishlist/me` | Get current user's wishlist | ✅ |

### Admin - Users

| Method | Endpoint | Description | Auth |
|:-------|:---------|:------------|:-----|
| `GET` | `/admin/getusers` | List all users (paginated, searchable) | 🔒 Admin |
| `GET` | `/admin/getusers/{id}` | Get user by ID | 🔒 Admin |
| `POST` | `/admin/create-user` | Create a new user (with role assignment) | 🔒 Admin |
| `PATCH` | `/admin/users/{id}` | Partially update user | 🔒 Admin |
| `PUT` | `/admin/users/{id}` | Fully update user | 🔒 Admin |
| `DELETE` | `/admin/users/{id}` | Delete a user | 🔒 Admin |
| `PATCH` | `/admin/users/{id}/reset-password` | Reset user's password | 🔒 Admin |

### Admin - Products

| Method | Endpoint | Description | Auth |
|:-------|:---------|:------------|:-----|
| `GET` | `/admin/products` | List all products (paginated, searchable) | 🔒 Admin |
| `GET` | `/admin/products/{id}` | Get product by ID | 🔒 Admin |
| `POST` | `/admin/products` | Create a new product | 🔒 Admin |
| `PUT` | `/admin/products/{id}` | Update a product | 🔒 Admin |
| `DELETE` | `/admin/products/{id}` | Delete a product | 🔒 Admin |
| `POST` | `/admin/products/images/product/{id}` | Upload product images | 🔒 Admin |

### Admin - Categories

| Method | Endpoint | Description | Auth |
|:-------|:---------|:------------|:-----|
| `GET` | `/admin/categories` | List categories (paginated, search + filter by status) | 🔒 Admin |
| `POST` | `/admin/categories` | Create a new category | 🔒 Admin |
| `PUT` | `/admin/categories/{id}` | Update a category | 🔒 Admin |
| `DELETE` | `/admin/categories/{id}` | Soft-delete a category | 🔒 Admin |

### Admin - Orders

| Method | Endpoint | Description | Auth |
|:-------|:---------|:------------|:-----|
| `GET` | `/admin/orders/all` | List all orders (paginated, searchable) | 🔒 Admin |
| `PATCH` | `/admin/orders/{id}/status` | Update single order status | 🔒 Admin |
| `PATCH` | `/admin/orders/bulk-status` | Bulk update order statuses | 🔒 Admin |
| `GET` | `/admin/orders/user/{id}` | Get orders for a specific user | 🔒 Admin |

### Admin - Dashboard & Reports

| Method | Endpoint | Description | Auth |
|:-------|:---------|:------------|:-----|
| `GET` | `/admin/dashboard/stats` | Get dashboard statistics (with date range) | 🔒 Admin |
| `GET` | `/admin/dashboard/reports/export-full` | Export full report as PDF | 🔒 Admin |

### Admin - Settings

| Method | Endpoint | Description | Auth |
|:-------|:---------|:------------|:-----|
| `GET` | `/admin/settings` | Get current admin settings | 🔒 Admin |
| `PUT` | `/admin/settings` | Update admin settings | 🔒 Admin |

---

## 🚀 Getting Started

### Prerequisites
- **Java 17** or higher
- **Maven 3.8+**
- **PostgreSQL 14+**

### 1. Clone the Repository

```bash
git clone https://github.com/Niranjan-4975/grocery-store-backend.git
cd grocery-store-backend
```

### 2. Setup PostgreSQL Database

```sql
CREATE DATABASE grocery_db;
CREATE USER grocery WITH PASSWORD 'grocery';
GRANT ALL PRIVILEGES ON DATABASE grocery_db TO grocery;
```

### 3. Run the Application

```bash
# Using Maven
./mvnw spring-boot:run

# Or on Windows
mvnw.cmd spring-boot:run
```

The server will start at `http://localhost:8080`

---

## ⚙ Environment Configuration

### Development (`application-dev.properties`)

| Property | Default Value | Description |
|:---------|:-------------|:------------|
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/grocery_db` | Database URL |
| `spring.datasource.username` | `grocery` | DB username |
| `spring.datasource.password` | `grocery` | DB password |
| `spring.jpa.hibernate.ddl-auto` | `validate` | Schema validation mode |
| `app.cors.allowed-origins` | `http://localhost:5173` | Frontend URL for CORS |
| `cloudinary.cloud-name` | *(configured)* | Cloudinary cloud name for image uploads |
| `cloudinary.api-key` | *(configured)* | Cloudinary API key |
| `cloudinary.api-secret` | *(configured)* | Cloudinary API secret |

### Profiles
- **`dev`** – Local development (default active profile)
- **`prod`** – Production configuration

---

## 🐳 Docker Support

The project includes a **multi-stage Dockerfile** for optimized builds:

```bash
# Build the Docker image
docker build -t grocery-store-backend .

# Run the container
docker run -p 8080:8080 grocery-store-backend
```

> **Note:** Make sure to configure database connection via environment variables when running in Docker.

---

## 🖥 Frontend Integration

This backend is designed to work with the **Grocery Store Frontend** built with:

| Technology | Purpose |
|:-----------|:--------|
| **Vue 3** | Frontend framework (Composition API + `<script setup>`) |
| **Vuetify 3** | UI component library (Material Design) |
| **Vue Router** | Client-side routing with role-based guards |
| **Axios** | HTTP client with request/response interceptors |
| **Pinia** | State management (toast notifications) |
| **ApexCharts** | Dashboard charts (sales trends, best sellers) |
| **STOMP.js** | WebSocket client for real-time notifications |
| **Vue I18n** | Multi-language support (English, Hindi, Marathi) |

### Frontend Routes

| Route | Component | Role |
|:------|:----------|:-----|
| `/login` | Login/Register | Public |
| `/home` | Home page | Customer |
| `/product` | Product listing | Customer |
| `/product/:id` | Product detail | Customer |
| `/checkout` | Checkout | Customer |
| `/profile` | User profile | Customer |
| `/orderHistory` | Order history | Customer |
| `/admin` | Dashboard | Admin |
| `/admin/users` | User management | Admin |
| `/admin/products` | Product management | Admin |
| `/admin/categories` | Category management | Admin |
| `/admin/orders` | Order management | Admin |
| `/admin/reports` | Reports & exports | Admin |
| `/admin/settings` | App settings | Admin |

---

## 📄 License

This project is for educational and personal use.

---

> Built with ❤️ using Spring Boot & Vue.js
