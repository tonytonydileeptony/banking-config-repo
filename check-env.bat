@echo off
REM Environment Variables Check Script for CMD
REM This script helps you check and understand environment variables
REM Usage: Simply run this batch file

cls
echo ========================================
echo Environment Variables Checker
echo ========================================
echo.

REM Check AWS credentials
echo CHECKING AWS CREDENTIALS:
echo.

if defined AWS_ACCESS_KEY_ID (
    echo [OK] AWS_ACCESS_KEY_ID is SET
) else (
    echo [MISSING] AWS_ACCESS_KEY_ID is NOT SET
)

if defined AWS_SECRET_ACCESS_KEY (
    echo [OK] AWS_SECRET_ACCESS_KEY is SET
) else (
    echo [MISSING] AWS_SECRET_ACCESS_KEY is NOT SET
)

echo.
echo ========================================
echo.
echo ALL AWS-RELATED VARIABLES:
echo.
set | findstr AWS

if errorlevel 1 (
    echo No AWS environment variables found
)

echo.
echo ========================================
echo.
echo TO SET VARIABLES TEMPORARILY (current session only):
echo.
echo   set AWS_ACCESS_KEY_ID=your_access_key_here
echo   set AWS_SECRET_ACCESS_KEY=your_secret_key_here
echo.

echo TO SET VARIABLES PERMANENTLY:
echo.
echo   setx AWS_ACCESS_KEY_ID your_access_key_here
echo   setx AWS_SECRET_ACCESS_KEY your_secret_key_here
echo.
echo NOTE: You must close and reopen CMD for permanent changes to take effect.
echo.
echo ========================================
pause
