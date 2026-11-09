---
name: frontend-experience-engineering
description: Android UI/UX design engineering, Material Design 3 components, ViewBinding, responsive layouts across screen sizes, touch feedback, and accessibility.
---

# Frontend Experience Engineering Skill

## 1. Purpose
Transform the Android UI from rudimentary, brittle XML layouts into a cohesive, responsive, polished, accessible, and delight-inducing mobile experience adhering to Material Design 3 and Android UI excellence standards.

## 2. Activation Criteria
Activate this skill when:
- Designing, editing, or refactoring Android layout XML files (`res/layout/**`).
- Implementing or standardizing styles, themes, typography, and color tokens (`res/values/**`).
- Eliminating fragile `findViewById` calls in favor of type-safe `ViewBinding`.
- Improving touch feedback, button states, transitions, accessibility contrast, and soft keyboard ergonomics.

## 3. Project Context
- **Design Foundations**: Material Design 3 (`Theme.Material3.DayNight.NoActionBar`).
- **Issues Identified in Reconnaissance**:
  - Overlapping and non-scrollable forms: `activity_sign_up.xml` overflows without a `ScrollView`, making inputs unreachable when the soft keyboard is open.
  - Fragile fixed dimensions: hardcoded `layout_height="642dp"`, `layout_marginTop="300dp"`, causing clipping on smaller screens or landscape mode.
  - Theme contrast defects: `themes.xml` has `colorPrimary` and `colorOnPrimary` both set to `@color/black`, creating invisible black-on-black text on buttons.
  - Duplicated bottom navigation bars across 4 separate XML files instead of a shared include or `BottomNavigationView`.
  - Unsafe `findViewById` calls risking NullPointerExceptions upon layout changes.

## 4. Specialist Engineering Standards
- **ViewBinding Everywhere**: Never use `findViewById`. Always enable ViewBinding in `build.gradle.kts` (`buildFeatures { viewBinding = true }`).
- **Responsive Layout Principles**:
  - Always wrap scrollable content in `ScrollView` or `NestedScrollView` with `android:fillViewport="true"`.
  - Use `match_parent`, `0dp` with ConstraintLayout / LinearLayout weights, and flexible constraints instead of fixed large heights (`600dp+`).
  - Use `wrap_content` for text views and buttons; never truncate labels.
- **Material 3 Token Architecture**:
  - Semantic color naming: `md_theme_primary`, `md_theme_onPrimary`, `md_theme_surface`, `md_theme_error`.
  - Scalable font sizing: Always use `sp` for typography; use `dp` for dimensions and spacing.
  - Touch target accessibility: Every interactive element must be at least 48dp x 48dp.
- **Visual Polish & Feedback**:
  - All clickable views must have ripple effects (`?attr/selectableItemBackground`).
  - Inputs must provide hint animations and clear error states via `TextInputLayout` and `TextInputEditText`.

## 5. Step-by-Step Execution Procedure
1. **Layout Audit**: Review layout hierarchy in `res/layout/`. Identify deeply nested linear layouts and convert to `ConstraintLayout` where performance benefits.
2. **ViewBinding Migration**: Replace manual view binding fields with auto-generated Binding classes (`ActivitySignUpBinding.inflate(layoutInflater)`).
3. **Form Refactoring**: Wrap overflowing forms into `ScrollView`, add `android:windowSoftInputMode="adjustResize"` to the activity manifest.
4. **Theme Harmonization**: Correct color palette tokens in `res/values/colors.xml` and `res/values/themes.xml` (ensuring WCAG AA contrast ratio of at least 4.5:1 for normal text).
5. **Component Reusability**: Extract duplicated bottom navigation or card layouts into `<include layout="@layout/..." />` or a dedicated custom View.

## 6. Decision Tree for Ambiguous Cases
- *ConstraintLayout vs LinearLayout?*
  -> Use `LinearLayout` for simple 1D linear stacks (e.g. form fields inside a ScrollView). Use `ConstraintLayout` for complex flat 2D dashboards or overlapping relative views.
- *Native BottomNavigationView vs Custom CardView bottom bar?*
  -> Standardize on `BottomNavigationView` or a reusable included layout to avoid duplicating 4 identical XML blocks across activities.

## 7. Tool Instructions
- Inspect Android layouts and lint warnings via `./gradlew.bat lint`.
- Build and verify APK layout parsing with `./gradlew.bat assembleDebug`.

## 8. Safety Constraints
- Never delete View IDs (`android:id="@+id/..."`) that are referenced in active Java/Kotlin code without simultaneously updating code references.
- Always maintain RTL support (`supportsRtl="true"` in Manifest, use `marginStart`/`marginEnd` instead of `marginLeft`/`marginRight`).

## 9. Verification Steps
- Verify layout rendering across diverse screen densities (hdpi, xhdpi, xxhdpi).
- Verify that soft keyboard opening does not obscure active input fields.
- Check text readability under both Day (Light) and Night (Dark) themes.

## 10. Expected Output & Evidence Format
- Modernized layout XMLs with zero lint warnings.
- Clean ViewBinding implementations in Activity files.
- Before/after layout behavior documentation.

