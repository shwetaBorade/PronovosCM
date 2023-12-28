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
# Retrofit
#-keep class com.google.gson.** { *; }
#-keep public class com.google.gson.** {public private protected *;}
#-keep class com.google.inject.** { *; }
#-keep class org.apache.james.mime4j.** { *; }
#-keep class javax.inject.** { *; }
#-keep class javax.xml.stream.** { *; }
#-keep class com.google.appengine.** { *; }
#-keepattributes Signature

#-keep class org.apache.http.** { *; }
-keep class retrofit.** { *; }
#-keepattributes *Annotation*
-dontwarn com.squareup.okhttp.*
-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn org.greenrobot.greendao.**
-dontwarn android.support.**

-keep class okio.** {*;}
# Project classes
-keep class com.pronovoscm.** {*;}
# GreenDao and EventBus class
-keep class org.greenrobot.** {*;}
# To keep fabric crashlytics classes
-keep class com.crashlytics.android.** {*;}
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keep class okhttp3.Headers { *; }
-keep class android.support.** { *; }

-keepattributes Exceptions
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

-dontwarn com.google.android.material.**
-keep class com.google.android.material.** { *; }

-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

-keepattributes EnclosingMethod
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclasseswithmembers interface * {
    @retrofit2.* <methods>;
}
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class com.beloo.widget.chipslayoutmanager.* { *; }
-keep class com.beloo.widget.chipslayoutmanager.** { *; }
-keep class com.beloo.widget.chipslayoutmanager.*$* { *; }
-keep class RestrictTo.*
-keep class RestrictTo.**
-keep class RestrictTo.*$*


# Save the obfuscation mapping to a file, so we can de-obfuscate any stack
# traces later on. Keep a fixed source file attribute and all line number
# tables to get line numbers in the stack traces.
# You can comment this out if you're not interested in stack traces.

-printmapping out.map
-keepparameternames
-renamesourcefileattribute SourceFile
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,EnclosingMethod

# Preserve all annotations.

-keepattributes Annotation

# Preserve all public classes, and their public and protected fields and
# methods.

-keep public class * {
public protected *;
}

# Preserve all .class method names.

-keepclassmembernames class * {
java.lang.Class class$(java.lang.String);
java.lang.Class class$(java.lang.String, boolean);
}

# Preserve all native method names and the names of their classes.

-keepclasseswithmembernames class * {
native <methods>;
}

# Preserve the special static methods that are required in all enumeration
# classes.

-keepclassmembers class * extends java.lang.Enum {
public static **[] values();
public static ** valueOf(java.lang.String);
}

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
# You can comment this out if your library doesn't use serialization.
# If your code contains serializable classes that have to be backward
# compatible, please refer to the manual.

-keepclassmembers class * implements java.io.Serializable {
static final long serialVersionUID;
static final java.io.ObjectStreamField[] serialPersistentFields;
private void writeObject(java.io.ObjectOutputStream);
private void readObject(java.io.ObjectInputStream);
java.lang.Object writeReplace();
java.lang.Object readResolve();
}

# Your library may contain more items that need to be preserved;
