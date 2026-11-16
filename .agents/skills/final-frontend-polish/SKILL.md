---
name: final-frontend-polish
description: Design-system consistency, route-by-route refinement, typography, responsive design, navigation, motion, accessibility, and visual QA across all Android Activities.
---

# Final Frontend Polish Skill

## 1. Purpose & Scope
Guides route-by-route inspection and visual, responsive, and interaction polish across all 12 Activities of the Relief Distribution Android application.

## 2. Standards & Constraints
- **Material Design 3**: Maintain color consistency (`#1A56DB` primary, `#F8FAFC` background, `#0E1726` surface).
- **Responsive Enclosure**: Universal `ScrollView` or `NestedScrollView` enclosure with `android:fillViewport="true"` and `windowSoftInputMode="adjustResize"`.
- **Touch & Accessibility**: Minimum 48x48dp touch targets, clear `contentDescription` on interactive elements, contrast ratio >= 4.5:1.
- **Microinteractions**: Purposeful ripple effects, elevation changes on cards, non-blocking smooth transitions.

## 3. Step-by-Step Procedure
1. **Route Inventory**: Inspect all 12 XML layouts in `app/src/main/res/layout/`.
2. **Layout Consistency**: Ensure margin/padding tokens adhere to 8dp/16dp/24dp grid.
3. **Typography & Hierarchy**: Ensure headline, body, and caption text use consistent sizes and contrast-compliant colors.
4. **State Handling**: Provide clean loading spinners, empty states, and descriptive error dialogs.
5. **Multi-Density Testing**: Verify rendering under mdpi, hdpi, xhdpi, xxhdpi, and diverse screen widths (320dp to 840dp+).
