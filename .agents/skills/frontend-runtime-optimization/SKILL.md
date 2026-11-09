---
name: frontend-runtime-optimization
description: Android layout hierarchy flattening, RecyclerView recycling efficiency, ViewBinding memory optimization, overdraw reduction, and smooth 60/120 FPS interaction design.
---

# Frontend Runtime Optimization Skill

## 1. Purpose
Optimize Android UI rendering performance, eliminating frame jank, reducing view hierarchy depth, maximizing RecyclerView item recycling efficiency, and maintaining 60/120 FPS display fluidity without degrading accessibility or design polish.

## 2. Activation Criteria
Activate this skill when:
- Profiling screen rendering latency, layout measurement/layout passes, or UI frame rates.
- Tuning RecyclerView adapters (`ManageRequestsActivity`, `InventoryManagementActivity`, `NotificationsActivity`, `MyReliefRequestsActivity`).
- Eliminating layout overdraw and view hierarchy nesting in XML layouts.
- Optimizing vector drawables, images, and visual components for memory efficiency.

## 3. Project Context
- **UI Architecture**: ViewBinding across all Activities; Material Design 3 tokens.
- **Key Lists**:
  - `ManageRequestsActivity`: Multi-tab request triage with filtering.
  - `InventoryManagementActivity`: Real-time warehouse inventory tally.
  - `NotificationsActivity`: Disaster alert and notification stream.
  - `MyReliefRequestsActivity`: Survivor submitted requests feed.
- **Frame Budget**: 16.6ms per frame (60 FPS) / 8.3ms per frame (120 FPS high refresh).

## 4. Engineering Standards
- **RecyclerView Efficiency**: Always configure `setHasFixedSize(true)` when item bounds are static. Reuse ViewHolders efficiently and avoid allocating objects inside `onBindViewHolder`.
- **ViewBinding Optimization**: Never create redundant view lookup loops; reuse binding references.
- **Preserve Accessibility**: Optimization must never remove `contentDescription`, touch targets (>= 48dp), or keyboard focus navigation.

## 5. Step-by-Step Execution Procedure
1. **Layout Hierarchy Audit**:
   - Flatten nested layouts (replace nested LinearLayouts with ConstraintLayout or single-level containers).
2. **Adapter Recycling Review**:
   - Verify ViewHolder pattern is strictly adhered to with zero allocations in `onBindViewHolder`.
3. **Hardware Acceleration & Overdraw**:
   - Eliminate redundant window/container background colors to prevent multi-pass overdraw.

## 6. Decision Tree for Ambiguous Cases
- *Should we remove animations to speed up navigation?*
  -> No. Hardware-accelerated property animations (opacity, translation) should be retained and optimized, not stripped away.
