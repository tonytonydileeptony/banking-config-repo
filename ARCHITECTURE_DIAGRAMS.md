# Configuration Architecture Diagrams

## BEFORE: Properties Duplicated Locally

```
┌──────────────────────────────────────────────────────────────────┐
│                    MICROSERVICES SETUP (BEFORE)                   │
└──────────────────────────────────────────────────────────────────┘

📦 account-service
├── pom.xml
└── src/main/resources/
    └── application.yml  (30 lines) ❌ DUPLICATED
        ├── server.port: 8082
        ├── spring.datasource.url: jdbc:mysql://localhost:3306/accountdb
        ├── spring.datasource.username: root
        ├── spring.datasource.password: root
        ├── spring.jpa.hibernate.ddl-auto: update
        ├── spring.jpa.show-sql: true
        └── eureka.*: configuration

📦 api-gateway
├── pom.xml
└── src/main/resources/
    └── application.yml  (31 lines) ❌ DUPLICATED
        ├── server.port: 8080
        ├── spring.cloud.gateway.routes: [multiple routes]
        ├── logging.level.org.springframework.cloud.gateway: DEBUG
        └── eureka.*: configuration

📦 transcation-service
├── pom.xml
└── src/main/resources/
    └── application.yml  (45 lines) ❌ DUPLICATED
        ├── server.port: 8083
        ├── spring.datasource.url: jdbc:mysql://localhost:3306/transactiondb
        ├── spring.kafka.bootstrap-servers: localhost:9092
        ├── spring.jpa.hibernate.ddl-auto: update
        └── eureka.*: configuration

📂 config-server
├── account-service.yml    (NOT USED ❌)
├── api-gateway.yml        (NOT USED ❌)
├── transaction-service.yml (NOT USED ❌)
└── ...

❌ PROBLEMS:
   • Configuration scattered across services
   • Duplicated properties in multiple files
   • Hard to update globally
   • Requires rebuild to change config
   • Difficult to support multiple environments
   • No clear single source of truth
```

---

## AFTER: Centralized Configuration in Config Server

```
┌──────────────────────────────────────────────────────────────────┐
│                   MICROSERVICES SETUP (AFTER)                     │
└──────────────────────────────────────────────────────────────────┘

                    🎯 CONFIG SERVER (Port 8888)
                    ├── Git: banking-config-repo
                    └── Local Files:
                        ├── account-service.yml (25 lines) ✅ SINGLE SOURCE
                        ├── api-gateway.yml (13 lines) ✅ SINGLE SOURCE
                        ├── transaction-service.yml (26 lines) ✅ SINGLE SOURCE
                        ├── auth-server.yml (24 lines) ✅ SINGLE SOURCE
                        ├── notification-service.yml (34 lines) ✅ SINGLE SOURCE
                        └── discovery-server.yml (7 lines) ✅ SINGLE SOURCE
                         
                              ↑↑↑ Services fetch from here ↑↑↑

📦 account-service
├── pom.xml
└── src/main/resources/
    └── application.yml  (5 lines) ✅ MINIMAL BOOTSTRAP
        ├── spring.application.name: account-service
        └── spring.config.import: optional:configserver:http://localhost:8888

📦 api-gateway
├── pom.xml
└── src/main/resources/
    └── application.yml  (5 lines) ✅ MINIMAL BOOTSTRAP
        ├── spring.application.name: api-gateway
        └── spring.config.import: optional:configserver:http://localhost:8888

📦 transcation-service
├── pom.xml
└── src/main/resources/
    └── application.yml  (6 lines) ✅ MINIMAL BOOTSTRAP
        ├── spring.application.name: transcation-service
        └── spring.config.import: optional:configserver:http://localhost:8888

✅ BENEFITS:
   • All configuration centralized in config-server
   • No duplication
   • Easy global updates
   • No rebuild needed for config changes
   • Git-based version control
   • Simple environment management (dev/staging/prod)
   • Smaller microservice JARs
```

---

## Configuration Loading Flow

```
┌────────────────────────────────────────────────────────────────────┐
│                 SERVICE STARTUP SEQUENCE                            │
└────────────────────────────────────────────────────────────────────┘

TIME →

┌─────────────────────────────────────────────────────────────────┐
│ 1️⃣  SERVICE STARTUP (account-service)                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  JVM starts → Spring Boot Initialization                         │
│                          ↓                                       │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│ 2️⃣  BOOTSTRAP PHASE                                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Read: application.yml (BOOTSTRAP)                               │
│  ┌──────────────────────────────────────┐                       │
│  │ spring:                              │                       │
│  │   application:                       │                       │
│  │     name: account-service            │ ← Service identity    │
│  │   config:                            │                       │
│  │     import: optional:configserver:   │ ← Config location    │
│  │             http://localhost:8888    │                       │
│  └──────────────────────────────────────┘                       │
│                          ↓                                       │
│  Spring Cloud Config Client activated                            │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│ 3️⃣  CONFIG SERVER CONNECTION                                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Config Client sends request:                                    │
│  ┌──────────────────────────────────────┐                       │
│  │ GET /account-service/default HTTP/1.1│                       │
│  │ Host: localhost:8888                 │                       │
│  └──────────────────────────────────────┘                       │
│         ↓ Network ↓                                              │
│  ┌──────────────────────────────────────┐                       │
│  │      CONFIG SERVER (Port 8888)       │                       │
│  │      Received request for:           │                       │
│  │      Service: account-service        │                       │
│  │      Profile: default                │                       │
│  │                                      │                       │
│  │  Searches for config file:           │                       │
│  │  account-service.yml ✓ FOUND         │                       │
│  │                                      │                       │
│  │  Reads Git repo:                     │                       │
│  │  banking-config-repo/config-server/  │                       │
│  │  account-service.yml                 │                       │
│  └──────────────────────────────────────┘                       │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│ 4️⃣  CONFIG RESPONSE                                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Config Server returns full configuration:                       │
│  ┌──────────────────────────────────────┐                       │
│  │ HTTP/1.1 200 OK                      │                       │
│  │ Content-Type: application/json       │                       │
│  │                                      │                       │
│  │ {                                    │                       │
│  │   "name": "account-service",         │                       │
│  │   "profiles": ["default"],           │                       │
│  │   "propertySources": [{              │                       │
│  │     "name": "account-service.yml",   │                       │
│  │     "source": {                      │                       │
│  │       "server.port": 8082,           │                       │
│  │       "spring.datasource.url": "...  │                       │
│  │       "spring.datasource.username":  │                       │
│  │       "spring.datasource.password":  │                       │
│  │       "spring.jpa.hibernate...":     │                       │
│  │       "eureka.client.service-url...":│                       │
│  │       ...all properties...           │                       │
│  │     }                                │                       │
│  │   }]                                 │                       │
│  │ }                                    │                       │
│  └──────────────────────────────────────┘                       │
│         ↓ Network ↓                                              │
│                          ↓                                       │
│  account-service receives configuration                          │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│ 5️⃣  APPLICATION CONTEXT CREATION                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  Merge properties:                                               │
│  ┌──────────────────────────────────────┐                       │
│  │ Bootstrap Properties        Config   │                       │
│  │ + Server Properties        Server    │                       │
│  │                                      │                       │
│  │ Result:                              │                       │
│  │ • server.port = 8082                 │                       │
│  │ • spring.application.name = account  │                       │
│  │ • spring.datasource.url = mysql://..│                       │
│  │ • All JPA settings                   │                       │
│  │ • All Eureka settings                │                       │
│  │ • All other runtime properties       │                       │
│  └──────────────────────────────────────┘                       │
│                                                                  │
│  Create Spring ApplicationContext with merged properties        │
│                          ↓                                       │
│  • Initialize DataSource                                        │
│  • Setup JPA/Hibernate                                          │
│  • Create Beans                                                 │
│  • Setup Actuator                                               │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│ 6️⃣  SERVICE STARTUP COMPLETE                                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ✓ Listening on port 8082 (from config)                        │
│  ✓ Database connected (from config)                             │
│  ✓ Eureka registered (from config)                              │
│  ✓ Ready to serve requests                                      │
│                                                                  │
│  Log output:                                                     │
│  "Located environment: name=account-service,                    │
│   profiles=[default], label=null,                               │
│   version=38263a9152e9a4dcc4f4fb87b6e84831c69b10ca, state=null"│
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## Multi-Service Deployment Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                      COMPLETE SYSTEM OVERVIEW                        │
└─────────────────────────────────────────────────────────────────────┘


                          Git Repository
                   banking-config-repo (GitHub)
                              │
                              ▼
                    ┌──────────────────┐
                    │  Config Server   │
                    │   (Port 8888)    │
                    │  ┌────────────┐  │
                    │  │ Serves:    │  │
                    │  │ *.yml      │  │
                    │  │ configs    │  │
                    │  └────────────┘  │
                    └────────┬─────────┘
        ┌───────────────────┼───────────────────┐
        │                   │                   │
        ▼                   ▼                   ▼
    ┌─────────┐         ┌─────────┐        ┌──────────┐
    │ Account │         │   API   │        │Transaction
    │Service  │         │Gateway  │        │Service
    │(8082)   │         │(8080)   │        │(8083)
    └────┬────┘         └────┬────┘        └────┬─────┘
         │ MySQL              │ Route          │ MySQL
         │ accountdb         │ API            │ txndb
         │                    │ calls          │ Kafka
         └────────────┬───────┴──────────┬─────┘
                      │                  │
                      ▼                  ▼
                   ┌────────────────────────────┐
                   │  Discovery Server (Eureka) │
                   │       (Port 8761)          │
                   │  Service Registry & LB     │
                   └────────────────────────────┘
                      ▲         │         ▲
                      │ Register│ Discover│
                      │         ▼         │
                   ┌────────────────────────────┐
                   │  Auth Server (8081)        │
                   │  Notification (8084)       │
                   └────────────────────────────┘
```

---

## Configuration File Organization

```
┌─────────────────────────────────────────────────────────────────────┐
│              CONFIG SERVER DIRECTORY STRUCTURE                        │
└─────────────────────────────────────────────────────────────────────┘

config-server/
│
├── src/main/resources/
│   └── application.yml
│       ├── server.port: 8888
│       └── spring.cloud.config.server.git.uri: https://github.com/...
│           (Points Config Server to Git repository)
│
├── account-service.yml ─────────────┐
│   ├── server.port: 8082            │
│   ├── spring.datasource.*          │
│   ├── spring.jpa.*                 │
│   └── eureka.*                     │
│                                    │
├── api-gateway.yml ─────────────┐  │ Served to
│   ├── server.port: 8080         │  │ corresponding
│   ├── spring.cloud.gateway.*    │  │ services
│   └── eureka.*                  │  │
│                                 │  │
├── auth-server.yml              │  │
│   ├── server.port: 8081         │  │
│   ├── spring.datasource.*       │  │
│   ├── spring.jpa.*              │  │
│   └── eureka.*                  │  │
│                                 │  │
├── transaction-service.yml       │  │
│   ├── server.port: 8083         │  │
│   ├── spring.datasource.*       │  │
│   ├── spring.kafka.*            │  │
│   ├── spring.jpa.*              │  │
│   └── eureka.*                  │  │
│                                 │  │
├── notification-service.yml      │  │
│   ├── server.port: 8084         │  │
│   ├── spring.datasource.*       │  │
│   ├── spring.kafka.*            │  │
│   ├── spring.jpa.*              │  │
│   └── eureka.*                  │  │
│                                 │  │
└── discovery-server.yml ─────────┘  │
    ├── server.port: 8761             │
    └── eureka.client.register-with... ◄─┘

                           │
                           │ Git backed
                           ▼
        ┌──────────────────────────────────┐
        │  GitHub Repository               │
        │  banking-config-repo             │
        │                                  │
        │  Branches:                       │
        │  • main                          │
        │  • development                   │
        │  • staging                       │
        │  • production                    │
        │                                  │
        │  Config Server pulls latest     │
        │  config on startup               │
        └──────────────────────────────────┘
```

---

## Environment-Specific Configuration Pattern

```
┌─────────────────────────────────────────────────────────────────────┐
│            MULTI-ENVIRONMENT CONFIGURATION SETUP                     │
└─────────────────────────────────────────────────────────────────────┘

Git Repository: banking-config-repo
│
├── config-server/
│   ├── account-service.yml             (default/dev)
│   ├── account-service-prod.yml        (production)
│   ├── account-service-staging.yml     (staging)
│   │
│   ├── transaction-service.yml         (default/dev)
│   ├── transaction-service-prod.yml    (production)
│   ├── transaction-service-staging.yml (staging)
│   └── ... (more services)


Service Startup with Profile:

Development (Default):
┌─────────────────────────────────────────┐
│ SPRING_PROFILES_ACTIVE=default          │
│ ↓                                       │
│ Loads: account-service.yml            │
│ (local MySQL, less logging)            │
└─────────────────────────────────────────┘

Production:
┌─────────────────────────────────────────┐
│ SPRING_PROFILES_ACTIVE=prod            │
│ ↓                                       │
│ Loads: account-service-prod.yml        │
│ (production DB, enhanced monitoring)    │
│ (no debug logging)                     │
│ (optimized Kafka settings)              │
└─────────────────────────────────────────┘

Staging:
┌─────────────────────────────────────────┐
│ SPRING_PROFILES_ACTIVE=staging         │
│ ↓                                       │
│ Loads: account-service-staging.yml     │
│ (staging DB, test data)                 │
│ (verbose logging for debugging)         │
│ (staging Kafka settings)                │
└─────────────────────────────────────────┘

Same JAR file → Different configs → Different environments
```

---

## Summary

```
┌──────────────────────────────────────────────────────────────┐
│                    TRANSFORMATION                             │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  BEFORE: Properties scattered across 6+ services            │
│          Duplicated configuration files                      │
│          Difficult to maintain and update                   │
│          Requires rebuild for any config change            │
│          Hard to support multiple environments             │
│                                                              │
│  AFTER:  All properties centralized in Config Server       │
│          Single source of truth                            │
│          Easy maintenance and updates                      │
│          No rebuild needed for config changes              │
│          Supports multiple environments via profiles        │
│          Git-backed version control                        │
│          Smaller microservice JARs                         │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```
