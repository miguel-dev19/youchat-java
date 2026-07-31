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

-keepnames public class com.google.android.flexbox.FlexboxLayoutManager

-dontwarn kotlin.**
-dontwarn org.jetbrains.annotations.**

-if class androidx.appcompat.app.AppCompatViewInflater
-keep class com.google.android.material.theme.MaterialComponentsViewInflater {
    <init>();
}

-keep public class * extends androidx.coordinatorlayout.widget.CoordinatorLayout {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>();
}

-libraryjars libs/mail.jar
-libraryjars libs/activation.jar
-libraryjars libs/additionnal.jar

-keeppackagenames javax.mail.*
-keeppackagenames java.mail.*
-keeppackagenames com.sun.mail.*

-keeppackagenames com.sun.activation.*
-keeppackagenames javax.activation.*

-keeppackagenames myjava.awt.*
-keeppackagenames org.apache.*

# Make sure we keep annotations for CoordinatorLayout's DefaultBehavior
-keepattributes RuntimeVisible*Annotation*