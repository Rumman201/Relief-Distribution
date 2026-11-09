# Phase 8 — Infrastructure as Code (IaC) & Declarative Cloud Configuration

## 1. Overview & Declarative Artifacts

All cloud infrastructure configurations for the Relief Distribution System are managed strictly via version-controlled declarative files, eliminating manual configuration drift.

### Declarative Files:
1. **`firebase.json`**:
   Binds database rules and deployment targets to project assets.
   ```json
   {
     "database": {
       "rules": "database.rules.json"
     }
   }
   ```
2. **`.firebaserc`**:
   Defines project aliases ensuring absolute environment separation:
   ```json
   {
     "projects": {
       "default": "relief-distribution-2w5",
       "staging": "relief-distribution-staging",
       "production": "relief-distribution-prod"
     }
   }
   ```
3. **`database.rules.json`**:
   Defines server-enforced role-based access control, write validation, and indexing for optimal query performance.

---

## 2. Infrastructure Drift Prevention

- **Git-Centric Deployment**: Security rules and index specifications must be committed to the `main` branch before deploying to production.
- **Rule Verification Gate**: Rule changes must pass automated syntax validation and simulated authorization tests before promotion.
- **Console Lockout**: Manual editing of rules in the Firebase Web Console is disabled for production instances.

