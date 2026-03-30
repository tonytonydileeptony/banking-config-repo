# How to Check Environment Variables - Complete Guide

## Quick Answer

### Check AWS Variables (PowerShell)
```powershell
$env:AWS_ACCESS_KEY_ID
$env:AWS_SECRET_ACCESS_KEY
```

### Check AWS Variables (CMD)
```cmd
echo %AWS_ACCESS_KEY_ID%
echo %AWS_SECRET_ACCESS_KEY%
```

---

## 1️⃣ Check a Single Environment Variable

### PowerShell
```powershell
# Check AWS access key
$env:AWS_ACCESS_KEY_ID

# Check AWS secret key
$env:AWS_SECRET_ACCESS_KEY

# Check any variable
$env:VARIABLE_NAME

# Check PATH
$env:PATH

# Check JAVA_HOME
$env:JAVA_HOME
```

### Command Prompt (CMD)
```cmd
REM Check AWS access key
echo %AWS_ACCESS_KEY_ID%

REM Check AWS secret key
echo %AWS_SECRET_ACCESS_KEY%

REM Check any variable
echo %VARIABLE_NAME%

REM Check PATH
echo %PATH%

REM Check JAVA_HOME
echo %JAVA_HOME%
```

---

## 2️⃣ List ALL Environment Variables

### PowerShell (Recommended)
```powershell
# Simple list
Get-ChildItem env:

# Formatted nicely
Get-ChildItem env: | Sort-Object Name | Format-Table -AutoSize

# Save to file
Get-ChildItem env: | Out-File -FilePath "env-vars.txt"
```

### Command Prompt
```cmd
REM Show all variables
set

REM Show and save to file
set > env-vars.txt
```

---

## 3️⃣ Find Specific Variables (Search)

### PowerShell
```powershell
# Find all AWS variables
Get-ChildItem env: | Where-Object { $_.Name -like "*AWS*" }

# Find all JAVA variables
Get-ChildItem env: | Where-Object { $_.Name -like "*JAVA*" }

# Find all SPRING variables
Get-ChildItem env: | Where-Object { $_.Name -like "*SPRING*" }

# Find variables containing "PATH"
Get-ChildItem env: | Where-Object { $_.Name -like "*PATH*" }
```

### Command Prompt
```cmd
REM Find AWS variables
set | findstr AWS

REM Find JAVA variables
set | findstr JAVA

REM Find SPRING variables
set | findstr SPRING

REM Find PATH variables
set | findstr PATH
```

---

## 4️⃣ Set Environment Variables (Temporary)

These variables are only set for the current session and will be lost when you close PowerShell/CMD.

### PowerShell
```powershell
# Set single variable
$env:AWS_ACCESS_KEY_ID = "your_access_key_here"

# Set multiple variables
$env:AWS_ACCESS_KEY_ID = "AKIAIOSFODNN7EXAMPLE"
$env:AWS_SECRET_ACCESS_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"

# Verify it was set
$env:AWS_ACCESS_KEY_ID
$env:AWS_SECRET_ACCESS_KEY
```

### Command Prompt
```cmd
REM Set single variable
set AWS_ACCESS_KEY_ID=your_access_key_here

REM Set multiple variables
set AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE
set AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY

REM Verify
echo %AWS_ACCESS_KEY_ID%
echo %AWS_SECRET_ACCESS_KEY%
```

---

## 5️⃣ Set Environment Variables (Permanent)

### PowerShell - Current User (Recommended)
```powershell
# Set for current user (persists after restart)
[Environment]::SetEnvironmentVariable("AWS_ACCESS_KEY_ID", "your_access_key", "User")
[Environment]::SetEnvironmentVariable("AWS_SECRET_ACCESS_KEY", "your_secret_key", "User")

# Verify (may need to restart PowerShell)
$env:AWS_ACCESS_KEY_ID
$env:AWS_SECRET_ACCESS_KEY
```

### PowerShell - System-Wide (Requires Admin)
```powershell
# Run PowerShell as Administrator first!
# Then set for entire system
[Environment]::SetEnvironmentVariable("AWS_ACCESS_KEY_ID", "your_key", "Machine")
[Environment]::SetEnvironmentVariable("AWS_SECRET_ACCESS_KEY", "your_secret", "Machine")
```

### Command Prompt - Current User
```cmd
REM Run CMD as Administrator!
REM Set for current user
setx AWS_ACCESS_KEY_ID your_access_key_here
setx AWS_SECRET_ACCESS_KEY your_secret_key_here

REM Verify (need to open new CMD window)
echo %AWS_ACCESS_KEY_ID%
```

### Command Prompt - System-Wide
```cmd
REM Run CMD as Administrator!
REM Set for entire system
setx /M AWS_ACCESS_KEY_ID your_access_key_here
setx /M AWS_SECRET_ACCESS_KEY your_secret_key_here

REM Verify (need to open new CMD window)
echo %AWS_ACCESS_KEY_ID%
```

### Windows GUI (Easiest)
1. Press `Win + X` → Click "System"
2. Click "Advanced system settings" (left panel)
3. Click "Environment Variables" button (bottom right)
4. Click "New..." button
5. Enter variable name: `AWS_ACCESS_KEY_ID`
6. Enter variable value: `your_access_key_here`
7. Click OK
8. Repeat for `AWS_SECRET_ACCESS_KEY`
9. **Important:** Close and reopen PowerShell/CMD for changes to take effect

---

## 6️⃣ Verify Before Running Services

### Quick Check Script (PowerShell)
```powershell
Write-Host "Checking AWS credentials..."
if ($env:AWS_ACCESS_KEY_ID) {
    Write-Host "✓ AWS_ACCESS_KEY_ID is set"
} else {
    Write-Host "✗ AWS_ACCESS_KEY_ID is NOT set"
}

if ($env:AWS_SECRET_ACCESS_KEY) {
    Write-Host "✓ AWS_SECRET_ACCESS_KEY is set"
} else {
    Write-Host "✗ AWS_SECRET_ACCESS_KEY is NOT set"
}
```

### Quick Check Script (CMD)
```cmd
@echo off
if defined AWS_ACCESS_KEY_ID (
    echo ✓ AWS_ACCESS_KEY_ID is set
) else (
    echo ✗ AWS_ACCESS_KEY_ID is NOT set
)

if defined AWS_SECRET_ACCESS_KEY (
    echo ✓ AWS_SECRET_ACCESS_KEY is set
) else (
    echo ✗ AWS_SECRET_ACCESS_KEY is NOT set
)
```

---

## 7️⃣ For Your Banking Project

### Step-by-Step Guide

#### 1. Get Your AWS Credentials
- Log in to AWS Management Console
- Go to IAM → Users → Your User → Security Credentials
- Create Access Key (if you don't have one)
- Copy Access Key ID and Secret Access Key
- **Keep these safe!** Never share them.

#### 2. Set Temporary Credentials (for testing)
```powershell
cd "C:\Users\Dinesh Naidu\Downloads\banking-2026"

# Set your actual credentials (replace with real values)
$env:AWS_ACCESS_KEY_ID = "AKIAIOSFODNN7EXAMPLE"
$env:AWS_SECRET_ACCESS_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"

# Verify
$env:AWS_ACCESS_KEY_ID
$env:AWS_SECRET_ACCESS_KEY

# Now run your service
cd account-service
mvn spring-boot:run
```

#### 3. Set Permanent Credentials (recommended)
```powershell
# As Administrator, set once
[Environment]::SetEnvironmentVariable("AWS_ACCESS_KEY_ID", "AKIAIOSFODNN7EXAMPLE", "User")
[Environment]::SetEnvironmentVariable("AWS_SECRET_ACCESS_KEY", "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY", "User")

# Close and reopen PowerShell
# Then verify
$env:AWS_ACCESS_KEY_ID
$env:AWS_SECRET_ACCESS_KEY
```

---

## 8️⃣ Troubleshooting

### Variable Returns Empty
```powershell
# This means the variable is not set
$env:AWS_ACCESS_KEY_ID  # Returns nothing or blank

# Solution: Set it first
$env:AWS_ACCESS_KEY_ID = "your_key"
```

### Variable Set Temporarily But Lost After Restart
- This is normal behavior for temporary variables
- Use permanent method (section 5) to make them persist

### Changes Not Taking Effect
- **You must close and reopen PowerShell/CMD**
- Or use GUI method and restart computer
- Check with `$env:VARIABLE_NAME` to verify

### Can't Set Permanent Variables
- You may need Administrator rights
- Right-click PowerShell → "Run as Administrator"
- Then run the `[Environment]::SetEnvironmentVariable()` command

---

## 9️⃣ Security Best Practices

### ✅ DO:
- Store credentials in environment variables (not in code)
- Use different credentials for different environments (dev, test, prod)
- Rotate credentials regularly
- Use AWS IAM roles when possible
- Add `.env` to `.gitignore`

### ❌ DON'T:
- Hardcode credentials in source code
- Share credentials in chat, email, or documents
- Commit credentials to Git repository
- Use the same credentials for all environments
- Leave credentials in console history

### Check Git History for Exposed Credentials
```powershell
# Search for AWS keys in git history
git log -S "AKIA" --all
git log -S "aws_secret_access_key" --all
```

---

## 📋 Quick Reference Table

| What You Want | PowerShell | CMD |
|---|---|---|
| Check one variable | `$env:VAR_NAME` | `echo %VAR_NAME%` |
| List all variables | `Get-ChildItem env:` | `set` |
| Search variables | `Get-ChildItem env: \| Where-Object { $_.Name -like "*AWS*" }` | `set \| findstr AWS` |
| Set temporary | `$env:VAR = "value"` | `set VAR=value` |
| Set permanent (user) | `[Environment]::SetEnvironmentVariable("VAR", "value", "User")` | `setx VAR value` |
| Set permanent (system) | `[Environment]::SetEnvironmentVariable("VAR", "value", "Machine")` | `setx /M VAR value` |

---

## 🎯 Most Common Commands

```powershell
# PowerShell - For your banking project

# 1. Check if AWS credentials are set
$env:AWS_ACCESS_KEY_ID
$env:AWS_SECRET_ACCESS_KEY

# 2. Find all AWS variables
Get-ChildItem env: | Where-Object { $_.Name -like "*AWS*" }

# 3. Set temporarily (for current session)
$env:AWS_ACCESS_KEY_ID = "your_key"
$env:AWS_SECRET_ACCESS_KEY = "your_secret"

# 4. List all environment variables
Get-ChildItem env: | Sort-Object Name

# 5. Set permanently (current user)
[Environment]::SetEnvironmentVariable("AWS_ACCESS_KEY_ID", "your_key", "User")
[Environment]::SetEnvironmentVariable("AWS_SECRET_ACCESS_KEY", "your_secret", "User")
```

---

**Last Updated:** March 30, 2026
**For Project:** Banking System Microservices
