# Spring Boot Project Setup Guide

A beginner-friendly explanation of the Moyo Order Management System codebase.

---

## What is This Project?

Think of this project like building a house:

| Concept | File | Purpose |
|---------|------|---------|
| Blueprint | `pom.xml` | Lists all materials and tools needed |
| Foundation | `MoyoOmsApplication.java` | The starting point |
| Settings | `application.yml` | How things should behave |
| Rooms | The packages | Organized spaces for different code |

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
│  ▼ health-controller                                │
│    ┌─────────────────────────────────────────────┐  │
│    │ GET  /api/health    Returns health status   │  │
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

## 1. pom.xml - The Shopping List

This is a **Maven** file. Maven is a tool that:
- Downloads libraries (dependencies) your project needs
- Compiles your code
- Runs tests
- Packages your app into a JAR file

### Project Identity

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.9</version>
</parent>
```

**Translation:** "My project inherits from Spring Boot 3.5.9" - this gives you sensible defaults so you don't configure everything manually.

```xml
<groupId>com.moyo</groupId>
<artifactId>oms</artifactId>
```

**Translation:** Your project's unique ID. `com.moyo` is the organization, `oms` is the project name (Order Management System).

### Dependencies Explained

| Dependency | What It Does |
|------------|--------------|
| `spring-boot-starter-web` | Build REST APIs (handle GET, POST requests) |
| `spring-boot-starter-data-jpa` | Talk to databases using Java objects |
| `spring-boot-starter-security` | Add login/authentication features |
| `spring-boot-starter-validation` | Validate input (e.g., "email must be valid") |
| `h2` | In-memory database for testing |
| `lombok` | Auto-generates getters/setters |
| `springdoc-openapi` | Creates Swagger UI to test your API |
| `jjwt-*` | Create/verify JWT login tokens |

### Dependency Scopes

```xml
<scope>runtime</scope>  <!-- Only needed when app runs -->
<scope>test</scope>     <!-- Only needed for tests -->
<!-- No scope = always needed -->
```

---

## 2. MoyoOmsApplication.java - The On Switch

```java
@SpringBootApplication
public class MoyoOmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(MoyoOmsApplication.class, args);
    }
}
```

This is the **entry point**. When you run the app, Java calls `main()`.

### What @SpringBootApplication Does

This single annotation does 3 things:

| Hidden Annotation | What It Does |
|-------------------|--------------|
| `@Configuration` | "This class can define beans" |
| `@EnableAutoConfiguration` | "Spring, configure things automatically" |
| `@ComponentScan` | "Find all my classes under `com.moyo.oms`" |

**Simple version:** This annotation tells Spring Boot to "figure out what I need and set it up for me."

---

## 3. application.yml - The Settings File

YAML is a configuration format (like JSON but more readable). Indentation matters!

### Server Settings

```yaml
server:
  port: 8080
```

**Translation:** "Run on port 8080" - access at `http://localhost:8080`

### Database Settings

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:moyodb
    driver-class-name: org.h2.Driver
    username: sa
    password:
```

| Setting | Meaning |
|---------|---------|
| `jdbc:h2:mem:moyodb` | H2 database, in-memory, named "moyodb" |
| `username: sa` | Default H2 username |
| `password:` | Empty (fine for development) |

### H2 Console

```yaml
  h2:
    console:
      enabled: true
      path: /h2-console
```

**Translation:** "Enable the database web viewer at `/h2-console`"

### JPA/Hibernate Settings

```yaml
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

| Setting | Meaning |
|---------|---------|
| `ddl-auto: create-drop` | Create tables on start, drop on stop |
| `show-sql: true` | Print SQL queries to console |

> **Warning:** `create-drop` is for development only. Production uses `validate` or `none`.

### Swagger Settings

```yaml
springdoc:
  swagger-ui:
    path: /swagger-ui.html
```

**Translation:** "Put API documentation at `/swagger-ui.html`"

---

## 4. SecurityConfig.java - The Bouncer

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF disabled: This is a stateless REST API using JWT authentication.
            // No session cookies are used, so CSRF protection is not applicable.
            .csrf(csrf -> csrf.disable())
            // Stateless session: No server-side session storage (JWT-based auth)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // Temporary: permit all requests until JWT auth is implemented in Story 2.x
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
        return http.build();
    }
}
```

### Line by Line

| Code | Meaning |
|------|---------|
| `@Configuration` | "This class contains configuration" |
| `@EnableWebSecurity` | "Turn on Spring Security" |
| `@Bean` | "Spring, manage this object for me" |
| `.csrf(csrf -> csrf.disable())` | Disable CSRF (not needed for stateless REST APIs with JWT) |
| `.sessionManagement(...)` | Configure how sessions are handled |
| `SessionCreationPolicy.STATELESS` | "Never create HTTP sessions - we use JWT tokens instead" |
| `.anyRequest().permitAll()` | "Allow ALL requests without login" |

### Why Stateless?

REST APIs with JWT authentication don't need server-side sessions:

| Approach | How It Works | Storage |
|----------|--------------|---------|
| **Session-based** | Server stores session, sends cookie | Server memory/database |
| **JWT (Stateless)** | Token contains all info, sent in header | Client only |

By setting `STATELESS`, Spring won't create `JSESSIONID` cookies. This is the correct setup for JWT authentication.

### Current State

**The bouncer is letting everyone in.** This is temporary for development. The `permitAll()` will be replaced with proper JWT authorization rules in Story 2.x.

---

## 5. SwaggerConfig.java - API Documentation

```java
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Moyo Order Management System API")
                .description("Vendor pricing, inventory, and order allocation system")
                .version("1.0.0"));
    }
}
```

This sets the title, description, and version shown on the Swagger UI page.

---

## 6. HealthController.java - Your First Endpoint

```java
@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = Map.of("status", "UP");
        return ResponseEntity.ok(response);
    }
}
```

### Annotations Explained

| Annotation | Meaning |
|------------|---------|
| `@RestController` | "Handle HTTP requests, return JSON" |
| `@RequestMapping("/api")` | "All endpoints start with `/api`" |
| `@GetMapping("/health")` | "Handle GET requests to `/health`" |

**Combined URL:** `GET /api/health`

### What the Method Does

1. Creates a Map: `{"status": "UP"}`
2. Wraps it with HTTP 200 OK status
3. Spring converts it to JSON automatically

**Response:** `{"status":"UP"}`

---

## 7. Project Structure - The Room Layout

```
src/main/java/com/moyo/oms/
├── MoyoOmsApplication.java   # Entry point
├── config/                   # Configuration classes
├── controller/               # HTTP handlers (front door)
├── service/                  # Business logic (brain)
├── repository/               # Database access
├── model/                    # Entity classes (DB tables)
├── dto/                      # Request/response shapes
├── security/                 # Auth code
└── exception/                # Error handling
```

### The Layered Architecture

Data flows through layers:

```
        HTTP Request
             │
             ▼
    ┌─────────────────┐
    │   Controller    │  Receives request, validates input
    └────────┬────────┘
             │
             ▼
    ┌─────────────────┐
    │    Service      │  Business logic
    └────────┬────────┘
             │
             ▼
    ┌─────────────────┐
    │   Repository    │  Talks to database
    └────────┬────────┘
             │
             ▼
        Database
```

**Rule:** Each layer only talks to the layer directly below it.

---

## 8. JJWT - JSON Web Tokens

The JWT libraries are installed but **not used yet**. They're for authentication in later stories.

### How JWT Works

1. You log in with username/password
2. Server gives you a JWT token (encoded string)
3. You include this token in every future request
4. Server verifies the token

### Example Token

```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ2ZW5kb3ItMTIzIn0.abc123...
```

### The Three JJWT Modules

| Module | Purpose |
|--------|---------|
| `jjwt-api` | Interfaces you code against |
| `jjwt-impl` | Actual implementation (runtime) |
| `jjwt-jackson` | Converts JWT to/from JSON |

---

## 9. How to Run and Test

### Start the Application

```bash
mvn spring-boot:run
```

### Test the Health Endpoint

**Browser or curl:**
```
http://localhost:8080/api/health
```

**Expected response:**
```json
{"status":"UP"}
```

### View Swagger UI

```
http://localhost:8080/swagger-ui.html
```

You'll see your API documented with a "Try it out" button.

### View H2 Database Console

```
http://localhost:8080/h2-console
```

| Field | Value |
|-------|-------|
| JDBC URL | `jdbc:h2:mem:moyodb` |
| Username | `sa` |
| Password | (leave empty) |

---

## 10. Docker - Running Your App Anywhere

Docker lets you package your application with everything it needs (Java, settings, libraries) into a single "container" that runs the same way on any machine.

### Why Docker?

**The Problem:**
```
Developer: "It works on my machine!"
Server:    "Well, it doesn't work on mine."
```

Different machines have different:
- Java versions (8? 11? 17? 21?)
- Operating systems
- Environment variables
- Installed software

**The Solution:** Docker packages your app + its entire environment together.

---

### Key Concepts

Think of Docker like shipping:

| Concept | Analogy | What It Really Is |
|---------|---------|-------------------|
| **Image** | A recipe/blueprint | A read-only template with your app + OS + dependencies |
| **Container** | A dish made from the recipe | A running instance of an image |
| **Dockerfile** | The recipe instructions | Text file that tells Docker how to build the image |
| **Docker Compose** | A meal plan with multiple dishes | Tool to run multiple containers together |

---

### Container vs Virtual Machine

```
┌─────────────────────────────────────────────────────────────────┐
│                    VIRTUAL MACHINE                               │
├─────────────────────────────────────────────────────────────────┤
│  ┌──────────┐  ┌──────────┐  ┌──────────┐                       │
│  │  App 1   │  │  App 2   │  │  App 3   │                       │
│  ├──────────┤  ├──────────┤  ├──────────┤                       │
│  │  Libs    │  │  Libs    │  │  Libs    │                       │
│  ├──────────┤  ├──────────┤  ├──────────┤                       │
│  │ Guest OS │  │ Guest OS │  │ Guest OS │  ← Full OS each!      │
│  └──────────┘  └──────────┘  └──────────┘                       │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                    Hypervisor                            │    │
│  └─────────────────────────────────────────────────────────┘    │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                     Host OS                              │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                      CONTAINERS                                  │
├─────────────────────────────────────────────────────────────────┤
│  ┌──────────┐  ┌──────────┐  ┌──────────┐                       │
│  │  App 1   │  │  App 2   │  │  App 3   │                       │
│  ├──────────┤  ├──────────┤  ├──────────┤                       │
│  │  Libs    │  │  Libs    │  │  Libs    │                       │
│  └──────────┘  └──────────┘  └──────────┘                       │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                   Docker Engine                          │    │
│  └─────────────────────────────────────────────────────────┘    │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                     Host OS                              │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
```

| Aspect | Virtual Machine | Container |
|--------|-----------------|-----------|
| Size | 10-50 GB | 100-500 MB |
| Startup | Minutes | Seconds |
| Isolation | Complete (separate OS) | Process-level |
| Resource usage | Heavy | Lightweight |

**Containers share the host OS kernel** - that's why they're so fast and small.

---

### Dockerfile Explained

Our project's `Dockerfile`:

```dockerfile
# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Install curl for healthcheck
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/oms-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### Line by Line

| Line | What It Does |
|------|--------------|
| `FROM maven:3.9-eclipse-temurin-17 AS build` | Start from a Maven+Java 17 image, name this stage "build" |
| `WORKDIR /app` | Create and switch to `/app` directory |
| `COPY pom.xml .` | Copy pom.xml into the container |
| `COPY src ./src` | Copy source code into the container |
| `RUN mvn clean package -DskipTests` | Build the JAR file |
| `FROM eclipse-temurin:17-jre` | Start fresh with a smaller Java runtime image |
| `RUN apt-get update && apt-get install...` | Install curl (needed for Docker healthcheck) |
| `&& rm -rf /var/lib/apt/lists/*` | Clean up apt cache to keep image small |
| `COPY --from=build /app/target/oms-*.jar app.jar` | Copy JAR from build stage |
| `EXPOSE 8080` | Document that port 8080 is used |
| `ENTRYPOINT ["java", "-jar", "app.jar"]` | Command to run when container starts |

#### Why Install curl?

The `eclipse-temurin:17-jre` image is minimal - it only contains the Java runtime. But our `docker-compose.yml` uses curl for healthchecks:

```yaml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8080/api/health"]
```

Without curl installed, the healthcheck would fail and Docker would report the container as "unhealthy" even when the app is running fine.

#### Why Two Stages?

This is called a **multi-stage build**:

```
Stage 1 (build):     Stage 2 (runtime):
┌─────────────┐      ┌─────────────┐
│ Maven       │      │ Java JRE    │
│ JDK         │      │ Your JAR    │
│ Source code │  →   │             │
│ Dependencies│      │             │
│ ~500MB      │      │ ~200MB      │
└─────────────┘      └─────────────┘
```

The final image only contains what's needed to **run** the app, not build it.

---

### Docker Compose Explained

Our project's `docker-compose.yml`:

```yaml
services:
  moyo-oms:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/api/health"]
      interval: 10s
      timeout: 5s
      retries: 3
      start_period: 30s
```

#### Line by Line

| Section | What It Does |
|---------|--------------|
| `services:` | List of containers to run |
| `moyo-oms:` | Name of our service |
| `build: context: .` | Build image from current directory |
| `dockerfile: Dockerfile` | Use the file named "Dockerfile" |
| `ports: "8080:8080"` | Map host port 8080 to container port 8080 |
| `environment:` | Set environment variables |
| `SPRING_PROFILES_ACTIVE=docker` | Activates `application-docker.yml` settings |
| `healthcheck:` | How Docker knows if the app is healthy |

#### Port Mapping Explained

```
"8080:8080"
   │    │
   │    └── Container port (inside Docker)
   └─────── Host port (your machine)
```

If you used `"3000:8080"`:
- Access via `http://localhost:3000` on your machine
- App still listens on 8080 inside the container

---

### Spring Profiles with Docker

We have two configuration files:

| File | Used When | Purpose |
|------|-----------|---------|
| `application.yml` | Local development | Show SQL, debug settings |
| `application-docker.yml` | Running in Docker | Quieter logs, container-friendly |

The `SPRING_PROFILES_ACTIVE=docker` environment variable tells Spring to load `application-docker.yml`.

#### application-docker.yml Key Settings

```yaml
spring:
  sql:
    init:
      mode: always  # Ensure seed data loads in Docker environment
  jpa:
    show-sql: false  # Reduce log noise in container
    defer-datasource-initialization: true  # Wait for schema before data.sql
```

| Setting | Why It Matters |
|---------|----------------|
| `sql.init.mode: always` | Explicitly ensures `data.sql` runs (doesn't rely on inheritance) |
| `show-sql: false` | Cleaner container logs (SQL logging is for debugging) |
| `defer-datasource-initialization: true` | Prevents "table not found" errors in data.sql |

---

### Running with Docker

#### First Time (Build + Run)

```bash
docker-compose up --build
```

This:
1. Builds the Docker image (compiles your code)
2. Creates a container from the image
3. Starts the container
4. Shows logs in your terminal

#### Run in Background

```bash
docker-compose up -d
```

The `-d` means "detached" (runs in background).

#### View Logs

```bash
docker-compose logs -f moyo-oms
```

The `-f` means "follow" (live updates).

#### Stop Everything

```bash
docker-compose down
```

This stops and removes the containers.

---

### Common Docker Commands

| Command | What It Does |
|---------|--------------|
| `docker-compose up` | Start containers |
| `docker-compose up -d` | Start in background |
| `docker-compose up --build` | Rebuild then start |
| `docker-compose down` | Stop and remove containers |
| `docker-compose logs moyo-oms` | View logs |
| `docker-compose logs -f moyo-oms` | View logs (live) |
| `docker-compose ps` | List running containers |
| `docker-compose build --no-cache` | Rebuild without cache |

### Useful Docker Commands (Without Compose)

| Command | What It Does |
|---------|--------------|
| `docker images` | List all images |
| `docker ps` | List running containers |
| `docker ps -a` | List all containers (including stopped) |
| `docker stop <id>` | Stop a container |
| `docker rm <id>` | Remove a container |
| `docker rmi <image>` | Remove an image |

---

### When to Use What

| Scenario | Command |
|----------|---------|
| Development (need to debug) | `mvn spring-boot:run` |
| Test Docker setup | `docker-compose up --build` |
| Demo to someone | `docker-compose up -d` |
| CI/CD pipeline | `docker-compose up --build` |

---

### Troubleshooting Docker

| Problem | Solution |
|---------|----------|
| "Port already in use" | Stop local Spring Boot: `mvn spring-boot:stop` or check `docker ps` |
| "Cannot connect to Docker daemon" | Start Docker Desktop |
| Build fails | Check `docker-compose logs` for errors |
| Old code running | Rebuild: `docker-compose up --build` |
| Container unhealthy | Check app logs: `docker-compose logs moyo-oms` |

---

## 11. Database Entities & Seed Data

This section covers the data layer - how we store vendors, products, and their relationships.

### What is an Entity?

An **entity** is a Java class that maps to a database table. Each instance of the class = one row in the table.

```
Java World                    Database World
─────────────────────────────────────────────
Vendor.java        ←→         vendors table
  - id             ←→           id column
  - username       ←→           username column
  - password       ←→           password column
```

Spring Data JPA handles the translation automatically.

---

### Our Three Entities

We have three entities that form the core data model:

```
┌─────────────┐       ┌──────────────────┐       ┌─────────────┐
│   vendors   │       │  vendor_products │       │  products   │
├─────────────┤       ├──────────────────┤       ├─────────────┤
│ id (PK)     │───┐   │ id (PK)          │   ┌───│ id (PK)     │
│ username    │   └──>│ vendor_id (FK)   │   │   │ product_code│
│ password    │       │ product_id (FK)  │<──┘   │ name        │
│ name        │       │ price            │       │ description │
│ created_at  │       │ stock            │       │ created_at  │
└─────────────┘       │ updated_at       │       └─────────────┘
                      └──────────────────┘
```

| Entity | Purpose | Key Fields |
|--------|---------|------------|
| `Vendor` | A supplier who sells products | username, password, name |
| `Product` | Something that can be sold | productCode, name, description |
| `VendorProduct` | Links a vendor to a product with price/stock | vendor, product, price, stock |

**Why VendorProduct?** Different vendors can sell the same product at different prices with different stock levels. This is called a **many-to-many relationship with extra attributes**.

---

### Entity Anatomy: Vendor.java

```java
@Entity
@Table(name = "vendors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

#### Annotations Explained

| Annotation | What It Does |
|------------|--------------|
| `@Entity` | "This class maps to a database table" |
| `@Table(name = "vendors")` | "The table is called 'vendors'" |
| `@Data` | Lombok: generates getters, setters, toString, equals, hashCode |
| `@NoArgsConstructor` | Lombok: generates empty constructor (required by JPA) |
| `@AllArgsConstructor` | Lombok: generates constructor with all fields |
| `@Id` | "This is the primary key" |
| `@GeneratedValue(IDENTITY)` | "Database auto-generates the ID" |
| `@Column(...)` | Configure column: name, constraints, length |
| `@PrePersist` | "Run this method before saving to database" |

#### Naming Conventions

| Java | Database | Rule |
|------|----------|------|
| `Vendor` | `vendors` | Table: snake_case, plural |
| `productCode` | `product_code` | Column: snake_case |
| `VendorProduct` | `vendor_products` | Table: snake_case, plural |

---

### Relationships: VendorProduct.java

```java
@Entity
@Table(name = "vendor_products", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"vendor_id", "product_id"})
})
public class VendorProduct {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "stock")
    private Integer stock;
}
```

#### Key Concepts

| Annotation | Meaning |
|------------|---------|
| `@ManyToOne` | "Many VendorProducts can belong to one Vendor" |
| `@JoinColumn` | "This is the foreign key column" |
| `FetchType.LAZY` | "Don't load related data until needed" (performance) |
| `@UniqueConstraint` | "Each vendor-product pair must be unique" |

#### Relationship Types

```
Vendor (1) ────────< (Many) VendorProduct (Many) >──────── (1) Product

One vendor can have many VendorProducts (different products)
One product can have many VendorProducts (sold by different vendors)
```

---

### What is a Repository?

A **repository** is an interface that provides database operations. You define the method signature, Spring Data JPA implements it automatically.

```java
@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByUsername(String username);
}
```

#### Magic Method Names

Spring Data JPA creates queries from method names:

| Method Name | Generated SQL |
|-------------|---------------|
| `findByUsername(String)` | `SELECT * FROM vendors WHERE username = ?` |
| `findByProductCode(String)` | `SELECT * FROM products WHERE product_code = ?` |
| `findByVendorId(Long)` | `SELECT * FROM vendor_products WHERE vendor_id = ?` |
| `findByProductIdAndStockGreaterThanOrderByPriceAsc(...)` | Complex query with conditions and sorting |

**No SQL needed!** Just name your method correctly.

---

### Our Three Repositories

| Repository | Entity | Custom Methods |
|------------|--------|----------------|
| `VendorRepository` | Vendor | `findByUsername()` |
| `ProductRepository` | Product | `findByProductCode()` |
| `VendorProductRepository` | VendorProduct | `findByVendorId()`, `findByVendorIdAndProductId()`, `findByProductIdAndStockGreaterThanOrderByPriceAsc()` |

#### The Allocation Query

This method finds vendors who can fulfill an order (have stock) sorted by cheapest price:

```java
List<VendorProduct> findByProductIdAndStockGreaterThanOrderByPriceAsc(
    Long productId,
    Integer minStock
);
```

Translates to:
```sql
SELECT * FROM vendor_products
WHERE product_id = ?
  AND stock > ?
ORDER BY price ASC
```

---

### Seed Data: data.sql

When the application starts, it loads demo data from `src/main/resources/data.sql`:

```sql
-- Seed Vendors (passwords are BCrypt hashed: "password123")
INSERT INTO vendors (username, password, name, created_at) VALUES
('vendor-a', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n7tHxh1Bq9xdWy.yMzDNi', 'Vendor Alpha', CURRENT_TIMESTAMP),
('vendor-b', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n7tHxh1Bq9xdWy.yMzDNi', 'Vendor Beta', CURRENT_TIMESTAMP),
('vendor-c', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n7tHxh1Bq9xdWy.yMzDNi', 'Vendor Charlie', CURRENT_TIMESTAMP);

-- Seed Products
INSERT INTO products (product_code, name, description, created_at) VALUES
('widget-001', 'Widget', 'Standard widget for demo purposes', CURRENT_TIMESTAMP);

-- Seed VendorProducts
INSERT INTO vendor_products (vendor_id, product_id, price, stock, updated_at) VALUES
(1, 1, 50.00, 100, CURRENT_TIMESTAMP),
(2, 1, 45.00, 50, CURRENT_TIMESTAMP),
(3, 1, 40.00, 0, CURRENT_TIMESTAMP);
```

#### Demo Credentials

| Vendor | Username | Password | Price | Stock | Notes |
|--------|----------|----------|-------|-------|-------|
| Vendor Alpha | `vendor-a` | `password123` | $50.00 | 100 | Expensive but has stock |
| Vendor Beta | `vendor-b` | `password123` | $45.00 | 50 | **Wins allocations** (cheapest with stock) |
| Vendor Charlie | `vendor-c` | `password123` | $40.00 | 0 | Cheapest but no stock |

#### Password Security

Passwords are stored as **BCrypt hashes**, not plain text:
- Original: `password123`
- Stored: `$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n7tHxh1Bq9xdWy.yMzDNi`

The `$2a$10$` prefix means BCrypt with 10 rounds of hashing.

---

### How Data Loading Works

1. App starts → Hibernate creates tables from entity annotations
2. Spring sees `spring.sql.init.mode=always` in config
3. Spring runs `data.sql` after tables exist
4. Demo data is now available

```yaml
# application.yml
spring:
  sql:
    init:
      mode: always  # Run data.sql on startup
  jpa:
    defer-datasource-initialization: true  # Wait for schema first
```

---

### Viewing the Data

#### Option 1: H2 Console

1. Go to `http://localhost:8080/h2-console`
2. Enter JDBC URL: `jdbc:h2:mem:moyodb`
3. Username: `sa`, Password: (empty)
4. Run queries:

```sql
SELECT * FROM vendors;
SELECT * FROM products;
SELECT * FROM vendor_products;
```

#### Option 2: Application Logs

With `show-sql: true`, you'll see Hibernate queries in the console:

```
Hibernate: select v1_0.id, v1_0.username, ... from vendors v1_0
```

---

### Project Structure After Story 1.3

```
src/main/java/com/moyo/oms/
├── model/
│   ├── Vendor.java           ← Entity
│   ├── Product.java          ← Entity
│   └── VendorProduct.java    ← Entity
├── repository/
│   ├── VendorRepository.java         ← Data access
│   ├── ProductRepository.java        ← Data access
│   └── VendorProductRepository.java  ← Data access
└── ...

src/main/resources/
├── application.yml           ← Configuration
└── data.sql                  ← Seed data

src/test/resources/
└── application.yml           ← Test configuration (disables seed data)
```

---

### Common Issues & Solutions

| Problem | Cause | Solution |
|---------|-------|----------|
| "Table not found" in data.sql | Schema not created yet | Set `defer-datasource-initialization: true` |
| "Unique constraint violation" | Duplicate data | H2 is in-memory, restarts fresh each time |
| Tests fail with constraint errors | Seed data conflicts with test data | Use separate `src/test/resources/application.yml` with `sql.init.mode: never` |
| "Could not determine type for: Vendor" | Missing `@Entity` annotation | Add `@Entity` to your class |

---

## 12. Quick Reference

### Key URLs

| URL | Purpose |
|-----|---------|
| `http://localhost:8080/api/health` | Health check endpoint |
| `http://localhost:8080/swagger-ui.html` | API documentation |
| `http://localhost:8080/h2-console` | Database viewer |

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

### Key Files Summary

| File | Purpose |
|------|---------|
| `pom.xml` | Dependencies and build config |
| `MoyoOmsApplication.java` | Entry point |
| `application.yml` | App configuration |
| `application-docker.yml` | Docker-specific config |
| `SecurityConfig.java` | Security rules |
| `SwaggerConfig.java` | API docs metadata |
| `HealthController.java` | Sample endpoint |
| `Dockerfile` | Container build instructions |
| `docker-compose.yml` | Container orchestration |
| `model/Vendor.java` | Vendor entity |
| `model/Product.java` | Product entity |
| `model/VendorProduct.java` | Vendor-Product relationship entity |
| `repository/*Repository.java` | Database access interfaces |
| `data.sql` | Demo seed data |

---

## What's Next?

This is the foundation. Future stories will add:

- ~~**Entities** - Java classes that map to database tables~~ ✅ Done (Story 1.3)
- ~~**Repositories** - Interfaces for database operations~~ ✅ Done (Story 1.3)
- ~~**Database with Seed Data** - Demo data for testing~~ ✅ Done (Story 1.3)
- **Services** - Business logic layer
- **More Controllers** - API endpoints for vendors, products, orders
- **JWT Security** - Real authentication with tokens

---

*Created: January 2026*
*Updated: January 2026 (Added Database Entities & Seed Data section)*
*Updated: January 2026 (Code Review: Added stateless session config, Docker curl install, explicit docker profile settings)*
*For: Moyo Order Management System - Stories 1.1, 1.2 & 1.3*
