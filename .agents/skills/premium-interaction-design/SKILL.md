---
name: premium-interaction-design
description: Tactical user interactions, feedback states, dialogs, bottom sheets, form validation feedback, and tactile state changes.
---

# Premium Interaction Design

This skill governs interactive state modeling, tactile feedback, floating labels, validation states, and emergency response ergonomics.

## Core Directives

1. **Floating Label Text Inputs**: Use `TextInputLayout` with `TextInputEditText` for clear field labels, helper text, and real-time validation error messaging.
2. **Clear Feedback States**: Loading spinners on buttons during async requests, instant confirmation toasts/snackbars, and disabled states during execution.
3. **Emergency Hotline Ergonomics**: Immediate confirmation with `ACTION_DIAL` to prevent accidental 999 calls while maintaining one-tap access.
4. **Bottom Sheet & Modal Clarity**: Clean scrims, draggable bottom sheets, and clear primary/dismiss actions.

## Verification Checklist

- [ ] Forms provide instant inline validation feedback.
- [ ] Buttons display loading indicators or disable during network calls.
- [ ] Emergency calls trigger system dialer safely.

