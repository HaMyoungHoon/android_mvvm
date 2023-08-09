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
-dontwarn org.slf4j.impl.StaticLoggerBinder
-dontwarn com.google.appengine.api.urlfetch.FetchOptions$Builder
-dontwarn com.google.appengine.api.urlfetch.FetchOptions
-dontwarn com.google.appengine.api.urlfetch.HTTPHeader
-dontwarn com.google.appengine.api.urlfetch.HTTPMethod
-dontwarn com.google.appengine.api.urlfetch.HTTPRequest
-dontwarn com.google.appengine.api.urlfetch.HTTPResponse
-dontwarn com.google.appengine.api.urlfetch.URLFetchService
-dontwarn com.google.appengine.api.urlfetch.URLFetchServiceFactory

-keep public class * extends java.lang.Exception  # Optional: Keep custom exceptions.

-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type

-keep, allowoptimization class com.google.android.libraries.maps.** { *; }
-keep class com.google.maps.** { *; }

-keep class * extends com.google.gson.TypeAdapter
-keep, allowobfuscation, allowoptimization class org.kodein.di.TypeReference
-keep, allowobfuscation, allowoptimization class * extends org.kodein.di.TypeReference