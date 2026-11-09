---
name: advanced-motion-engineering
description: Android view animations, choreography, ripple feedback, smooth transitions, and performance-aware microinteractions.
---

# Advanced Motion Engineering

This skill governs animation choreography, interactive state transitions, card elevations, and tactile feedback for mobile applications.

## Core Directives

1. **Purposeful Feedback**: Every motion must communicate physical state (e.g. card press depth, button ripple, connection badge slide-in).
2. **Performance Budget**: Keep animation runtimes concise (150ms–300ms) with decel/standard interpolators (`@android:interpolator/decelerate_cubic`).
3. **Hardware Acceleration**: Animate composite-friendly properties (alpha, translation, scale, elevation) rather than forcing layout redraws.
4. **Accessible Transitions**: Honor accessibility settings and avoid disorienting motion.

## Verification Checklist

- [ ] All animations use standard Android interpolators.
- [ ] No layout thrashing or dropped frames.
- [ ] Visual ripples and touch feedback present on interactive surfaces.

