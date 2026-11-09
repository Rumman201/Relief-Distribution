---
name: visual-motion-regression
description: Visual consistency, Material 3 token conformity, layout density scaling, animation smoothness, and non-blocking motion QA.
---

# Visual & Motion Regression QA Skill

## 1. Purpose
Validate visual presentation fidelity, Material 3 design token adherence, multi-density responsive scaling, and animation lifecycle stability across all screens and components of the Relief Distribution System.

## 2. Activation Criteria
Activate this skill when:
- Reviewing visual rendering across screen densities (mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi) and form factors.
- Auditing hardware-accelerated animations (`fade_in.xml`, `slide_in_bottom.xml`, `scale_up.xml`).
- Verifying `prefers-reduced-motion` or transition animator listener cleanup.
- Detecting unwanted layout shifts, clipped text, or overlapping UI widgets.

## 3. Project Context
- **Design Tokens**: Material Design 3 semantic color palette (`colorPrimary`, `colorSecondary`, `colorSurface`, `colorError`).
- **Motion Architecture**: Hardware-accelerated interpolators with short durations (200ms–350ms) to ensure UI responsiveness under urgent field conditions.

## 4. Engineering Standards
- **Zero Layout Clipping**: All cards, badges, and headers must scale gracefully without truncating vital text in low-density or large-font modes.
- **Scrollview Enclosure**: Every form screen must be enclosed in `ScrollView` or `NestedScrollView` with `fillViewport="true"`.
- **Animator Cleanup**: All ViewPropertyAnimators and ObjectAnimators must cancel and release listeners on activity `onStop()` / `onDestroy()`.

## 5. Step-by-Step Execution Procedure
1. **Layout Hierarchy Inspection**: Verify XML nesting depth <= 4 levels to prevent layout passes exceeding 16ms frame budget.
2. **Density Scaling Audit**: Test layouts across 320dp, 360dp, 400dp, and tablet viewports.
3. **Motion Verification**: Confirm animations trigger smoothly without dropping frames or blocking touch interaction.
4. **Baseline Attestation**: Record screenshot baselines and visual diff reports.

## 6. Decision Tree for Ambiguous Cases
- *Should animations run if device is in Battery Saver mode?*
  -> No. Hardware animations should be skipped or duration reduced to 0ms when low-power mode is active.

