# 🤖 Configuración de Actualizaciones Automáticas

## 📚 Tabla de Contenidos
- [Introducción](#introducción)
- [Opción 1: Renovate Bot](#opción-1-renovate-bot-recomendado)
- [Opción 2: Dependabot](#opción-2-dependabot)
- [Estrategia Recomendada](#estrategia-recomendada-usar-ambos)
- [Verificación y Monitoreo](#verificación-y-monitoreo)
- [Troubleshooting](#troubleshooting)

---

## Introducción

Este proyecto tiene **2 sistemas de auto-actualización ya configurados**:

| Sistema | Archivo Config | Estado | Uso Recomendado |
|---------|---------------|---------|-----------------|
| **Renovate Bot** | `renovate.json` | ✅ Configurado | Java/Gradle dependencies |
| **Dependabot** | `.github/dependabot.yml` | ✅ Configurado | GitHub Actions workflows |

### ¿Por Qué Necesitamos Esto?

El comando `./gradlew dependencyUpdates` solo **genera reportes**, pero:
- ❌ No modifica archivos automáticamente
- ❌ Requiere actualización manual de `libs.versions.toml`
- ❌ No crea PRs con tests automáticos

**Renovate/Dependabot solucionan esto:**
- ✅ Detectan actualizaciones automáticamente
- ✅ Crean PRs con los cambios
- ✅ Ejecutan tests antes de merge
- ✅ Auto-merge de patches/minor seguros

---

## Opción 1: Renovate Bot (Recomendado)

### 🎯 Ventajas de Renovate

- **Auto-merge inteligente**: Patches/minor se mergean solos después de tests
- **Agrupación avanzada**: Un solo PR para "Spring Boot", otro para "Testing", etc.
- **Schedule flexible**: Solo trabaja de noche (10pm-5am) para no molestar
- **Manejo de Version Catalogs**: Lee y actualiza `gradle/libs.versions.toml` directamente
- **Rebase automático**: Mantiene PRs actualizados con main

### 📋 Paso 1: Subir Proyecto a GitHub

```bash
cd tasksapi

# Inicializar git (si no existe)
git init
git add .
git commit -m "chore: initial commit with dependency management"

# Crear repositorio en GitHub (https://github.com/new)
# Nombre sugerido: tasksapi

# Conectar y subir
git remote add origin https://github.com/TU-USUARIO/tasksapi.git
git branch -M main
git push -u origin main
```

### 📋 Paso 2: Instalar Renovate Bot

#### 🌐 **1. Visita la página de Renovate:**
```
https://github.com/apps/renovate
```

#### 🟢 **2. Click en el botón verde `Install`**
- Aparecerá en la parte superior derecha de la página
- Si ya lo instalaste antes, verás `Configure` en su lugar

#### 🎯 **3. Selecciona repositorios:**
- ✅ **Recomendado:** "Only select repositories"
  - Click en el dropdown
  - Busca y selecciona: `tasksapi`
  - Esto mantiene control sobre qué repos usa Renovate
  
- 🌍 **Alternativa:** "All repositories"
  - Solo si confías en Renovate para todos tus proyectos
  - Renovate seguirá solo repos con `renovate.json`

#### 🔐 **4. Click en `Install & Authorize`**
- Renovate pedirá permisos para:
  - ✅ Leer código (detectar dependencias)
  - ✅ Crear branches (para PRs)
  - ✅ Crear PRs (actualizaciones)
  - ✅ Leer/escribir checks (tests)

#### ⏳ **5. Espera el onboarding (5-10 minutos)**
- Renovate escaneará tu proyecto
- Detectará todas las dependencias
- Creará el PR inicial de configuración

### 📋 Paso 3: Onboarding PR

#### 🎉 **Renovate creará automáticamente su primer PR:**

```
🔷 PR #1: "Configure Renovate"
   Título:  Configure Renovate
   Labels:  🏷️ renovate, dependencies
   Estado:  ✅ Lista para merge
   Author:  🤖 renovate[bot]
```

#### 📦 **Este PR incluirá:**
- 📊 **Dependencias detectadas:** Lista completa de todo lo que encontró
- 📈 **Actualizaciones disponibles:** Qué se puede actualizar ahora
- ⚙️ **Configuración aplicada:** Preview de cómo trabajará (desde `renovate.json`)
- 🔍 **Previsualización:** Ejemplos de futuros PRs que creará
- 📝 **Dashboard:** Link al dashboard de Renovate con issues detectadas

#### 🚀 **Acción requerida (mergearlo):**

#### 🖱️ **Opción A: Desde GitHub UI (Recomendado):**
1. 🌐 Ve a: `https://github.com/TU-USUARIO/tasksapi/pulls`
2. 👆 Click en el PR "Configure Renovate"
3. 📖 Lee el contenido (opcional pero recomendado)
4. 🟢 Click en botón verde **"Merge pull request"**
5. ✅ Click en **"Confirm merge"**
6. 🗑️ Click en **"Delete branch"** (limpieza)

#### 💻 **Opción B: Desde línea de comandos:**
```bash
# Actualizar referencias remotas
git fetch origin

# Ver el PR localmente
git checkout renovate/configure
git log  # Ver commits del PR

# Mergear a main
git checkout main
git merge renovate/configure
git push origin main

# Limpiar branch
git branch -d renovate/configure
git push origin --delete renovate/configure
```

### 📋 Paso 4: Configurar Auto-merge (Opcional pero Recomendado)

#### 🎯 **¿Por qué habilitar auto-merge?**
- ⚡ Actualizaciones de seguridad se aplican automáticamente
- 🛡️ Patches y minor versions son typicalidad seguros
- ⏰ No necesitas revisar cada actualización pequeña
- 🧪 Solo se mergea si todos los tests pasan

#### ⚙️ **En GitHub (Habilitar la funcionalidad):**

1. 🌐 **Ve a configuración del repo:**
   ```
   https://github.com/TU-USUARIO/tasksapi/settings
   ```

2. 📜 **Scroll hasta la sección "Pull Requests"**

3. ✅ **Activa estas opciones:**
   - ☑️ **"Allow auto-merge"**
     - Permite que PRs se mergeen automáticamente
     - Necesario para que Renovate use auto-merge
   
   - ☑️ **"Automatically delete head branches"** 
     - Limpia branches después de merge
     - Mantiene el repo ordenado
   
   - ☑️ **"Allow squash merging"** (si no está activo)
     - Renovate usa squash merge por defecto
     - Mantiene historial limpio

4. 💾 **Guarda cambios** (scroll abajo y click en "Save")

#### **Verificar en renovate.json:**
```json
{
  "packageRules": [
    {
      "matchUpdateTypes": ["minor", "patch"],
      "automerge": true,  // ✅ Ya configurado
      "automergeType": "pr",
      "automergeStrategy": "squash"
    }
  ]
}
```

### 📋 Paso 5: Esperar los PRs de Actualización

#### 🕐 **Horario de trabajo de Renovate:**

```
🌙 Horario:  Lunes-Viernes, 10pm - 5am (no molesta durante el día)
🔢 Límite:   Máximo 5 PRs concurrentes
📊 Rate:     1 PR cada 5 minutos (evita spam)
🔄 Rebase:   Automático si main cambia
```

#### 📬 **Ejemplo de PRs que recibirás:**

##### 🟢 **PR #2: Update Spring Boot Dependencies (grouped)**
```diff
📦 Grupo: Spring Boot
🏷️ Labels: renovate, dependencies, spring-boot

+ spring-boot: 3.3.0 → 3.3.1
+ spring-boot-starter-web: 3.3.0 → 3.3.1
+ spring-boot-starter-data-jpa: 3.3.0 → 3.3.1

✅ Status: Auto-merge after tests pass
🧪 Checks: All passing ✓
⏰ Auto-merge: In 2 hours (stability period)
```

##### 🟢 **PR #3: Update Testing Dependencies (grouped)**
```diff
📦 Grupo: Testing
🏷️ Labels: renovate, dependencies, testing

+ junit-jupiter: 5.12.1 → 5.12.2
+ mockito-core: 5.15.2 → 5.15.3
+ assertj-core: 3.26.3 → 3.27.0

✅ Status: Auto-merge after tests pass
🧪 Checks: All passing ✓
⏰ Auto-merge: In 2 hours (stability period)
```

##### 🔴 **PR #4: Update dependency org.postgresql:postgresql [MAJOR]**
```diff
📦 Actualización: MAJOR version
🏷️ Labels: renovate, dependencies, major
⚠️ Requiere: Revisión manual

+ postgresql: 42.7.5 → 43.0.0

❌ Status: Manual review required
📝 Notes: Breaking changes possible
🔍 Changelog: https://github.com/pgjdbc/pgjdbc/releases/tag/43.0.0
```

PR #4: Update dependency org.postgresql:postgresql to v42.7.10
Status: ✅ Auto-merge after tests pass
```

---

## Opción 2: Dependabot

### 🎯 Ventajas de Dependabot

- **Zero setup**: GitHub lo activa automáticamente al detectar `dependabot.yml`
- **Nativo de GitHub**: Integración perfecta sin apps externas
- **Simple**: Menos configuración, más directo
- **Gratis**: Incluido en todos los planes de GitHub

### 📋 Paso 1: Verificar Configuración

El archivo `.github/dependabot.yml` ya está listo:

```yaml
version: 2
updates:
  # Java dependencies
  - package-ecosystem: "gradle"
    directory: "/"
    schedule:
      interval: "weekly"
      day: "monday"
      time: "03:00"
      timezone: "UTC"
    labels:
      - "dependencies"
      - "automated"
    commit-message:
      prefix: "chore(deps)"
    groups:
      spring-boot:
        patterns: ["org.springframework.boot*"]
      testing:
        patterns: ["org.junit*", "org.mockito*", "org.assertj*"]
      database:
        patterns: ["org.postgresql*", "com.h2database*"]

  # GitHub Actions
  - package-ecosystem: "github-actions"
    directory: "/"
    schedule:
      interval: "weekly"
      day: "monday"
      time: "03:00"
    labels:
      - "dependencies"
      - "github-actions"
```

### 📋 Paso 2: Subir a GitHub

```bash
cd tasksapi
git add .github/dependabot.yml
git commit -m "chore: add Dependabot configuration"
git push
```

**Dependabot se activa automáticamente** en cuanto detecta el archivo.

### 📋 Paso 3: Verificar Activación

#### **Método 1: Insights**
1. Ve a: `https://github.com/TU-USUARIO/tasksapi`
2. Click en **"Insights"** (barra superior)
3. Click en **"Dependency graph"** (menú izquierdo)
4. Click en **"Dependabot"**
5. Deberías ver: `✓ Dependabot alerts are enabled`

#### **Método 2: Security Tab**
1. Ve a: `https://github.com/TU-USUARIO/tasksapi/security`
2. Click en **"Dependabot"**
3. Verás: "Dependabot is monitoring your dependencies"

### 📋 Paso 4: Esperar el Primer Ciclo

**Dependabot ejecuta:**
```
Día: Lunes
Hora: 3:00 AM UTC
Frecuencia: Semanal
```

**Ejemplo de PRs que recibirás:**

```
PR #1: Bump spring-boot from 3.3.0 to 3.3.1 in the spring-boot group
Labels: dependencies, automated
Commit: chore(deps): bump spring-boot from 3.3.0 to 3.3.1

PR #2: Bump postgresql from 42.7.3 to 42.7.10 in the database group
Labels: dependencies, automated
Commit: chore(deps): bump postgresql from 42.7.3 to 42.7.10
```

### 📋 Paso 5: Forzar Ejecución Inmediata (Opcional)

Dependabot no tiene botón "Run now", pero puedes:

```bash
# Editar el schedule temporalmente
# Cambia en .github/dependabot.yml:
schedule:
  interval: "daily"  # Era "weekly"

# Commit y push
git add .github/dependabot.yml
git commit -m "chore: test Dependabot with daily schedule"
git push

# Espera 2-3 horas
# Vuelve a cambiar a "weekly" después
```

---

## Estrategia Recomendada: Usar Ambos

### 🎯 División de Responsabilidades

| Herramienta | Ecosistema | Ventaja |
|-------------|------------|---------|
| **Renovate Bot** | Gradle dependencies | Auto-merge, agrupación inteligente, horario nocturno |
| **Dependabot** | GitHub Actions | Nativo, actualiza workflows automáticamente |

### 📋 Configuración Óptima

#### **1. renovate.json (Ya aplicado)**
```json
{
  "enabledManagers": ["gradle", "gradle-wrapper"],
  // Solo gestiona Gradle, ignora GitHub Actions
}
```

#### **2. dependabot.yml (Ya configurado)**
```yaml
updates:
  - package-ecosystem: "github-actions"
    # Solo gestiona GitHub Actions
```

### 🚀 Resultado Final

**Renovate Bot manejará:**
- ✅ `gradle/libs.versions.toml` (Spring Boot, PostgreSQL, JUnit, etc.)
- ✅ Gradle Wrapper (`gradlew`, `gradle-wrapper.properties`)
- ✅ Auto-merge de patches/minor
- ✅ Agrupación: Spring Boot, Testing, Database

**Dependabot manejará:**
- ✅ `.github/workflows/*.yml` (GitHub Actions)
- ✅ `actions/checkout@v4` → `@v5`
- ✅ Setup de Java, Gradle, etc.

**Sin conflictos**: Cada uno en su ecosistema.

---

## Verificación y Monitoreo

### 📊 Dashboard de Renovate

**Ver actividad:**
```
https://app.renovatebot.com/dashboard
```

**O en GitHub:**
```
https://github.com/TU-USUARIO/tasksapi/pulls/app/renovate
```

### 📊 Dashboard de Dependabot

```
https://github.com/TU-USUARIO/tasksapi/security/dependabot
```

### 📧 Notificaciones

**Configurar en GitHub:**
1. Settings → Notifications
2. **"Actions"** → ✅ Pull request reviews
3. **"Dependabot alerts"** → ✅ Email notifications

### 📈 Métricas de Éxito

**Indicadores sanos:**
```
✅ PRs semanales: 3-10 (dependiendo de actualizaciones)
✅ Auto-merge rate: 60-80% (patches/minor)
✅ Tiempo de merge: < 24h (con tests automáticos)
✅ PRs agrupados: Spring Boot, Testing, Database
```

**Señales de alerta:**
```
⚠️ PRs sin mergear > 20
⚠️ Tests fallando constantemente
⚠️ Conflictos de merge frecuentes
⚠️ Dependabot y Renovate duplicando PRs (revisar enabledManagers)
```

---

## Troubleshooting

### ❌ Problema: Renovate No Crea PRs

#### **Síntoma:**
```
Onboarding PR mergeado, pero no aparecen PRs nuevos
```

#### **Diagnóstico:**
```bash
# Ver logs de Renovate en GitHub
# Ve a: https://github.com/TU-USUARIO/tasksapi/pulls
# Busca comentarios de Renovate Bot explicando por qué no actualizó
```

#### **Soluciones:**

**1. Verificar que no haya PRs existentes:**
```bash
# Renovate respeta su prConcurrentLimit: 5
# Si hay 5 PRs abiertas, esperará a que se mergeen
```

**2. Revisar dependencias ignoradas:**
```json
// renovate.json
{
  "ignoreDeps": [], // ✅ Debe estar vacío
  "enabledManagers": ["gradle", "gradle-wrapper"] // ✅ Incluir Gradle
}
```

**3. Forzar actualización:**
```bash
# Crear issue en el repo con:
# Título: "Renovate: Check for updates"
# Renovate responderá al issue
```

### ❌ Problema: Dependabot No Se Activa

#### **Síntoma:**
```
Archivo dependabot.yml subido, pero no aparece en Security → Dependabot
```

#### **Diagnóstico:**
```bash
# Ver si hay errores de parsing
# GitHub → Settings → Code security and analysis → Dependabot alerts
```

#### **Soluciones:**

**1. Validar YAML:**
```bash
# Usar validador online
# https://yamllint.com/
# Copiar contenido de .github/dependabot.yml
```

**2. Verificar ruta:**
```bash
# CORRECTO: .github/dependabot.yml
# INCORRECTO: dependabot.yml (en raíz)
# INCORRECTO: .github/workflows/dependabot.yml

# Verificar en repo:
ls -la .github/
# Debe aparecer: dependabot.yml
```

**3. Repositorio privado:**
```bash
# Dependabot funciona en repos privados SOLO con GitHub Pro/Team
# En repos públicos funciona siempre
```

### ❌ Problema: Auto-merge No Funciona

#### **Síntoma:**
```
Renovate marca PR como "automerge: true" pero no se mergea
Tests pasan ✅, pero PR sigue abierta
```

#### **Diagnóstico:**
```bash
# Revisar configuración de GitHub
# Settings → General → Pull Requests
```

#### **Soluciones:**

**1. Habilitar auto-merge en repo:**
```bash
# GitHub → Settings → General
# Scroll a "Pull Requests"
# ✅ Allow auto-merge
```

**2. Verificar branch protection:**
```bash
# GitHub → Settings → Branches
# Si "main" tiene protección:
# ✅ Require status checks to pass: OK (Renovate esperará)
# ⚠️ Require pull request reviews: PROBLEMA (necesitas aprobar manualmente)
```

**Solución para repos con review obligatorio:**
```json
// renovate.json
{
  "packageRules": [
    {
      "matchUpdateTypes": ["minor", "patch"],
      "automerge": false, // Deshabilitar auto-merge
      "labels": ["auto-approve"] // Usar GitHub Actions para aprobar
    }
  ]
}
```

### ❌ Problema: Demasiados PRs Abiertos

#### **Síntoma:**
```
Renovate creó 20+ PRs el primer día
GitHub Actions consumiendo minutos
```

#### **Soluciones:**

**1. Reducir límite concurrente:**
```json
// renovate.json
{
  "prConcurrentLimit": 3, // Era 5
  "prHourlyLimit": 2 // Máximo 2 PRs por hora
}
```

**2. Agrupar más dependencias:**
```json
// renovate.json
{
  "packageRules": [
    {
      "groupName": "all non-major dependencies",
      "matchUpdateTypes": ["minor", "patch"],
      "groupSlug": "all-minor-patch"
    }
  ]
}
```

**3. Habilitar schedule estricto:**
```json
// renovate.json
{
  "schedule": ["after 10pm on sunday"], // Solo domingos
  "timezone": "Europe/Madrid"
}
```

### ❌ Problema: Tests Fallan en PRs de Renovate

#### **Síntoma:**
```
PR #5: Update Spring Boot to 3.4.0
Status: ❌ Tests failed
```

#### **Diagnóstico:**
```bash
# Clonar la branch del PR
git fetch origin pull/5/head:renovate-test
git checkout renovate-test

# Ejecutar tests localmente
./gradlew clean test

# Ver qué falló
./gradlew test --info
```

#### **Soluciones:**

**1. Actualización breaking (major version):**
```bash
# Normal que falle → Requiere cambios en código
# Cerrar el PR o modificar código manualmente
git checkout renovate-test
# ... hacer cambios necesarios
git commit -m "fix: adapt to Spring Boot 3.4.0"
git push origin renovate-test
```

**2. Incompatibilidad entre dependencias:**
```bash
# Ejemplo: Spring Boot 3.4 + Java 17 incompatible
# Revisar en renovate.json:
{
  "packageRules": [
    {
      "matchPackagePatterns": ["spring-boot"],
      "allowedVersions": "< 3.4" // Limitar hasta resolver
    }
  ]
}
```

**3. Tests flaky:**
```bash
# Rerun tests en GitHub
# En el PR, click "Re-run jobs" → "Re-run failed jobs"

# Si persiste, configurar retry:
# build.gradle.kts
test {
    retry {
        maxRetries.set(3)
        maxFailures.set(3)
    }
}
```

---

## 📚 Referencias

### Documentación Oficial

- **Renovate Bot:** https://docs.renovatebot.com/
- **Dependabot:** https://docs.github.com/en/code-security/dependabot
- **Gradle Versions Plugin:** https://github.com/ben-manes/gradle-versions-plugin

### Archivos de Configuración

- `renovate.json` - Configuración de Renovate Bot
- `.github/dependabot.yml` - Configuración de Dependabot
- `gradle/libs.versions.toml` - Version Catalog de Gradle

### Herramientas de Monitoreo

- **Renovate Dashboard:** https://app.renovatebot.com/dashboard
- **Dependabot Alerts:** https://github.com/USERNAME/REPO/security/dependabot
- **Gradle Versions Report:** `./gradlew dependencyUpdates` (manual check)

---

## ✅ Checklist de Configuración

### Pre-requisitos
- [ ] Proyecto subido a GitHub
- [ ] Tests automatizados funcionando (`./gradlew test`)
- [ ] GitHub Actions configurado (optional, pero recomendado)

### Renovate Bot
- [ ] Instalado desde https://github.com/apps/renovate
- [ ] Onboarding PR mergeado
- [ ] Auto-merge habilitado en repo settings
- [ ] Primeros PRs aparecieron (después de ~24h)

### Dependabot
- [ ] Archivo `.github/dependabot.yml` subido
- [ ] Visible en Security → Dependabot
- [ ] Primeros PRs aparecieron (después del primer lunes 3 AM)

### Verificación
- [ ] Al menos 1 PR de Renovate mergeado
- [ ] Al menos 1 PR de Dependabot mergeado
- [ ] Tests pasan en PRs automáticos
- [ ] Auto-merge funciona (si configurado)
- [ ] Notificaciones recibidas por email

---

**¿Necesitas ayuda?** Consulta la sección [Troubleshooting](#troubleshooting) o crea un issue en este repositorio.
