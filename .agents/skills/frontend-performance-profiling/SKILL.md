---
name: frontend-performance-profiling
description: Android layout hierarchy flattening, overdraw reduction, ViewBinding efficiency, memory leak avoidance, and frame budget optimization.
---

# Frontend Performance Profiling

This skill focuses on runtime view hierarchy depth, overdraw minimization, and memory footprint management for mobile Android apps.

## Core Directives

1. **Flatten View Hierarchies**: Replace deeply nested `LinearLayout` / `RelativeLayout` chains with `ConstraintLayout` to keep hierarchy depth $\le 5$ levels.
2. **Eliminate Overdraw**: Remove redundant window backgrounds and container backgrounds that cause the GPU to render multiple pixels per frame.
3. **ViewBinding Efficiency**: Clear view binding references in fragment lifecycles; instantiate bindings once in Activity `onCreate()`.
4. **Fast Frame Budget**: Ensure smooth 60fps / 120fps scrolling by avoiding heavy operations on the main thread during render.

## Verification Checklist

- [ ] View hierarchy depth $\le 5$ levels.
- [ ] No nested redundant background colors.
- [ ] Zero frame drops during list and dashboard scrolling.

