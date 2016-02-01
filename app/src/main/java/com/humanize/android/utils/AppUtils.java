package com.humanize.android.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.humanize.android.data.UserDevice;
import com.humanize.android.helper.ApplicationState;

/**
 * Created by kamal on 1/31/16.
 */
public class AppUtils {

    public static String getAppVersionName() {
        return getPackageInfo(0).versionName;
    }

    public static String getApplicationDisplayName() {
        return  "HUMANIZE";
    }

    public static int getAppVersionCode() {
        return getPackageInfo(0).versionCode;
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ApplicationState.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.isConnected();
        }

        return false;
    }

    public static String getDeviceId() {
        return Settings.Secure.getString(ApplicationState.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static UserDevice getDeviceInformation() {

        UserDevice userDevice = new UserDevice();
        TelephonyManager telephonyManager = (TelephonyManager) ApplicationState.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);

        if (telephonyManager != null) {
            userDevice.setPhoneType(telephonyManager.getPhoneType() + "");
            userDevice.setNetworkOperatorName(telephonyManager.getNetworkOperatorName());
            userDevice.setNetworkType(telephonyManager.getNetworkType() + "");
            userDevice.setSimOperatorName(telephonyManager.getSimOperatorName());
        }

        userDevice.setDeviceId(getDeviceId());
        userDevice.setManufacturer(Build.MANUFACTURER);
        userDevice.setModel(Build.MODEL);
        userDevice.setSdkVersion(Build.VERSION.SDK_INT + "");

        return userDevice;
    }

    public static PackageInfo getPackageInfo(int flags) {
        try {
            PackageManager packageManager = ApplicationState.getAppContext().getPackageManager();
            return packageManager.getPackageInfo(ApplicationState.getAppContext().getPackageName(), flags);
        } catch (PackageManager.NameNotFoundException ex) {
        } catch (Exception ex) {
        }

        return null;
    }
}
