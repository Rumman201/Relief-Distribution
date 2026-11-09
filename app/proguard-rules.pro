# ==============================================================================
# Relief Distribution System — Production ProGuard / R8 Hardening Rules
# ==============================================================================

# --- Line Number and Source Attribution for Crash Diagnostics ---
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# --- Firebase Realtime Database and Auth Data Models ---
# Preserve all data model classes, constructors, and bean accessors required
# for Firebase reflection-based deserialization (snapshot.getValue(Class)).
-keep class com.example.reliefdistribution.data.model.** { *; }
-keep class com.example.reliefdistribution.User { *; }
-keep class com.example.reliefdistribution.ReliefRequest { *; }
-keep class com.example.reliefdistribution.InventoryItem { *; }
-keep class com.example.reliefdistribution.Notification { *; }

-keepclassmembers class * {
    @com.google.firebase.database.IgnoreExtraProperties <fields>;
    @com.google.firebase.database.Exclude <methods>;
    @com.google.firebase.database.PropertyName <fields>;
    @com.google.firebase.database.PropertyName <methods>;
}

# --- Android Jetpack Architecture & ViewModels ---
-keepclassmembers public class * extends androidx.lifecycle.ViewModel {
    public <init>(...);
}
-keep class * extends androidx.lifecycle.ViewModel

# --- ViewBinding Reflection Protection ---
-keep class * implements androidx.viewbinding.ViewBinding {
    public static *** inflate(...);
    public static *** bind(...);
}

# --- Google Play Services & Maps ---
-keep class com.google.android.gms.maps.** { *; }
-keep interface com.google.android.gms.maps.** { *; }
-keep class com.google.android.gms.location.** { *; }

# --- Security Hardening: Strip Sensitive Logging in Production ---
# Strips Android logging calls from compiled release binaries to prevent PII leakage.
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
}

-assumenosideeffects class java.io.PrintStream {
    public void println(...);
    public void print(...);
}
