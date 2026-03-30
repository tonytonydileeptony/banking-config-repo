#!/usr/bin/env pwsh
# AWS Environment Variables Check and Setup Script
# Purpose: Verify and set AWS credentials for the banking application

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "AWS Environment Variables Manager" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Function to check if variable is set
function Check-EnvVariable {
    param(
        [string]$VariableName
    )

    $value = Get-Item -Path "env:$VariableName" -ErrorAction SilentlyContinue

    if ($value) {
        $displayValue = if ($value.Value.Length -gt 10) {
            $value.Value.Substring(0, 4) + "..." + $value.Value.Substring($value.Value.Length - 4)
        } else {
            "****" + $value.Value.Substring([Math]::Max(0, $value.Value.Length - 2))
        }
        Write-Host "✓ $VariableName is SET" -ForegroundColor Green
        Write-Host "  Masked value: $displayValue" -ForegroundColor Gray
        return $true
    } else {
        Write-Host "✗ $VariableName is NOT SET" -ForegroundColor Red
        return $false
    }
}

# Check current state
Write-Host "CHECKING CURRENT STATE:" -ForegroundColor Yellow
Write-Host ""

$hasAccessKey = Check-EnvVariable "AWS_ACCESS_KEY_ID"
$hasSecretKey = Check-EnvVariable "AWS_SECRET_ACCESS_KEY"

Write-Host ""
Write-Host "---" -ForegroundColor Gray
Write-Host ""

if ($hasAccessKey -and $hasSecretKey) {
    Write-Host "✓ All AWS credentials are configured!" -ForegroundColor Green
    Write-Host "You can now run the banking services." -ForegroundColor Green
} else {
    Write-Host "⚠ Missing AWS credentials!" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "TO SET CREDENTIALS TEMPORARILY (current session only):" -ForegroundColor Cyan
    Write-Host '  $env:AWS_ACCESS_KEY_ID = "your_access_key_here"' -ForegroundColor Gray
    Write-Host '  $env:AWS_SECRET_ACCESS_KEY = "your_secret_key_here"' -ForegroundColor Gray
    Write-Host ""
    Write-Host "TO SET CREDENTIALS PERMANENTLY (current user):" -ForegroundColor Cyan
    Write-Host '  [Environment]::SetEnvironmentVariable("AWS_ACCESS_KEY_ID", "your_key", "User")' -ForegroundColor Gray
    Write-Host '  [Environment]::SetEnvironmentVariable("AWS_SECRET_ACCESS_KEY", "your_secret", "User")' -ForegroundColor Gray
    Write-Host ""
    Write-Host "THEN close and reopen PowerShell for changes to take effect." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "---" -ForegroundColor Gray
Write-Host ""

# List all AWS-related variables
Write-Host "ALL AWS-RELATED ENVIRONMENT VARIABLES:" -ForegroundColor Yellow
Write-Host ""

$awsVars = Get-ChildItem env: | Where-Object { $_.Name -like "*AWS*" } | Sort-Object Name

if ($awsVars) {
    $awsVars | ForEach-Object {
        $displayValue = if ($_.Value.Length -gt 10) {
            $_.Value.Substring(0, 4) + "..." + $_.Value.Substring($_.Value.Length - 4)
        } else {
            "****"
        }
        Write-Host "$($_.Name): $displayValue" -ForegroundColor Gray
    }
} else {
    Write-Host "No AWS environment variables found" -ForegroundColor Gray
}

Write-Host ""
Write-Host "---" -ForegroundColor Gray
Write-Host ""

# Additional helpful info
Write-Host "QUICK COMMANDS:" -ForegroundColor Cyan
Write-Host ""
Write-Host "Check all env vars:        Get-ChildItem env:" -ForegroundColor Gray
Write-Host "Search for specific var:   Get-ChildItem env: | Where-Object { \`$_.Name -like '*JAVA*' }" -ForegroundColor Gray
Write-Host "View single variable:      \`$env:VARIABLE_NAME" -ForegroundColor Gray
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
