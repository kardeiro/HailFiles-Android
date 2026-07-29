# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.kardeiro.hailfiles.data.model.** { *; }
-keep class kotlinx.serialization.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
