---
name: accessibility-verification
description: WCAG 2.2 AA compliance, Android TalkBack accessibility, 48dp touch targets, color contrast, and inclusive UX auditing.
---

# Accessibility & Inclusive UX Verification Skill

## 1. Purpose
Evaluate, enforce, and verify comprehensive accessibility standards (WCAG 2.2 Level AA and Android Accessibility Guidelines) across the Relief Distribution application to guarantee barrier-free access for all disaster survivors and relief personnel.

## 2. Activation Criteria
Activate this skill when:
- Auditing user interface components for TalkBack screen reader compatibility.
- Verifying touch target sizing (minimum 48x48dp bounds) across all clickable views.
- Validating color contrast ratios (minimum 4.5:1 for normal text, 3:1 for large text and UI controls).
- Checking focus order, keyboard navigation, and accessibility announcements.

## 3. Project Context
- **Target Platform**: Android 7.0 (API 24) to Android 14 (API 34).
- **Core Accessibility Elements**:
  - `contentDescription` on all `ImageView`, `ImageButton`, and interactive icons.
  - `importantForAccessibility="no"` on purely decorative elements.
  - Floating labels and accessible error announcements on `TextInputLayout`.
  - Contrast ratios verified across emergency status chips (`CRITICAL`, `HIGH`, `MEDIUM`, `LOW`).

## 4. Engineering Standards
- **Non-Text Content**: Every interactive image element must possess a meaningful, localized `contentDescription`.
- **Target Size**: Minimum touch target dimension of 48dp horizontally and vertically (`minHeight="48dp"`, `minWidth="48dp"`).
- **No Color-Only Information**: Status indicators (e.g. priority badges) must use both text labels and color hues.

## 5. Step-by-Step Execution Procedure
1. **Automated Static Audit**: Inspect layout XMLs for missing `contentDescription` or sub-48dp touch targets.
2. **Contrast Analysis**: Calculate contrast ratios between foreground text and surface backgrounds.
3. **Screen Reader Traversal**: Verify logical focus traversal order from top-left to bottom-right.
4. **Accessible Feedback**: Confirm form submission errors and loading states announce status to accessibility services.

## 6. Decision Tree for Ambiguous Cases
- *Is a 40dp button acceptable if surrounded by 8dp margin?*
  -> No. The touchable target area must physically measure at least 48x48dp (using padding or `TouchDelegate`).

