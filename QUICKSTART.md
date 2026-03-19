# Quick Start Guide - Banking System Microservices

## Prerequisites
- Java 21+
- Maven 3.8+
- MySQL Server (running on localhost:3306)
- Kafka (running on localhost:9092) - for notification service

## Startup Order (Important!)

### Step 1: Start Config Server
```powershell
cd C:\Users\Dinesh Naidu\Downloads\banking-2026\config-server
mvn clean install
mvn spring-boot:run
```
**Expected**: Server runs on http://localhost:8888
**Log to look for**: `Started ConfigServerApplication`

### Step 2: Start Discovery Server (Eureka)
```powershell
cd C:\Users\Dinesh Naidu\Downloads\banking-2026\discovery-server
mvn clean install
mvn spring-boot:run
```
**Expected**: Server runs on http://localhost:8761
**Log to look for**: `Started EurekaServerApplication` or similar
**Test**: Visit http://localhost:8761/

### Step 3: Start Microservices (in any order after Step 1 & 2)

#### Account Service
```powershell
cd C:\Users\Dinesh Naidu\Downloads\banking-2026\account-service
mvn clean install
mvn spring-boot:run
```
**Expected**: 
- Port: 8082
- Registers with Eureka
- Connects to MySQL: accountdb
- Log: `Located environment: name=account-service, profiles=[default]`

#### API Gateway
```powershell
cd C:\Users\Dinesh Naidu\Downloads\banking-2026\api-gateway
mvn clean install
mvn spring-boot:run
```
**Expected**: 
- Port: 8080
- Routes requests to other services
- Log: `Located environment: name=api-gateway, profiles=[default]`

#### Auth Server
```powershell
cd C:\Users\Dinesh Naidu\Downloads\banking-2026\auth-server
mvn clean install
mvn spring-boot:run
```
**Expected**: 
- Port: 8081
- Connects to MySQL: authdb
- Log: `Located environment: name=auth-server, profiles=[default]`

#### Transaction Service
```powershell
cd C:\Users\Dinesh Naidu\Downloads\banking-2026\transcation-service
mvn clean install
mvn spring-boot:run
```
**Expected**: 
- Port: 8083
- Connects to MySQL: transactiondb
- Connects to Kafka: localhost:9092
- Log: `Located environment: name=transcation-service, profiles=[default]`

#### Notification Service
```powershell
cd C:\Users\Dinesh Naidu\Downloads\banking-2026\notification-service
mvn clean install
mvn spring-boot:run
```
**Expected**: 
- Port: 8084
- Connects to MySQL: notificationdb
- Connects to Kafka: localhost:9092
- Log: `Located environment: name=notification-service, profiles=[default]`

## Verify Everything is Running

### 1. Check Eureka Dashboard
Open browser: http://localhost:8761/
- Should show all registered services

### 2. Check Config Server is Serving Configurations
```powershell
curl http://localhost:8888/account-service/default
```
Should return JSON with account-service configuration

### 3. Check API Gateway Health
```powershell
curl http://localhost:8080/actuator/health
```
Should return: `{"status":"UP"}`

### 4. Check Database Connections
Each service should have created its tables:
- accountdb (account-service)
- authdb (auth-server)
- transactiondb (transaction-service)
- notificationdb (notification-service)

## All-in-One Startup Script (PowerShell)

Create a file named `startup-all.ps1`:

```powershell
# Config Server
Start-Process powershell -ArgumentList @("-NoExit", "-Command", "cd 'C:\Users\Dinesh Naidu\Downloads\banking-2026\config-server'; mvn clean install; mvn spring-boot:run")
Start-Sleep -Seconds 10

# Discovery Server
Start-Process powershell -ArgumentList @("-NoExit", "-Command", "cd 'C:\Users\Dinesh Naidu\Downloads\banking-2026\discovery-server'; mvn clean install; mvn spring-boot:run")
Start-Sleep -Seconds 10

# Account Service
Start-Process powershell -ArgumentList @("-NoExit", "-Command", "cd 'C:\Users\Dinesh Naidu\Downloads\banking-2026\account-service'; mvn clean install; mvn spring-boot:run")

# API Gateway
Start-Process powershell -ArgumentList @("-NoExit", "-Command", "cd 'C:\Users\Dinesh Naidu\Downloads\banking-2026\api-gateway'; mvn clean install; mvn spring-boot:run")

# Auth Server
Start-Process powershell -ArgumentList @("-NoExit", "-Command", "cd 'C:\Users\Dinesh Naidu\Downloads\banking-2026\auth-server'; mvn clean install; mvn spring-boot:run")

# Transaction Service
Start-Process powershell -ArgumentList @("-NoExit", "-Command", "cd 'C:\Users\Dinesh Naidu\Downloads\banking-2026\transcation-service'; mvn clean install; mvn spring-boot:run")

# Notification Service
Start-Process powershell -ArgumentList @("-NoExit", "-Command", "cd 'C:\Users\Dinesh Naidu\Downloads\banking-2026\notification-service'; mvn clean install; mvn spring-boot:run")

Write-Host "All services started!"
```

Run it:
```powershell
.\startup-all.ps1
```

## Configuration Summary

| Service | Port | Database | Kafka | Config Source |
|---------|------|----------|-------|---|
| Config Server | 8888 | - | - | Git Repo |
| Eureka | 8761 | - | - | Local |
| Account Service | 8082 | accountdb | - | Config Server |
| API Gateway | 8080 | - | - | Config Server |
| Auth Server | 8081 | authdb | - | Config Server |
| Transaction Service | 8083 | transactiondb | Yes | Config Server |
| Notification Service | 8084 | notificationdb | Yes | Config Server |

## Common Issues & Solutions

### Issue: "Connection refused" to Config Server
**Solution**: Make sure config-server is started first and running on port 8888

### Issue: "UnknownHostException: localhost"
**Solution**: Check if services are trying to connect before config server is running

### Issue: Database connection errors
**Solution**: 
1. Verify MySQL is running
2. Create required databases: accountdb, authdb, transactiondb, notificationdb
3. Check credentials in config server files match MySQL setup

### Issue: Kafka connection errors
**Solution**: 
1. Verify Kafka is running on localhost:9092
2. Only transaction-service and notification-service need Kafka

### Issue: "Spring Boot [3.3.2] is not compatible"
**Solution**: This is expected warning if using Spring Boot 3.3.2. Config says to use 3.4.x, but 3.3.2 should work with optional config server

## Health Checks

```powershell
# Check all services health
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
        Write-Host "✓ $($service.Key): OK" -ForegroundColor Green
    } catch {
        Write-Host "✗ $($service.Key): FAILED" -ForegroundColor Red
    }
}
```

## Monitoring Logs

Look for these success indicators:

1. **Config Server**: `ConfigServerApplication started`
2. **Discovery Server**: `Started EurekaServerApplication`
3. **Other Services**: `Located environment: name={service-name}, profiles=[default]`
4. **Service Registration**: `Registering application {SERVICE-NAME} with eureka with initial status UP`

## Next Steps

- API Documentation: Check Swagger/OpenAPI endpoints
- Test APIs through API Gateway: http://localhost:8080
- Monitor through Eureka dashboard: http://localhost:8761
