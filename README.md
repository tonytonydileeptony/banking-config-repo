# Banking System - Microservices with Centralized Configuration

## 📋 Project Overview

A Spring Boot microservices banking system with **centralized configuration management** using Spring Cloud Config Server. All microservices load their runtime properties from a centralized configuration server instead of maintaining local configuration files.

### Key Architecture
- **Config Server**: Centralized configuration from Git repository (Port 8888)
- **Eureka Server**: Service discovery and registry (Port 8761)
- **Microservices**: Account, Transaction, Auth, Notification, and API Gateway
- **Database**: MySQL with separate databases per service
- **Message Queue**: Kafka for async notifications
- **Configuration**: Environment-specific (dev/staging/prod)

---

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────┐
│         Git Repository (banking-config-repo)        │
└────────────────────────┬────────────────────────────┘
                         │
                         ▼
        ┌────────────────────────────┐
        │  Config Server (Port 8888) │
        │  ├─ Serves: *.yml configs  │
        │  └─ Git backed             │
        └────────────┬───────────────┘
                     │
       ┌─────────────┼─────────────┐
       │             │             │
       ▼             ▼             ▼
    ┌─────────┐  ┌─────────┐  ┌─────────┐
    │Account  │  │   API   │  │Transaction
    │Service  │  │Gateway  │  │Service
    │(8082)   │  │(8080)   │  │(8083)
    └────┬────┘  └────┬────┘  └────┬─────┘
         │            │            │
         └────────────┼────────────┘
                      │
                      ▼
         ┌────────────────────────┐
         │ Eureka (Port 8761)     │
         │ Service Registry & LB  │
         └────────────────────────┘
```

---

## 📦 Microservices

| Service | Port | Database | Features |
|---------|------|----------|----------|
| **API Gateway** | 8080 | - | Route requests, load balancing |
| **Auth Server** | 8081 | authdb | User authentication |
| **Account Service** | 8082 | accountdb | Account management |
| **Transaction Service** | 8083 | transactiondb | Transaction processing, Kafka producer |
| **Notification Service** | 8084 | notificationdb | Email/SMS notifications, Kafka consumer |
| **Discovery Server (Eureka)** | 8761 | - | Service discovery |
| **Config Server** | 8888 | - | Centralized configuration |

---

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Maven 3.8+
- MySQL Server (localhost:3306)
- Kafka (localhost:9092) - for transaction notifications

### Step 1: Create Databases
```sql
CREATE DATABASE accountdb;
CREATE DATABASE authdb;
CREATE DATABASE transactiondb;
CREATE DATABASE notificationdb;
```

### Step 2: Start Services (in order)

**Terminal 1 - Config Server**:
```powershell
cd config-server
mvn clean install
mvn spring-boot:run
```

**Terminal 2 - Eureka Server**:
```powershell
cd discovery-server
mvn clean install
mvn spring-boot:run
```

**Terminals 3-7 - Microservices** (in any order):
```powershell
# Terminal 3
cd account-service && mvn clean install && mvn spring-boot:run

# Terminal 4
cd api-gateway && mvn clean install && mvn spring-boot:run

# Terminal 5
cd auth-server && mvn clean install && mvn spring-boot:run

# Terminal 6
cd transcation-service && mvn clean install && mvn spring-boot:run

# Terminal 7
cd notification-service && mvn clean install && mvn spring-boot:run
```

### Step 3: Verify

1. **Config Server**: http://localhost:8888/account-service/default
2. **Eureka Dashboard**: http://localhost:8761
3. **API Gateway Health**: http://localhost:8080/actuator/health

---

## 📁 Project Structure

```
banking-2026/
├── config-server/                    # Centralized Config Server
│   ├── src/main/resources/
│   │   └── application.yml          # Config server config
│   ├── account-service.yml          # Account service config
│   ├── api-gateway.yml              # API gateway config
│   ├── auth-server.yml              # Auth server config
│   ├── transaction-service.yml      # Transaction service config
│   ├── notification-service.yml     # Notification service config
│   ├── discovery-server.yml         # Eureka config
│   └── pom.xml
│
├── discovery-server/                 # Eureka Server
│   ├── src/main/resources/
│   │   └── application.yml          # Only bootstrap properties
│   └── pom.xml
│
├── account-service/                  # Account Microservice
│   ├── src/main/resources/
│   │   └── application.yml          # Only bootstrap properties
│   ├── src/main/java/...            # Java code
│   └── pom.xml
│
├── api-gateway/                      # API Gateway
│   ├── src/main/resources/
│   │   └── application.yml          # Only bootstrap properties
│   └── pom.xml
│
├── auth-server/                      # Auth Microservice
│   ├── src/main/resources/
│   │   └── application.yml          # Only bootstrap properties
│   └── pom.xml
│
├── transcation-service/              # Transaction Microservice
│   ├── src/main/resources/
│   │   └── application.yml          # Only bootstrap properties
│   └── pom.xml
│
├── notification-service/             # Notification Microservice
│   ├── src/main/resources/
│   │   └── application.yml          # Only bootstrap properties
│   └── pom.xml
│
├── bank-common/                      # Shared DTOs & Utilities
│   └── pom.xml
│
├── pom.xml                           # Parent POM (Multi-module)
├── CHANGES_SUMMARY.md                # What was changed
├── QUICKSTART.md                     # Quick start guide
├── CONFIG_SERVER_SETUP.md            # Config server details
├── BOOTSTRAP_PROPERTIES_GUIDE.md     # Configuration concepts
├── ARCHITECTURE_DIAGRAMS.md          # System architecture
└── VERIFICATION_CHECKLIST.md         # Verification steps
```

---

## 🔧 Configuration Management

### Bootstrap Properties (Local - Minimal)
Each service's `application.yml` contains only:
```yaml
spring:
  application:
    name: account-service              # Service identifier
  config:
    import: optional:configserver:http://localhost:8888  # Config server location
```

### Runtime Properties (Config Server - Complete)
Each service's config in `config-server/{service}.yml` contains:
```yaml
server:
  port: 8082

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/accountdb
    username: root
    password: root
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
```

### Environment-Specific Configuration
Create environment-specific configs in config-server:
- `account-service.yml` (default/dev)
- `account-service-prod.yml` (production)
- `account-service-staging.yml` (staging)

Start service with profile:
```powershell
# Use production config
set SPRING_PROFILES_ACTIVE=prod
mvn spring-boot:run

# Or via command line
mvn spring-boot:run -Dspring.profiles.active=prod
```

---

## 📚 Documentation

1. **CHANGES_SUMMARY.md** - Detailed summary of all changes made
2. **QUICKSTART.md** - Step-by-step startup guide
3. **CONFIG_SERVER_SETUP.md** - Config server architecture and setup
4. **BOOTSTRAP_PROPERTIES_GUIDE.md** - Understanding bootstrap vs runtime properties
5. **ARCHITECTURE_DIAGRAMS.md** - Visual system architecture and data flows
6. **VERIFICATION_CHECKLIST.md** - Testing and verification procedures

---

## ✨ Key Features

### ✅ Centralized Configuration
- All configurations in one place (Config Server)
- Git-backed for version control
- No configuration duplication

### ✅ Dynamic Configuration
- Change configs without rebuilding services
- Push to Git → Services pick up changes (with refresh scope)

### ✅ Multi-Environment Support
- dev, staging, production configurations
- Profile-based activation
- Different settings per environment

### ✅ Microservices Architecture
- Service discovery with Eureka
- API Gateway for routing
- Independent deployment
- Database per service

### ✅ Asynchronous Processing
- Transaction-to-Notification via Kafka
- Event-driven architecture
- Kafka producer/consumer patterns

---

## 🔍 Health Checks

### Check Config Server
```powershell
curl http://localhost:8888/account-service/default
```

### Check Eureka Dashboard
```powershell
Start-Process "http://localhost:8761"
```

### Check Service Health
```powershell
curl http://localhost:8082/actuator/health   # Account Service
curl http://localhost:8080/actuator/health   # API Gateway
curl http://localhost:8081/actuator/health   # Auth Server
curl http://localhost:8083/actuator/health   # Transaction Service
curl http://localhost:8084/actuator/health   # Notification Service
```

### Health Check Script
```powershell
# Paste in PowerShell:
$services = @{
    "Config Server" = "http://localhost:8888"
    "Eureka" = "http://localhost:8761"
    "Account Service" = "http://localhost:8082/actuator/health"
    "API Gateway" = "http://localhost:8080/actuator/health"
    "Auth Server" = "http://localhost:8081/actuator/health"
    "Transaction Service" = "http://localhost:8083/actuator/health"
    "Notification Service" = "http://localhost:8084/actuator/health"
}

foreach ($service in $services.GetEnumerator()) {
    try {
        $response = Invoke-WebRequest -Uri $service.Value -UseBasicParsing
        Write-Host "✅ $($service.Key): OK" -ForegroundColor Green
    } catch {
        Write-Host "❌ $($service.Key): FAILED" -ForegroundColor Red
    }
}
```

---

## 🐛 Troubleshooting

### Config Server Not Found
**Problem**: Services can't connect to Config Server  
**Solution**: 
1. Ensure Config Server is running on port 8888
2. Check firewall settings
3. Use `optional:configserver:` prefix to start without Config Server (dev only)

### Service Won't Start - Database Error
**Problem**: "Failed to configure DataSource"  
**Solution**:
1. Verify MySQL is running
2. Create required databases
3. Check credentials in config-server/*.yml

### Services Not Registering with Eureka
**Problem**: Services don't appear in Eureka dashboard  
**Solution**:
1. Ensure Eureka is running on port 8761
2. Check eureka configuration in config-server/*.yml
3. Look for "Registering application" in service logs

### Kafka Connection Errors
**Problem**: Transaction/Notification services fail to start  
**Solution**:
1. Ensure Kafka is running on localhost:9092
2. Only transaction-service and notification-service need Kafka
3. Check kafka configuration in config-server/*.yml

### "Spring Boot [3.3.2] is not compatible"
**Problem**: Version compatibility warning during startup  
**Solution**: This is expected. The system works with Spring Boot 3.3.2 and can be upgraded to 3.4.x if needed.

---

## 📊 Data Flow

### Request Flow
```
Client Request
    ↓
API Gateway (8080)
    ↓
Route to appropriate service
    ├→ Account Service (8082)
    ├→ Auth Server (8081)
    ├→ Transaction Service (8083)
    └→ Notification Service (8084)
    ↓
Service Process
    ↓
Response via API Gateway
    ↓
Client Response
```

### Configuration Loading Flow
```
Service Start
    ↓
Read application.yml (bootstrap)
    ├─ spring.application.name
    └─ spring.config.import: configserver URL
    ↓
Contact Config Server
    ↓
Config Server retrieves matching *.yml
    ↓
Service merges properties
    ↓
Service starts with complete configuration
```

### Transaction to Notification Flow
```
Transaction Service
    ├─ Process transaction
    ├─ Publish event to Kafka
    └─ Return response
    
    ↓ Kafka Topic
    
Notification Service
    ├─ Receive event
    ├─ Send notification (email/SMS)
    └─ Log result
```

---

## 🔐 Security Considerations

### Current Setup (Development)
- MySQL credentials in clear text
- No encryption of sensitive properties
- `optional:configserver:` allows startup without Config Server

### Production Recommendations
1. **Encrypt sensitive properties** in Config Server
   ```yaml
   spring:
     cloud:
       config:
         server:
           encrypt:
             enabled: true
   ```

2. **Use secret management** (AWS Secrets Manager, Azure KeyVault, etc.)

3. **Make Config Server mandatory**
   ```yaml
   spring:
     config:
       import: configserver:http://config-server:8888  # Remove "optional:"
   ```

4. **Use HTTPS** for Config Server
   ```yaml
   spring:
     config:
       import: configserver:https://config-server:8888
   ```

5. **Implement authentication** for Config Server endpoints

6. **Use environment variables** for sensitive data
   ```yaml
   spring:
     datasource:
       password: ${DB_PASSWORD}
   ```

---

## 📈 Scaling & Load Testing

### Horizontal Scaling
Add more instances of a service:
```powershell
# Run multiple instances on different ports
# Update config-server to use different ports
# Eureka will load balance across instances
```

### Performance Optimization
1. Increase database connection pools
2. Add caching with Redis
3. Use database indexing
4. Implement service-to-service caching
5. Monitor and tune Kafka throughput

---

## 🔄 CI/CD Integration

### Build Pipeline
```
Git Push
    ↓
Maven Build (mvn clean install)
    ↓
Run Tests
    ↓
Build Docker Image
    ↓
Push to Registry
    ↓
Deploy to Kubernetes/Docker Swarm
```

### Configuration Deployment
```
Update config-server/*.yml
    ↓
Git Push to banking-config-repo
    ↓
Config Server pulls latest
    ↓
Services refresh configuration
    (optional - manual refresh via actuator)
```

---

## 📝 Files Modified

✅ **account-service/src/main/resources/application.yml** - Minimized to 5 lines  
✅ **api-gateway/src/main/resources/application.yml** - Minimized to 5 lines  
✅ **transcation-service/src/main/resources/application.yml** - Minimized to 6 lines  
✅ **discovery-server/src/main/resources/application.yml** - Verified/minimized  
✅ **auth-server/src/main/resources/application.yml** - Already minimal (verified)  
✅ **notification-service/src/main/resources/application.yml** - Already minimal (verified)  

---

## 📚 Additional Resources

- [Spring Cloud Config Documentation](https://spring.io/projects/spring-cloud-config)
- [Spring Cloud Eureka Documentation](https://spring.io/projects/spring-cloud-netflix)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Kafka Documentation](https://kafka.apache.org/documentation/)

---

## 👥 Team & Support

For questions or issues:
1. Check the documentation files in the project root
2. Review service logs for error messages
3. Use the verification checklist to diagnose issues
4. Check Spring Cloud Config documentation

---

## ✅ Status

**Current**: All microservices configured to use centralized Config Server  
**Configuration**: Fully centralized in `config-server/` directory  
**Version Control**: Git-backed configuration in banking-config-repo  
**Ready for**: Development, Staging, Production (with security updates)

---

**Last Updated**: March 19, 2026  
**Configuration Type**: Centralized (Spring Cloud Config Server)  
**Environment**: Development/Testing (Use for production with security hardening)
