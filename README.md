# Moyo Order Management System

> A production-ready Order Management System implementing intelligent vendor allocation, real-time inventory management, and secure authentication.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## Overview

This Order Management System (OMS) is a backend implementation of Moyo's case study for managing multi-vendor product ordering. The system intelligently allocates orders to vendors based on real-time pricing and inventory availability, ensuring optimal vendor selection for each order.

Built as part of a software development case study, this project demonstrates enterprise-level Java development practices, RESTful API design, security implementation, and comprehensive testing strategies.

### Case Study Context

This implementation represents the **Order Management System** component from Moyo's "Online Order Solution" case study (Java Focused Stream). The system serves as the central hub that:
- Manages vendor authentication and authorization
- Enables vendors to configure pricing and inventory
- Implements intelligent order allocation algorithms
- Provides real-time order processing and status updates

## Key Features

### Core Functionality
- **Intelligent Vendor Allocation**: Automatically selects the optimal vendor based on price and stock availability
- **Real-Time Inventory Management**: Vendors can update stock levels and pricing in real-time
- **Secure Authentication**: JWT-based authentication with Spring Security
- **RESTful API Design**: Clean, well-documented REST endpoints with OpenAPI/Swagger
- **Order Processing**: Complete order lifecycle management from creation to fulfillment

### Technical Highlights
- **Production-Ready Architecture**: Clean layered architecture with separation of concerns
- **Comprehensive Error Handling**: Global exception handling with meaningful error responses
- **API Documentation**: Interactive Swagger UI for API exploration and testing
- **Database Integration**: JPA/Hibernate with H2 database (production-ready schema)
- **Docker Support**: Containerized deployment with health checks
- **Security**: JWT token-based authentication with role-based access control

## Technology Stack

### Backend Framework
- **Java 17** - Latest LTS version with modern language features
- **Spring Boot 3.5.9** - Enterprise-grade framework
- **Spring Security** - Comprehensive security framework
- **Spring Data JPA** - Data persistence layer
- **Hibernate** - ORM implementation

### Security & Authentication
- **JWT (JSON Web Tokens)** - Stateless authentication
- **jjwt 0.12.6** - JWT implementation library
- **Spring Security** - Authorization and authentication

### Database
- **H2 Database** - In-memory database for development/demo
- **Spring Data JPA** - Repository abstraction
- **Hibernate Validation** - Input validation

### API Documentation
- **SpringDoc OpenAPI 2.8.15** - OpenAPI 3.0 specification
- **Swagger UI** - Interactive API documentation

### Development Tools
- **Lombok** - Reduces boilerplate code
- **Maven** - Dependency management and build tool
- **Docker** - Containerization

### Testing
- **Spring Boot Test** - Integration testing framework
- **Spring Security Test** - Security testing utilities
- **JUnit 5** - Unit testing framework

## Architecture

### System Design

```
┌─────────────────────────────────────────────────────────┐
│                    REST API Layer                        │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐        │
│  │   Auth     │  │  Vendor    │  │   Order    │        │
│  │Controller  │  │ Controller │  │ Controller │        │
│  └────────────┘  └────────────┘  └────────────┘        │
└─────────────────────────────────────────────────────────┘
                         │
┌─────────────────────────────────────────────────────────┐
│                   Service Layer                          │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐        │
│  │   Auth     │  │  Vendor    │  │   Order    │        │
│  │  Service   │  │  Service   │  │  Service   │        │
│  └────────────┘  └────────────┘  └────────────┘        │
└─────────────────────────────────────────────────────────┘
                         │
┌─────────────────────────────────────────────────────────┐
│                 Repository Layer                         │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐        │
│  │   Vendor   │  │  Product   │  │   Order    │        │
│  │ Repository │  │ Repository │  │ Repository │        │
│  └────────────┘  └────────────┘  └────────────┘        │
└─────────────────────────────────────────────────────────┘
                         │
┌─────────────────────────────────────────────────────────┐
│                    Data Layer                            │
│              H2 In-Memory Database                       │
│   Vendors | Products | VendorProducts | Orders          │
└─────────────────────────────────────────────────────────┘
```

### Key Architecture Decisions

1. **Layered Architecture**: Clear separation between controllers, services, and repositories
2. **DTO Pattern**: Separate DTOs for requests/responses to decouple API from domain models
3. **Repository Pattern**: Spring Data JPA repositories for data access abstraction
4. **Global Exception Handling**: Centralized exception handling using @ControllerAdvice
5. **JWT Stateless Authentication**: Scalable authentication without server-side sessions

## Getting Started

### Prerequisites

- **Java 17 or higher** ([Download](https://adoptium.net/))
- **Maven 3.6+** ([Download](https://maven.apache.org/download.cgi))
- **Docker** (optional, for containerized deployment)

### Quick Start (5 Minutes)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd "Moyo Order Management System"
   ```

2. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

3. **Verify it's running**
   - Health check: http://localhost:8080/api/health
   - Swagger UI: http://localhost:8080/swagger-ui.html

4. **Test the API**
   - Open Swagger UI
   - Use the Auth endpoint to login:
     ```json
     {
       "username": "vendor-a",
       "password": "password123"
     }
     ```
   - Copy the JWT token from the response
   - Click "Authorize" button and paste the token
   - Explore other endpoints

### Detailed Setup

See [START_GUIDE.md](START_GUIDE.md) for comprehensive startup instructions including troubleshooting.

## API Documentation

### Interactive API Docs
Access the Swagger UI at: **http://localhost:8080/swagger-ui.html**

### Key Endpoints

#### Authentication
```
POST /api/auth/login         - Authenticate and receive JWT token
```

#### Vendor Management
```
GET  /api/vendors            - List all vendors (authenticated)
GET  /api/vendors/{id}       - Get vendor details
GET  /api/vendors/{id}/products - Get vendor's products with pricing
POST /api/vendors/{id}/products/{productId}/price - Update product price
POST /api/vendors/{id}/products/{productId}/stock - Update product stock
```

#### Order Management
```
POST /api/orders             - Create new order (allocates to best vendor)
GET  /api/orders/{id}        - Get order details
GET  /api/orders             - List all orders
```

#### System Health
```
GET  /api/health             - Health check endpoint
```

### Authentication Flow

1. **Login**: POST to `/api/auth/login` with credentials
2. **Receive JWT**: Get token from response
3. **Authorize**: In Swagger, click "Authorize" and enter: `Bearer <your-token>`
4. **Access Protected Endpoints**: All other endpoints require authentication

### Sample Users

| Username | Password | Role |
|----------|----------|------|
| vendor-a | password123 | VENDOR |
| vendor-b | password123 | VENDOR |
| vendor-c | password123 | VENDOR |

## Key Features Deep Dive

### 1. Intelligent Order Allocation

The system implements a sophisticated vendor selection algorithm:

```
When an order is created:
1. Fetch all vendors selling the requested product
2. Filter vendors with sufficient stock
3. Select vendor with lowest price
4. Allocate order to selected vendor
5. Deduct stock automatically
6. Return order confirmation with vendor details
```

**Implementation**: `OrderService.createOrder()` at [src/main/java/com/moyo/oms/service/OrderService.java](src/main/java/com/moyo/oms/service/OrderService.java)

### 2. Real-Time Inventory Management

Vendors can update their inventory in real-time:
- **Stock Updates**: Immediately reflected in order allocation
- **Price Updates**: Dynamic pricing per vendor per product
- **Concurrent Safety**: Transaction management prevents race conditions

### 3. Security Implementation

Multi-layer security approach:
- **JWT Authentication**: Stateless, scalable token-based auth
- **Password Encryption**: BCrypt hashing (configured for production use)
- **Role-Based Access**: Spring Security with custom UserDetails
- **Public Endpoints**: Health check and Swagger accessible without auth

**Security Config**: [src/main/java/com/moyo/oms/config/SecurityConfig.java](src/main/java/com/moyo/oms/config/SecurityConfig.java)

## Testing

### Manual Testing

Comprehensive testing documentation is provided:

1. **Quick Start Test** (5 minutes): [QUICK_START_TEST.md](QUICK_START_TEST.md)
2. **Complete Test Suite** (30-60 minutes): [MANUAL_TESTING_CHECKLIST.md](MANUAL_TESTING_CHECKLIST.md)
3. **Testing Guide**: [TESTING_README.md](TESTING_README.md)
4. **Results Template**: [TEST_RESULTS_TEMPLATE.md](TEST_RESULTS_TEMPLATE.md)

### Test Coverage

The project includes test cases for:
- ✅ Authentication flow (login, token validation)
- ✅ Vendor operations (list, view, update pricing/stock)
- ✅ Order creation with vendor allocation
- ✅ Inventory management and stock deduction
- ✅ Error handling (insufficient stock, invalid products)
- ✅ Security (unauthorized access, token expiration)

## Docker Deployment

### Build and Run with Docker

```bash
# Build and start
docker-compose up --build

# Run in background
docker-compose up -d

# View logs
docker-compose logs -f

# Stop
docker-compose down
```

### Container Health Check

The Docker container includes health checks:
```bash
docker ps
# Look for "healthy" status
```

Access the application at: http://localhost:8080

## Database Schema

### Entity Relationship

```
┌─────────────┐         ┌──────────────────┐         ┌─────────────┐
│   Vendor    │         │  VendorProduct   │         │   Product   │
├─────────────┤         ├──────────────────┤         ├─────────────┤
│ id (PK)     │◄───────┤│ vendor_id (FK)   │         │ id (PK)     │
│ username    │         │ product_id (FK)  │────────►│ name        │
│ password    │         │ price            │         │ description │
│ name        │         │ stock            │         └─────────────┘
│ email       │         └──────────────────┘
└─────────────┘                  ▲
       ▲                         │
       │                         │
       │         ┌──────────────────┐
       │         │     Order        │
       │         ├──────────────────┤
       └────────┤│ vendor_id (FK)   │
                 │ product_id (FK)  │
                 │ quantity         │
                 │ total_price      │
                 │ status           │
                 │ created_at       │
                 └──────────────────┘
```

### Database Access

Access H2 Console at: http://localhost:8080/h2-console

**Connection Settings:**
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

## Project Structure

```
moyo-oms/
├── src/
│   ├── main/
│   │   ├── java/com/moyo/oms/
│   │   │   ├── config/           # Security, Swagger configuration
│   │   │   ├── controller/       # REST API endpoints
│   │   │   ├── dto/              # Request/Response DTOs
│   │   │   ├── entity/           # JPA entities
│   │   │   ├── exception/        # Custom exceptions & handlers
│   │   │   ├── repository/       # Spring Data repositories
│   │   │   ├── security/         # JWT filters, UserDetails
│   │   │   ├── service/          # Business logic
│   │   │   └── MoyoOmsApplication.java
│   │   └── resources/
│   │       ├── application.yml   # Application configuration
│   │       └── data.sql          # Sample data initialization
│   └── test/                     # Unit and integration tests
├── docs/                         # Additional documentation
├── docker-compose.yml            # Docker composition
├── Dockerfile                    # Container definition
├── pom.xml                       # Maven dependencies
├── START_GUIDE.md               # Detailed startup guide
├── QUICK_START_TEST.md          # Quick testing guide
├── MANUAL_TESTING_CHECKLIST.md  # Comprehensive test cases
├── TESTING_README.md            # Testing documentation
└── README.md                    # This file
```

## Configuration

### Application Properties

Key configurations in `application.yml`:

```yaml
server:
  port: 8080                    # Server port

spring:
  datasource:
    url: jdbc:h2:mem:testdb    # H2 in-memory database
  jpa:
    hibernate:
      ddl-auto: create-drop     # Auto-create schema
  h2:
    console:
      enabled: true             # Enable H2 console

jwt:
  secret: <your-secret-key>     # JWT signing key
  expiration: 86400000          # Token expiration (24h)
```

## Development

### Building from Source

```bash
# Clean and build
mvn clean install

# Build without tests
mvn clean install -DskipTests

# Run tests only
mvn test

# Package as JAR
mvn clean package
```

### Running the JAR

```bash
java -jar target/moyo-oms-0.0.1-SNAPSHOT.jar
```

## Production Considerations

### What's Included
✅ Production-ready architecture with layered design
✅ Comprehensive error handling and validation
✅ Security with JWT authentication
✅ API documentation with Swagger
✅ Docker containerization
✅ Health check endpoints
✅ Transaction management
✅ Logging configuration

### Production Enhancements Needed
🔄 Replace H2 with production database (PostgreSQL/MySQL)
🔄 Externalize configuration (Spring Cloud Config)
🔄 Add rate limiting and API throttling
🔄 Implement caching layer (Redis)
🔄 Add monitoring and metrics (Actuator, Prometheus)
🔄 Implement asynchronous processing (message queues)
🔄 Add comprehensive integration tests
🔄 Setup CI/CD pipeline
🔄 Implement audit logging
🔄 Add API versioning

## Future Enhancements

### Planned Features
- **Frontend Application**: React/Angular client portal
- **Asynchronous Communication**: Message queue integration (RabbitMQ/Kafka)
- **Product Management System**: Separate microservice for product catalog
- **Advanced Reporting**: Order analytics and vendor performance metrics
- **Notification System**: Email/SMS notifications for order updates
- **Multi-tenancy**: Support for multiple organizations
- **Payment Integration**: Payment gateway integration
- **Advanced Allocation**: ML-based vendor selection considering delivery time, ratings, etc.

### Scalability Improvements
- Microservices architecture
- Event-driven design
- CQRS pattern for read/write separation
- Database sharding for horizontal scaling
- CDN integration for static content

## Troubleshooting

### Common Issues

**Port 8080 already in use**
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Change port in application.yml
server.port: 8081
```

**Application fails to start**
```bash
# Clear Maven cache and rebuild
mvn clean install -U
```

See [START_GUIDE.md](START_GUIDE.md) for comprehensive troubleshooting.

## Contributing

This project was developed as a case study demonstration. Suggestions and improvements are welcome:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/improvement`)
3. Commit changes (`git commit -am 'Add new feature'`)
4. Push to branch (`git push origin feature/improvement`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact & Links

**Developer**: [Your Name]
**Email**: [Your Email]
**LinkedIn**: [Your LinkedIn Profile]
**Portfolio**: [Your Portfolio Website]

## Acknowledgments

- **Moyo** - For the comprehensive case study and opportunity
- **Spring Framework Team** - For the excellent documentation and framework
- **Community** - For open-source libraries and tools

---

**Project Status**: ✅ Fully Functional | 🚀 Production-Ready Architecture | 📚 Comprehensive Documentation

*Built with passion to demonstrate enterprise Java development capabilities*
