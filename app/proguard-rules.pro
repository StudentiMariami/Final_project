# ProGuard rules for release builds
# ProGuard removes unused code and obfuscates class names to reduce APK size

# Keep Retrofit and OkHttp (needed at runtime for network calls)
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }

# Keep Room entities (the database table classes must NOT be obfuscated)
-keep class com.example.afinal.model.** { *; }

# Keep Gson serialization (needed for Retrofit JSON parsing)
-keep class com.google.gson.** { *; }
