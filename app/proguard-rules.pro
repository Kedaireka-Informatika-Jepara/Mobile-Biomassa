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
-keep class * extends androidx.fragment.app.Fragment{}
-dontwarn com.github.mikephil.**
-keep class com.github.mikephil.charting.animation.ChartAnimator{*;}

# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
#-keepattributes Signature, InnerClasses, EnclosingMethod
#
## Retrofit does reflection on method and parameter annotations.
#-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
#
## Keep annotation default values (e.g., retrofit2.http.Field.encoded).
#-keepattributes AnnotationDefault
#
## Retain service method parameters when optimizing.
#-keepclassmembers,allowshrinking,allowobfuscation interface * {
#    @retrofit2.http.* <methods>;
#}
#
## Ignore annotation used for build tooling.
#-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
#
## Ignore JSR 305 annotations for embedding nullability information.
#-dontwarn javax.annotation.**
#
## Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
#-dontwarn kotlin.Unit
#
## Top-level functions that can only be used by Kotlin.
#-dontwarn retrofit2.KotlinExtensions
#
## With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
## and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
#-if interface * { @retrofit2.http.* <methods>; }
#-keep,allowobfuscation interface <1>
#
## Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
#-keep,allowobfuscation,allowshrinking interface retrofit2.Call
#-keep,allowobfuscation,allowshrinking class retrofit2.Response
#
## With R8 full mode generic signatures are stripped for classes that are not
## kept. Suspend functions are wrapped in continuations where the type argument
## is used.
#-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
#
## JSR 305 annotations are for embedding nullability information.
#-dontwarn javax.annotation.**
#
#-keepclasseswithmembers class * {
#    @com.squareup.moshi.* <methods>;
#}
#
## Enum field names are used by the integrated EnumJsonAdapter.
## values() is synthesized by the Kotlin compiler and is used by EnumJsonAdapter indirectly
## Annotate enums with @JsonClass(generateAdapter = false) to use them with Moshi.
#-keepclassmembers @com.squareup.moshi.JsonClass class * extends java.lang.Enum {
#    <fields>;
#    **[] values();
#}
#
## Keep helper method to avoid R8 optimisation that would keep all Kotlin Metadata when unwanted
#-keepclassmembers class com.squareup.moshi.internal.Util {
#    private static java.lang.String getKotlinMetadataClassName();
#}
#
#-dontwarn org.codehaus.mojo.animal_sniffer.*
#
## JSR 305 annotations are for embedding nullability information.
#-dontwarn javax.annotation.**
#
## A resource is loaded with a relative path so the package of this class must be preserved.
#-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
#
## Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
#-dontwarn org.codehaus.mojo.animal_sniffer.*
#
## OkHttp platform used only on JVM and when Conscrypt and other security providers are available.
#-dontwarn okhttp3.internal.platform.**
#-dontwarn org.conscrypt.**
#-dontwarn org.bouncycastle.**
#-dontwarn org.openjsse.**
## If you keep the line number information, uncomment this to
## hide the original source file name.
##-renamesourcefileattribute SourceFile