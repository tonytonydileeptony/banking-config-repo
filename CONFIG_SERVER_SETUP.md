# Config Server Setup - Properties Management

## Overview
All microservices have been configured to load properties from the **Config Server** instead of maintaining them locally in `application.yml` files. This ensures centralized configuration management.

## Architecture

```
Config Server (Port 8888)
    ├── Git Repository: https://github.com/tonytonydileeptony/banking-config-repo
    └── Local Config Files:
        ├── account-service.yml
        ├── api-gateway.yml
        ├── auth-server.yml
        ├── transaction-service.yml
        ├── notification-service.yml
        ├── discovery-server.yml

Microservices (minimal application.yml)
    ├── Account Service (Port 8082)
    ├── API Gateway (Port 8080)
    ├── Auth Server (Port 8081)
    ├── Transaction Service (Port 8083)
    ├── Notification Service (Port 8084)
    └── Discovery Server (Port 8761)
```

## Changes Made

### 1. **Account Service**
   - **Location**: `account-service/src/main/resources/application.yml`
   - **Before**: Contained datasource, JPA, port configuration
   - **After**: Only contains bootstrap properties
   ```yaml
   spring:
     application:
       name: account-service
     config:
       import: optional:configserver:http://localhost:8888
   ```
   - **Properties loaded from**: `config-server/account-service.yml`

### 2. **API Gateway**
   - **Location**: `api-gateway/src/main/resources/application.yml`
   - **Before**: Contained gateway routes, cloud config, logging
   - **After**: Only contains bootstrap properties
   ```yaml
   spring:
     application:
       name: api-gateway
     config:
       import: optional:configserver:http://localhost:8888
   ```
   - **Properties loaded from**: `config-server/api-gateway.yml`

### 3. **Transaction Service**
   - **Location**: `transcation-service/src/main/resources/application.yml`
   - **Before**: Contained datasource, Kafka, JPA, Eureka configuration
   - **After**: Only contains bootstrap properties
   ```yaml
   spring:
     application:
       name: transcation-service
     config:
       import: optional:configserver:http://localhost:8888
   ```
   - **Properties loaded from**: `config-server/transaction-service.yml`

### 4. **Auth Server**
   - **Location**: `auth-server/src/main/resources/application.yml`
   - **Note**: Already minimal, only bootstrap properties
   - **Properties loaded from**: `config-server/auth-server.yml`

### 5. **Notification Service**
   - **Location**: `notification-service/src/main/resources/application.yml`
   - **Note**: Already minimal, only bootstrap properties
   - **Properties loaded from**: `config-server/notification-service.yml`

### 6. **Discovery Server (Eureka)**
   - **Location**: `discovery-server/src/main/resources/application.yml`
   - **After**: Only contains bootstrap properties
   ```yaml
   spring:
     application:
       name: discovery-server
     config:
       import: optional:configserver:http://localhost:8888
   ```
   - **Properties loaded from**: `config-server/discovery-server.yml`

## How It Works

1. **Service Startup Sequence**:
   - Microservice reads `application.yml` (bootstrap)
   - Microservice contacts Config Server at `http://localhost:8888`
   - Config Server serves `{service-name}.yml` based on `spring.application.name`
   - All remaining properties are loaded from config server

2. **Config Server Flow**:
   ```
   Microservice Request
        ↓
   Config Server receives: GET /config-server/{app-name}/default
        ↓
   Config Server reads: config-server/{app-name}.yml
        ↓
   Returns properties to microservice
        ↓
   Properties are loaded and available to application
   ```

## Centralized Configuration Files

All configuration is now centralized in:
- `config-server/account-service.yml` - Database, JPA, Eureka settings
- `config-server/api-gateway.yml` - Gateway routes, Eureka settings
- `config-server/auth-server.yml` - Database, JPA, Eureka settings
- `config-server/transaction-service.yml` - Database, Kafka, JPA, Eureka settings
- `config-server/notification-service.yml` - Database, Kafka, JPA, Eureka settings
- `config-server/discovery-server.yml` - Eureka server configuration

## Benefits

✅ **Centralized Management**: All properties in one location  
✅ **Easier Updates**: Change properties without rebuilding JAR files  
✅ **Environment-Specific Config**: Can have dev, staging, prod configurations  
✅ **Version Control**: Git-backed configuration repository  
✅ **Reduced Duplication**: No duplicate properties in multiple files  
✅ **Dynamic Updates**: Potential to reload properties without restart (with refresh scope)

## Testing the Setup

### 1. Start Config Server First
```powershell
cd config-server
mvn spring-boot:run
```

### 2. Verify Config Server is Running
```powershell
curl http://localhost:8888/account-service/default
```

### 3. Start Other Services
```powershell
# Start in separate terminals
cd discovery-server && mvn spring-boot:run
cd account-service && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
cd auth-server && mvn spring-boot:run
cd transcation-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
```

### 4. Verify Properties Are Loaded
Look for log messages like:
```
o.s.c.c.c.ConfigServerConfigDataLoader : Located environment: name=account-service, profiles=[default]
```

## Troubleshooting

### Issue: "400: Name for argument of type [java.lang.Long] not specified"
- This is a parameter name inference issue
- Ensure all services have the `-parameters` flag in compiler plugin

### Issue: Config Server Not Found
- Ensure Config Server is running on port 8888
- Check firewall settings
- Verify URL: `http://localhost:8888`

### Issue: Properties Not Loading
- Check service name matches config file name
- Verify config file exists in `config-server/`
- Check Config Server logs for errors
- Ensure `spring.config.import` is present in application.yml

## Notes

- The optional prefix in `optional:configserver:http://localhost:8888` means the app will start even if config server is unavailable (not recommended for production)
- For production, consider removing `optional:` to enforce config server availability
- Consider using Spring Cloud Config with encryption for sensitive properties
