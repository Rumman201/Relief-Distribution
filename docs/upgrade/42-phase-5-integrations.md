# Phase 5 — Third-Party Integrations & Interoperability

**Project**: Relief Distribution System (`com.example.reliefdistribution`)  
**Phase**: 5 of 10  
**Date**: 2026-11-09  
**Status**: ACTIVE  

---

## 1. Third-Party Integration Matrix

The Relief Distribution application integrates with five external platforms and operating system services:

| Provider / Service | Protocol / SDK | Purpose | Security & Credential Boundary | Status |
| :--- | :--- | :--- | :--- | :--- |
| **Google Maps SDK** | `com.google.android.gms:play-services-maps` | Interactive field map, relief depot markers, distributor live tracking | API Key managed via `local.properties` (`MAPS_API_KEY`) and AndroidManifest manifest placeholder | **Integrated & Verified** |
| **Google Play Services Location** | `com.google.android.gms:play-services-location` | FusedLocationProviderClient GPS coordinate acquisition | Scoped runtime permissions (`ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`) | **Integrated & Verified** |
| **External Turn-by-Turn Navigation** | Android System Intent (`Intent.ACTION_VIEW`, `geo:lat,lng?q=...`) | Google Maps / Waze turn-by-turn vehicle routing to survivor coordinates | No credentials required; standard sanitized URI scheme | **Integrated & Verified** |
| **National Emergency Hotline (999)** | Android System Telecom Intent (`Intent.ACTION_DIAL`, `tel:999`) | Immediate voice telephone access to Bangladesh National Emergency Service | Invokes system dialer; zero privileged `CALL_PHONE` permissions required | **Integrated & Verified** |
| **Firebase Cloud Backend** | Firebase SDK (Auth, Realtime Database, Analytics) | User authentication, synchronized database, event notifications | Authenticated sessions, Google Services JSON configuration | **Integrated & Verified** |

---

## 2. Navigation Intent Implementation & Fallback

In [`ManageRequestsActivity.java`](file:///H:/Projects/Relief-Distribution/app/src/main/java/com/example/reliefdistribution/ManageRequestsActivity.java), distributors can launch external turn-by-turn navigation directly to the survivor's coordinates:

```java
Uri gmmIntentUri = Uri.parse("geo:" + request.getLatitude() + "," + request.getLongitude() +
        "?q=" + request.getLatitude() + "," + request.getLongitude() +
        "(" + Uri.encode(request.getItemName() != null ? request.getItemName() : "Relief Aid Target") + ")");
Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
mapIntent.setPackage("com.google.android.apps.maps");

if (mapIntent.resolveActivity(itemView.getContext().getPackageManager()) != null) {
    itemView.getContext().startActivity(mapIntent);
} else {
    // Graceful fallback to internal MapActivity
    Intent intent = new Intent(itemView.getContext(), MapActivity.class);
    intent.putExtra("LATITUDE", request.getLatitude());
    intent.putExtra("LONGITUDE", request.getLongitude());
    itemView.getContext().startActivity(intent);
}
```

This guarantees that distributors with Google Maps installed get voice turn-by-turn navigation across rural roads, while devices without Google Play Services can still view the survivor's coordinates on the internal `MapActivity`.

---

## 3. Emergency Telephony Safety Invariant

The application adheres strictly to the safety standard defined in `AGENTS.md`:
- **Forbids**: `Intent.ACTION_CALL` requiring dangerous runtime permission `android.permission.CALL_PHONE`.
- **Enforces**: `Intent.ACTION_DIAL` (`Uri.parse("tel:999")`), pre-populating the phone number on the system dialer pad while giving the human user final physical confirmation before initiating the call.

---

## 4. API Key Hygiene & Secrets Management

The Google Maps API key is decoupled from source code:
- `local.properties`: `MAPS_API_KEY=AIzaSy...` (ignored by `.gitignore`)
- `app/build.gradle.kts`:
  ```kotlin
  val mapsApiKey: String = (localProperties.getProperty("MAPS_API_KEY") ?: System.getenv("MAPS_API_KEY") ?: "").trim()
  defaultConfig {
      manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
  }
  ```
- `local.properties.example` committed as a template for developers and CI/CD pipelines.
