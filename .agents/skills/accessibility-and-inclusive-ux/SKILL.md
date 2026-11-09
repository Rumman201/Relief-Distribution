---
name: accessibility-and-inclusive-ux
description: WCAG 2.2 AA compliance, touch target sizing, contentDescription tagging, keyboard navigation, and high contrast legibility.
---

# Accessibility and Inclusive UX

This skill enforces inclusive design standards, screen reader compatibility, and physical touch ergonomics.

## Core Directives

1. **48dp Touch Targets**: Every clickable view, icon, or button must have a minimum touch area of $48 \times 48\,\text{dp}$.
2. **Descriptive Content Descriptions**: All `ImageView` and non-text interactive elements must define a meaningful `android:contentDescription`. Purely decorative images use `@null`.
3. **High Contrast Ratios**: Text against backgrounds must satisfy WCAG AA minimums ($\ge 4.5:1$ for body, $\ge 3:1$ for large headings).
4. **Focus & Keyboard Navigation**: Ensure sequential tab traversal order is logical and visible.

## Verification Checklist

- [ ] All interactive touch targets $\ge 48\,\text{dp}$.
- [ ] Zero missing `contentDescription` attributes on interactive views.
- [ ] Contrast meets or exceeds 4.5:1.

