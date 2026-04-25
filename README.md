# Expiry Rescue App

A full-stack web application that reduces food waste by connecting supermarkets with customers. Supermarkets list soon-to-expire products at discounted prices, and customers can browse and order them through an intuitive web interface.

---

## Features

### For Customers
- Browse discounted, near-expiry products from local supermarkets
- Add products to cart and place orders
- View order history with status tracking (Pending / Confirmed / Cancelled)
- Google OAuth2 login

### Technical Highlights
- **Optimistic locking** – prevents race conditions during inventory updates (overselling)
- **Soft delete** – records are never permanently removed; `deleted_at` timestamp is used throughout
- **JWT authentication** with refresh token support

---

## Project Structure

```
expiry-rescue-app/
├── web-app/          # Frontend – Nuxt 3 (Vue 3)
└── web-server/       # Backend  – Spring Boot 4 (Java 21)
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Frontend | Nuxt 3, Vue 3, JavaScript, Pinia |
| Backend | Spring Boot 4, Java 21, Gradle |
| Database | PostgreSQL, Spring Data JPA / Hibernate |
| Auth | Spring Security, JWT, Google OAuth2 |


---

## 📡 API Overview

| Resource | Base Path |
|---|---|
| Authentication | `POST /api/v1/auth/login` |
| Orders | `/api/v1/orders` |
| Product Inventory | `/api/v1/products/inventory` |
| Product Master | `/api/v1/products/masters` |
| Categories | `/api/v1/product-categories` |
| Supermarkets | `/api/v1/supermarkets` |

---

## Planned Features

- Admin / Staff dashboard for managing inventory, products, and supermarkets
- Role-based access control (Admin / Staff)
- Idempotency filter to prevent duplicate orders from repeated requests
- Firebase Cloud Storage for product image uploads
- Customer feedback and rating system

