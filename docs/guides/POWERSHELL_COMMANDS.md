# PowerShell Commands - Developer Guide

Essential PowerShell commands for managing processes, ports, and application troubleshooting.

---

## Table of Contents

- [Port Management](#port-management)
- [Process Management](#process-management)
- [Network Diagnostics](#network-diagnostics)
- [Docker Commands](#docker-commands)
- [Gradle Operations](#gradle-operations)
- [Git Operations](#git-operations)
- [Best Practices](#best-practices)

---

## Port Management

### Check What's Running on a Specific Port

```powershell
# Find process using port 8080
netstat -ano | findstr :8080

# Alternative: Get more detailed information
Get-NetTCPConnection -LocalPort 8080 | Select-Object LocalAddress, LocalPort, State, OwningProcess
```

**Output explanation:**
```
TCP    0.0.0.0:8080    0.0.0.0:0    LISTENING    12345
                                                  ^^^^^
                                                  PID (Process ID)
```

### List All Listening Ports

```powershell
# Show all TCP listening ports
netstat -ano | findstr LISTENING

# PowerShell-native alternative
Get-NetTCPConnection -State Listen | Select-Object LocalAddress, LocalPort, State, OwningProcess | Sort-Object LocalPort
```

### Check if Port is Available

```powershell
# Test if port 8080 is in use (returns True if in use)
Test-NetConnection -ComputerName localhost -Port 8080 -InformationLevel Quiet
```

---

## Process Management

### Find Process Details

```powershell
# Get process by PID
Get-Process -Id 12345

# Get process by name
Get-Process -Name java

# Find all Java processes with details
Get-Process java* | Select-Object Id, Name, CPU, WorkingSet, Path
```

### Stop Process

```powershell
# Stop by PID (graceful)
Stop-Process -Id 12345

# Stop by PID (force kill)
Stop-Process -Id 12345 -Force

# Stop by name (all matching processes)
Stop-Process -Name java -Force

# Stop with confirmation prompt
Stop-Process -Id 12345 -Confirm
```

### Kill Spring Boot Application

```powershell
# Method 1: Find and kill by port
$pid = (Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue).OwningProcess
if ($pid) { Stop-Process -Id $pid -Force }

# Method 2: Kill Gradle bootRun
Get-Process | Where-Object {$_.ProcessName -like "*java*" -and $_.CommandLine -like "*bootRun*"} | Stop-Process -Force

# Method 3: Press Ctrl+C in the terminal running bootRun
```

---

## Network Diagnostics

### Test HTTP Endpoints

```powershell
# Simple GET request
Invoke-WebRequest -Uri http://localhost:8080/actuator/health -UseBasicParsing

# GET request with response details
$response = Invoke-WebRequest -Uri http://localhost:8080/api/tasks -UseBasicParsing
$response.StatusCode
$response.Content

# POST request with JSON body
$body = @{
    title = "New Task"
    description = "Task description"
} | ConvertTo-Json

Invoke-WebRequest -Uri http://localhost:8080/api/tasks `
    -Method POST `
    -ContentType "application/json" `
    -Body $body `
    -UseBasicParsing
```

### Check Network Connectivity

```powershell
# Test if database port is reachable
Test-NetConnection -ComputerName localhost -Port 5432

# Ping host
Test-Connection -ComputerName google.com -Count 4

# DNS lookup
Resolve-DnsName github.com
```

---

## Docker Commands

### Container Management

```powershell
# List running containers
docker ps

# List all containers (including stopped)
docker ps -a

# Stop all running containers
docker stop $(docker ps -q)

# Remove all stopped containers
docker container prune -f

# View container logs
docker logs tasksapi-db-1 --tail 100 -f
```

### Docker Compose

```powershell
# Start services in background
docker-compose up -d

# Start services with build
docker-compose up -d --build

# Stop and remove containers
docker-compose down

# Stop, remove containers and volumes
docker-compose down -v

# View logs
docker-compose logs -f

# Restart specific service
docker-compose restart postgres
```

### Database Container

```powershell
# Connect to PostgreSQL container
docker exec -it tasksapi-db-1 psql -U postgres -d tasksdb

# Execute SQL query
docker exec -it tasksapi-db-1 psql -U postgres -d tasksdb -c "SELECT * FROM tasks;"

# Dump database
docker exec tasksapi-db-1 pg_dump -U postgres tasksdb > backup.sql

# Restore database
Get-Content backup.sql | docker exec -i tasksapi-db-1 psql -U postgres -d tasksdb
```

---

## Gradle Operations

### Build Commands

```powershell
# Clean build
.\gradlew.bat clean build

# Build without tests
.\gradlew.bat build -x test

# Build with stacktrace
.\gradlew.bat build --stacktrace

# Refresh dependencies
.\gradlew.bat build --refresh-dependencies
```

### Run Application

```powershell
# Run Spring Boot application
.\gradlew.bat bootRun

# Run with debug enabled
.\gradlew.bat bootRun --debug-jvm

# Run in background (Windows)
Start-Process -NoNewWindow -FilePath .\gradlew.bat -ArgumentList "bootRun"
```

### Testing

```powershell
# Run all tests
.\gradlew.bat test

# Run specific test class
.\gradlew.bat test --tests "TaskServiceTest"

# Run with coverage
.\gradlew.bat test jacocoTestReport

# View test report
Start-Process build\reports\tests\test\index.html
```

### Dependency Management

```powershell
# Check for dependency updates
.\gradlew.bat dependencyUpdates

# View dependency tree
.\gradlew.bat dependencies

# View outdated dependencies
.\gradlew.bat dependencyUpdates -Drevision=release
```

---

## Git Operations

### Common Commands

```powershell
# Check status
git status

# View changes
git diff

# Stage all changes
git add .

# Commit with message
git commit -m "fix(deps): downgrade testcontainers to 1.21.4"

# Push to remote
git push origin develop

# Pull latest changes
git pull origin develop --rebase
```

### Branch Management

```powershell
# Create new branch
git checkout -b feature/new-feature

# Switch branch
git checkout develop

# List branches
git branch -a

# Delete local branch
git branch -d feature/old-feature

# Delete remote branch
git push origin --delete feature/old-feature
```

### Stash Operations

```powershell
# Stash changes
git stash save "Work in progress"

# List stashes
git stash list

# Apply latest stash
git stash apply

# Apply and remove stash
git stash pop

# Clear all stashes
git stash clear
```

---

## Best Practices

### 1. Always Check Before Killing

```powershell
# ❌ BAD: Kill without checking
Stop-Process -Id 12345 -Force

# ✅ GOOD: Verify process first
$process = Get-Process -Id 12345 -ErrorAction SilentlyContinue
if ($process) {
    Write-Host "Killing process: $($process.Name) (PID: $($process.Id))"
    Stop-Process -Id $process.Id -Force
} else {
    Write-Host "Process not found"
}
```

### 2. Use Error Handling

```powershell
# ✅ GOOD: Handle potential errors
try {
    $response = Invoke-WebRequest -Uri http://localhost:8080/actuator/health -UseBasicParsing
    Write-Host "Application is running: $($response.StatusCode)"
} catch {
    Write-Host "Application is not responding: $_"
}
```

### 3. Use Variables for Reusability

```powershell
# ✅ GOOD: Define constants
$APP_PORT = 8080
$DB_PORT = 5432

# Find application process
$appPid = (Get-NetTCPConnection -LocalPort $APP_PORT -ErrorAction SilentlyContinue).OwningProcess
```

### 4. Graceful Shutdown First

```powershell
# ✅ GOOD: Try graceful shutdown before force kill
# Method 1: Graceful
Stop-Process -Id $pid

# Wait a few seconds
Start-Sleep -Seconds 3

# Method 2: Force if still running
if (Get-Process -Id $pid -ErrorAction SilentlyContinue) {
    Stop-Process -Id $pid -Force
}
```

### 5. Clean Up Resources

```powershell
# ✅ GOOD: Clean up Docker resources periodically
docker system prune -f
docker volume prune -f

# ✅ GOOD: Stop all running containers before major changes
docker-compose down
```

### 6. Use Confirmation for Destructive Operations

```powershell
# ✅ GOOD: Ask for confirmation
Stop-Process -Id $pid -Confirm

# ✅ GOOD: Implement manual confirmation
$response = Read-Host "Kill process $pid? (y/n)"
if ($response -eq 'y') {
    Stop-Process -Id $pid -Force
}
```

### 7. Log Important Operations

```powershell
# ✅ GOOD: Log what you're doing
$logFile = "operations.log"
$timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
Add-Content -Path $logFile -Value "[$timestamp] Stopped process $pid"
```

---

## Common Troubleshooting Scenarios

### Port Already in Use

```powershell
# Problem: Application won't start - "Address already in use"
# Solution:
$port = 8080
$pid = (Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue).OwningProcess
if ($pid) {
    Write-Host "Port $port is used by PID: $pid"
    $process = Get-Process -Id $pid
    Write-Host "Process: $($process.Name)"
    Stop-Process -Id $pid -Force
    Write-Host "Process killed successfully"
} else {
    Write-Host "Port $port is available"
}
```

### Database Connection Failed

```powershell
# Problem: Cannot connect to PostgreSQL
# Solution:
# 1. Check if container is running
docker ps | findstr postgres

# 2. Check if port is accessible
Test-NetConnection -ComputerName localhost -Port 5432

# 3. Restart database container
docker-compose restart postgres

# 4. Check logs
docker-compose logs postgres --tail 50
```

### Gradle Build Hanging

```powershell
# Problem: Gradle build freezes
# Solution:
# 1. Find Gradle daemon processes
Get-Process | Where-Object {$_.ProcessName -like "*java*" -and $_.CommandLine -like "*gradle*"}

# 2. Stop Gradle daemons
.\gradlew.bat --stop

# 3. Clear Gradle cache (if needed)
Remove-Item -Path "$env:USERPROFILE\.gradle\caches" -Recurse -Force

# 4. Retry build
.\gradlew.bat clean build --no-daemon
```

### Application Won't Stop

```powershell
# Problem: Ctrl+C doesn't work
# Solution:
# 1. Find the main Java process
$javaProcesses = Get-Process java | Where-Object {
    $_.MainWindowTitle -like "*Spring Boot*" -or 
    $_.CommandLine -like "*TasksApiApplication*"
}

# 2. Kill all matching processes
$javaProcesses | ForEach-Object {
    Write-Host "Killing: $($_.Name) (PID: $($_.Id))"
    Stop-Process -Id $_.Id -Force
}

# 3. Verify port is free
if (-not (Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue)) {
    Write-Host "Port 8080 is now free"
}
```

---

## Quick Reference

### Kill Process on Port 8080
```powershell
(Get-NetTCPConnection -LocalPort 8080).OwningProcess | ForEach-Object { Stop-Process -Id $_ -Force }
```

### Kill All Java Processes
```powershell
Get-Process java* | Stop-Process -Force
```

### Restart Application Stack
```powershell
docker-compose down && docker-compose up -d && .\gradlew.bat bootRun
```

### Clean Everything
```powershell
.\gradlew.bat clean
docker-compose down -v
docker system prune -f
Remove-Item -Path build -Recurse -Force
```

---

## Additional Resources

- [PowerShell Documentation](https://docs.microsoft.com/en-us/powershell/)
- [Docker Documentation](https://docs.docker.com/)
- [Gradle Documentation](https://docs.gradle.org/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)

---

**Last Updated:** February 2026  
**Version:** 1.0.0
