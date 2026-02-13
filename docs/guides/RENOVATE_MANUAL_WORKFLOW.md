# 🔄 Renovate Manual Workflow Guide

## 📚 Table of Contents
- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Setup Instructions](#setup-instructions)
  - [Step 1: Generate GitHub Personal Access Token](#step-1-generate-github-personal-access-token)
  - [Step 2: Add Token to Repository Secrets](#step-2-add-token-to-repository-secrets)
  - [Step 3: Verify Configuration](#step-3-verify-configuration)
- [How to Run the Workflow](#how-to-run-the-workflow)
- [What Happens When You Run It](#what-happens-when-you-run-it)
- [Configuration Details](#configuration-details)
- [Troubleshooting](#troubleshooting)

---

## Overview

The **Renovate Manual Workflow** allows you to trigger dependency updates on-demand instead of waiting for the scheduled automatic runs. This is useful when:

- 🚀 You want to check for updates immediately
- 🐛 Testing Renovate configuration changes
- 🔍 Investigating dependency issues
- ⚡ Need urgent security updates

**Workflow File:** `.github/workflows/renovate-manual.yml`  
**Configuration File:** `renovate.json` (root directory)

---

## Prerequisites

- Repository hosted on GitHub
- GitHub account with admin/maintain permissions on the repository
- Existing Renovate configuration in `renovate.json`

---

## Setup Instructions

### Step 1: Generate GitHub Personal Access Token

1. **Navigate to GitHub Settings:**
   ```
   GitHub → Click your profile (top-right) → Settings
   → Developer settings (left sidebar, bottom)
   → Personal access tokens → Tokens (classic)
   ```

2. **Generate New Token:**
   - Click **"Generate new token (classic)"**
   - **Note:** `Renovate Bot Token for tasksapi` (or similar descriptive name)
   - **Expiration:** Choose appropriate expiration (90 days recommended)
   
3. **Select Scopes:**
   - ✅ **repo** (full control of private repositories)
     - ✅ repo:status
     - ✅ repo_deployment
     - ✅ public_repo
     - ✅ repo:invite
     - ✅ security_events

4. **Generate and Copy Token:**
   - Click **"Generate token"** at the bottom
   - ⚠️ **IMPORTANT:** Copy the token immediately (format: `ghp_xxxxxxxxxxxxx`)
   - You won't be able to see it again!

### Step 2: Add Token to Repository Secrets

1. **Navigate to Repository Settings:**
   ```
   Go to your repository → Settings tab
   → Secrets and variables (left sidebar)
   → Actions
   ```

2. **Create New Secret:**
   - Click **"New repository secret"**
   - **Name:** `RENOVATE_TOKEN` (must be exactly this name)
   - **Secret:** Paste the token you copied in Step 1
   - Click **"Add secret"**

3. **Verify:**
   - You should see `RENOVATE_TOKEN` listed under "Repository secrets"
   - The value will be hidden for security

### Step 3: Verify Configuration

Ensure the workflow file references the correct configuration:

```yaml
# .github/workflows/renovate-manual.yml
- name: Run Renovate
  uses: renovatebot/github-action@v40
  with:
    token: ${{ secrets.RENOVATE_TOKEN }}
    configurationFile: renovate.json  # ✅ Points to root renovate.json
```

---

## How to Run the Workflow

### Via GitHub Web Interface (Easiest)

#### Step-by-Step Visual Guide:

```
GitHub Repository Page
├─ Code  Issues  Pull requests  [ACTIONS]  ← Click this tab
│
└─ Actions Page Opens:
    │
    ├─ Left Sidebar (All workflows):     │  Main Content Area:
    │  ┌─────────────────────────┐     │  ┌─────────────────────────────┐
    │  │ All workflows           │     │  │ [Filter] [Branch: develop]  │
    │  ├─────────────────────────┤     │  ├─────────────────────────────┤
    │  │ 🔄 Run Renovate (manual)│ ←── CLICK HERE FIRST
    │  │ ✓ Auto-merge Dependency │     │  │ Recent workflow runs...      │
    │  │ 🔒 Dependency Check     │     │  │ ✅ Build succeeded           │
    │  └─────────────────────────┘     │  └─────────────────────────────┘
    │                                   │
    └── After clicking workflow name:  │
                                        │  ┌─────────────────────────────┐
                                        │  │ Run Renovate (manual)       │
                                        │  │                             │
                                        │  │     [▶ Run workflow] ← Button appears!
                                        │  │                             │
                                        │  │ Recent runs:                │
                                        │  │ • #12 - 2 hours ago ✅      │
                                        │  └─────────────────────────────┘
```

#### Detailed Instructions:

1. **Navigate to Actions:**
   ```
   Go to: https://github.com/MiguelPedraz/tasksapi/actions
   ```
   Or click the **"Actions"** tab at the top of your repository

2. **Select Workflow:**
   - Look at the **left sidebar** under "All workflows"
   - Find and click **"Run Renovate (manual)"**
   - ⚠️ **Important:** You must click on the workflow name first!

3. **Run Workflow:**
   After clicking the workflow name, you'll see:
   - A blue **"Run workflow"** button appears on the right side
   - Click it to open a dropdown
   - Select branch: **`develop`** (recommended) or your desired branch
   - Click the green **"Run workflow"** button to confirm

4. **Monitor Execution:**
   - A new workflow run will appear at the top of the runs list
   - Click on it to see real-time logs
   - Typical duration: 1-3 minutes
   - Status indicator: 🟡 In Progress → ✅ Success / ❌ Failed

### Via GitHub CLI (Advanced)

If you have [GitHub CLI](https://cli.github.com/) installed:

```bash
# Trigger workflow on develop branch
gh workflow run "Run Renovate (manual)" --ref develop

# Check workflow status
gh run list --workflow="renovate-manual.yml"

# View logs of the latest run
gh run view --log
```

---

## What Happens When You Run It

### Execution Flow

1. **Checkout Repository:**
   - Renovate clones your repository with the latest code

2. **Load Configuration:**
   - Reads `renovate.json` from the repository root
   - Applies all rules, schedules, and grouping configurations

3. **Scan Dependencies:**
   - Analyzes `gradle/libs.versions.toml`
   - Checks Maven Central for available updates
   - Detects Gradle Wrapper versions
   - Reviews GitHub Actions versions (if configured)

4. **Compare Versions:**
   - Identifies outdated dependencies
   - Respects version constraints and rules
   - Groups updates according to `packageRules`

5. **Create/Update Pull Requests:**
   - Opens new PRs for dependency updates
   - Updates existing PRs if they're outdated
   - Follows naming convention: `fix(deps): update [group-name]`

6. **Trigger CI/CD:**
   - Each PR automatically triggers:
     - Build verification
     - Test execution
     - Coverage checks (JaCoCo)
     - Security scans (OWASP)
   - Auto-merge workflow evaluates if it's safe to merge

### Expected Outcome

After successful execution, you'll see:

- ✅ New PRs created in the **Pull Requests** tab
- 📊 Dependency Dashboard issue updated (if enabled)
- 🔔 Notifications for new PRs (if configured)

Example PR names based on current configuration:
- `fix(deps): update Gradle - actualizaciones no mayores`
- `fix(deps): update Spring Boot`
- `fix(deps): update Testing Dependencies`
- `fix(deps): update Gradle Wrapper`

---

## Configuration Details

### Current Renovate Configuration (`renovate.json`)

```json
{
  "baseBranches": ["develop"],          // Targets develop branch
  "enabledManagers": ["gradle", "gradle-wrapper"],
  "timezone": "Europe/Madrid",
  "schedule": [
    "after 10pm every weekday",         // Runs during night hours
    "before 5am every weekday"
  ],
  "automerge": true,                    // Auto-merge safe updates
  "prConcurrentLimit": 5,               // Max 5 PRs at once
  "packageRules": [
    {
      "matchDatasources": ["maven"],
      "matchUpdateTypes": ["minor", "patch"],
      "automerge": true                 // Minor/patch auto-merge
    },
    {
      "matchPackagePatterns": ["^org.springframework.boot"],
      "groupName": "Spring Boot"        // Group all Spring Boot updates
    }
    // ... more rules
  ]
}
```

### Key Features

| Feature | Configuration | Benefit |
|---------|---------------|---------|
| **Target Branch** | `develop` | PRs created against develop, not main |
| **Auto-merge** | Enabled for minor/patch | Reduces manual work |
| **Grouping** | Spring Boot, Testing, Database | Fewer PRs, easier review |
| **Scheduling** | 10pm-5am weekdays | No interruptions during work hours |
| **Timezone** | Europe/Madrid | Respects local business hours |
| **Rate Limiting** | Max 5 PRs | Prevents overwhelming reviewers |

---

## Troubleshooting

### Issue: "Run workflow" button not visible

**Common causes and solutions:**

1. **You haven't selected a specific workflow:**
   - ❌ Wrong: Looking at the main Actions page with the list of runs
   - ✅ Correct: Click on **"Run Renovate (manual)"** in the left sidebar first
   - The button only appears when viewing a specific workflow

2. **Workflow not yet synced by GitHub:**
   - If the workflow was just merged/pushed, GitHub may need 1-2 minutes to index it
   - Solution: Refresh the page (F5) or wait a moment and try again

3. **Wrong branch selected:**
   - GitHub shows workflows from the branch you're currently viewing
   - Solution: Use the branch dropdown at the top to switch to `develop`
   - Or go directly to: `https://github.com/MiguelPedraz/tasksapi/actions/workflows/renovate-manual.yml`

4. **Insufficient permissions:**
   - You need `write` or `admin` access to the repository
   - Collaborators with `read` access cannot trigger workflows
   - Solution: Ask repository owner to grant you proper permissions

5. **Workflow file has syntax errors:**
   - Check if the workflow file is valid YAML
   - Look for error markers in the Actions tab
   - Solution: Validate the workflow file at https://www.yamllint.com/

**Quick fix - Direct links:**

**If workflow is in `develop` branch:**
```
https://github.com/MiguelPedraz/tasksapi/actions/workflows/renovate-manual.yml?query=branch%3Adevelop
```

**If workflow is in `main` branch:**
```
https://github.com/MiguelPedraz/tasksapi/actions/workflows/renovate-manual.yml
```

These take you directly to the workflow filtered by branch where the "Run workflow" button should be visible.

💡 **Tip:** GitHub Actions shows workflows from the repository's default branch (`main`) by default. If your workflow is only in `develop`, you need to either:
- Use the direct link above with `?query=branch%3Adevelop`
- Switch branch using the branch selector in the Actions tab
- Merge the workflow to `main` (if you want it always visible)

---

### Issue: "Workflow not found" or doesn't appear

**Cause:** Workflow file not merged to the branch  
**Solution:**
```bash
# Ensure you're on the correct branch
git checkout develop
git pull origin develop

# Verify file exists
ls .github/workflows/renovate-manual.yml

# If missing, merge the PR or checkout the branch with the workflow
```

### Issue: "Error: Resource not accessible by integration"

**Cause:** Missing or incorrect `RENOVATE_TOKEN`  
**Solution:**
1. Verify token exists: Repository → Settings → Secrets → Actions
2. Check token name is exactly `RENOVATE_TOKEN`
3. Ensure token has `repo` scope
4. Regenerate token if expired

### Issue: "No dependency updates found"

**Cause:** All dependencies are up-to-date or ignored  
**Solution:**
- Check Renovate logs in the workflow run
- Review `renovate.json` for `ignoreDeps` or `packageRules` that might exclude packages
- Verify `gradle/libs.versions.toml` is readable

### Issue: "Rate limit exceeded"

**Cause:** Too many API calls to Maven Central or GitHub  
**Solution:**
- Wait a few minutes and retry
- Check `prConcurrentLimit` in `renovate.json`
- Ensure token is not shared across multiple repositories

### Issue: PRs created against wrong branch

**Cause:** `baseBranches` misconfigured  
**Solution:**
```json
// renovate.json
{
  "baseBranches": ["develop"],  // ✅ Ensure this matches your target
  // ...
}
```

### Issue: Workflow runs but no PRs created

**Check the following:**

1. **Dependencies are actually outdated:**
   ```bash
   ./gradlew dependencyUpdates
   ```

2. **Renovate logs show what it found:**
   - Go to Actions → Click workflow run → Expand "Run Renovate" step
   - Look for "Package file detected" and "Dependency extraction complete"

3. **PRs might already exist:**
   - Check Pull Requests tab
   - Renovate won't create duplicates

4. **Updates might be scheduled:**
   - Major updates scheduled for Mondays before 3am
   - Check `packageRules` in `renovate.json`

---

## Best Practices

### When to Run Manually

✅ **Good use cases:**
- After adding new dependencies
- When a critical security vulnerability is announced
- Testing configuration changes
- Before sprint planning to see upcoming updates

❌ **Avoid running:**
- Multiple times per day (wastes resources)
- While other Renovate PRs are still open (creates conflicts)
- During CI/CD deployments

### Reviewing Renovate PRs

1. **Check CI Status:**
   - Ensure all tests pass
   - Review coverage report
   - Check security scan results

2. **Review Changes:**
   - Look at `gradle/libs.versions.toml` changes
   - Check release notes linked in PR description
   - Verify major version updates carefully

3. **Let Auto-merge Work:**
   - Minor/patch updates auto-merge after CI ✅
   - Only review major updates manually

4. **Close Stale PRs:**
   - If a PR is superseded, close it
   - Renovate will create a new one with the latest version

---

## Additional Resources

- **Renovate Documentation:** https://docs.renovatebot.com/
- **GitHub Actions Docs:** https://docs.github.com/en/actions
- **Project Dependency Management:** See `docs/guides/DEPENDENCY_MANAGEMENT.md`
- **Auto-update Setup (Spanish):** See `docs/guides/AUTO_UPDATE_SETUP.md`

---

## Summary

**Setup Steps:**
1. Generate GitHub Personal Access Token with `repo` scope
2. Add token as `RENOVATE_TOKEN` secret in repository
3. Verify `renovate.json` configuration

**To Run:**
1. Go to Actions tab
2. Select "Run Renovate (manual)"
3. Click "Run workflow"
4. Monitor the execution and review PRs created

**Expected Result:**
- Dependency update PRs created against `develop` branch
- Automatic CI/CD checks triggered
- Auto-merge for safe minor/patch updates

---

**Last Updated:** February 12, 2026  
**Workflow Version:** renovatebot/github-action@v40  
**Configuration:** renovate.json (root)
