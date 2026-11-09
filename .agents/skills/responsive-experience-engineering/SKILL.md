---
name: responsive-experience-engineering
description: Multi-screen responsiveness, keyboard adjustment, ScrollView containment, density scaling, and tablet layout adaptation.
---

# Responsive Experience Engineering

This skill ensures interfaces adapt fluidly across phone screen sizes, aspect ratios, orientations, and soft keyboard states.

## Core Directives

1. **ScrollView & NestedScrollView Containment**: All scrollable forms and dashboard content must be enclosed in scrolling containers with `fillViewport="true"`.
2. **Soft Keyboard Resilience**: Windows must declare `windowSoftInputMode="adjustResize"` to ensure action buttons and input fields remain accessible during typing.
3. **Density Independence**: All layout dimensions in `dp`, all text sizes in `sp`.
4. **Zero Overflow**: Verify layout reflow across small (320dp width) to large (600dp+ tablet) viewports.

## Verification Checklist

- [ ] All forms scrollable with soft keyboard visible.
- [ ] No hardcoded pixel (`px`) values.
- [ ] No clipped text or unreadable button labels.

