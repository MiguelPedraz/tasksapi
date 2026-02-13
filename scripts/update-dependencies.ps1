# Tasks API - Dependency Update Tool (PowerShell)
# This script checks for available dependency updates and helps manage them

$ErrorActionPreference = "Stop"

Write-Host "🔍 Tasks API - Dependency Update Tool" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

# Check if we're in the correct directory
if (-not (Test-Path "gradlew.bat")) {
    Write-Host "❌ Error: Must be run from the tasksapi directory" -ForegroundColor Red
    exit 1
}

# Function to display menu
function Show-Menu {
    Write-Host "Select an option:" -ForegroundColor Yellow
    Write-Host "1) Check for dependency updates"
    Write-Host "2) Run security vulnerability scan"
    Write-Host "3) View dependency tree"
    Write-Host "4) Update Gradle wrapper"
    Write-Host "5) Run all checks (updates + security)"
    Write-Host "6) Exit"
    Write-Host ""
}

# Function to check updates
function Check-Updates {
    Write-Host ""
    Write-Host "🔎 Checking for dependency updates..." -ForegroundColor Cyan
    Write-Host ""
    .\gradlew.bat dependencyUpdates
    
    Write-Host ""
    Write-Host "📊 Report generated at: app\build\dependencyUpdates\report.html" -ForegroundColor Green
    Write-Host ""
    
    $response = Read-Host "Open HTML report? (y/n)"
    if ($response -eq 'y' -or $response -eq 'Y') {
        Start-Process "app\build\dependencyUpdates\report.html"
    }
}

# Function to check security
function Check-Security {
    Write-Host ""
    Write-Host "🔒 Running security vulnerability scan..." -ForegroundColor Cyan
    Write-Host ""
    .\gradlew.bat dependencyCheckAnalyze
    
    Write-Host ""
    Write-Host "📊 Security report generated at: app\build\reports\dependency-check-report.html" -ForegroundColor Green
    Write-Host ""
    
    $response = Read-Host "Open HTML report? (y/n)"
    if ($response -eq 'y' -or $response -eq 'Y') {
        Start-Process "app\build\reports\dependency-check-report.html"
    }
}

# Function to view dependency tree
function View-Tree {
    Write-Host ""
    Write-Host "🌳 Dependency tree:" -ForegroundColor Cyan
    Write-Host ""
    .\gradlew.bat dependencies --configuration runtimeClasspath
    Write-Host ""
}

# Function to update gradle wrapper
function Update-Gradle {
    Write-Host ""
    Write-Host "⬆️  Updating Gradle wrapper to latest version..." -ForegroundColor Cyan
    Write-Host ""
    .\gradlew.bat wrapper --gradle-version latest
    Write-Host ""
    Write-Host "✅ Gradle wrapper updated!" -ForegroundColor Green
    Write-Host ""
}

# Function to run all checks
function Run-All {
    Check-Updates
    Check-Security
    
    Write-Host ""
    Write-Host "✅ All checks completed!" -ForegroundColor Green
    Write-Host ""
}

# Main loop
while ($true) {
    Show-Menu
    $choice = Read-Host "Enter your choice [1-6]"
    
    switch ($choice) {
        "1" { Check-Updates }
        "2" { Check-Security }
        "3" { View-Tree }
        "4" { Update-Gradle }
        "5" { Run-All }
        "6" {
            Write-Host ""
            Write-Host "👋 Goodbye!" -ForegroundColor Cyan
            exit 0
        }
        default {
            Write-Host ""
            Write-Host "❌ Invalid option. Please try again." -ForegroundColor Red
            Write-Host ""
        }
    }
}
