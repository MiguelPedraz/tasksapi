# Security Scanning Setup Guide

## 📋 Table of Contents

- [Overview](#overview)
- [NVD API Key Configuration](#nvd-api-key-configuration)
  - [Why You Need an NVD API Key](#why-you-need-an-nvd-api-key)
  - [Obtaining Your API Key](#obtaining-your-api-key)
  - [Configuring the API Key](#configuring-the-api-key)
  - [Verifying the Setup](#verifying-the-setup)
- [GitHub Repository Configuration](#github-repository-configuration)
  - [Required Secrets](#required-secrets)
  - [Permissions Setup](#permissions-setup)
- [Local Development Setup](#local-development-setup)
- [Troubleshooting](#troubleshooting)
- [Best Practices](#best-practices)

---

## Overview

This project uses **OWASP Dependency-Check** to scan dependencies for known security vulnerabilities. To optimize scan performance and avoid rate limiting, you should configure an NVD (National Vulnerability Database) API key.

**Performance Impact:**
- ❌ **Without API Key:** 20-25 minutes per scan (rate limited to ~1 request/second)
- ✅ **With API Key:** 1-3 minutes per scan (up to 50 requests/30 seconds)

---

## NVD API Key Configuration

### Why You Need an NVD API Key

The National Vulnerability Database (NVD) provides the vulnerability data used by OWASP Dependency-Check. Without an API key:

- Requests are **heavily rate-limited** (1 request every 6 seconds)
- Initial database download takes **20+ minutes**
- CI/CD pipelines may timeout
- Updates become impractical for daily use

With an API key:

- Rate limit increases to **50 requests per 30 seconds**
- Scans complete in **1-3 minutes**
- Regular updates are feasible
- Better resource utilization in CI/CD

### Obtaining Your API Key

#### Step 1: Request an API Key

1. Visit the NVD API Key request page:
   ```
   https://nvd.nist.gov/developers/request-an-api-key
   ```

2. Fill out the required information:
   - **Email Address:** Use your organization or personal email
   - **Organization:** Your company/project name (optional for personal use)
   - **Intended Use:** Select "Research" or "Development Testing"

3. Complete the CAPTCHA verification

4. Submit the request

#### Step 2: Check Your Email

- You'll receive an email from `nvd@nist.gov` within minutes
- The email contains your API key (a UUID format string)
- Example format: `12345678-1234-1234-1234-123456789abc`

#### Step 3: Store the Key Securely

⚠️ **IMPORTANT:** Never commit API keys to version control!

Store your key securely:
- In a password manager
- In environment variables
- In GitHub Secrets (for CI/CD)

---

### Configuring the API Key

#### For GitHub Actions (Recommended for CI/CD)

**Step 1: Add as GitHub Secret**

1. Navigate to your repository on GitHub
2. Go to **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Set the secret details:
   - **Name:** `NVD_API_KEY`
   - **Value:** Your NVD API key (e.g., `12345678-1234-1234-1234-123456789abc`)
5. Click **Add secret**

**Step 2: Verify Workflow Configuration**

The following workflows are already configured to use the API key:

- `.github/workflows/dependency-check.yml`
- `.github/workflows/auto-merge-dependencies.yml`

Example workflow configuration:
```yaml
- name: Check for security vulnerabilities
  run: ./gradlew dependencyCheckAnalyze
  continue-on-error: true
  env:
    NVD_API_KEY: ${{ secrets.NVD_API_KEY }}
```

**Step 3: Test the Configuration**

1. Push your changes to GitHub
2. Navigate to **Actions** tab
3. Manually trigger the "Dependency Updates Check" workflow
4. Monitor the execution time (should be < 5 minutes)

---

#### For Local Development

**Option 1: Using Environment Variables (Recommended)**

**Windows PowerShell:**
```powershell
# Temporary (current session only)
$env:NVD_API_KEY = "your-api-key-here"

# Permanent (current user)
[System.Environment]::SetEnvironmentVariable('NVD_API_KEY', 'your-api-key-here', 'User')
```

**Windows Command Prompt:**
```cmd
set NVD_API_KEY=your-api-key-here
```

**Linux/macOS:**
```bash
# Temporary (current session)
export NVD_API_KEY="your-api-key-here"

# Permanent (add to ~/.bashrc or ~/.zshrc)
echo 'export NVD_API_KEY="your-api-key-here"' >> ~/.bashrc
source ~/.bashrc
```

**Option 2: Using gradle.properties (NOT Recommended)**

⚠️ **WARNING:** Only use if `gradle.properties` is in `.gitignore`

Create or edit `~/.gradle/gradle.properties`:
```properties
# NVD API Key for OWASP Dependency-Check
systemProp.nvd.api.key=your-api-key-here
```

**Option 3: Using Command Line**

Pass the key directly when running Gradle:
```bash
./gradlew dependencyCheckAnalyze -Dnvd.api.key=your-api-key-here
```

---

### Verifying the Setup

#### Test Locally

Run the dependency check and verify the API key is being used:

```bash
./gradlew dependencyCheckAnalyze
```

**Expected output with API key:**
```
> Task :app:dependencyCheckAnalyze
Verifying dependencies for project app
Checking for updates and analyzing dependencies for vulnerabilities
Using NVD API Key (first 8 chars): 12345678-****
Downloading NVD data (this may take a few minutes)...
Processing vulnerabilities...
```

**Expected output without API key (warning):**
```
An NVD API Key was not provided - it is highly recommended to use an NVD API key 
as the update can take a VERY long time without an API Key
```

#### Check Execution Time

- **First run with key:** 2-5 minutes (downloads full database)
- **Subsequent runs:** 30 seconds - 2 minutes (incremental updates)
- **Without key:** 20-30 minutes consistently

---

## GitHub Repository Configuration

### Required Secrets

Configure the following secrets in your GitHub repository:

| Secret Name | Description | Required For |
|-------------|-------------|--------------|
| `NVD_API_KEY` | NVD API key for vulnerability scanning | Security workflows |
| `RENOVATE_TOKEN` | GitHub Personal Access Token for Renovate | Dependency updates |
| `GITHUB_TOKEN` | Automatically provided by GitHub Actions | All workflows |

### How to Add Secrets

1. Navigate to: **Repository** → **Settings** → **Secrets and variables** → **Actions**
2. Click **New repository secret**
3. Enter the secret name and value
4. Click **Add secret**

### Permissions Setup

Ensure the repository has the required permissions enabled:

#### Actions Permissions

1. Go to **Settings** → **Actions** → **General**
2. Under "Workflow permissions", select:
   - ✅ **Read and write permissions**
   - ✅ **Allow GitHub Actions to create and approve pull requests**

#### Branch Protection Rules (Optional but Recommended)

For the `main` branch:
1. Go to **Settings** → **Branches** → **Branch protection rules**
2. Add rule for `main`:
   - ✅ Require pull request reviews before merging
   - ✅ Require status checks to pass before merging:
     - `build-and-test`
     - `dependency-check`
   - ✅ Require branches to be up to date before merging

---

## Local Development Setup

### Prerequisites

1. **Java 21** installed
2. **Git** configured
3. **Gradle** (wrapper included)

### Initial Setup

```bash
# Clone the repository
git clone https://github.com/your-org/tasksapi.git
cd tasksapi

# Set NVD API Key (choose your platform)
# Windows PowerShell:
$env:NVD_API_KEY = "your-api-key-here"

# Linux/macOS:
export NVD_API_KEY="your-api-key-here"

# Run initial build
./gradlew build

# Run security scan
./gradlew dependencyCheckAnalyze
```

### Recommended Git Configuration

```bash
# Set your identity
git config user.name "Your Name"
git config user.email "your.email@example.com"

# Enable credential caching (Linux/macOS)
git config --global credential.helper cache
git config --global credential.helper 'cache --timeout=3600'

# Enable credential manager (Windows)
git config --global credential.helper manager-core

# Set default branch name
git config --global init.defaultBranch main

# Enable color output
git config --global color.ui auto

# Set default editor (optional)
git config --global core.editor "code --wait"  # VS Code
```

---

## Troubleshooting

### Issue: "NVD API Key not detected"

**Symptoms:**
```
An NVD API Key was not provided - it is highly recommended to use an NVD API key
```

**Solutions:**

1. **Verify environment variable is set:**
   ```bash
   # Windows PowerShell
   echo $env:NVD_API_KEY
   
   # Linux/macOS
   echo $NVD_API_KEY
   ```

2. **Check GitHub secret exists:**
   - Navigate to: Settings → Secrets and variables → Actions
   - Verify `NVD_API_KEY` is listed

3. **Restart your terminal/IDE** after setting environment variables

4. **Check for typos** in the environment variable name (case-sensitive)

---

### Issue: "API key quota exceeded"

**Symptoms:**
```
Error: NVD API rate limit exceeded
```

**Solutions:**

1. **Wait 30 seconds** and retry (free tier: 50 requests per 30 seconds)

2. **Use caching** (already configured in workflows):
   ```yaml
   - uses: actions/cache@v4
     with:
       path: |
         ~/.gradle/caches
         app/build/reports
       key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*') }}
   ```

3. **Review your API key tier:**
   - Free tier: 50 requests/30 seconds
   - Sufficient for most projects

---

### Issue: Workflows failing with 403 errors

**Symptoms:**
```
Error: Resource not accessible by integration
```

**Solutions:**

1. **Check workflow permissions:**
   - Settings → Actions → General → Workflow permissions
   - Set to "Read and write permissions"

2. **Verify token has required scopes:**
   - For `RENOVATE_TOKEN`: Requires `repo`, `workflow` scopes
   - For `GITHUB_TOKEN`: Automatically provided, check permissions

3. **Ensure Actions are enabled:**
   - Settings → Actions → General → Actions permissions
   - Set to "Allow all actions and reusable workflows"

---

### Issue: Dependency check hangs or times out

**Symptoms:**
- Workflow runs for 20+ minutes
- Timeout errors in CI/CD

**Solutions:**

1. **Ensure NVD API Key is configured** (see above)

2. **Increase timeout in workflow** (temporary solution):
   ```yaml
   - name: Check for security vulnerabilities
     run: ./gradlew dependencyCheckAnalyze
     timeout-minutes: 30  # Increase if needed
   ```

3. **Cache NVD database** (reduces download time):
   - Already configured in workflows
   - First run will still be slower

4. **Review suppression file:**
   - Check `dependency-check-suppression.xml` for false positives
   - Reduces processing time

---

## Best Practices

### Security

- ✅ **Never commit API keys** to version control
- ✅ **Use GitHub Secrets** for CI/CD environments
- ✅ **Use environment variables** for local development
- ✅ **Rotate API keys periodically** (every 6-12 months)
- ✅ **Limit secret access** to necessary team members only
- ✅ **Enable branch protection** on main/develop branches

### Performance

- ✅ **Always use NVD API Key** to optimize scan times
- ✅ **Cache Gradle dependencies** in CI/CD
- ✅ **Run security scans** on schedule (weekly) rather than every commit
- ✅ **Use `continue-on-error: true`** for security scans in non-blocking workflows
- ✅ **Suppress false positives** using `dependency-check-suppression.xml`

### Maintenance

- ✅ **Review CVE reports** weekly
- ✅ **Update dependencies** regularly via Renovate/Dependabot
- ✅ **Document suppressions** with justifications
- ✅ **Monitor scan execution time** (should be < 5 minutes)
- ✅ **Keep OWASP Dependency-Check plugin** updated

### CI/CD Integration

- ✅ **Fail builds on HIGH/CRITICAL vulnerabilities**
- ✅ **Allow MEDIUM vulnerabilities** with manual review
- ✅ **Generate HTML reports** for detailed analysis
- ✅ **Archive reports** as workflow artifacts
- ✅ **Set up notifications** for security findings

---

## Additional Resources

### Documentation

- **NVD API:** https://nvd.nist.gov/developers
- **OWASP Dependency-Check:** https://jeremylong.github.io/DependencyCheck/
- **Gradle Plugin:** https://jeremylong.github.io/DependencyCheck/dependency-check-gradle/
- **GitHub Actions Security:** https://docs.github.com/en/actions/security-guides

### Related Guides

- [Dependency Management Guide](DEPENDENCY_MANAGEMENT.md)
- [Renovate Manual Workflow Guide](RENOVATE_MANUAL_WORKFLOW.md)
- [Auto Update Setup](AUTO_UPDATE_SETUP.md)

### Support

For issues or questions:
1. Check the [Troubleshooting](#troubleshooting) section
2. Review workflow logs in GitHub Actions
3. Consult OWASP Dependency-Check documentation
4. Open an issue in the project repository

---

## Quick Reference Card

### Commands

```bash
# Run security scan locally
./gradlew dependencyCheckAnalyze

# View report
open app/build/reports/dependency-check-report.html  # macOS
start app/build/reports/dependency-check-report.html # Windows

# Clean and rebuild
./gradlew clean build

# Update dependencies
./gradlew dependencyUpdates
```

### Workflow Triggers

| Workflow | Trigger | Purpose |
|----------|---------|---------|
| `ci.yml` | Push to develop, PRs | Build and test |
| `dependency-check.yml` | Weekly, manual | Security scan |
| `auto-merge-dependencies.yml` | Dependabot/Renovate PRs | Auto-merge safe updates |
| `renovate-manual.yml` | Manual | On-demand dependency updates |

### Key Files

```
tasksapi/
├── .github/workflows/          # CI/CD workflows
├── dependency-check-suppression.xml    # CVE suppressions
├── gradle/libs.versions.toml   # Dependency versions
├── renovate.json              # Renovate configuration
└── docs/guides/               # Documentation
```

---

**Last Updated:** February 2026  
**Version:** 1.0.0
