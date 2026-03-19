# Bootstrap Properties vs Runtime Properties

## Understanding the Difference

### Bootstrap Properties
**Location**: `application.yml` in each microservice  
**When loaded**: BEFORE Spring context is created (during bootstrap phase)  
**Used for**: Connecting to Config Server, application name, profiles

```yaml
spring:
  application:
    name: account-service
  config:
    import: optional:configserver:http://localhost:8888
```

### Runtime Properties
**Location**: Config Server files (`account-service.yml`, etc.)  
**When loaded**: AFTER connecting to Config Server  
**Used for**: Database configs, logging, Kafka, Eureka, JPA, routes, etc.

```yaml
# config-server/account-service.yml
server:
  port: 8082

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/accountdb
    username: root
    password: root
    # ... more properties
```

## The Loading Sequence

```
1. JVM starts
   ↓
2. Spring Boot reads application.yml (BOOTSTRAP PROPERTIES)
   ├── spring.application.name = "account-service"
   └── spring.config.import = "optional:configserver:http://localhost:8888"
   ↓
3. Spring Cloud Config Client activates
   ↓
4. Config Client contacts Config Server
   Request: GET /account-service/default
   ↓
5. Config Server returns account-service.yml content (RUNTIME PROPERTIES)
   ├── server.port = 8082
   ├── spring.datasource.url = jdbc:mysql://localhost:3306/accountdb
   ├── spring.jpa.hibernate.ddl-auto = update
   ├── eureka configuration
   └── ... all other properties
   ↓
6. Spring creates ApplicationContext with all properties
   ↓
7. Application is ready to serve requests
```

## Key Points

### ✓ Must be in application.yml (Bootstrap)
- `spring.application.name` - Identifies the service
- `spring.config.import` - Tells where Config Server is
- Any properties needed BEFORE Config Server connection

### ✓ Should be in Config Server (Runtime)
- Database credentials and URLs
- Server port
- Logging configuration
- External service URLs (Eureka, Kafka, etc.)
- JPA/Hibernate settings
- API Gateway routes
- Application-specific business logic configurations

### ✗ Should NOT be in both
- Avoid duplication
- Single source of truth
- Easier maintenance

## Current Setup Validation

### Account Service Example

**Before (WRONG - everything in local file)**:
```yaml
# application.yml - Had 30 lines of config
server:
  port: 8082
spring:
  datasource: ...
  application:
    name: account-service
  config:
    import: optional:configserver:http://localhost:8888
  jpa: ...
```

**After (CORRECT - minimal bootstrap)**:
```yaml
# application.yml - Only 5 lines
spring:
  application:
    name: account-service
  config:
    import: optional:configserver:http://localhost:8888
```

**Config Server serves** (`config-server/account-service.yml`):
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
  instance:
    hostname: localhost
    prefer-ip-address: true
```

## Why This Approach is Better

### 1. **Centralized Configuration**
```
BEFORE:
├── account-service/application.yml (30 lines)
├── api-gateway/application.yml (30 lines)
├── auth-server/application.yml (30 lines)
└── transcation-service/application.yml (40 lines)
Total: ~130 lines of config scattered

AFTER:
├── config-server/account-service.yml (25 lines)
├── config-server/api-gateway.yml (13 lines)
├── config-server/auth-server.yml (24 lines)
├── config-server/transaction-service.yml (26 lines)
└── Each service's application.yml: ~5 lines
Total: ~65 lines of bootstrap + ~90 lines config = 155 lines, but centralized!
```

### 2. **Environment-Specific Configuration**
Easily support dev, staging, prod without code changes:
```
config-server/
├── account-service.yml          (default/dev)
├── account-service-prod.yml     (production)
├── account-service-staging.yml  (staging)
```

### 3. **Version Control**
Config is in Git repository, not embedded in JARs:
- Track config changes
- Rollback to previous config
- Audit trail of changes

### 4. **No Rebuild Required**
Change config → Push to repo → Services pick up changes  
No need to rebuild and redeploy JAR files

### 5. **Secrets Management**
Config Server can encrypt sensitive properties:
```yaml
# In config-server
spring:
  cloud:
    config:
      server:
        encrypt:
          enabled: true
```

## Best Practices

### ✓ DO

1. **Keep application.yml minimal**
   ```yaml
   spring:
     application:
       name: my-service
     config:
       import: configserver:http://config-server:8888
   ```

2. **Use meaningful application names**
   ```yaml
   spring:
     application:
       name: account-service  # Must match account-service.yml in config server
   ```

3. **Document config properties**
   - Keep README in config-server repo
   - Document each property's purpose

4. **Test configuration**
   ```powershell
   curl http://localhost:8888/account-service/default
   ```

5. **Monitor config changes**
   - Review Git commits in config repo
   - Test config changes in dev first

### ✗ DON'T

1. **Don't put production secrets in application.yml**
   ```yaml
   # WRONG!
   spring:
     datasource:
       password: mySecretPassword
   ```

2. **Don't mix bootstrap and runtime properties**
   ```yaml
   # WRONG - mixing both types in application.yml
   spring:
     application:
       name: account-service
     datasource:
       url: ...
     config:
       import: configserver:...
   ```

3. **Don't have duplicate properties**
   - If it's in application.yml, don't put it in config server
   - If it's in config server, don't put it in application.yml

4. **Don't make config server optional in production**
   ```yaml
   # Development (OK)
   import: optional:configserver:http://localhost:8888
   
   # Production (NOT OK)
   import: optional:configserver:http://config-server:8888
   
   # Production (CORRECT)
   import: configserver:http://config-server:8888
   ```

## Troubleshooting Config Loading

### Issue: "Could not locate PropertySource"
**Cause**: Config server is down or unreachable  
**Solution**: Start config server first

### Issue: Properties not updated
**Cause**: Config client didn't refresh  
**Solution**: Restart service or use `@RefreshScope` with POST /actuator/refresh

### Issue: Wrong config loaded
**Cause**: Application name doesn't match config file name  
**Solution**: Verify `spring.application.name` matches config file name:
```
spring.application.name: account-service  →  account-service.yml
spring.application.name: api-gateway      →  api-gateway.yml
```

### Issue: "ClassCastException" for config properties
**Cause**: Type mismatch in config yaml  
**Solution**: Ensure proper YAML syntax and types:
```yaml
# CORRECT
server:
  port: 8080  # Number, not string

# WRONG
server:
  port: "8080"  # String instead of number (sometimes)
```

## Reference: All Services Configuration

| Service | Bootstrap Config | Runtime Config |
|---------|---|---|
| **account-service** | `application.yml` (5 lines) | `config-server/account-service.yml` (25 lines) |
| **api-gateway** | `application.yml` (5 lines) | `config-server/api-gateway.yml` (13 lines) |
| **auth-server** | `application.yml` (5 lines) | `config-server/auth-server.yml` (24 lines) |
| **transaction-service** | `application.yml` (6 lines) | `config-server/transaction-service.yml` (26 lines) |
| **notification-service** | `application.yml` (5 lines) | `config-server/notification-service.yml` (34 lines) |
| **discovery-server** | `application.yml` (5 lines) | `config-server/discovery-server.yml` (7 lines) |

## Testing Configuration Changes

### 1. Modify config in config-server/
```yaml
# config-server/account-service.yml
server:
  port: 8082  # Change from 8082 to 8090 for testing
```

### 2. Push to Git
```powershell
cd config-server
git add account-service.yml
git commit -m "Change account service port to 8090"
git push origin main
```

### 3. Refresh service (if using @RefreshScope)
```powershell
curl -X POST http://localhost:8082/actuator/refresh
```

### 4. Or restart service
```powershell
# Stop and restart account-service
```

### 5. Verify
```powershell
curl http://localhost:8090/actuator/health  # If it moved to 8090
```

## Summary

✅ **Bootstrap Properties** (local `application.yml`):
- Minimal configuration
- Only service name and config server location
- Same for all environments

✅ **Runtime Properties** (Config Server):
- All environment-specific configuration
- Database, Kafka, Eureka, etc.
- Centralized management
- Version controlled

✅ **Result**:
- Cleaner code
- Better maintainability
- Environment flexibility
- No rebuild for config changes
