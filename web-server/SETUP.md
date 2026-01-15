# Expiry Rescue App - Backend Setup Guide

## Prerequisites

1. **Java 21** - Make sure Java 21 is installed
2. **PostgreSQL** - PostgreSQL database server running
3. **Firebase Account** - For image storage (optional for initial testing)

## Database Setup

### 1. Create PostgreSQL Database

```sql
CREATE DATABASE expiry_rescue_db;
```

### 2. Update Database Credentials

Edit [application.properties](src/main/resources/application.properties) and update:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/expiry_rescue_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_ACTUAL_PASSWORD
```

## Firebase Setup (Optional)

### 1. Get Firebase Service Account Key

1. Go to Firebase Console: https://console.firebase.google.com/
2. Select your project or create a new one
3. Go to Project Settings > Service Accounts
4. Click "Generate New Private Key"
5. Download the JSON file

### 2. Configure Firebase

1. Replace the placeholder file at `src/main/resources/firebase-service-account.json` with your actual Firebase service account JSON
2. Update the storage bucket in [application.properties](src/main/resources/application.properties):

```properties
firebase.storage.bucket=your-actual-project-id.appspot.com
```

**Note:** If you skip Firebase setup, the application will log a warning but will still start. Product image upload endpoints will fail until Firebase is properly configured.

## Security Configuration

### JWT Secret Key

Update the JWT secret in [application.properties](src/main/resources/application.properties):

```properties
jwt.secret=YOUR_STRONG_256_BIT_SECRET_KEY_HERE_CHANGE_THIS_IN_PRODUCTION
```

**Important:** Use a strong random secret key in production (at least 256 bits / 32 characters).

## Build and Run

### 1. Build the Application

```bash
./gradlew clean build
```

### 2. Run the Application

```bash
./gradlew bootRun
```

Or run the JAR file:

```bash
java -jar build/libs/expiry-rescue-app-0.0.1-SNAPSHOT.jar
```

The application will start on **http://localhost:8080**

## Default Accounts

The application automatically creates two default accounts on first startup:

| Username | Password | Role |
|----------|----------|------|
| admin    | Admin@123 | ADMIN |
| staff    | Staff@123 | STAFF |

**Important:** Change these default passwords in production!

## API Testing

### 1. Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "Admin@123"
  }'
```

Response:
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "expiresIn": 86400000,
    "staffInfo": {
      "id": "...",
      "username": "admin",
      "role": "ADMIN",
      "fullName": "System Administrator",
      "isActive": true
    }
  },
  "timestamp": "2025-11-28T..."
}
```

### 2. Test Authenticated Endpoint

Copy the token from login response and use it:

```bash
curl -X GET http://localhost:8080/api/v1/staffs \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## Available API Endpoints

### Authentication (Public)
- `POST /api/v1/auth/login` - Login
- `POST /api/v1/auth/refresh` - Refresh token

### Staff Management (Admin Only)
- `POST /api/v1/staffs` - Create staff
- `GET /api/v1/staffs` - List all staff
- `GET /api/v1/staffs/{id}` - Get staff by ID
- `PUT /api/v1/staffs/{id}` - Update staff
- `DELETE /api/v1/staffs/{id}` - Delete staff
- `PUT /api/v1/staffs/{id}/restore` - Restore deleted staff

### Supermarket Management (Authenticated)
- `POST /api/v1/supermarkets` - Create supermarket
- `GET /api/v1/supermarkets` - List all supermarkets
- `GET /api/v1/supermarkets/{id}` - Get supermarket by ID
- `PUT /api/v1/supermarkets/{id}` - Update supermarket
- `DELETE /api/v1/supermarkets/{id}` - Delete supermarket

### Product Category Management (Authenticated)
- `POST /api/v1/products/categories` - Create category
- `GET /api/v1/products/categories` - List all categories
- `GET /api/v1/products/categories/{id}` - Get category by ID
- `PUT /api/v1/products/categories/{id}` - Update category
- `DELETE /api/v1/products/categories/{id}` - Delete category

### Product Master Management (Authenticated)
- `POST /api/v1/products/masters` - Create product master
- `GET /api/v1/products/masters` - List all product masters
- `GET /api/v1/products/masters/{id}` - Get product master by ID
- `GET /api/v1/products/masters/category/{categoryId}` - Get by category
- `PUT /api/v1/products/masters/{id}` - Update product master
- `DELETE /api/v1/products/masters/{id}` - Delete product master

### Product Inventory Management (Authenticated)
- `POST /api/v1/products/inventory` - Create inventory item
- `GET /api/v1/products/inventory` - List all inventory
- `GET /api/v1/products/inventory/{id}` - Get inventory by ID
- `GET /api/v1/products/inventory/supermarket/{supermarketId}` - Get by supermarket
- `GET /api/v1/products/inventory/status/{status}` - Get by status
- `GET /api/v1/products/inventory/expiring?daysBeforeExpiry=7` - Get expiring items
- `PUT /api/v1/products/inventory/{id}` - Update inventory
- `PUT /api/v1/products/inventory/{id}/mark-unavailable` - Mark as unavailable
- `DELETE /api/v1/products/inventory/{id}` - Delete inventory

## Database Schema

The application uses JPA/Hibernate to auto-generate the database schema. Tables will be created automatically on first startup:

- `staffs` - Staff accounts
- `users` - Customer accounts (for future use)
- `supermarkets` - Supermarket information
- `product_categories` - Product categories
- `product_master` - Product master data
- `product_inventory` - Product inventory with expiry dates
- `orders` - Customer orders (for future use)
- `order_items` - Order line items (for future use)
- `feedbacks` - Customer feedback (for future use)

All tables include:
- `id` - UUID primary key
- `created_at` - Auto-populated creation timestamp
- `updated_at` - Auto-updated modification timestamp
- `deleted_at` - Soft delete timestamp (null = active)

## Troubleshooting

### Firebase Initialization Warning

If you see this warning:
```
Failed to initialize Firebase: firebase-service-account.json not found
```

This is expected if you haven't set up Firebase yet. The application will still work, but image upload endpoints will fail.

### Database Connection Error

If you see:
```
org.postgresql.util.PSQLException: Connection refused
```

Make sure:
1. PostgreSQL is running
2. Database `expiry_rescue_db` exists
3. Database credentials in `application.properties` are correct

### JWT Token Errors

If authentication fails with "Invalid token":
1. Check that the JWT secret is properly configured
2. Ensure the token hasn't expired (24-hour expiration)
3. Use the refresh token endpoint to get a new token

## Production Deployment Checklist

Before deploying to production:

- [ ] Change default admin/staff passwords
- [ ] Update JWT secret to a strong random key
- [ ] Update database credentials
- [ ] Configure proper Firebase service account
- [ ] Change `spring.jpa.hibernate.ddl-auto` to `validate` or use migrations
- [ ] Set up proper logging configuration
- [ ] Configure CORS for your frontend domain
- [ ] Enable HTTPS
- [ ] Set up database backups
- [ ] Review and update security configurations

## Tech Stack

- **Framework:** Spring Boot 4.0.0
- **Language:** Java 21
- **Build Tool:** Gradle 9.2.1
- **Database:** PostgreSQL with JPA/Hibernate
- **Security:** Spring Security 6.x with JWT
- **Storage:** Firebase Cloud Storage
- **Architecture:** 3-Layer (Controller → Service → Business → Repository)
