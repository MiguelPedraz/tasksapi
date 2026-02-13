# Dependency Management Guide

This document explains how to keep all project dependencies up-to-date using automated tools and manual processes.

## 🎯 Strategy

The project follows a **"Latest Stable Versions"** strategy for all dependencies:

- **Patch updates** (x.x.X): Auto-merge after tests pass
- **Minor updates** (x.X.0): Auto-merge after review
- **Major updates** (X.0.0): Manual review required

## 📦 Tools Configured

### 1. Version Catalog (`gradle/libs.versions.toml`)

All dependency versions are centrally managed in the version catalog:

```toml
[versions]
spring-boot = "3.3.0"
postgresql = "42.7.3"
junit-jupiter = "5.12.1"
```

**Benefits:**
- Single source of truth for versions
- Easy to update all related dependencies
- Type-safe references in build files
- IDE autocomplete support

### 2. Gradle Versions Plugin (v0.52.0)

Checks for available updates to dependencies.

**Usage:**
```bash
# Check for updates (use --no-configuration-cache with Gradle 9.2+)
./gradlew dependencyUpdates --no-configuration-cache

# View HTML report
open app/build/dependencyUpdates/report.html  # macOS/Linux
start app\build\dependencyUpdates\report.html  # Windows
```

> **⚠️ Gradle 9.2+ Note:** The plugin requires `--no-configuration-cache` flag due to compatibility limitations. This is a known issue and will be resolved in future plugin versions.

### 3. OWASP Dependency Check (v11.1.1)

Scans dependencies for known security vulnerabilities.

**Usage:**
```bash
# Run security scan
./gradlew dependencyCheckAnalyze

# View report
open app/build/reports/dependency-check-report.html
```

**Configuration:**
- Fails build if CVSS score >= 7.0 (HIGH or CRITICAL)
- Suppressions in `dependency-check-suppression.xml`
- Disabled analyzers: Assembly (.NET DLLs), NuSpec, NuGet (not needed for Java)

> **⏱️ First Run Note:** The initial execution downloads the NVD vulnerability database (~500 MB) and can take 5-10 minutes. Subsequent runs are much faster (1-2 minutes).
> 
> **💡 Performance Tip:** Get a free NVD API key from [nvd.nist.gov](https://nvd.nist.gov/developers/request-an-api-key) and add it to `gradle.properties`:
> ```properties
> nvdApiKey=YOUR-API-KEY-HERE
> ```

### 4. Renovate Bot

Automated dependency updates via Pull Requests.

**Configuration:** `renovate.json`

**Features:**
- Weekly checks for updates
- Groups related dependencies (Spring Boot, Testing, etc.)
- Auto-merge for patch/minor updates
- Labels PRs with "dependencies" and "automated"

### 5. Dependabot

GitHub's native dependency update tool.

**Configuration:** `.github/dependabot.yml`

**Features:**
- Weekly updates on Mondays at 3 AM
- Groups dependencies by category
- Creates separate PRs for GitHub Actions
- Automatic commit message formatting

### 6. GitHub Actions Workflows

#### `dependency-check.yml`
- Runs weekly on Mondays
- Checks for updates and vulnerabilities
- Creates issues with findings
- Uploads reports as artifacts

#### `auto-merge-dependencies.yml`
- Auto-merges safe dependency updates
- Runs all tests before merge
- Only merges patch/minor updates
- Requires: tests pass, coverage maintained

## 🔧 Manual Update Process

### Using Scripts

**PowerShell (Windows):**
```powershell
cd tasksapi
.\scripts\update-dependencies.ps1
```

**Bash (Linux/macOS):**
```bash
cd tasksapi
chmod +x scripts/update-dependencies.sh
./scripts/update-dependencies.sh
```

### Manual Commands

```bash
# 1. Check for updates
./gradlew dependencyUpdates

# 2. Review the report
# Open app/build/dependencyUpdates/report.html

# 3. Update versions in gradle/libs.versions.toml
# Example:
# spring-boot = "3.3.0" → "3.3.1"

# 4. Test the changes
./gradlew clean test

# 5. Check for vulnerabilities
./gradlew dependencyCheckAnalyze

# 6. Verify coverage
./gradlew jacocoTestCoverageVerification

# 7. Commit changes
git add gradle/libs.versions.toml
git commit -m "chore(deps): update dependencies to latest stable versions"
```

## 📋 Update Checklist

Before updating dependencies:

- [ ] Check current versions in `gradle/libs.versions.toml`
- [ ] Run `./gradlew dependencyUpdates` to see available updates
- [ ] Review changelogs/release notes for major updates
- [ ] Check for breaking changes

After updating:

- [ ] Run `./gradlew clean build`
- [ ] Run `./gradlew test`
- [ ] Run `./gradlew jacocoTestCoverageVerification`
- [ ] Run `./gradlew dependencyCheckAnalyze`
- [ ] Test the application locally
- [ ] Update documentation if needed

## 🔒 Security Best Practices

1. **Never ignore security vulnerabilities** without investigation
2. **Document suppressions** in `dependency-check-suppression.xml`
3. **Review auto-merged PRs** weekly
4. **Test major updates** in a separate environment first
5. **Monitor GitHub Security Advisories**

## 📊 Monitoring

### Badges (add to README.md)

```markdown
[![Dependency Status](https://img.shields.io/badge/dependencies-up%20to%20date-brightgreen.svg)](app/build/dependencyUpdates/report.html)
```

### Weekly Review

Every Monday:
1. Review Renovate/Dependabot PRs
2. Check dependency-check.yml workflow results
3. Review any security alerts
4. Update this document if needed

## 🚀 Automation Setup

### 🤖 Enable Renovate Bot

1. 🌐 **Visit:** https://github.com/apps/renovate
2. 🟢 **Click:** "Install" button (top right)
3. 🎯 **Select repositories:**
   - Choose "Only select repositories"
   - Select your `tasksapi` repository
4. 🔐 **Authorize:** Click "Install & Authorize"
5. ⏳ **Wait:** 5-10 minutes for Renovate to scan
6. 📬 **Merge onboarding PR:** Renovate will create "Configure Renovate" PR
7. 🎉 **Done:** Renovate will automatically create PRs based on `renovate.json`

### 🐛 Enable Dependabot

✅ **Automatically enabled** for GitHub repositories with `.github/dependabot.yml`

**Verification steps:**
1. 🔍 Go to your repository on GitHub
2. 📊 Click `Insights` → `Dependency graph`
3. 🐛 Click `Dependabot` tab
4. ✅ You should see "Dependabot is enabled"

### ⚡ Enable Auto-merge (Optional but Recommended)

1. ⚙️ **Go to repository Settings → General**
2. ✅ **Enable these options:**
   - ☑️ "Allow auto-merge"
   - ☑️ "Automatically delete head branches"
   - ☑️ "Allow squash merging"
3. 🔒 **Configure branch protection rules:**
   - Require status checks before merging
   - Require branches to be up to date
4. 🤖 **GitHub Actions workflow will handle auto-merge** for safe updates

## 📝 Version Catalog Structure

```toml
[versions]
# Major frameworks
spring-boot = "X.Y.Z"

# Databases
postgresql = "X.Y.Z"

# Testing
junit-jupiter = "X.Y.Z"

[libraries]
# Spring Boot (versions managed by BOM)
spring-boot-starter-web = { module = "..." }

# External libraries (explicit versions)
postgresql = { module = "...", version.ref = "postgresql" }

[bundles]
# Logical groupings
spring-boot-core = ["..."]
testing = ["..."]

[plugins]
# Build plugins
spring-boot = { id = "...", version.ref = "spring-boot" }
```

## 🔄 Update Frequency

| Type | Frequency | Auto-merge |
|------|-----------|------------|
| Security patches | Immediate | Yes (after tests) |
| Patch updates | Weekly | Yes (after tests) |
| Minor updates | Weekly | With review |
| Major updates | Manual | Never |

## 🆘 Troubleshooting

### ❌ Error: `./gradlew dependencyUpdates` fails with API error

**Error Message:**
```
'java.util.Set org.gradle.api.artifacts.LenientConfiguration.getFirstLevelModuleDependencies(org.gradle.api.specs.Spec)'
```

**Cause:** Gradle Versions Plugin version incompatibility with Gradle 9.2+

**Solution:**
```toml
# In gradle/libs.versions.toml
[plugins]
versions = { id = "com.github.ben-manes.versions", version = "0.52.0" }  # Minimum 0.52.0
```

**Alternative:** Use `--no-configuration-cache` flag:
```bash
./gradlew dependencyUpdates --no-configuration-cache
```

---

### ❌ Error: `./gradlew dependencyCheckAnalyze` Kotlin DSL error

**Error Message:**
```
Argument type mismatch: actual type is 'Function0<Unit>', 
but '(Closure<Any!>..Closure<*>?)' was expected.
```

**Cause:** Incorrect Kotlin DSL syntax in `build.gradle.kts`

**Solution:**
```kotlin
// ✅ CORRECT - Use extension API directly
dependencyCheck {
    formats = listOf("HTML", "JSON", "XML")
    failBuildOnCVSS = 7.0f
    suppressionFile = "${project.rootDir}/dependency-check-suppression.xml"
    
    analyzers.assemblyEnabled = false
    analyzers.nuspecEnabled = false
}

// ❌ WRONG - Don't use configure<> with this plugin
configure<DependencyCheckExtension> {
    // ... this syntax doesn't work
}
```

---

### ⏰ OWASP scan is taking too long (>10 minutes)

**Cause:** First run downloads NVD database (~500 MB) without API key

**Solution:**
1. Get a free NVD API key: https://nvd.nist.gov/developers/request-an-api-key
2. Add to `gradle.properties`:
   ```properties
   nvdApiKey=YOUR-API-KEY-HERE
   ```
3. API key reduces download time from 10+ minutes to <1 minute

**Monitor Progress:**
```bash
./gradlew dependencyCheckAnalyze --info
```

---

### ⚠️ Configuration cache warnings with `dependencyUpdates`

**Warning Message:**
```
Task :app:dependencyUpdates: cannot serialize Gradle script object references
```

**Cause:** Gradle Versions Plugin not fully compatible with configuration cache in Gradle 9.2

**Solution:** Add `--no-configuration-cache` flag:
```bash
./gradlew dependencyUpdates --no-configuration-cache
```

**Note:** This is expected behavior and doesn't affect functionality. The plugin works correctly, it just can't use Gradle's configuration cache.

---

### 🧹 Gradle cache issues

**Symptoms:**
- Dependency resolution failures
- "Could not resolve" errors
- Stale dependency versions

**Solution:**
```bash
# Clear all Gradle caches
./gradlew clean --refresh-dependencies

# Force re-download of dependencies
./gradlew build --refresh-dependencies

# Nuclear option - delete Gradle cache directory
# Windows:
rmdir /s /q %USERPROFILE%\.gradle\caches
# Linux/macOS:
rm -rf ~/.gradle/caches
```

---

### 🔴 Update breaks tests

1. Review dependency changelog for breaking changes
2. Check if test code needs updates
3. Check for deprecated API usage
4. Search project for removed/changed APIs
5. Revert update and create issue for investigation

**Example - Major Spring Boot update:**
```bash
# Check what changed
git diff gradle/libs.versions.toml

# Run tests with detailed output
./gradlew test --info

# Check deprecation warnings
./gradlew build --warning-mode all
```

---

### 🔒 Security vulnerabilities found

**Steps:**
1. Check if vulnerability affects your usage:
   ```bash
   # View detailed report
   open app/build/reports/dependency-check-report.html
   ```

2. Look for available patches/updates:
   ```bash
   ./gradlew dependencyUpdates
   ```

3. If no fix available:
   - Consider alternative libraries
   - Check if vulnerability is exploitable in your context
   - Add temporary suppression if false positive

4. Document suppression in `dependency-check-suppression.xml`:
   ```xml
   <suppress>
       <notes>False positive - not exploitable in our context</notes>
       <cve>CVE-2023-12345</cve>
   </suppress>
   ```

---

### ⚙️ Gradle version incompatibility

**Error:** Plugin requires different Gradle version

**Solution:**
```bash
# Update to latest Gradle
./gradlew wrapper --gradle-version=9.2.0

# Or specific version
./gradlew wrapper --gradle-version=8.10.2

# Verify version
./gradlew --version
```

**Check compatibility:**
- Spring Boot 3.3.x: Requires Gradle 8.5+
- Java 21: Requires Gradle 8.5+
- Kotlin 2.x: Requires Gradle 8.3+

---

### 📦 Plugin version conflicts

**Error:** Multiple plugin versions on classpath

**Solution:**
```bash
# Check dependency tree
./gradlew :app:dependencies --configuration runtimeClasspath

# Find version conflicts
./gradlew :app:dependencyInsight --dependency <dependency-name>
```

**Force specific version:**
```kotlin
// In build.gradle.kts
configurations.all {
    resolutionStrategy {
        force("com.example:library:1.2.3")
    }
}
```

## 📚 Resources

### Documentation
- [Gradle Version Catalog](https://docs.gradle.org/current/userguide/platforms.html)
- [Gradle Versions Plugin](https://github.com/ben-manes/gradle-versions-plugin) (v0.52.0+)
- [OWASP Dependency Check](https://jeremylong.github.io/DependencyCheck/) (v11.1.1+)
- [Renovate Documentation](https://docs.renovatebot.com/)
- [Dependabot Documentation](https://docs.github.com/en/code-security/dependabot)
- [Spring Boot Versions](https://spring.io/projects/spring-boot#support)

### API Keys & Services
- [NVD API Key Request](https://nvd.nist.gov/developers/request-an-api-key) (free, highly recommended)
- [GitHub Security Advisories](https://github.com/advisories)

### Compatibility
- [Gradle-Java Compatibility Matrix](https://docs.gradle.org/current/userguide/compatibility.html)
- [Spring Boot System Requirements](https://docs.spring.io/spring-boot/system-requirements.html)

### Troubleshooting
- [Gradle Configuration Cache](https://docs.gradle.org/current/userguide/configuration_cache.html)
- [Gradle Dependency Resolution](https://docs.gradle.org/current/userguide/dependency_resolution.html)

---

**Last Updated:** February 12, 2026  
**Gradle:** 9.2.0  
**Plugin Versions:**
- Gradle Versions Plugin: 0.52.0
- OWASP Dependency Check: 11.1.1  
**Maintainer:** Development Team
