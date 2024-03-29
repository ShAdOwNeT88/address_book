# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-printusage obfuscation/usage.txt

-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes *Annotation*, Signature, Exception
# Keep file names and line numbers
-keepattributes SourceFile,LineNumberTable
# Optional: Keep custom exceptions.
-keep public class * extends java.lang.Exception

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
-dontwarn java.rmi.**
-dontwarn org.bouncycastle.jsse.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keep class com.google.firebase.example.fireeats.model.** { *; }
-keep class * implements com.officinasocialeproarpaia.address_book.features.dashboard.domain.*
-keepclassmembers class com.officinasocialeproarpaia.address_book.features.dashboard.domain.** { *;}
-keep class * implements com.officinasocialeproarpaia.address_book.features.dashboard.network.response.*
-keepclassmembers class com.officinasocialeproarpaia.address_book.features.dashboard.network.response.** { *;}
-keep class * implements com.officinasocialeproarpaia.address_book.features.home.domain.*
-keepclassmembers class com.officinasocialeproarpaia.address_book.features.home.domain.** { *;}
-keep class * implements com.officinasocialeproarpaia.address_book.features.newsretriever.domain.*
-keepclassmembers class com.officinasocialeproarpaia.address_book.features.newsretriever.domain.** { *;}
-keep class com.shockwave.**

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {@com.google.gson.annotations.SerializedName <fields>;}
##---------------End: proguard configuration for Gson  ----------

# Prevent crash with androidx.fragment.app.FragmentContainerView
-keep class * extends androidx.fragment.app.Fragment{}
-keep class com.google.android.gms.maps.model.Marker { *; }
