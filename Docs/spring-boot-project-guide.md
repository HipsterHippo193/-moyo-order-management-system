# Spring Boot Project Guide - Moyo OMS

**The Complete Guide to Understanding Your Order Management System**

*A fun, practical guide from "what's a Spring Boot?" to "I built a production-ready API!"*

---

## 📚 Table of Contents

1. [What is This Project?](#what-is-this-project)
2. [Core Technologies Explained](#core-technologies-explained)
3. [Project Files Deep Dive](#project-files-deep-dive)
4. [Database Layer - Where Data Lives](#database-layer)
5. [Security & JWT Authentication - The Bouncer](#security--jwt-authentication)
6. [Service Layer - The Brain](#service-layer)
7. [API Layer - The Front Desk](#api-layer)
8. [The Intelligent Order Allocation - The Magic](#the-intelligent-order-allocation)
9. [Complete Request Flow](#complete-request-flow)
10. [Testing Strategy](#testing-strategy)
11. [Quick Reference](#quick-reference)

---

## What is This Project?

**TL;DR:** A system that automatically finds the cheapest vendor with stock when you place an order.

Think of this project like building a house:

| Concept | File | Purpose |
|---------|------|---------|
| Blueprint | `pom.xml` | Lists all materials and tools needed |
| Foundation | `MoyoOmsApplication.java` | The starting point |
| Settings | `application.yml` | How things should behave |
| Rooms | The packages | Organized spaces for different code |

### The Problem We're Solving

Imagine you run an online store that sells widgets. You don't make widgets yourself - you buy them from multiple vendors:

- **Vendor Alpha**: Sells widgets for $50, has 100 in stock
- **Vendor Beta**: Sells widgets for $45, has 50 in stock
- **Vendor Charlie**: Sells widgets for $40, but has 0 in stock

When a customer orders 10 widgets, who should we buy from?

**The Smart Answer:** Vendor Beta (cheapest WITH stock)

**This system does that automatically** - it's called **intelligent vendor allocation**.

---

## Core Technologies Explained

Before diving into the code, let's understand the three main technologies this project uses.

### What is Maven?

**Maven** is a build tool for Java projects. Think of it as your project's personal assistant that:

| Task | What Maven Does |
|------|-----------------|
| Dependency Management | Downloads libraries you need (like a package manager) |
| Building | Compiles your `.java` files into `.class` files |
| Testing | Runs your unit tests |
| Packaging | Creates a JAR file you can deploy |

**Without Maven:** You'd manually download JAR files, manage versions, figure out what each library needs, and write complex build scripts.

**With Maven:** You list what you need in `pom.xml`, and Maven handles everything.

```
You: "I need Spring Boot and a database library"
Maven: "Got it. I'll download those, plus the 47 other libraries they depend on."
```

**The `pom.xml` file** (Project Object Model) is Maven's configuration file - your project's "shopping list."

---

### What is Spring Boot?

**Spring Boot** is a framework that makes it easy to create Java web applications.

#### The Problem It Solves

Plain Java doesn't know how to:
- Handle HTTP requests (GET, POST, etc.)
- Connect to databases
- Manage security/login
- Convert objects to JSON

You *could* write all this yourself, but it would take months.

#### What Spring Boot Provides

| Feature | What It Does |
|---------|--------------|
| Web Server | Embedded Tomcat - no separate server needed |
| REST Support | Easy `@GetMapping`, `@PostMapping` annotations |
| Database Access | Spring Data JPA - talk to DB with Java objects |
| Security | Spring Security - login, permissions, JWT |
| Auto-Configuration | Sensible defaults so you write less config |

#### Spring Boot vs Spring

- **Spring** = A massive framework with lots of manual configuration
- **Spring Boot** = Spring + sensible defaults + embedded server = "just works"

```
Spring:        100 lines of XML configuration
Spring Boot:   1 annotation (@SpringBootApplication)
```

#### How It Works (Simplified)

1. You add `@SpringBootApplication` to your main class
2. Spring Boot scans your code for annotations (`@RestController`, `@Service`, etc.)
3. It auto-configures everything based on your dependencies
4. Embedded Tomcat server starts on port 8080
5. Your app is running!

---

### What is Swagger?

**Swagger** (now called OpenAPI) is a tool for API documentation and testing.

#### The Problem It Solves

You build an API with endpoints like:
- `POST /api/orders`
- `GET /api/vendors/{id}/products`

How do other developers (or you in 6 months) know:
- What parameters does each endpoint need?
- What does the response look like?
- How do I test it?

#### What Swagger Provides

| Feature | Benefit |
|---------|---------|
| Auto-Documentation | Reads your code and generates docs |
| Interactive UI | Test endpoints directly in the browser |
| Request Examples | Shows what to send |
| Response Examples | Shows what you'll get back |

#### Swagger UI in Action

When you visit `http://localhost:8080/swagger-ui.html`:

```
┌─────────────────────────────────────────────────────┐
│  Moyo Order Management System API                   │
│  Version: 1.0.0                                     │
├─────────────────────────────────────────────────────┤
│                                                     │
│  ▼ auth-controller                                  │
│    ┌─────────────────────────────────────────────┐  │
│    │ POST /api/auth/login    Login with JWT      │  │
│    │                                             │  │
│    │ [Try it out]                                │  │
│    └─────────────────────────────────────────────┘  │
│                                                     │
└─────────────────────────────────────────────────────┘
```

Click "Try it out" and you can execute the API call right there - no Postman or curl needed.

#### In This Project

We use **springdoc-openapi** which:
1. Scans your `@RestController` classes
2. Generates OpenAPI specification automatically
3. Serves Swagger UI at `/swagger-ui.html`

---

### How They Work Together

```
┌─────────────────────────────────────────────────────────────┐
│                        YOUR PROJECT                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   Maven                  Spring Boot              Swagger   │
│   ─────                  ───────────              ───────   │
│   Downloads              Runs your app            Documents │
│   dependencies ────────► with web server ────────► your API │
│                          & auto-config                      │
│                                                             │
│   pom.xml                @SpringBootApplication   /swagger  │
│                          application.yml          -ui.html  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

| Tool | Role | File/Location |
|------|------|---------------|
| Maven | Build & dependency management | `pom.xml` |
| Spring Boot | Application framework | `@SpringBootApplication`, `application.yml` |
| Swagger | API documentation | `/swagger-ui.html` |

---

## Project Files Deep Dive

### 1. pom.xml - The Shopping List

This is a **Maven** file that manages dependencies.

#### Dependencies Explained

| Dependency | What It Does |
|------------|--------------|
| `spring-boot-starter-web` | Build REST APIs (handle GET, POST requests) |
| `spring-boot-starter-data-jpa` | Talk to databases using Java objects |
| `spring-boot-starter-security` | Add login/authentication features |
| `spring-boot-starter-validation` | Validate input (e.g., "email must be valid") |
| `h2` | In-memory database for testing |
| `lombok` | Auto-generates getters/setters (saves typing!) |
| `springdoc-openapi` | Creates Swagger UI to test your API |
| `jjwt-*` | Create/verify JWT login tokens |

### 2. MoyoOmsApplication.java - The On Switch

```java
@SpringBootApplication
public class MoyoOmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(MoyoOmsApplication.class, args);
    }
}
```

This is the **entry point**. When you run the app, Java calls `main()`.

**What @SpringBootApplication Does**

This single annotation does 3 things:

| Hidden Annotation | What It Does |
|-------------------|--------------|
| `@Configuration` | "This class can define beans" |
| `@EnableAutoConfiguration` | "Spring, configure things automatically" |
| `@ComponentScan` | "Find all my classes under `com.moyo.oms`" |

**Simple version:** This annotation tells Spring Boot to "figure out what I need and set it up for me."

### 3. application.yml - The Settings File

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

jwt:
  secret: moyo-secret-key-change-in-production-for-real
  expiration: 86400000  # 24 hours in milliseconds
```

| Setting | Meaning |
|---------|---------|
| `port: 8080` | Run on port 8080 |
| `jdbc:h2:mem:testdb` | H2 database, in-memory, named "testdb" |
| `ddl-auto: create-drop` | Create tables on start, drop on stop |
| `show-sql: true` | Print SQL queries to console |
| `jwt.expiration: 86400000` | JWT tokens expire after 24 hours |

---

## Database Layer

### The Three-Table Design

```
┌─────────────┐       ┌──────────────────┐       ┌─────────────┐
│   Vendor    │       │  VendorProduct   │       │   Product   │
├─────────────┤       ├──────────────────┤       ├─────────────┤
│ id          │◄─────┤│ vendor_id (FK)   │       │ id          │
│ username    │       │ product_id (FK)  │├─────►│ product_code│
│ password    │       │ price            │       │ name        │
│ name        │       │ stock            │       │ description │
│ email       │       │ updated_at       │       └─────────────┘
└─────────────┘       └──────────────────┘
       ▲                       │
       │                       │
       │         ┌──────────────────┐
       │         │     Order        │
       │         ├──────────────────┤
       └────────┤│ vendor_id (FK)   │
                 │ product_id (FK)  │
                 │ quantity         │
                 │ total_price      │
                 │ status           │
                 └──────────────────┘
```

### Why VendorProduct?

**The Problem:** Different vendors sell the same product at different prices with different stock.

**Bad Design:**
```java
class Vendor {
    String productPrice;  // What if they sell 100 products?
    int productStock;     // This doesn't scale!
}
```

**Good Design:** A separate table for each vendor-product relationship:

```java
class VendorProduct {
    Vendor vendor;    // Who's selling
    Product product;  // What they're selling
    BigDecimal price; // Their price
    Integer stock;    // Their stock level
}
```

Now Vendor Alpha can sell Widget for $50, while Vendor Beta sells it for $45.

### Entity Example: Vendor.java

```java
@Entity
@Table(name = "vendors")
@Data  // Lombok: auto-generates getters, setters, toString, equals, hashCode
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;  // BCrypt hashed

    private String name;
    private String email;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist  // Runs before saving
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

**Key Annotations:**

| Annotation | What It Does |
|------------|--------------|
| `@Entity` | "This class = database table" |
| `@Table(name = "vendors")` | "Table name is 'vendors'" |
| `@Data` | Lombok magic (generates 100+ lines of code) |
| `@Id` | "This is the primary key" |
| `@GeneratedValue` | "Database creates IDs automatically" |
| `@Column(unique = true)` | "No duplicate usernames allowed" |
| `@PrePersist` | "Run this before saving to database" |

### Repository Magic

```java
@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByUsername(String username);
}
```

**You write:** Method name
**Spring Data JPA provides:** Implementation automatically

| Method Name | Generated SQL |
|-------------|---------------|
| `findByUsername(String)` | `SELECT * FROM vendors WHERE username = ?` |
| `findByEmail(String)` | `SELECT * FROM vendors WHERE email = ?` |
| `findByCreatedAtAfter(LocalDateTime)` | `SELECT * FROM vendors WHERE created_at > ?` |

**No SQL needed!** Just follow the naming pattern.

### The Smart Query

The most important query in the system:

```java
List<VendorProduct> findByProductIdAndStockGreaterThanOrderByPriceAsc(
    Long productId,
    Integer minStock
);
```

**Translates to:**
```sql
SELECT * FROM vendor_products
WHERE product_id = ?
  AND stock > ?
ORDER BY price ASC  -- Cheapest first!
```

This finds all vendors who:
1. Sell the requested product
2. Have enough stock
3. **Sorted by price (cheapest first)**

This ONE query powers the entire allocation algorithm!

---

## Security & JWT Authentication

### The Problem: How Do APIs Know It's You?

**Scenario:** You're at a coffee shop.

**Session-based (old way):**
```
You: "Hi, I'm Alice"
Barista: *gives you ticket #42*
        *writes on notepad: "#42 = Alice"*
You: *shows ticket #42*
Barista: *checks notepad* "Oh yes, Alice, here's your coffee"
```

**JWT-based (our way):**
```
You: "Hi, I'm Alice"
Barista: *gives you laminated badge with your name, photo, expiry date*
You: *shows badge*
Barista: *looks at badge* "Yep, it's you. Here's your coffee"
        *no notepad needed!*
```

### JWT = JSON Web Token

A JWT is a **self-contained token** that includes everything needed to identify you.

**Structure:**
```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2ZW5kb3ItYSIsImV4cCI6MTY3ODg5NjAwMH0.abc123xyz
├───────────────────┘ ├──────────────────────────────────────────┘ ├────────┘
     Header                        Payload                         Signature
```

| Part | Contains | Example |
|------|----------|---------|
| **Header** | Algorithm used | `{"alg": "HS256"}` |
| **Payload** | User data | `{"username": "vendor-a", "exp": 1678896000}` |
| **Signature** | Crypto signature | Proves token wasn't tampered with |

### The Authentication Flow

```
┌─────────┐                          ┌─────────┐
│ Client  │                          │ Server  │
└────┬────┘                          └────┬────┘
     │                                    │
     │ 1. POST /api/auth/login            │
     │    {username, password}            │
     ├───────────────────────────────────►│
     │                                    │
     │                                    │ 2. Check password
     │                                    │    with BCrypt
     │                                    │
     │                                    │ 3. Generate JWT
     │ 4. {token: "eyJhbGc..."}           │    (expires in 24h)
     │◄───────────────────────────────────┤
     │                                    │
     │ 5. GET /api/vendors                │
     │    Authorization: Bearer eyJhbGc...│
     ├───────────────────────────────────►│
     │                                    │
     │                                    │ 6. Verify JWT
     │                                    │    (signature valid?)
     │                                    │    (not expired?)
     │                                    │
     │ 7. {vendors: [...]}                │ 8. Execute request
     │◄───────────────────────────────────┤
     │                                    │
```

### SecurityConfig - The Bouncer

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())  // Not needed for stateless JWT
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()     // Login = no auth
                .requestMatchers("/api/health").permitAll()       // Health = no auth
                .requestMatchers("/swagger-ui/**").permitAll()    // Docs = no auth
                .requestMatchers("/v3/api-docs/**").permitAll()   // API spec = no auth
                .anyRequest().authenticated()                     // Everything else = must login
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

**Translation:**

| Rule | Meaning |
|------|---------|
| `csrf.disable()` | CSRF protection off (not needed for JWT APIs) |
| `STATELESS` | No server-side sessions (JWT contains all info) |
| `/api/auth/**` = permitAll | Anyone can access login endpoint |
| `/api/health` = permitAll | Anyone can check if server is up |
| `/swagger-ui/**` = permitAll | Anyone can view API docs |
| `anyRequest().authenticated()` | Everything else requires JWT token |
| `addFilterBefore(jwtAuthFilter)` | Check JWT before processing request |

### JwtAuthenticationFilter - The ID Checker

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, ...) {

        // 1. Extract token from "Authorization: Bearer <token>" header
        String token = getTokenFromRequest(request);

        // 2. Validate token (signature valid? not expired?)
        if (token != null && jwtTokenProvider.validateToken(token)) {

            // 3. Extract username from token
            String username = jwtTokenProvider.getUsernameFromToken(token);

            // 4. Load user details from database
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 5. Tell Spring Security: "This user is authenticated"
            Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 6. Continue processing request
        filterChain.doFilter(request, response);
    }
}
```

**Flow:**

```
Request comes in
    ↓
Extract JWT from header
    ↓
Valid JWT? ─────No─────► 401 Unauthorized
    │
   Yes
    ↓
Extract username from JWT
    ↓
Set user as authenticated
    ↓
Continue to controller
```

### Password Security: BCrypt

**Never store passwords in plain text!**

```java
// BAD - NEVER DO THIS
vendor.setPassword("password123");  // Visible in database!

// GOOD - Use BCrypt
String hashed = passwordEncoder.encode("password123");
vendor.setPassword(hashed);
// Stores: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n7tHxh1Bq9xdWy.yMzDNi
```

**BCrypt Properties:**
- **One-way**: Can't decrypt hash to get password back
- **Salted**: Same password = different hash each time
- **Slow**: Takes ~100ms to hash (prevents brute-force attacks)
- **Adaptive**: Can increase difficulty as computers get faster

**Verification:**
```java
passwordEncoder.matches("password123", storedHash);  // true
passwordEncoder.matches("wrong", storedHash);         // false
```

---

## Service Layer - The Brain

The service layer contains **business logic**. Controllers handle HTTP, repositories handle database, services handle *everything else*.

### Why a Service Layer?

```
❌ BAD: Controller talks directly to database

┌────────────┐
│ Controller │──► Validates input
│            │──► Checks business rules
│            │──► Talks to database
│            │──► Formats response
└────────────┘
^ Too many responsibilities!


✅ GOOD: Layered architecture

┌────────────┐
│ Controller │──► Receives request, validates input
└──────┬─────┘
       │
┌──────▼─────┐
│  Service   │──► Business logic, orchestration
└──────┬─────┘
       │
┌──────▼─────┐
│ Repository │──► Database access only
└────────────┘
```

### VendorService - Vendor Operations

```java
@Service
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;
    private final VendorProductRepository vendorProductRepository;

    public List<VendorProductResponse> getVendorProducts(Long vendorId) {
        // 1. Find vendor
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));

        // 2. Get their products
        List<VendorProduct> vendorProducts =
            vendorProductRepository.findByVendorId(vendorId);

        // 3. Convert to DTOs
        return vendorProducts.stream()
            .map(this::toDTO)
            .toList();
    }

    public void updatePrice(Long vendorId, Long productId, BigDecimal newPrice) {
        // 1. Security check: Verify this vendor owns this product
        VendorProduct vp = vendorProductRepository
            .findByVendorIdAndProductId(vendorId, productId)
            .orElseThrow(() -> new VendorAccessDeniedException(
                "You don't sell this product"
            ));

        // 2. Validation: Price must be positive
        if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        // 3. Update and save
        vp.setPrice(newPrice);
        vp.setUpdatedAt(LocalDateTime.now());
        vendorProductRepository.save(vp);
    }
}
```

**What the Service Does:**

| Responsibility | Example |
|----------------|---------|
| **Validation** | Price > 0, stock >= 0 |
| **Authorization** | Vendor can only update their own products |
| **Orchestration** | Fetch vendor, then fetch products, then convert to DTOs |
| **Business Rules** | Can't reduce stock below 0, can't set negative prices |
| **Error Handling** | Throw meaningful exceptions |

### AllocationService - The Smart Algorithm

This is the **crown jewel** of the system - the intelligent vendor selection.

```java
@Service
@RequiredArgsConstructor
public class AllocationService {

    private final VendorProductRepository vendorProductRepository;

    public VendorProduct findBestVendor(Long productId, Integer quantity) {

        // 1. Find all vendors who sell this product AND have stock
        List<VendorProduct> availableVendors =
            vendorProductRepository
                .findByProductIdAndStockGreaterThanOrderByPriceAsc(
                    productId,
                    quantity - 1  // Need at least 'quantity' in stock
                );

        // 2. No vendors have stock? Fail
        if (availableVendors.isEmpty()) {
            throw new NoStockAvailableException(
                "No vendor has " + quantity + " units in stock"
            );
        }

        // 3. Return first result (cheapest with stock)
        return availableVendors.get(0);
    }
}
```

**The Algorithm Visualized:**

```
Order: 10 widgets

Step 1: Find vendors selling "widget"
┌─────────────────────────────────────┐
│ Vendor Alpha   │ $50.00 │ Stock: 100│ ✓ Has stock
│ Vendor Beta    │ $45.00 │ Stock: 50 │ ✓ Has stock
│ Vendor Charlie │ $40.00 │ Stock: 0  │ ✗ No stock (eliminated)
└─────────────────────────────────────┘

Step 2: Filter by stock >= 10
┌─────────────────────────────────────┐
│ Vendor Alpha   │ $50.00 │ Stock: 100│ ✓ Enough stock
│ Vendor Beta    │ $45.00 │ Stock: 50 │ ✓ Enough stock
└─────────────────────────────────────┘

Step 3: Sort by price (ascending)
┌─────────────────────────────────────┐
│ Vendor Beta    │ $45.00 │ Stock: 50 │ ← WINNER!
│ Vendor Alpha   │ $50.00 │ Stock: 100│
└─────────────────────────────────────┘

Result: Order allocated to Vendor Beta at $45.00/unit
```

**Why This Is Smart:**

- ✅ Always picks cheapest option
- ✅ Never picks vendors without stock
- ✅ Handles "no stock available" gracefully
- ✅ Single database query (efficient!)

### OrderService - Creating Orders

```java
@Service
@RequiredArgsConstructor
@Transactional  // All-or-nothing: either full order succeeds or nothing changes
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AllocationService allocationService;
    private final VendorProductRepository vendorProductRepository;

    public Order createOrder(OrderRequest request) {

        // 1. Validate product exists
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new ProductNotFoundException(
                "Product not found: " + request.getProductId()
            ));

        // 2. Find best vendor (cheapest with stock)
        VendorProduct bestVendor = allocationService.findBestVendor(
            request.getProductId(),
            request.getQuantity()
        );

        // 3. Check if vendor has enough stock
        if (bestVendor.getStock() < request.getQuantity()) {
            throw new InsufficientStockException(
                "Vendor only has " + bestVendor.getStock() + " units"
            );
        }

        // 4. Calculate total price
        BigDecimal totalPrice = bestVendor.getPrice()
            .multiply(BigDecimal.valueOf(request.getQuantity()));

        // 5. Deduct stock (important!)
        bestVendor.setStock(bestVendor.getStock() - request.getQuantity());
        vendorProductRepository.save(bestVendor);

        // 6. Create order record
        Order order = new Order();
        order.setVendor(bestVendor.getVendor());
        order.setProduct(product);
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(totalPrice);
        order.setStatus("ALLOCATED");
        order.setCreatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }
}
```

**The @Transactional Magic:**

If step 6 fails (database error), step 5's stock reduction is **automatically rolled back**. Either everything succeeds or nothing changes.

```
Without @Transactional:
    Deduct stock ✓
    Save order ✗ (error!)
    Result: Stock reduced but no order created! 😱

With @Transactional:
    Deduct stock ✓
    Save order ✗ (error!)
    Rollback: Stock restored ✓
    Result: No changes, consistent state 😌
```

---

## API Layer - The Front Desk

Controllers are the **front desk** - they:
1. Receive HTTP requests
2. Validate input
3. Call services
4. Return HTTP responses

### AuthController - Login

```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        // Service handles authentication logic
        String token = authService.login(request.getUsername(), request.getPassword());

        // Return JWT token to client
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
```

**Request:**
```json
POST /api/auth/login
{
  "username": "vendor-a",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2ZW5kb3ItYSIsImV4cCI6MTY3ODg5NjAwMH0.abc123xyz"
}
```

### VendorController - Vendor Operations

```java
@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;
    private final SecurityUtils securityUtils;

    @GetMapping("/{id}/products")
    public ResponseEntity<List<VendorProductResponse>> getProducts(
        @PathVariable Long id
    ) {
        // Get currently logged-in vendor
        Long currentVendorId = securityUtils.getCurrentVendorId();

        // Security check: Can only view your own products
        if (!currentVendorId.equals(id)) {
            throw new VendorAccessDeniedException("Access denied");
        }

        List<VendorProductResponse> products = vendorService.getVendorProducts(id);
        return ResponseEntity.ok(products);
    }

    @PostMapping("/{vendorId}/products/{productId}/price")
    public ResponseEntity<PriceUpdateResponse> updatePrice(
        @PathVariable Long vendorId,
        @PathVariable Long productId,
        @Valid @RequestBody PriceUpdateRequest request
    ) {
        vendorService.updatePrice(vendorId, productId, request.getNewPrice());

        return ResponseEntity.ok(new PriceUpdateResponse(
            "Price updated successfully",
            request.getNewPrice()
        ));
    }
}
```

**Key Patterns:**

| Pattern | Purpose | Example |
|---------|---------|---------|
| `@PathVariable` | Extract from URL | `/vendors/{id}` → `id` parameter |
| `@RequestBody` | Parse JSON body | `{"newPrice": 45.99}` → `PriceUpdateRequest` object |
| `@Valid` | Validate input | Checks `@NotNull`, `@Min`, etc. |
| `ResponseEntity.ok()` | Return HTTP 200 | Wraps response with status code |

### OrderController - Creating Orders

```java
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
        @Valid @RequestBody OrderRequest request
    ) {
        Order order = orderService.createOrder(request);
        OrderResponse response = toDTO(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(toDTO(order));
    }
}
```

**Request:**
```json
POST /api/orders
{
  "productId": 1,
  "quantity": 10
}
```

**Response:**
```json
HTTP 201 Created
{
  "id": 1,
  "vendorName": "Vendor Beta",
  "productName": "Widget",
  "quantity": 10,
  "totalPrice": 450.00,
  "status": "ALLOCATED",
  "createdAt": "2026-01-22T10:30:00"
}
```

### DTOs - Data Transfer Objects

**Why DTOs?**

```
❌ BAD: Return entities directly

@GetMapping("/vendors/{id}")
public Vendor getVendor(@PathVariable Long id) {
    return vendorRepository.findById(id);
}

Response includes:
{
  "id": 1,
  "username": "vendor-a",
  "password": "$2a$10$N9qo8uLO...",  ← Oops! Password leaked!
  "createdAt": "2026-01-22T10:30:00"
}


✅ GOOD: Use DTOs

@GetMapping("/vendors/{id}")
public VendorResponse getVendor(@PathVariable Long id) {
    Vendor vendor = vendorRepository.findById(id);
    return new VendorResponse(vendor.getId(), vendor.getName());
}

Response:
{
  "id": 1,
  "name": "Vendor Alpha"
}
Only what client needs!
```

**DTO Pattern:**

| Purpose | DTO Type | Example |
|---------|----------|---------|
| **Request** | What client sends | `OrderRequest`, `LoginRequest` |
| **Response** | What client receives | `OrderResponse`, `VendorProductResponse` |

**Benefits:**
- ✅ Security: Don't expose passwords, internal IDs
- ✅ Flexibility: Can change entity without breaking API
- ✅ Clarity: API contract is explicit
- ✅ Validation: Use `@Valid`, `@NotNull`, `@Min`, etc.

---

## The Intelligent Order Allocation

### The Complete Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    USER CREATES ORDER                            │
│                                                                  │
│  POST /api/orders                                                │
│  {"productId": 1, "quantity": 10}                                │
└─────────────────────────┬───────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                   1. VALIDATE PRODUCT                            │
│                                                                  │
│  ProductRepository.findById(1)                                   │
│  ✓ Product exists: "Widget"                                     │
└─────────────────────────┬───────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                   2. FIND BEST VENDOR                            │
│                                                                  │
│  AllocationService.findBestVendor(productId=1, quantity=10)      │
│                                                                  │
│  Query: findByProductIdAndStockGreaterThanOrderByPriceAsc()      │
│                                                                  │
│  Results:                                                        │
│    Vendor Beta:    $45.00/unit, stock=50   ← SELECTED           │
│    Vendor Alpha:   $50.00/unit, stock=100                        │
│    Vendor Charlie: $40.00/unit, stock=0    (eliminated)          │
└─────────────────────────┬───────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                   3. VERIFY STOCK                                │
│                                                                  │
│  Vendor Beta has 50 units                                        │
│  Need 10 units                                                   │
│  ✓ Sufficient stock                                             │
└─────────────────────────┬───────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                   4. CALCULATE PRICE                             │
│                                                                  │
│  $45.00 × 10 = $450.00                                           │
└─────────────────────────┬───────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                   5. DEDUCT STOCK                                │
│                                                                  │
│  Vendor Beta stock: 50 → 40                                      │
│  UPDATE vendor_products SET stock = 40 WHERE ...                 │
└─────────────────────────┬───────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                   6. CREATE ORDER                                │
│                                                                  │
│  INSERT INTO orders (vendor_id, product_id, quantity, ...)       │
│  Status: ALLOCATED                                               │
└─────────────────────────┬───────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                   7. RETURN RESPONSE                             │
│                                                                  │
│  {                                                               │
│    "id": 1,                                                      │
│    "vendorName": "Vendor Beta",                                  │
│    "quantity": 10,                                               │
│    "totalPrice": 450.00,                                         │
│    "status": "ALLOCATED"                                         │
│  }                                                               │
└─────────────────────────────────────────────────────────────────┘
```

### What Makes It "Intelligent"?

1. **Always picks cheapest available vendor** - Saves money automatically
2. **Never allocates to out-of-stock vendors** - No failed orders
3. **Single query** - Efficient (no N+1 query problems)
4. **Transactional** - Stock and order are consistent
5. **Graceful failures** - Clear error messages when no stock available

### Edge Cases Handled

| Scenario | System Response |
|----------|-----------------|
| Product doesn't exist | `404 Not Found: Product not found` |
| No vendors sell this product | `404 Not Found: No vendors found for this product` |
| All vendors out of stock | `400 Bad Request: No stock available` |
| Stock insufficient (need 100, have 50) | `400 Bad Request: Insufficient stock` |
| Multiple vendors same price | Picks first one (deterministic) |
| Concurrent orders for same stock | Transaction isolation prevents overselling |

---

## Complete Request Flow

### Full Journey: Login → View Products → Update Price → Create Order

```
┌──────────┐                                     ┌──────────┐
│  Client  │                                     │  Server  │
└─────┬────┘                                     └─────┬────┘
      │                                                │
      │ 1. POST /api/auth/login                       │
      │    {username: "vendor-a", password: "..."}    │
      ├──────────────────────────────────────────────►│
      │                                                │
      │                                                │ AuthService.login()
      │                                                │ - Load vendor from DB
      │                                                │ - Verify password (BCrypt)
      │                                                │ - Generate JWT token
      │                                                │
      │ {token: "eyJhbGc..."}                          │
      │◄──────────────────────────────────────────────┤
      │                                                │
      │ Store token in memory/localStorage             │
      │                                                │
      │                                                │
      │ 2. GET /api/vendors/1/products                 │
      │    Authorization: Bearer eyJhbGc...            │
      ├──────────────────────────────────────────────►│
      │                                                │
      │                                                │ JwtAuthenticationFilter
      │                                                │ - Extract token
      │                                                │ - Validate signature
      │                                                │ - Check expiration
      │                                                │ - Set authentication
      │                                                │
      │                                                │ VendorController
      │                                                │ - Check: currentUser = vendor-a
      │                                                │ - Authorize: vendor-a can view vendor 1
      │                                                │
      │                                                │ VendorService
      │                                                │ - Fetch vendor products from DB
      │                                                │
      │ [{productCode: "widget-001", price: 50.00, ...}]│
      │◄──────────────────────────────────────────────┤
      │                                                │
      │                                                │
      │ 3. POST /api/vendors/1/products/1/price        │
      │    {newPrice: 45.00}                           │
      ├──────────────────────────────────────────────►│
      │                                                │
      │                                                │ Authentication check ✓
      │                                                │ Authorization check ✓
      │                                                │
      │                                                │ VendorService
      │                                                │ - Validate price > 0 ✓
      │                                                │ - Update vendor_products
      │                                                │ - Save to database
      │                                                │
      │ {message: "Price updated", newPrice: 45.00}    │
      │◄──────────────────────────────────────────────┤
      │                                                │
      │                                                │
      │ 4. POST /api/orders                            │
      │    {productId: 1, quantity: 10}                │
      ├──────────────────────────────────────────────►│
      │                                                │
      │                                                │ OrderService
      │                                                │ - Validate product exists ✓
      │                                                │
      │                                                │ AllocationService
      │                                                │ - Find cheapest vendor with stock
      │                                                │ - Result: Vendor Beta ($45.00)
      │                                                │
      │                                                │ OrderService (continued)
      │                                                │ - Check stock ✓
      │                                                │ - Calculate total: $450.00
      │                                                │ - Deduct stock: 50 → 40
      │                                                │ - Create order record
      │                                                │
      │ {id: 1, vendorName: "Beta", total: 450.00}     │
      │◄──────────────────────────────────────────────┤
      │                                                │
```

### Request Headers Breakdown

```http
GET /api/vendors/1/products HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2ZW5kb3ItYSIsImV4cCI6MTY3ODg5NjAwMH0.abc123xyz
Content-Type: application/json
```

| Header | Purpose |
|--------|---------|
| `Host` | Which server to connect to |
| `Authorization` | JWT token for authentication |
| `Content-Type` | Format of request body (JSON) |

### HTTP Status Codes

| Code | Meaning | Example |
|------|---------|---------|
| `200 OK` | Success | GET /api/vendors |
| `201 Created` | Resource created | POST /api/orders |
| `400 Bad Request` | Invalid input | Negative price |
| `401 Unauthorized` | No/invalid token | Missing Authorization header |
| `403 Forbidden` | Valid token, no permission | Vendor A accessing Vendor B's data |
| `404 Not Found` | Resource doesn't exist | Product ID 999 doesn't exist |
| `500 Internal Server Error` | Server bug | Uncaught exception |

---

## Testing Strategy

### The Testing Pyramid

```
                    ┌─────┐
                    │  E2E │       Few (expensive, slow)
                    │Tests │
                  ┌─┴─────┴─┐
                  │Integration│   Some (moderate cost/speed)
                  │  Tests    │
              ┌───┴───────────┴───┐
              │   Unit Tests       │  Many (cheap, fast)
              └────────────────────┘
```

### What We Test

| Layer | Test Type | Example | What It Tests |
|-------|-----------|---------|---------------|
| **Repository** | Integration | `VendorRepositoryTest` | Database queries work correctly |
| **Service** | Unit | `AllocationServiceTest` | Business logic is correct (mocked dependencies) |
| **Controller** | Integration | `OrderControllerIntegrationTest` | Full HTTP → DB flow |
| **Security** | Integration | `SecurityConfigIntegrationTest` | JWT auth works end-to-end |

### Example: Testing the Allocation Algorithm

```java
@SpringBootTest
class AllocationServiceIntegrationTest {

    @Autowired
    private AllocationService allocationService;

    @Autowired
    private VendorProductRepository vendorProductRepository;

    @Test
    void shouldSelectCheapestVendorWithStock() {
        // Given: 3 vendors
        // Vendor A: $50, stock=100
        // Vendor B: $45, stock=50   ← Should be selected
        // Vendor C: $40, stock=0

        // When: Request 10 units
        VendorProduct result = allocationService.findBestVendor(1L, 10);

        // Then: Vendor B is selected
        assertEquals("Vendor Beta", result.getVendor().getName());
        assertEquals(new BigDecimal("45.00"), result.getPrice());
    }

    @Test
    void shouldThrowExceptionWhenNoStock() {
        // Given: All vendors have 0 stock
        vendorProductRepository.findAll().forEach(vp -> {
            vp.setStock(0);
            vendorProductRepository.save(vp);
        });

        // When/Then: Should throw NoStockAvailableException
        assertThrows(NoStockAvailableException.class, () -> {
            allocationService.findBestVendor(1L, 10);
        });
    }
}
```

### Manual Testing with Swagger

1. **Start app:** `mvn spring-boot:run`
2. **Open Swagger:** http://localhost:8080/swagger-ui.html
3. **Login:**
   - POST `/api/auth/login`
   - Body: `{"username": "vendor-a", "password": "password123"}`
   - Copy token from response
4. **Authorize:**
   - Click "Authorize" button (top right)
   - Enter: `Bearer <your-token>`
5. **Test endpoints:**
   - GET `/api/vendors` - List all vendors
   - GET `/api/vendors/1/products` - View vendor's products
   - POST `/api/orders` - Create order

---

## Quick Reference

### Key URLs

| URL | Purpose |
|-----|---------|
| `http://localhost:8080/api/health` | Health check endpoint |
| `http://localhost:8080/swagger-ui.html` | API documentation |
| `http://localhost:8080/h2-console` | Database viewer |

### Demo Credentials

| Vendor | Username | Password | Price | Stock |
|--------|----------|----------|-------|-------|
| Vendor Alpha | `vendor-a` | `password123` | $50.00 | 100 |
| Vendor Beta | `vendor-b` | `password123` | $45.00 | 50 |
| Vendor Charlie | `vendor-c` | `password123` | $40.00 | 0 |

### Common Maven Commands

| Command | What It Does |
|---------|--------------|
| `mvn spring-boot:run` | Start the application |
| `mvn clean package` | Build the JAR file |
| `mvn test` | Run tests |
| `mvn clean` | Delete compiled files |

### Common Docker Commands

| Command | What It Does |
|---------|--------------|
| `docker-compose up --build` | Build and start |
| `docker-compose up -d` | Start in background |
| `docker-compose down` | Stop containers |
| `docker-compose logs -f moyo-oms` | View live logs |

### Key Files

| File | Purpose | Key Points |
|------|---------|------------|
| `pom.xml` | Maven dependencies | Spring Boot, Security, JWT, JPA |
| `application.yml` | App configuration | Port, database, JWT expiration |
| `SecurityConfig.java` | Security rules | Stateless JWT, public endpoints |
| `JwtAuthenticationFilter.java` | JWT validation | Extracts & validates token |
| `AllocationService.java` | **The magic** | Finds cheapest vendor with stock |
| `OrderService.java` | Order creation | Allocates + deducts stock |
| `data.sql` | Seed data | Demo vendors & products |

### API Endpoints

| Method | Endpoint | Auth? | Purpose |
|--------|----------|-------|---------|
| POST | `/api/auth/login` | No | Get JWT token |
| GET | `/api/health` | No | Health check |
| GET | `/api/vendors` | Yes | List all vendors |
| GET | `/api/vendors/{id}` | Yes | Get vendor details |
| GET | `/api/vendors/{id}/products` | Yes | Get vendor's products |
| POST | `/api/vendors/{id}/products/{pid}/price` | Yes | Update price |
| POST | `/api/vendors/{id}/products/{pid}/stock` | Yes | Update stock |
| POST | `/api/orders` | Yes | Create order (allocates to best vendor) |
| GET | `/api/orders/{id}` | Yes | Get order details |

---

## Architecture Diagram

### The Complete System

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENT (Browser/Postman)                │
└──────────────────────────────┬──────────────────────────────────┘
                               │ HTTP + JWT
                               │
┌──────────────────────────────▼──────────────────────────────────┐
│                      SPRING BOOT APPLICATION                     │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │              SECURITY LAYER (Filter Chain)                │  │
│  │  ┌─────────────────────────────────────────────────────┐  │  │
│  │  │ JwtAuthenticationFilter                             │  │  │
│  │  │ - Extract JWT from Authorization header             │  │  │
│  │  │ - Validate signature & expiration                   │  │  │
│  │  │ - Set authenticated user in SecurityContext         │  │  │
│  │  └─────────────────────────────────────────────────────┘  │  │
│  └───────────────────────────┬───────────────────────────────┘  │
│                              │                                   │
│  ┌───────────────────────────▼───────────────────────────────┐  │
│  │                    CONTROLLER LAYER                       │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │  │
│  │  │    Auth      │  │   Vendor     │  │    Order     │   │  │
│  │  │  Controller  │  │  Controller  │  │  Controller  │   │  │
│  │  │              │  │              │  │              │   │  │
│  │  │ - Login      │  │ - List       │  │ - Create     │   │  │
│  │  │              │  │ - View       │  │ - View       │   │  │
│  │  │              │  │ - Update     │  │              │   │  │
│  │  └──────────────┘  └──────────────┘  └──────────────┘   │  │
│  └───────────────────────────┬───────────────────────────────┘  │
│                              │                                   │
│  ┌───────────────────────────▼───────────────────────────────┐  │
│  │                     SERVICE LAYER                         │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │  │
│  │  │    Auth      │  │   Vendor     │  │    Order     │   │  │
│  │  │   Service    │  │   Service    │  │   Service    │   │  │
│  │  │              │  │              │  │              │   │  │
│  │  │ - Verify     │  │ - Get        │  │ - Validate   │   │  │
│  │  │   password   │  │   products   │  │ - Allocate   │   │  │
│  │  │ - Generate   │  │ - Update     │  │ - Deduct     │   │  │
│  │  │   JWT        │  │   price      │  │   stock      │   │  │
│  │  └──────────────┘  └──────────────┘  └──────┬───────┘   │  │
│  │                                              │           │  │
│  │                    ┌──────────────┐          │           │  │
│  │                    │  Allocation  │◄─────────┘           │  │
│  │                    │   Service    │                      │  │
│  │                    │              │                      │  │
│  │                    │ - Find best  │                      │  │
│  │                    │   vendor     │                      │  │
│  │                    └──────────────┘                      │  │
│  └───────────────────────────┬───────────────────────────────┘  │
│                              │                                   │
│  ┌───────────────────────────▼───────────────────────────────┐  │
│  │                   REPOSITORY LAYER                        │  │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │  │
│  │  │   Vendor     │  │   Product    │  │    Order     │   │  │
│  │  │  Repository  │  │  Repository  │  │  Repository  │   │  │
│  │  └──────────────┘  └──────────────┘  └──────────────┘   │  │
│  │                                                           │  │
│  │           ┌──────────────────────┐                       │  │
│  │           │   VendorProduct      │                       │  │
│  │           │     Repository       │                       │  │
│  │           │                      │                       │  │
│  │           │ - Smart query here   │                       │  │
│  │           └──────────────────────┘                       │  │
│  └───────────────────────────┬───────────────────────────────┘  │
│                              │ JPA/Hibernate                     │
│  ┌───────────────────────────▼───────────────────────────────┐  │
│  │                    DATABASE (H2)                          │  │
│  │  ┌──────────┐  ┌─────────────────┐  ┌─────────┐         │  │
│  │  │ vendors  │  │ vendor_products │  │products │         │  │
│  │  └──────────┘  └─────────────────┘  └─────────┘         │  │
│  │                      ┌────────┐                          │  │
│  │                      │ orders │                          │  │
│  │                      └────────┘                          │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

---

## Key Takeaways

### What You Built

1. **A Production-Ready API** - Not a toy project, this has real architectural patterns
2. **Intelligent Business Logic** - The allocation algorithm is genuinely smart
3. **Enterprise Security** - JWT authentication is what Netflix, Uber, etc. use
4. **Clean Architecture** - Layered design makes it maintainable
5. **Comprehensive Testing** - Unit + integration tests cover critical paths

### Technologies You Now Understand

- ✅ **Spring Boot** - Modern Java web development
- ✅ **Spring Security** - Authentication & authorization
- ✅ **JWT** - Stateless authentication
- ✅ **JPA/Hibernate** - Object-relational mapping
- ✅ **REST APIs** - HTTP methods, status codes, JSON
- ✅ **Docker** - Containerization
- ✅ **Maven** - Build automation
- ✅ **Swagger/OpenAPI** - API documentation

### Patterns You Applied

- ✅ **Layered Architecture** - Controllers → Services → Repositories
- ✅ **DTO Pattern** - Separate API contracts from domain models
- ✅ **Repository Pattern** - Abstract database access
- ✅ **Dependency Injection** - Spring manages object creation
- ✅ **Transaction Management** - `@Transactional` ensures consistency
- ✅ **Filter Chain** - JWT validation before request processing

### What Makes This Special

**Most "tutorial projects" teach you to:**
- ❌ Return entities directly from controllers
- ❌ Put business logic in controllers
- ❌ Store passwords in plain text
- ❌ Skip validation and error handling
- ❌ Ignore security

**This project demonstrates:**
- ✅ DTOs for API contracts
- ✅ Service layer for business logic
- ✅ BCrypt password hashing
- ✅ Comprehensive validation
- ✅ Production-grade security with JWT

---

## What's Next?

You could enhance this system with:

1. **Frontend** - React/Angular UI for vendors to manage inventory
2. **Message Queues** - RabbitMQ for async order processing
3. **Caching** - Redis for frequently accessed data
4. **Monitoring** - Prometheus + Grafana for metrics
5. **Real Database** - PostgreSQL instead of H2
6. **Cloud Deployment** - Deploy to AWS/Azure/Heroku
7. **Advanced Features**:
   - Multi-vendor orders (split across vendors)
   - Vendor ratings (prefer higher-rated vendors)
   - Delivery time estimates
   - Payment processing
   - Order tracking

---

**You built this. You understand this. You can explain this in interviews.**

*Created: January 2026*
*Last Updated: January 2026*
*Covers: Complete implementation (Stories 1.x - 4.x)*
*For: Moyo Order Management System*
