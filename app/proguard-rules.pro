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


#保留泛型。
-keepattributes Signature
#保留异常
-keepattributes Exceptions
#保留内部类
-keepattributes InnerClasses
#保留枚举
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#-libraryjars libs/okhttp3
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }

#-libraryjars libs/okio
-dontwarn okio.**
-keep class okio.** { *; }
#Parcelable
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

##Glide
-dontwarn com.bumptech.glide.**
-keep class com.bumptech.glide.**{*;}
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}


# Gson specific classes
-keep class sun.misc.Unsafe {*;}
-keep class com.google.gson.stream.** {*;}
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.** {*;}
# 参与json解析的bean
-keep class com.dataenlighten.mj_serious_base.common.bean.** {*;}
# 注解
-keepattributes *Annotation*
# 反射
-keepattributes EnclosingMethod
#---------------------------------基本指令区----------------------------------
-optimizationpasses 5       #代码混淆压缩比，在0~7之间，默认为5，一般不做修改
-dontusemixedcaseclassnames     #混合时不使用大小写混合，混合后的类名为小写
-dontskipnonpubliclibraryclasses    #指定不去忽略非公共库的类
-dontskipnonpubliclibraryclassmembers   #指定不去忽略非公共库的类
-dontshrink
-dontpreverify    #不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-verbose
-optimizations !code/simplification/cast,!field/*,!class/merging/*  # 混淆时所采用的算法

#---------------------------------默认保留区---------------------------------
-dontwarn android.support.**
-keep class android.** {*;}
-dontwarn android.**
-keep class android.support.** {*;}
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

-keepnames class * extends android.view.View
-keep class * extends android.app.Fragment {
    public void setUserVisibleHint(boolean);
    public void onHiddenChanged(boolean);
    public void onResume();
    public void onPause();
}
-keep class android.support.v4.app.Fragment {
    public void setUserVisibleHint(boolean);
    public void onHiddenChanged(boolean);
    public void onResume();
    public void onPause();
}
-keep class * extends android.support.v4.app.Fragment {
    public void setUserVisibleHint(boolean);
    public void onHiddenChanged(boolean);
    public void onResume();
    public void onPause();
}

-keepclassmembers class * {public <init> (org.json.JSONObject);}
-keep public class com.mj.lossassessment.R$*{public static final int *;}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class **.R$* {*;}
-keepclassmembers class * {void *(**On*Event);}

#忽略警告
#-ignorewarning

#base
-keep class com.dataenlighten.mj_serious_base.view.DrawManager { *; }
-keep class com.dataenlighten.mj_serious_base.view.DrawPartView { *; }
-keep class com.dataenlighten.mj_serious_base.view.OnDrawQueryListener { *; }
-keep class com.dataenlighten.mj_serious_base.common.bean.** {*;}
-keep class com.dataenlighten.mj_serious_base.exception.** { *; }
-keep class com.dataenlighten.mj_serious_base.callback.** { *; }
-keep class com.dataenlighten.mj_serious_base.service.IMJSdkService { *; }
-keep class com.dataenlighten.mj_serious_base.service.MJSdkService {
   public static ** getInstance();
}

-keep class com.dataenlighten.mj_serious_ui.service.** { *; }