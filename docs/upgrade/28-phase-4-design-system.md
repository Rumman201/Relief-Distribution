# Phase 4 — Material Design 3 Design System Architecture

**Project**: Relief Distribution System (`com.example.reliefdistribution`)  
**Phase**: 4 of 10  
**Discipline**: Frontend Design System Engineering  
**Status**: Completed  
**Date**: 2026-11-09  

---

## 1. Design Token System

To eliminate fragmented hardcoded styling across layouts, all spatial dimensions, color mappings, and component styles are codified into structured resource dictionaries:

- `app/src/main/res/values/dimens.xml`
- `app/src/main/res/values/colors.xml`
- `app/src/main/res/values/themes.xml`

### 1.1 Spacing Scale (4dp Grid)

```xml
<dimen name="spacing_xxs">2dp</dimen>
<dimen name="spacing_xs">4dp</dimen>
<dimen name="spacing_s">8dp</dimen>
<dimen name="spacing_m">16dp</dimen>
<dimen name="spacing_l">24dp</dimen>
<dimen name="spacing_xl">32dp</dimen>
<dimen name="spacing_xxl">48dp</dimen>
```

### 1.2 Component Geometric Tokens

```xml
<dimen name="min_touch_target">48dp</dimen>
<dimen name="card_corner_radius">20dp</dimen>
<dimen name="card_corner_radius_small">12dp</dimen>
<dimen name="button_corner_radius">14dp</dimen>
<dimen name="badge_corner_radius">8dp</dimen>
<dimen name="button_height">54dp</dimen>
<dimen name="input_field_height">56dp</dimen>
<dimen name="bottom_nav_height">72dp</dimen>
```

---

## 2. Reusable Component Styles

All primary UI widgets inherit from standardized Material 3 styles defined in `themes.xml`:

### 2.1 Elevated Content Cards (`Widget.Relief.CardView`)

```xml
<style name="Widget.Relief.CardView" parent="Widget.Material3.CardView.Elevated">
    <item name="cardCornerRadius">@dimen/card_corner_radius</item>
    <item name="cardElevation">4dp</item>
    <item name="cardBackgroundColor">@color/card_surface</item>
    <item name="strokeColor">@color/card_stroke</item>
    <item name="strokeWidth">1dp</item>
</style>
```

### 2.2 Clickable Action Cards (`Widget.Relief.CardView.Clickable`)

```xml
<style name="Widget.Relief.CardView.Clickable" parent="Widget.Relief.CardView">
    <item name="android:clickable">true</item>
    <item name="android:focusable">true</item>
    <item name="android:foreground">?attr/selectableItemBackground</item>
</style>
```

### 2.3 Emergency Hero Cards (`Widget.Relief.CardView.Emergency`)

```xml
<style name="Widget.Relief.CardView.Emergency" parent="Widget.Relief.CardView.Clickable">
    <item name="cardBackgroundColor">#FFF8F8</item>
    <item name="strokeColor">@color/emergency_red</item>
    <item name="strokeWidth">1.5dp</item>
</style>
```

### 2.4 Buttons (`Primary`, `Emergency`, `Outlined`)

```xml
<style name="Widget.Relief.Button.Primary" parent="Widget.Material3.Button">
    <item name="cornerRadius">@dimen/button_corner_radius</item>
    <item name="backgroundTint">@color/primary</item>
    <item name="android:textColor">@color/on_primary</item>
    <item name="android:textStyle">bold</item>
    <item name="android:minHeight">@dimen/button_height</item>
</style>

<style name="Widget.Relief.Button.Emergency" parent="Widget.Material3.Button">
    <item name="cornerRadius">@dimen/button_corner_radius</item>
    <item name="backgroundTint">@color/emergency_red</item>
    <item name="android:textColor">@color/on_emergency</item>
    <item name="android:textStyle">bold</item>
    <item name="android:minHeight">@dimen/button_height</item>
</style>

<style name="Widget.Relief.Button.Outlined" parent="Widget.Material3.Button.OutlinedButton">
    <item name="cornerRadius">@dimen/button_corner_radius</item>
    <item name="strokeColor">@color/primary</item>
    <item name="android:textColor">@color/primary</item>
    <item name="android:textStyle">bold</item>
    <item name="android:minHeight">@dimen/button_height</item>
</style>
```

### 2.5 Text Input Layouts (`Widget.Relief.TextInputLayout`)

```xml
<style name="Widget.Relief.TextInputLayout" parent="Widget.MaterialComponents.TextInputLayout.OutlinedBox">
    <item name="boxCornerRadiusTopStart">@dimen/card_corner_radius_small</item>
    <item name="boxCornerRadiusTopEnd">@dimen/card_corner_radius_small</item>
    <item name="boxCornerRadiusBottomStart">@dimen/card_corner_radius_small</item>
    <item name="boxCornerRadiusBottomEnd">@dimen/card_corner_radius_small</item>
    <item name="boxStrokeColor">@color/primary</item>
    <item name="hintTextColor">@color/primary</item>
</style>
```

---

## 3. Drawable Tokens

- `circle_avatar.xml`: Oval container with `@color/primary_container` fill and `@color/primary` border.
- `badge_online.xml`: Pill shape with `@color/status_online_container` and emerald border.
- `badge_offline.xml`: Pill shape with `@color/status_offline_container` and amber border.
- `badge_role.xml`: Pill shape with `@color/primary_container` and teal border.
- `bottom_nav_item_bg.xml`: Oval ripple drawable for tactile feedback on bottom navigation icons.

