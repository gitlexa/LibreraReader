package com.foobnix.android.utils;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import com.foobnix.pdf.info.R;

import java.util.List;

import static android.content.Context.ACCESSIBILITY_SERVICE;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS;

public class Apps {

    public static int ANDROID_VERSION = Build.VERSION.SDK_INT;

    public static String getAndoroidID(Context c) {
        return Secure.getString(c.getContentResolver(), Secure.ANDROID_ID);
    }

    public static boolean isPackageInstalled(String packageName, Context context) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (Exception e) {
            LOG.e(e);
            return false;
        }
    }

    public static Drawable getApplicationImage(Context context) {
        return context.getPackageManager().getApplicationIcon(context.getApplicationInfo());
    }

    public static String getApplicationName(Context context) {
        try {
            return (String) context.getPackageManager().getApplicationLabel(context.getApplicationInfo());
        } catch (Exception e) {
            LOG.e(e);
        }
        return "";
    }


    public static String getVersionName(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (Exception e) {
            LOG.e(e);
        }
        return "";
    }

    public static int getVersionCode(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (Exception e) {
            LOG.e(e);
        }
        return -1;
    }

    public static int getTargetSdkVersion(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.applicationInfo.targetSdkVersion;
        } catch (Exception e) {
            LOG.e(e);
        }
        return -1;
    }

    public static String getPackageName(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.packageName;
        } catch (Exception e) {
            LOG.e(e);
        }
        return "";
    }

    public static void showDesctop(Context c) {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(startMain);
    }

    public static void onCrashEmail(Context c, String msg, String title) {
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

        String string = c.getResources().getString(R.string.my_email).replace("<u>", "").replace("</u>", "");
        final String aEmailList[] = {string};
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList);
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getApplicationName(c) + " " + Apps.getVersionName(c) + " Crash report");
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg);

        try {
            c.startActivity(Intent.createChooser(emailIntent, title));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(c, R.string.there_are_no_email_applications_installed_, Toast.LENGTH_SHORT).show();
        }
    }


    public static String getMetaData(Context context, String name) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                return appInfo.metaData.getString(name);
            }
        } catch (PackageManager.NameNotFoundException e) {
            LOG.e(e);
        }
        new RuntimeException("can't find meta-data:" + name);
        return null;
    }

    public static boolean isWifiEnabled(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }
        return false;
    }

    public static boolean isIntetConnected(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }
        return false;
    }

    public static boolean isNight(Activity a) {
        float screenBrightness = a.getWindow().getAttributes().screenBrightness;
        LOG.d("isNight screenBrightness", screenBrightness);
        boolean isNight = false;
        if (screenBrightness == -1) {
            try {
                int value = Settings.System.getInt(a.getContentResolver(), SCREEN_BRIGHTNESS);

                LOG.d("isNight value", value);
                isNight = value < 50;
            } catch (Settings.SettingNotFoundException e) {
                LOG.d(e);
            }
        } else {
            isNight = screenBrightness < 0.15;
        }
        LOG.d("isNight result", isNight);

        return isNight;

    }

    public static boolean isAccessibilityServiceEnabled(Context context) {
        try {
            AccessibilityManager am = (AccessibilityManager) context.getSystemService(ACCESSIBILITY_SERVICE);
            boolean isAccessibilityEnabled = am.isEnabled();
            boolean isExploreByTouchEnabled = am.isTouchExplorationEnabled();
            LOG.d("isAccessibilityServiceEnabled isAccessibilityEnabled", isAccessibilityEnabled);
            LOG.d("isAccessibilityServiceEnabled isExploreByTouchEnabled", isExploreByTouchEnabled);
            return isExploreByTouchEnabled;
        } catch (Exception e) {
            LOG.e(e);
        }
        return false;
    }

    public static void accessibilityText(Context context, String text) {
        try {
            AccessibilityManager am = (AccessibilityManager) context.getSystemService(ACCESSIBILITY_SERVICE);
            boolean isAccessibilityEnabled = am.isEnabled();
            LOG.d("isAccessibilityEnabled", isAccessibilityEnabled);
            if (isAccessibilityEnabled) {
                AccessibilityEvent accessibilityEvent = AccessibilityEvent.obtain();
                accessibilityEvent.setEventType(AccessibilityEvent.TYPE_ANNOUNCEMENT);

                accessibilityEvent.getText().add(text);
                if (am != null) {
                    am.sendAccessibilityEvent(accessibilityEvent);
                    LOG.d("sendAccessibilityEvent", text);
                }

            }
        } catch (Exception e) {
            LOG.e(e);
        }


    }
}
