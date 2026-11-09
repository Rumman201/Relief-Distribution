---
name: type-safety-and-contracts
description: Compile-time type safety enforcement, ViewBinding null safety, contract validation, and eliminating unchecked casts.
---

# Type Safety & Contracts Skill

## 1. Purpose
Eliminate runtime `NullPointerException` and `ClassCastException` hazards by enforcing compile-time type safety, Jetpack `ViewBinding`, generic state wrappers (`Resource<T>`), and strict contract validation.

## 2. Activation Criteria
Activate this skill when:
- Replacing unsafe `findViewById` calls with type-safe `ViewBinding`.
- Creating asynchronous result contracts (`Callback<T>`, `Resource<T>`).
- Validating external boundary data (Firebase DataSnapshot parsing).

## 3. Engineering Standards
- **ViewBinding Mandate**: Every Activity must hold a typed binding property (`ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater())`) and call `setContentView(binding.getRoot())`. Never cast views manually.
- **Null-Safe Parsing**: Guard all Firebase `DataSnapshot.getValue(Class)` calls with explicit null checks.
- **Typed Result Wrappers**: Standardize all asynchronous outcomes with `Resource.success(data)`, `Resource.error(message)`, and `Resource.loading()`.

