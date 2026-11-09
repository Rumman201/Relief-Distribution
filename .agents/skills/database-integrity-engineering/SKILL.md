---
name: database-integrity-engineering
description: Inspect schemas, enforce constraints, author security rules, guarantee transaction safety, and optimize queries for Firebase RTDB and mobile databases.
---

# Database Integrity Engineering

This skill governs schema design, constraint verification, declarative security rules, transaction boundaries, and query safety for Firebase Realtime Database and local caches.

## Core Directives

1. **Declarative Security Rules**: No database collection or path may be left unprotected. All paths must define `.read`, `.write`, and `.validate` rules.
2. **Atomic Transitions**: Multi-field mutations or counter increments must use atomic transactions (`runTransaction`) or multi-path updates to eliminate race conditions.
3. **Index Optimization**: Paths queried by child keys (e.g., status, role, timestamp) must declare `.indexOn` rules.
4. **Offline Resiliency**: In disaster conditions, disk persistence (`setPersistenceEnabled`) must be configured on application bootstrap to survive offline states.
5. **Non-Destructive Evolutions**: Changes to JSON schemas or SQLite tables must never drop user data without verified backwards compatibility.

## Verification Checklist

- [ ] Firebase `database.rules.json` contains authentication and schema validations.
- [ ] Offline persistence verified on cold start.
- [ ] Concurrency and transactional mutations tested against race hazards.

