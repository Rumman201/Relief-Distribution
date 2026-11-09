---
name: frontend-design-system
description: Systematic Material Design 3 token architecture, color palettes, typography scales, shape tokens, and reusable component specifications.
---

# Frontend Design System

This skill defines the token hierarchy (primitive, semantic, component), themes, and reusable component libraries.

## Core Directives

1. **Three-Layer Token Architecture**:
   - Primitive tokens: raw color hex codes, font weights, dimensions.
   - Semantic tokens: `colorPrimary`, `colorOnPrimary`, `colorSurface`, `colorError`.
   - Component tokens: Card elevation, button corner radius, toolbar height.
2. **Standardized Spacing Scale**: 4dp grid (4dp, 8dp, 12dp, 16dp, 24dp, 32dp).
3. **Typography Scale**: Display, Title, Body, Label with explicit line-height and letter-spacing.
4. **Theme Consistency**: All UI elements reference theme attributes (`?attr/...`) or semantic color resources rather than hardcoded inline hex values.

## Verification Checklist

- [ ] Zero inline hex colors in refactored layouts.
- [ ] Spacing adheres to 4dp/8dp multiples.
- [ ] Consistent corner radii and elevations across cards and buttons.

