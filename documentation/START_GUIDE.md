# Complete Startup Guide - No Hickups! 🚀

This guide will help you start your Fuchs Order Management System without any issues.

---

## ✅ Pre-Flight Checklist

Before starting, verify these prerequisites:

### 1. Check Java Version
```bash
java -version
```
**Required**: Java 17 or higher

**Expected output**:
```
java version "17.0.x" or higher
```

**If wrong version**:
- Download Java 17: https://adoptium.net/
- Set JAVA_HOME environment variable
- Restart your terminal

---

### 2. Check Maven Version
```bash
mvn -version
```
**Required**: Maven 3.6 or higher

**Expected output**:
```
Apache Maven 3.x.x
```

**If Maven not found**:
- Download Maven: https://maven.apache.org/download.cgi
- Add to PATH
- Restart terminal

---

### 3. Check Port 8080 Availability
**On Windows**:
```bash
netstat -ano | findstr :8080
```

**If port is in use** (you see output):
```bash
# Find the PID (last column) and kill it
taskkill /PID <PID> /F
```

**Alternative**: Change port in `application.yml`:
```yaml
server:
  port: 8081  # Use different port
```

---

## 🚀 Startup Methods

Choose **ONE** method below:

---

## Method 1: Maven (Recommended for Development)

### Step 1: Open Terminal in Project Directory
```bash
cd "C:\Users\fuchs\Documents\Projects\Fuchs Order Management System"
```

### Step 2: Clean Previous Build (First Time Only)
```bash
mvn clean
```

### Step 3: Start Application
```bash
mvn spring-boot:run
```

### Step 4: Wait for Success Message
**Look for this in console**:
```
Started FuchsOmsApplication in X.XXX seconds
```

**Common startup messages (these are NORMAL)**:
- ✅ `Bootstrapping Spring Data JPA repositories`
- ✅ `HHH000412: Hibernate ORM core version`
- ✅ `Database: jdbc:h2:mem:testdb`
- ✅ `Tomcat started on port 8080`
- ✅ `Started FuchsOmsApplication`

**If you see errors**, see Troubleshooting section below.

---

## Method 2: Docker (Recommended for Production Testing)

### Step 1: Verify Docker is Running
```bash
docker --version
docker-compose --version
```

### Step 2: Build and Start
```bash
docker-compose up --build
```

**First build takes 2-5 minutes** (downloads dependencies)

### Step 3: Wait for Health Check
Look for:
```
fuchs-oms-1  | Started FuchsOmsApplication
```

### Step 4: Verify Container is Healthy
```bash
docker ps
```
Look for STATUS: `healthy`

---

## Method 3: Build JAR and Run

### Step 1: Build JAR
```bash
mvn clean package -DskipTests
```

### Step 2: Run JAR
```bash
java -jar target/fuchs-oms-0.0.1-SNAPSHOT.jar
```

---

## ✅ Verification Steps (Do This After Startup)

### Step 1: Test Health Endpoint
Open browser or use curl:

**Browser**: http://localhost:8080/api/health

**Or curl**:
```bash
curl http://localhost:8080/api/health
```

**Expected response**:
```json
{"status":"UP"}
```

**If you get connection refused**: Application hasn't started yet, wait 10 more seconds

**If you get 401 error**: Security config issue (I just fixed this!)

---

### Step 2: Access Swagger UI
**Browser**: http://localhost:8080/swagger-ui.html

**Expected**: You should see the Swagger UI interface with all API endpoints listed

**If you get 401 error**: Restart the application (I just fixed the security config)

**If you get 404 error**: Try alternative URLs:
- http://localhost:8080/swagger-ui/index.html
- http://localhost:8080/swagger-ui/

---

### Step 3: Access H2 Database Console (Optional)
**Browser**: http://localhost:8080/h2-console

**Login credentials**:
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

**Expected**: Database console opens, you can run SQL queries

---

### Step 4: Quick API Test
Open Swagger UI and test login:

1. Find `auth-controller`
2. Click `POST /api/auth/login`
3. Click "Try it out"
4. Enter:
```json
{
  "username": "vendor-a",
  "password": "password123"
}
```
5. Click "Execute"

**Expected**: `200 OK` with JWT token

**If this works, you're ready to test!** 🎉

---

## 🔧 Troubleshooting Common Issues

### Issue 1: "Port 8080 already in use"

**Error message**:
```
Web server failed to start. Port 8080 was already in use.
```

**Solution A** - Kill the process:
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

**Solution B** - Change port:
Edit `src/main/resources/application.yml`:
```yaml
server:
  port: 8081
```
Then access at http://localhost:8081

---

### Issue 2: "Could not find or load main class"

**Error message**:
```
Error: Could not find or load main class com.fuchs.oms.FuchsOmsApplication
```

**Solution**:
```bash
mvn clean install
mvn spring-boot:run
```

---

### Issue 3: "Failed to load ApplicationContext"

**Possible causes**:
- Database initialization error
- Bean creation error
- Missing dependencies

**Solution**:
```bash
# Clean everything and rebuild
mvn clean
mvn install -U
mvn spring-boot:run
```

---

### Issue 4: Application Starts But Endpoints Return 404

**Symptom**: Application starts successfully, but http://localhost:8080/api/health returns 404

**Solution**: Check the context path in logs. Look for:
```
Tomcat started on port 8080 (http) with context path ''
```

If context path is not empty, access endpoints at:
```
http://localhost:8080/<context-path>/api/health
```

---

### Issue 5: "401 Unauthorized" on Swagger UI

**Symptom**: Accessing swagger-ui.html returns 401 error

**Solution**: **I just fixed this!** Restart the application:
1. Stop the application (Ctrl+C)
2. Start again: `mvn spring-boot:run`
3. Access http://localhost:8080/swagger-ui.html

---

### Issue 6: Maven Download Issues

**Error**: Dependencies failing to download

**Solution**:
```bash
# Clear Maven cache and retry
rmdir /s /q %USERPROFILE%\.m2\repository
mvn clean install
```

---

### Issue 7: Java Version Mismatch

**Error**: `Unsupported class file major version` or similar

**Solution**:
```bash
# Check Java version
java -version

# Set JAVA_HOME to Java 17
set JAVA_HOME=C:\Program Files\Java\jdk-17

# Verify
echo %JAVA_HOME%
```

---

### Issue 8: Docker Build Fails

**Error**: Docker build fails or times out

**Solution A** - Clear Docker cache:
```bash
docker-compose down
docker system prune -a
docker-compose up --build
```

**Solution B** - Increase Docker resources:
- Docker Desktop → Settings → Resources
- Increase Memory to 4GB
- Increase CPU to 2 cores

---

### Issue 9: Application Starts Slowly

**Expected startup time**: 10-30 seconds
**If taking longer**: Check console for warnings

**Common slow startups**:
- First Maven run (downloads dependencies): 2-5 minutes ✅ Normal
- Docker build (downloads base images): 3-10 minutes ✅ Normal
- Subsequent runs: <30 seconds ✅ Expected

**Speed up**:
```bash
# Skip tests during startup
mvn spring-boot:run -DskipTests
```

---

### Issue 10: Cannot Access from Another Device

**Symptom**: Works on localhost but not from another computer/phone

**Solution**: Change binding address in `application.yml`:
```yaml
server:
  address: 0.0.0.0  # Listen on all network interfaces
  port: 8080
```

**Then access using your IP**:
```
http://<your-ip-address>:8080/swagger-ui.html
```

Find your IP:
```bash
ipconfig  # Windows
```
Look for IPv4 Address

---

## 📋 Complete Startup Checklist

Use this checklist every time you start:

- [ ] Java 17+ installed and verified
- [ ] Maven 3.6+ installed and verified
- [ ] Port 8080 is available (or changed port in config)
- [ ] In correct project directory
- [ ] Run `mvn spring-boot:run`
- [ ] Wait for "Started FuchsOmsApplication" message
- [ ] Test http://localhost:8080/api/health (should return `{"status":"UP"}`)
- [ ] Open http://localhost:8080/swagger-ui.html (should show API docs)
- [ ] Test login endpoint in Swagger UI
- [ ] **Ready to test!** 🎉

---

## 🎯 Quick Start Commands (Copy-Paste Ready)

**Full startup sequence**:
```bash
# Navigate to project
cd "C:\Users\fuchs\Documents\Projects\Fuchs Order Management System"

# Verify prerequisites
java -version
mvn -version

# Check port (should be empty)
netstat -ano | findstr :8080

# Start application
mvn spring-boot:run
```

**Wait for**: `Started FuchsOmsApplication`

**Then open in browser**:
- http://localhost:8080/swagger-ui.html
- http://localhost:8080/api/health
- http://localhost:8080/h2-console

---

## 🛑 How to Stop the Application

**If running with Maven**:
- Press `Ctrl+C` in terminal
- Type `Y` if prompted

**If running with Docker**:
```bash
docker-compose down
```

**If running as JAR**:
- Press `Ctrl+C` in terminal

**Force kill** (if stuck):
```bash
# Find process
netstat -ano | findstr :8080

# Kill it
taskkill /PID <PID> /F
```

---

## 🔄 Restart Process (After Making Code Changes)

### For Code Changes:
1. Stop application (Ctrl+C)
2. No need to rebuild if only Java code changed
3. Start again: `mvn spring-boot:run`

### For Configuration Changes (application.yml, pom.xml):
1. Stop application
2. Clean build: `mvn clean install`
3. Start again: `mvn spring-boot:run`

### For Database Schema Changes:
1. Stop application
2. Delete H2 database (auto-recreated on startup)
3. Start again: `mvn spring-boot:run`
4. Data from `data.sql` will reload automatically

---

## 📱 Alternative Access URLs

Try these if standard URLs don't work:

**Swagger UI**:
- http://localhost:8080/swagger-ui.html ✅ Primary
- http://localhost:8080/swagger-ui/index.html
- http://localhost:8080/swagger-ui/

**API Docs**:
- http://localhost:8080/v3/api-docs
- http://localhost:8080/api-docs

**Health Check**:
- http://localhost:8080/api/health

---

## ✅ Success Indicators

**You know startup was successful when you see ALL of these**:

1. ✅ Console shows: `Started FuchsOmsApplication in X.XXX seconds`
2. ✅ No red ERROR messages in console (warnings are OK)
3. ✅ http://localhost:8080/api/health returns `{"status":"UP"}`
4. ✅ http://localhost:8080/swagger-ui.html loads without 401/404
5. ✅ Can login via Swagger UI and get JWT token

**If all 5 checks pass, you're ready!** Proceed to `QUICK_START_TEST.md`

---

## 🆘 Still Having Issues?

### Check Application Logs
Look for errors in console output. Common keywords:
- `ERROR` - Something went wrong
- `WARN` - Might be OK, but check
- `Failed to` - Initialization failed
- `Exception` - Error occurred

### Enable Debug Logging
Edit `application.yml`:
```yaml
logging:
  level:
    com.fuchs.oms: DEBUG
    org.springframework: DEBUG
```

### Test Database Connection
In H2 Console, run:
```sql
SELECT * FROM vendors;
SELECT * FROM products;
SELECT * FROM vendor_products;
```

Should see 3 vendors, 1 product, 3 vendor-product relationships

---

## 📞 Next Steps

Once application starts successfully:
1. ✅ **Quick Test** → Open `QUICK_START_TEST.md` (5 minutes)
2. ✅ **Full Test** → Open `MANUAL_TESTING_CHECKLIST.md` (30-60 minutes)
3. ✅ **Document Results** → Use `TEST_RESULTS_TEMPLATE.md`

**Happy testing!** 🚀
