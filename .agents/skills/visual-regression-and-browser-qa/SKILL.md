---
name: visual-regression-and-browser-qa
description: Multi-device layout verification, visual inspection, regression testing, lint checks, and automated UI validation.
---

# Visual Regression and Mobile Layout QA

This skill guides visual inspection, screenshot baselines, resource linting, and multi-screen regression protection.

## Core Directives

1. **Systematic Screen Audit**: Verify every layout XML across portrait and landscape, phone and tablet dimensions.
2. **Resource Linting**: Run Android Lint to detect missing translations, invalid resource references, and layout inflation hazards.
3. **Touch and Visual Contrast Audits**: Validate that text elements meet contrast requirements and touch targets meet size thresholds.
4. **Activity Transition Verification**: Verify that navigation flows move smoothly between Activities without layout flashing or stutter.

## Verification Checklist

- [ ] Every screen verified against visual criteria.
- [ ] `./gradlew.bat compileDebugSources` and `./gradlew.bat testDebugUnitTest` pass with 0 errors.
- [ ] No layout crashes on screen orientation change.

