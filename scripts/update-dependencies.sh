#!/bin/bash

# Dependency Update Script
# This script checks for available dependency updates and helps manage them

set -e

echo "🔍 Tasks API - Dependency Update Tool"
echo "======================================"
echo ""

# Check if we're in the correct directory
if [ ! -f "gradlew" ]; then
    echo "❌ Error: Must be run from the tasksapi directory"
    exit 1
fi

# Function to display menu
show_menu() {
    echo "Select an option:"
    echo "1) Check for dependency updates"
    echo "2) Run security vulnerability scan"
    echo "3) View dependency tree"
    echo "4) Update Gradle wrapper"
    echo "5) Run all checks (updates + security)"
    echo "6) Exit"
    echo ""
}

# Function to check updates
check_updates() {
    echo ""
    echo "🔎 Checking for dependency updates..."
    echo ""
    ./gradlew dependencyUpdates
    
    echo ""
    echo "📊 Report generated at: app/build/dependencyUpdates/report.html"
    echo ""
    
    # Try to open the report
    if command -v xdg-open &> /dev/null; then
        read -p "Open HTML report? (y/n) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            xdg-open app/build/dependencyUpdates/report.html
        fi
    fi
}

# Function to check security
check_security() {
    echo ""
    echo "🔒 Running security vulnerability scan..."
    echo ""
    ./gradlew dependencyCheckAnalyze
    
    echo ""
    echo "📊 Security report generated at: app/build/reports/dependency-check-report.html"
    echo ""
}

# Function to view dependency tree
view_tree() {
    echo ""
    echo "🌳 Dependency tree:"
    echo ""
    ./gradlew dependencies --configuration runtimeClasspath
    echo ""
}

# Function to update gradle wrapper
update_gradle() {
    echo ""
    echo "⬆️  Updating Gradle wrapper to latest version..."
    echo ""
    ./gradlew wrapper --gradle-version latest
    echo ""
    echo "✅ Gradle wrapper updated!"
    echo ""
}

# Function to run all checks
run_all() {
    check_updates
    check_security
    
    echo ""
    echo "✅ All checks completed!"
    echo ""
}

# Main loop
while true; do
    show_menu
    read -p "Enter your choice [1-6]: " choice
    
    case $choice in
        1)
            check_updates
            ;;
        2)
            check_security
            ;;
        3)
            view_tree
            ;;
        4)
            update_gradle
            ;;
        5)
            run_all
            ;;
        6)
            echo ""
            echo "👋 Goodbye!"
            exit 0
            ;;
        *)
            echo ""
            echo "❌ Invalid option. Please try again."
            echo ""
            ;;
    esac
done
