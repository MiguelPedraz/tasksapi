# Renovate Bot Configuration Guide

## Table of Contents
- [Overview](#overview)
- [Configuration File Location](#configuration-file-location)
- [Global Configuration](#global-configuration)
- [Package Rules Explained](#package-rules-explained)
- [Scheduling Strategy](#scheduling-strategy)
- [Pull Request Management](#pull-request-management)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)

---

## Overview

Renovate Bot is an automated dependency update tool that:
- 🔍 Scans dependencies in Gradle, GitHub Actions, and wrapper configurations
- 🚫 Filters unstable versions (alpha, beta, RC, snapshots)
- 📦 Groups related updates into single PRs
- ✅ Auto-merges safe updates (minor/patch) when tests pass
- ⏰ Operates during off-hours to avoid workflow disruption
- 🎫 Automatically assigns PRs for review

**Result:** Zero-maintenance dependency updates with minimal PR noise.

---

## Configuration File Location

```
tasksapi/
├── renovate.json          ← Main configuration file
├── gradle/
│   └── libs.versions.toml ← Version catalog (scanned automatically)
├── app/
│   └── build.gradle.kts   ← Build file (scanned automatically)
└── .github/
    └── workflows/         ← GitHub Actions (scanned automatically)
```

---

## Global Configuration

### 1. Repository Discovery

```json
{
  "platform": "github",
  "autodiscover": true,
  "autodiscoverFilter": ["MiguelPedraz/tasksapi"]
}
```

**Purpose:**
- `autodiscover`: Automatically finds repositories to scan
- `autodiscoverFilter`: Limits scope to specific repo(s)

**Best Practice:** Use autodiscoverFilter to avoid scanning unintended repositories if running Renovate org-wide.

---

### 2. Base Branch Configuration

```json
{
  "baseBranches": ["develop"]
}
```

**Purpose:** Creates PRs against `develop` instead of `main`

**Use Case:** Follows GitFlow workflow where features/updates merge to develop first

---

### 3. Enabled Managers

```json
{
  "enabledManagers": ["gradle", "gradle-wrapper", "github-actions"]
}
```

**What gets scanned:**
| Manager | Scans | Example Files |
|---------|-------|---------------|
| `gradle` | Dependencies, plugins, versions | `build.gradle.kts`, `libs.versions.toml` |
| `gradle-wrapper` | Gradle wrapper version | `gradle-wrapper.properties` |
| `github-actions` | GitHub Actions versions | `.github/workflows/*.yml` |

**Auto-detection:** Renovate automatically finds ALL dependencies in these files without manual listing.

---

### 4. Version Stability Filters

```json
{
  "ignoreUnstable": true,
  "respectLatest": true
}
```

**Purpose:**
- `ignoreUnstable`: Skips pre-release versions
- `respectLatest`: Only updates to versions marked as "latest" by maintainers

---

### 5. Maven Central Configuration

```json
{
  "hostRules": [
    {
      "matchHost": "https://repo1.maven.org",
      "description": "Maven Central (Sonatype CDN)"
    }
  ]
}
```

**Purpose:** Explicitly declares Maven Central (central.sonatype.com) as the dependency source

**Repository Resolution:**
- Gradle build file declares: `mavenCentral()`
- Renovate resolves to: `https://repo1.maven.org/maven2/`
- Operated by Sonatype at central.sonatype.com

---

## Package Rules Explained

### Rule 1: Block Unstable Versions ❌

```json
{
  "description": "Ignore non-stable versions globally (all managers)",
  "matchUpdateTypes": ["major", "minor", "patch"],
  "allowedVersions": "!/^.*(-alpha|-beta|-rc|-snapshot|-dev|-pre|-milestone|-m\\d+).*$/i"
}
```

**Blocks:**
- `3.5.0-RC1` (release candidate)
- `2.1.0-SNAPSHOT` (development snapshot)
- `1.0.0-beta` (beta release)
- `4.2.0-M3` (milestone)

**Allows:**
- `3.5.0` (stable release)
- `2.1.0` (stable release)

**Best Practice:** Pre-release versions are unstable and may contain breaking changes. This rule ensures only production-ready versions are suggested.

---

### Rule 2: Auto-Merge Minor/Patch Updates ✅

```json
{
  "description": "Auto-merge minor/patch updates for all Maven dependencies",
  "matchDatasources": ["maven"],
  "matchUpdateTypes": ["minor", "patch"],
  "automerge": true,
  "automergeType": "pr",
  "automergeStrategy": "squash"
}
```

**Behavior:**
| Update Type | Example | Auto-Merge? |
|-------------|---------|-------------|
| Patch | `3.4.1 → 3.4.2` | ✅ Yes (if tests pass) |
| Minor | `3.4.0 → 3.5.0` | ✅ Yes (if tests pass) |
| Major | `3.x → 4.x` | ❌ No (requires manual review) |

**Safety Mechanism:**
1. Renovate creates PR with minor/patch update
2. CI runs tests automatically
3. If all tests pass → Auto-merge
4. If tests fail → PR remains open for manual review

**Best Practice:** Minor/patch updates follow semantic versioning and should be backward-compatible. Major updates may contain breaking changes and require manual review.

---

### Rule 3-7: Dependency Grouping 📦

Instead of creating 15 individual PRs, Renovate groups related dependencies into single PRs.

#### Rule 3: Spring Framework

```json
{
  "description": "Group Spring Framework updates",
  "matchDatasources": ["maven"],
  "matchPackagePrefixes": ["org.springframework"],
  "schedule": ["before 3am on Monday"],
  "groupName": "Spring Framework"
}
```

**Groups:**
- `spring-boot` 3.4.1 → 3.4.2
- `spring-boot-starter-web`
- `spring-boot-starter-data-jpa`
- `spring-dependency-management`

**Schedule:** Updates only on Monday mornings (03:00 Europe/Madrid)

**Rationale:** Spring updates are often interconnected. Updating them together reduces integration issues.

---

#### Rule 4: Testing Frameworks

```json
{
  "description": "Group Testing frameworks",
  "matchDatasources": ["maven"],
  "matchPackagePrefixes": ["org.junit", "org.mockito", "org.testcontainers", "io.rest-assured"],
  "groupName": "Testing Dependencies"
}
```

**Groups:**
- JUnit Jupiter
- Mockito Core + Jupiter
- Testcontainers (PostgreSQL, JUnit)
- REST Assured

**Example PR:** "Update Testing Dependencies" containing all 8 testing library updates

---

#### Rule 5: Database Drivers

```json
{
  "description": "Group Database drivers",
  "matchDatasources": ["maven"],
  "matchPackagePrefixes": ["org.postgresql", "com.h2database"],
  "groupName": "Database Drivers"
}
```

**Groups:**
- PostgreSQL JDBC Driver
- H2 Database (test database)

---

#### Rule 6: Observability & Documentation

```json
{
  "description": "Group Observability tools",
  "matchDatasources": ["maven"],
  "matchPackagePrefixes": ["io.micrometer", "org.springdoc"],
  "groupName": "Observability & Documentation"
}
```

**Groups:**
- Micrometer (Prometheus metrics)
- SpringDoc OpenAPI (Swagger UI)

---

#### Rule 7: Build Tools & Gradle Plugins

```json
{
  "description": "Group Build tools and Gradle plugins",
  "matchManagers": ["gradle"],
  "matchDepTypes": ["plugin"],
  "groupName": "Build Tools & Plugins"
}
```

**Groups:**
- `com.github.ben-manes.versions` (dependency version checker)
- `org.owasp.dependencycheck` (security scanner)
- Other Gradle plugins

---

### Rule 8: GitHub Actions Versioning 🤖

```json
{
  "description": "Stable releases for GitHub Actions",
  "matchManagers": ["github-actions"],
  "pinDigests": false,
  "semanticCommits": "enabled"
}
```

**Updates:**
- `actions/checkout@v3` → `actions/checkout@v4`
- `actions/setup-java@v3` → `actions/setup-java@v4`

**Configuration:**
- `pinDigests: false`: Uses version tags instead of commit SHAs
- `semanticCommits: enabled`: Uses conventional commit messages

---

## Scheduling Strategy

### Daily Scan Schedule

```json
{
  "schedule": ["after 10pm every weekday", "before 5am every weekday"],
  "timezone": "Europe/Madrid"
}
```

**Behavior:**
- **Scan window:** 22:00 - 05:00 (Monday-Friday)
- **Timezone:** Europe/Madrid (CET/CEST)
- **Purpose:** Avoids disrupting daytime workflow

**Best Practice:** Schedule scans during off-hours to:
1. Prevent PR noise during active development
2. Allow Morning code review of overnight updates
3. Reduce GitHub API rate limiting during peak hours

---

### Special Schedules

```json
{
  "schedule": ["before 3am on Monday"]  // Spring Framework updates
}
```

**Rationale:** Major framework updates (Spring) are scheduled for start of week:
1. More time to test if issues arise (Mon-Fri)
2. Easier rollback before weekend
3. Team is fresh and available for troubleshooting

---

### Lock File Maintenance

```json
{
  "lockFileMaintenance": {
    "enabled": true,
    "schedule": ["before 3am on Monday"]
  }
}
```

**Purpose:** Refreshes lock files (gradle.lockfile) to ensure reproducible builds

**Weekly Schedule:** Same as Spring updates (Monday 03:00) to batch maintenance tasks

---

## Pull Request Management

### Concurrent PR Limit

```json
{
  "prConcurrentLimit": 5
}
```

**Purpose:** Maximum 5 open PRs at once to avoid overwhelming the review queue

**Behavior:**
- If 5 PRs are open, Renovate waits until one is merged/closed
- Then creates the next pending update

---

### PR Creation Strategy

```json
{
  "prCreation": "immediate",
  "rebaseWhen": "behind-base-branch"
}
```

**Settings:**
- `immediate`: Creates PRs as soon as updates are detected
- `rebaseWhen: behind-base-branch`: Auto-rebases PR if develop branch advances

**Alternative Options:**
- `prCreation: "not-pending"`: Wait until status checks pass before creating PR
- `prCreation: "approval"`: Require manual approval before creating PR

---

### Automatic Assignment

```json
{
  "labels": ["dependencies", "automated"],
  "assignees": ["@MiguelPedraz"]
}
```

**Purpose:**
- **Labels:** Tag PRs for filtering (`is:pr label:dependencies`)
- **Assignees:** Auto-assigns to team members for review

**Best Practice:** Use labels to create saved searches and dashboard views for dependency updates

---

## Best Practices

### 1. Automatic Dependency Detection

**✅ DO:**
- Add new dependencies to `libs.versions.toml` or `build.gradle.kts`
- Renovate automatically detects them without config changes

**❌ DON'T:**
- Manually list dependencies in `renovate.json`
- Use `matchPackageNames` (specific) instead of `matchPackagePrefixes` (flexible)

**Example:**
```toml
# Add to libs.versions.toml
jackson = "2.17.0"
```
Renovate automatically detects and updates `jackson` without renovate.json changes.

---

### 2. Grouping Strategy

**✅ DO:** Group dependencies by functional area:
- Framework (Spring)
- Testing (JUnit, Mockito)
- Infrastructure (Database drivers)

**❌ DON'T:** Group unrelated dependencies:
- Spring + Database + Testing in one group
- Creates large PRs that are hard to review

---

### 3. Auto-Merge Policy

**✅ Auto-merge:**
- Patch updates: `3.4.1 → 3.4.2`
- Minor updates: `3.4.0 → 3.5.0`
- If all tests pass

**❌ Manual review required:**
- Major updates: `3.x → 4.x`
- Security vulnerabilities (CVSS > 7.0)
- Breaking changes in changelog

---

### 4. Scheduling Considerations

**✅ DO:**
- Schedule scans during off-hours (nights, weekends)
- Use different schedules for critical vs routine updates
- Align with team's code review availability

**❌ DON'T:**
- Scan during peak development hours
- Create PRs right before deployments
- Use same schedule for all dependency types

---

### 5. Version Stability

**✅ DO:**
- Block unstable versions globally (`-alpha`, `-beta`, `-rc`, `-snapshot`)
- Use `respectLatest: true` to follow maintainer recommendations
- Test updates in develop branch before merging to main

**❌ DON'T:**
- Allow pre-release versions in production builds
- Ignore semantic versioning (major.minor.patch)
- Skip testing before auto-merge

---

## Troubleshooting

### Issue 1: Renovate Not Creating PRs

**Symptoms:**
- No PRs created after initial setup
- Dashboard shows "No updates found"

**Diagnosis:**
```bash
# Check if repository is discoverable
grep "autodiscoverFilter" renovate.json

# Verify managers are enabled
grep "enabledManagers" renovate.json
```

**Solution:**
- Ensure `autodiscoverFilter` matches your repository name
- Check `baseBranches` points to correct branch
- Verify RENOVATE_TOKEN has correct permissions (repo + workflows)

---

### Issue 2: Too Many PRs Created

**Symptoms:**
- 10+ PRs created simultaneously
- Overwhelming review queue

**Diagnosis:**
```bash
# Check concurrent limit
grep "prConcurrentLimit" renovate.json

# Check grouping rules
grep "groupName" renovate.json
```

**Solution:**
```json
{
  "prConcurrentLimit": 3,  // Reduce from 5 to 3
  "packageRules": [
    {
      "matchDatasources": ["maven"],
      "groupName": "All Maven Dependencies"  // Aggressive grouping
    }
  ]
}
```

---

### Issue 3: Auto-Merge Not Working

**Symptoms:**
- PRs created but not auto-merged
- Tests pass but PR remains open

**Diagnosis:**
```bash
# Check CI workflow requires specific checks
cat .github/workflows/ci.yml

# Verify branch protection rules
# GitHub → Settings → Branches → develop → Required checks
```

**Solution:**
- Ensure CI workflow name matches branch protection required checks
- Grant Renovate Bot permission to merge PRs
- Check `automerge: true` is set in package rules

---

### Issue 4: Unstable Versions Still Suggested

**Symptoms:**
- Seeing `3.5.0-RC1` or `2.1-SNAPSHOT` in PRs

**Diagnosis:**
```bash
# Check allowedVersions regex
grep "allowedVersions" renovate.json
```

**Solution:**
```json
{
  "allowedVersions": "!/^.*(-alpha|-beta|-rc|-snapshot|-dev|-pre|-milestone|-m\\d+).*$/i"
}
```

**Test Regex:** Use [regex101.com](https://regex101.com/) to verify pattern matches unwanted versions

---

### Issue 5: GitHub API Rate Limiting

**Symptoms:**
- "Rate limit exceeded" in Renovate logs
- Updates delayed or skipped

**Diagnosis:**
```bash
# Check GitHub API rate limit
curl -H "Authorization: token YOUR_TOKEN" \
  https://api.github.com/rate_limit
```

**Solution:**
1. Reduce scan frequency:
   ```json
   {
     "schedule": ["before 3am on Monday"]  // Once per week
   }
   ```

2. Use GitHub App instead of Personal Access Token (higher rate limits)

3. Reduce `prConcurrentLimit` to minimize API calls

---

## Validation Commands

### Test Configuration Locally

```bash
# Validate renovate.json syntax
npx renovate-config-validator

# Dry-run (preview updates without creating PRs)
npx renovate --platform=github --token=$GITHUB_TOKEN --dry-run=full
```

### Monitor Renovate Execution

```bash
# Check workflow runs
gh run list --workflow=renovate-manual.yml

# View logs
gh run view <run-id> --log
```

---

## Related Documentation

- [Renovate Manual Workflow](RENOVATE_MANUAL_WORKFLOW.md) - How to trigger Renovate manually
- [Dependency Management](DEPENDENCY_MANAGEMENT.md) - Overview of dependency strategy
- [Auto Update Setup](AUTO_UPDATE_SETUP.md) - Initial Renovate setup guide
- [Security Scanning](SECURITY_SCANNING_SETUP.md) - NVD security scanning with Renovate

---

## References

- [Renovate Documentation](https://docs.renovatebot.com/)
- [Configuration Options](https://docs.renovatebot.com/configuration-options/)
- [Package Rules](https://docs.renovatebot.com/configuration-options/#packagerules)
- [Maven Datasource](https://docs.renovatebot.com/modules/datasource/maven/)
- [Semantic Versioning](https://semver.org/)

---

**Last Updated:** February 14, 2026  
**Configuration Version:** 1.0  
**Tested With:** Renovate Bot v46.1.0
