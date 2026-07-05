# Relief Distribution App

A comprehensive Android application designed to facilitate disaster relief operations. The app bridges the gap between relief distributors and survivors, ensuring efficient, organized, and transparent distribution of resources during crises.

## Features

* **Role-Based Access:** Dedicated features and dashboards tailored specifically for **Survivors** and **Distributors**.
* **Authentication:** Secure user registration, login, and profile management using Firebase Authentication.
* **Location & Mapping:** Integrated with Google Maps and Location Services to pinpoint survivor locations and coordinate relief efforts accurately.
* **Real-time Data:** Utilizes Firebase Realtime Database to store and sync distribution data, requests, and updates seamlessly.
* **Communication:** Includes phone call intent permissions to allow direct communication between distributors and survivors.

## Tech Stack

* **Platform:** Android (Java / Kotlin)
* **Backend:** Firebase (Realtime Database, Authentication, Analytics, BOM)
* **Maps/Location:** Google Maps API, Google Play Services Location
* **UI Components:** AndroidX, Material Design, ConstraintLayout

## Requirements

* **Minimum SDK:** 24 (Android 7.0 Nougat)
* **Target SDK:** 34 (Android 14)
* **Java Compatibility:** JavaVersion 11

## Setup & Installation

1. **Clone the repository:**
   ```bash
   git clone <your-repository-url>
   ```
2. **Open the project** in Android Studio.
3. **Firebase Setup:** 
   * Create a project in the [Firebase Console](https://console.firebase.google.com/).
   * Download the `google-services.json` file and place it in the `app/` directory.
   * Enable Authentication and Realtime Database in your Firebase project.
4. **Google Maps API Setup:**
   * Generate a Google Maps API Key from the [Google Cloud Console](https://console.cloud.google.com/).
   * Add the API key to your `res/values/strings.xml` file:
     ```xml
     <string name="my_map_api_key">YOUR_API_KEY_HERE</string>
     ```
5. **Sync and Build:** Sync the project with Gradle files and build the app.
6. **Run:** Deploy the app on an Android emulator or a physical device.

## Project Structure

* `StartActivity` / `WelcomeActivity` / `MainActivity`: Onboarding and initial navigation.
* `LoginActivity` / `SignUpActivity`: User authentication flows.
* `DistributorDashboard`: Core interface for relief distributors.
* `SurvivorDashboard`: Core interface for users requesting/receiving relief.
* `GoogleMap`: Map views for routing and location tracking.
* `Informations` / `Profile`: User data and details management.
