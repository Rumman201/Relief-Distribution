---
name: modular-codebase-engineering
description: Package structure design, encapsulation, feature-oriented modularity, clean boundaries, and dependency cycle prevention.
---

# Modular Codebase Engineering Skill

## 1. Purpose
Structure codebase packages into cohesive, loosely-coupled modules with clear public APIs, strict encapsulation, and zero circular dependencies.

## 2. Activation Criteria
Activate this skill when:
- Organizing new packages, subpackages, and domain boundaries.
- Resolving circular imports or tangled package dependencies.
- Defining public vs package-private visibility.

## 3. Package Blueprint for Relief Distribution System
```
com.example.reliefdistribution/
├── data/
│   ├── model/         # User, Resource, ReliefPoint, etc.
│   └── repository/    # AuthRepository, UserRepository, LocationRepository
├── ui/
│   ├── auth/          # LoginActivity, SignUpActivity, StartActivity, AuthViewModel
│   ├── dashboard/     # DistributorDashboard, SurvivorDashboard, DashboardViewModel
│   ├── map/           # MapActivity, MapViewModel
│   ├── profile/       # ProfileActivity
│   └── info/          # InformationsActivity
└── util/              # ValidationUtils, Constants
```

## 4. Engineering Standards
- Keep package visibility as tight as possible (package-private by default where feasible).
- UI packages must never depend directly on remote data sources (Firebase); only on ViewModels and Repositories.
- Model classes must remain clean POJOs/JavaBeans without framework or UI dependencies.

