# User Service

A **microservice** responsible for managing user accounts, profiles, and authentication details. It provides comprehensive user management APIs including CRUD operations, profile pictures, and user lookups consumed by the API Gateway and Next.js frontend.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Dependencies](#dependencies)
- [Data Model](#data-model)
- [API Endpoints](#api-endpoints)
- [Configuration](#configuration)
- [Setup & Installation](#setup--installation)
- [Building & Running](#building--running)
- [Database Management](#database-management)
- [Service Features](#service-features)
- [Error Handling](#error-handling)
- [Troubleshooting](#troubleshooting)

---

## 🎯 Overview

The **User Service** is a core microservice in the NexaShopping ecosystem. It:

- ✅ Manages **user account** creation, reading, updating, and deletion (CRUD)
- ✅ Handles **user profile** information and metadata
- ✅ Manages **profile picture uploads** and retrieval
- ✅ Provides **user lookup** by ID and retrieve all users
- ✅ Validates **user data** with comprehensive constraints
- ✅ Registers with **Service Registry** (Eureka) for discovery
- ✅ Fetches **configuration** from Config-Server
- ✅ Exposes **health and metrics** via Actuator

**Application Name:** `user-service`  
**Default Port:** `8001`  
**Artifact ID:** `User-Service`  
**Group ID:** `lk.ijse.eca`  
**Database:** PostgreSQL (default port `5432`)

---

## 🏗️ Architecture

The User Service operates within the microservices architecture:

```
┌─────────────────────────────────────────────────────┐
│  Next.js Frontend / API Gateway                     │
│  (requests routed via lb://USER-SERVICE)           │
└────────┬────────────────────────────────────────────┘
         │ HTTP/REST
         ▼
┌─────────────────────────────────────────────────────┐
│  User Service (Port 8001)                           │
│                                                     │
│  ┌─────────────────────────────────────────────┐   │
│  │  REST API Layer (@RestController)           │   │
│  │  - User CRUD operations                     │   │
│  │  - Picture upload/retrieval                 │   │
│  └─────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────┐   │
│  │  Service Layer (Business Logic)             │   │
│  │  - User validation and processing           │   │
│  │  - Picture handling                         │   │
│  │  - DTO ↔ Entity mapping                     │   │
│  └─────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────┐   │
│  │  Repository Layer (JPA)                     │   │
│  │  - Database access and persistence          │   │
│  └─────────────────────────────────────────────┘   │
└────────┬────────────────────────────────────────────┘
         │ JDBC
         ▼
┌─────────────────────────────────────────────────────┐
│  PostgreSQL Database                                │
│  (Schema: NexaShopping)                             │
│  (Table: students - user records)                   │
└─────────────────────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

| Component | Version | Purpose |
|-----------|---------|---------|
| **Java** | JDK 21 | Language runtime |
| **Spring Boot** | 4.0.3 | Application framework |
| **Spring Cloud** | 2025.1.0 | Microservices framework |
| **Spring Data JPA** | Latest | ORM & database abstraction |
| **PostgreSQL Driver** | Latest | Database connectivity |
| **MapStruct** | 1.6.3 | DTO ↔ Entity mapping |
| **Lombok** | Latest | Boilerplate reduction |
| **Spring Validation** | Latest | Bean validation & constraints |
| **Spring AOP** | Latest | Aspect-oriented programming |
| **Netflix Eureka Client** | Latest | Service discovery & registration |
| **Spring Cloud Config Client** | Latest | Centralized configuration |
| **Spring Boot Actuator** | Latest | Health checks & monitoring |
| **Spring Boot DevTools** | Latest (runtime) | Hot reload development |

---

## 📁 Project Structure

```
user-service/
├── src/
│   ├── main/
│   │   ├── java/lk/ijse/eca/userservice/
│   │   │   ├── UserServiceApplication.java        # Main Spring Boot entry point
│   │   │   ├── controller/
│   │   │   │   └── UserController.java            # REST API endpoints
│   │   │   ├── service/
│   │   │   │   ├── UserService.java               # Service interface
│   │   │   │   └── impl/
│   │   │   │       └── UserServiceImpl.java        # Business logic implementation
│   │   │   ├── repository/
│   │   │   │   └── UserRepository.java            # JPA data access
│   │   │   ├── entity/
│   │   │   │   └── User.java                      # JPA entity (mapped to DB)
│   │   │   ├── dto/
│   │   │   │   ├── UserRequestDTO.java            # API request object
│   │   │   │   └── UserResponseDTO.java           # API response object
│   │   │   ├── mapper/
│   │   │   │   └── UserMapper.java                # MapStruct DTO/Entity mapping
│   │   │   ├── exception/
│   │   │   │   └── [Custom exceptions]            # Business exceptions
│   │   │   ├── handler/
│   │   │   │   └── [Error handlers]               # Global exception handling
│   │   │   ├── validation/
│   │   │   │   └── [Custom validators]            # Bean validation constraints
│   │   │   └── aspect/
│   │   │       └── [AOP aspects]                  # Cross-cutting concerns
│   │   └── resources/
│   │       └── application.yaml                   # Service configuration
│   └── test/
├── pom.xml                                        # Maven configuration
├── mvnw / mvnw.cmd                               # Maven wrapper scripts
└── README.md                                      # This file
```

### Key Classes

- **UserServiceApplication.java**: Spring Boot main application class
  - Bootstraps the User Service on port 8001
  - Auto-discovers and configures components

- **UserController.java**: REST API endpoint handler
  - Handles HTTP requests for user operations
  - Validates input and returns responses

- **UserService.java / UserServiceImpl.java**: Business logic
  - Core user management functionality
  - Picture handling and validation

- **User.java**: JPA Entity
  - Mapped to database `students` table
  - Represents user data in application

- **UserRequestDTO / UserResponseDTO**: Data Transfer Objects
  - API request/response contracts
  - Decouples API from internal entity structure

- **UserRepository.java**: Data access layer
  - JPA interface for database operations
  - CRUD methods and custom queries

---

## 📦 Dependencies

### Core Dependencies

1. **spring-boot-starter-data-jpa**
   - Spring Data JPA for database operations
   - Hibernate ORM for persistent layer
   - Query methods and custom repositories

2. **spring-boot-starter-webmvc**
   - Spring Web MVC for REST API handling
   - Request/response processing
   - Content negotiation and serialization

3. **postgresql** (runtime)
   - PostgreSQL JDBC driver
   - Database connectivity

4. **spring-cloud-starter-netflix-eureka-client**
   - Service registration with Eureka
   - Health checks to Service Registry
   - Enables API Gateway discovery

5. **spring-cloud-starter-config**
   - Fetches configuration from Config-Server
   - Externalized properties management
   - Configuration URI: `http://localhost:9000`

6. **spring-boot-starter-validation**
   - Bean validation (Jakarta Validation)
   - Input validation and constraints
   - Custom validators

7. **spring-boot-starter-aspectj**
   - Aspect-oriented programming support
   - Cross-cutting concerns
   - Method interception and logging

8. **spring-boot-starter-actuator**
   - Health endpoints: `/actuator/health`
   - Metrics and monitoring
   - Application info endpoints

9. **mapstruct** (with mapstruct-processor)
   - DTO ↔ Entity mapping
   - Type-safe mapping code generation
   - Version: 1.6.3

10. **lombok**
    - Boilerplate reduction (`@Getter`, `@Setter`, `@AllArgsConstructor`)
    - Annotation processing for cleaner code
    - Logging support (`@Slf4j`)

11. **spring-boot-devtools** (optional, runtime only)
    - Hot reload during development
    - Automatic application restart on code changes

---

## 📊 Data Model

### User Entity

**Table name:** `students`

```sql
CREATE TABLE students (
    nic VARCHAR(50) PRIMARY KEY,        -- User ID (flexible format)
    name VARCHAR(255) NOT NULL,         -- User full name
    address VARCHAR(255) NOT NULL,      -- User address
    mobile VARCHAR(20) NOT NULL,        -- Contact phone number
    email VARCHAR(255),                 -- Email address (optional)
    picture VARCHAR(255) NOT NULL       -- Picture file path
);
```

### Entity Fields

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| **nic** | String (50) | PRIMARY KEY, NOT NULL | User ID / Username |
| **name** | String (255) | NOT NULL | Full name of user |
| **address** | String (255) | NOT NULL | Residential address |
| **mobile** | String (20) | NOT NULL | Phone number |
| **email** | String (255) | Nullable | Email address |
| **picture** | String (255) | NOT NULL | Path to profile picture |

### User Class (Java Entity)

```java
@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(name = "nic", nullable = false, length = 50)
    private String nic;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "address", nullable = false)
    private String address;
    
    @Column(name = "mobile", nullable = false)
    private String mobile;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "picture", nullable = false)
    private String picture;
}
```

---

## 🔌 API Endpoints

The User Service exposes RESTful API endpoints at the base path `/api/v1/users`.

### Endpoint Overview

| Method | Endpoint | Description | Status Codes |
|--------|----------|-------------|--------------|
| **POST** | `/api/v1/users` | Create a new user | 201 Created, 400 Bad Request, 409 Conflict |
| **GET** | `/api/v1/users` | Retrieve all users | 200 OK |
| **GET** | `/api/v1/users/{nic}` | Get user by ID | 200 OK, 404 Not Found |
| **PUT** | `/api/v1/users/{nic}` | Update user | 200 OK, 400 Bad Request, 404 Not Found |
| **DELETE** | `/api/v1/users/{nic}` | Delete user | 204 No Content, 404 Not Found |
| **GET** | `/api/v1/users/{nic}/picture` | Download profile picture | 200 OK, 404 Not Found |

### Detailed Endpoint Documentation

#### 1. Create User
**POST** `/api/v1/users`

**Content-Type:** `multipart/form-data`

**Request Fields:**

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `nic` | String | Yes | Any string (flexible format, up to 50 chars) |
| `name` | String | Yes | Non-empty, allows letters and spaces |
| `address` | String | Yes | Non-empty |
| `mobile` | String | Yes | Valid format |
| `email` | String | No | Valid email if provided |
| `picture` | File | Yes | Image file (JPEG, PNG), max 5MB |

**Example Request:**
```bash
curl -X POST http://localhost:8001/api/v1/users \
  -F "nic=USER001" \
  -F "name=John Doe" \
  -F "address=123 Main St, City" \
  -F "mobile=0771234567" \
  -F "email=john@example.com" \
  -F "picture=@path/to/picture.jpg"
```

**Response (201 Created):**
```json
{
  "nic": "USER001",
  "name": "John Doe",
  "address": "123 Main St, City",
  "mobile": "0771234567",
  "email": "john@example.com",
  "picture": "/api/v1/users/USER001/picture"
}
```

**Error Response:**
```json
{
  "timestamp": "2024-03-19T10:30:00Z",
  "status": 400,
  "error": "Validation Failed",
  "message": "Email format is invalid"
}
```

#### 2. Get All Users
**GET** `/api/v1/users`

**Response (200 OK):**
```json
[
  {
    "nic": "USER001",
    "name": "John Doe",
    "address": "123 Main St, City",
    "mobile": "0771234567",
    "email": "john@example.com",
    "picture": "/api/v1/users/USER001/picture"
  },
  {
    "nic": "USER002",
    "name": "Jane Smith",
    "address": "456 Oak Ave, Town",
    "mobile": "0779876543",
    "email": "jane@example.com",
    "picture": "/api/v1/users/USER002/picture"
  }
]
```

#### 3. Get User by ID
**GET** `/api/v1/users/{nic}`

**Path Parameters:**
- `nic` (string): The user ID to retrieve

**Response (200 OK):**
```json
{
  "nic": "USER001",
  "name": "John Doe",
  "address": "123 Main St, City",
  "mobile": "0771234567",
  "email": "john@example.com",
  "picture": "/api/v1/users/USER001/picture"
}
```

**Error Response (404 Not Found):**
```json
{
  "timestamp": "2024-03-19T10:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "User with ID USER999 not found"
}
```

#### 4. Update User
**PUT** `/api/v1/users/{nic}`

**Content-Type:** `multipart/form-data`

**Path Parameters:**
- `nic` (string): The user ID to update

**Request Fields:** (same as create, but all optional)

**Example Request:**
```bash
curl -X PUT http://localhost:8001/api/v1/users/USER001 \
  -F "name=John Doe Updated" \
  -F "email=newemail@example.com" \
  -F "picture=@path/to/new-picture.jpg"
```

**Response (200 OK):**
```json
{
  "nic": "USER001",
  "name": "John Doe Updated",
  "address": "123 Main St, City",
  "mobile": "0771234567",
  "email": "newemail@example.com",
  "picture": "/api/v1/users/USER001/picture"
}
```

#### 5. Delete User
**DELETE** `/api/v1/users/{nic}`

**Path Parameters:**
- `nic` (string): The user ID to delete

**Response (204 No Content):**
```
(No body)
```

#### 6. Get User Picture
**GET** `/api/v1/users/{nic}/picture`

**Path Parameters:**
- `nic` (string): The user ID

**Response (200 OK):**
```
Content-Type: image/jpeg
[Binary image data]
```

**Error Response (404 Not Found):**
```
User or picture not found
```

---

## ⚙️ Configuration

### application.yaml

```yaml
spring:
  application:
    name: user-service                  # Service name registered with Eureka

  config:
    import: "configserver:"             # Enable Config-Server integration

  cloud:
    config:
      uri: http://localhost:9000        # Config-Server location
      
  # Database configuration (typically from Config-Server)
  datasource:
    url: jdbc:postgresql://localhost:5432/nexashopping  # PostgreSQL connection
    username: postgres                  # Database user
    password: postgres                  # Database password
    
  jpa:
    hibernate:
      ddl-auto: validate                # Don't auto-create schema
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        use_sql_comments: true
    show-sql: false

server:
  port: 8001                            # Service runs on port 8001

management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info    # Expose actuator endpoints
  endpoint:
    health:
      show-details: when-authorized     # Show detailed health info
```

### Environment-Specific Profiles

**application-dev.yaml** (Development)
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update                  # Auto-update schema in dev
    show-sql: true                      # Show SQL queries

logging:
  level:
    root: INFO
    lk.ijse.eca: DEBUG                  # Debug logs for our code
```

---

## 🚀 Setup & Installation

### Prerequisites

- **Java 21** (or compatible version)
- **Maven 3.6+** (or use `mvnw` wrapper)
- **PostgreSQL 14+** running on port 5432
- **Config-Server** running on port 9000
- **Service-Registry** running on port 9001

### PostgreSQL Setup

**1. Create database:**
```sql
CREATE DATABASE nexashopping;
```

**2. Create user table:**
```sql
CREATE TABLE students (
    nic VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    mobile VARCHAR(20) NOT NULL,
    email VARCHAR(255),
    picture VARCHAR(255) NOT NULL
);
```

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd NexaShopping-Project/services/user-service
   ```

2. **Verify Java version**
   ```bash
   java -version
   ```

3. **Build the project**
   ```bash
   ./mvnw clean install
   ```

4. **Verify dependencies**
   ```bash
   ./mvnw dependency:tree
   ```

---

## ▶️ Building & Running

### Option 1: Using Maven (Recommended)

**Build:**
```bash
./mvnw clean package
```

**Run:**
```bash
./mvnw spring-boot:run
```

### Option 2: Run JAR Directly

**After building:**
```bash
./mvnw clean package
java -jar target/User-Service-1.0.0.jar
```

### Option 3: IDE Run (IntelliJ/Eclipse)

1. Right-click `UserServiceApplication.java`
2. Select **Run** or **Debug**

### Startup Sequence

**Critical: Services must start in this order:**

1. **PostgreSQL Database** (ensure running on port 5432)
   ```bash
   # On Windows with PostgreSQL installed
   pg_ctl start -D "C:\Program Files\PostgreSQL\data"
   ```

2. **Config-Server** (port 9000)
   ```bash
   cd ../../platform/config-server
   ./mvnw spring-boot:run
   ```

3. **Service-Registry** (port 9001)
   ```bash
   cd ../service-registry
   ./mvnw spring-boot:run
   ```

4. **API-Gateway** (port 7000)
   ```bash
   cd ../api-gateway
   ./mvnw spring-boot:run
   ```

5. **User-Service** (port 8001) ⭐
   ```bash
   cd ../../services/user-service
   ./mvnw spring-boot:run
   ```

6. **Item-Service** and **Order-Service** (same pattern)

### Verification

Once started, verify the service is running:

```bash
# Check service health
curl http://localhost:8001/actuator/health

# Check Eureka registration
curl http://localhost:9001/eureka/apps/USER-SERVICE
```

---

## 🗄️ Database Management

### Creating Tables

```sql
CREATE TABLE students (
    nic VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    mobile VARCHAR(20) NOT NULL,
    email VARCHAR(255),
    picture VARCHAR(255) NOT NULL
);

CREATE INDEX idx_user_email ON students(email);
CREATE INDEX idx_user_name ON students(name);
```

### Sample Data

```sql
INSERT INTO students (nic, name, address, mobile, email, picture) VALUES
('USER001', 'John Doe', '123 Main St', '0771234567', 'john@example.com', '/path/to/pic1.jpg'),
('USER002', 'Jane Smith', '456 Oak Ave', '0779876543', 'jane@example.com', '/path/to/pic2.jpg'),
('USER003', 'Bob Johnson', '789 Pine Rd', '0771111111', 'bob@example.com', '/path/to/pic3.jpg');
```

### Backup and Restore

**Backup:**
```bash
pg_dump nexashopping > backup.sql
```

**Restore:**
```bash
psql nexashopping < backup.sql
```

---

## ✨ Service Features

### Picture Handling
- Uploads profile pictures during user creation/update
- Stores pictures on filesystem
- Serve pictures via `/api/v1/users/{nic}/picture` endpoint
- Returns image as JPEG binary data

### Data Validation
- Username/ID uniqueness checking
- Email format validation
- Phone number format validation
- File size restrictions for pictures
- Custom validation groups for different operations

### Error Handling
- Global exception handler for consistent error responses
- Proper HTTP status codes (400, 404, 409, etc.)
- Descriptive error messages
- Validation error details

### Logging
- SLF4J logging with Logback
- Request/response logging in controller
- Business logic logging in service layer
- Database query logging (configurable)

### DTOs and Mapping
- **UserRequestDTO**: API request input validation
- **UserResponseDTO**: Formatted API responses
- **UserMapper**: Type-safe MapStruct mapping
  - Automatic DTO ↔ Entity conversion
  - Custom mapping logic support
  - Null handling strategies

---

## 🐛 Error Handling

### Common HTTP Status Codes

| Code | Scenario |
|------|----------|
| **201** | User created successfully |
| **200** | Request successful (GET, PUT) |
| **204** | Resource deleted (DELETE) |
| **400** | Bad request / validation error |
| **404** | User not found |
| **409** | User already exists (conflict) |
| **500** | Server error |

### Error Response Format

```json
{
  "timestamp": "2024-03-19T10:30:00Z",
  "status": 400,
  "error": "Validation Failed",
  "message": "Email format is invalid",
  "path": "/api/v1/users"
}
```

---

## 🐛 Troubleshooting

### Issue 1: Port 8001 Already in Use

**Error:** `Address already in use: 8001`

**Solution:**
```bash
# Find process using port 8001
netstat -ano | findstr :8001

# Kill the process
taskkill /PID <PID> /F

# Or change port in config
server:
  port: 8002
```

### Issue 2: Cannot Connect to PostgreSQL

**Error:** `Connection refused / Unable to connect to database`

**Solutions:**
1. Verify PostgreSQL is running:
   ```bash
   psql -U postgres -c "SELECT 1"
   ```
2. Check database credentials in application.yaml
3. Verify database exists:
   ```bash
   psql -U postgres -l
   ```
4. Check database URL format is correct

### Issue 3: Service Not Registering with Eureka

**Error:** Dashboard shows no USER-SERVICE

**Solutions:**
1. Ensure Service-Registry is running on port 9001
2. Verify Eureka URL in configuration
3. Check service logs for registration errors
4. Restart the service

### Issue 4: Database Migrations Failed

**Error:** `Liquibase / Flyway validation error`

**Solutions:**
1. Ensure tables exist with `ddl-auto: validate`
2. Check table schema matches entity definitions
3. Run migration scripts manually if needed

### Common Log Messages

| Log | Meaning | Action |
|-----|---------|--------|
| `Registered instance with Eureka` | ✅ Service registered | Ready to receive requests |
| `Failed to register instance with Eureka` | ❌ Error | Check Eureka connection |
| `Database table 'students' not found` | ⚠️ Warning | Create table in DB |
| `User not found: USER999` | ℹ️ Info | Normal 404 response |

---

## 📊 Performance Considerations

- **Response Time**: Picture uploads may take time based on file size
- **Database Indexing**: Create indexes on frequently queried columns (email, name)
- **Picture Storage**: Consider separate file storage system for production
- **Connection Pooling**: HikariCP configured automatically for optimal performance

---

## 📚 References

- [Spring Data JPA Documentation](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring Boot REST API Guide](https://spring.io/guides/gs/rest-service/)
- [MapStruct User Guide](https://mapstruct.org/documentation/stable/reference/html/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Jakarta Validation](https://jakarta.ee/specifications/bean-validation/)

---

## 📝 Notes

- User Service is **stateless** - can be horizontally scaled
- Picture handling should migrate to cloud storage (S3/GCS) for production
- Service automatically recovers from database connection failures
- Configuration is externalized via Config-Server for easy environment changes

---

## 🤝 Support

For issues or questions:
1. Check service logs for error details
2. Verify all prerequisites are running (PostgreSQL, Config-Server, Service-Registry)
3. Test API endpoints using curl or Postman
4. Check database tables exist and have correct schema
5. Verify Eureka registration in dashboard
