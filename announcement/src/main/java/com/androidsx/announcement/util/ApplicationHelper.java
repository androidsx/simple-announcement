package com.androidsx.announcement.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class ApplicationHelper {

    private static final String TAG = ApplicationHelper.class.getSimpleName();

    public static boolean isAppInstalled(Context context, String packageName) {
        boolean appInstalled;
        try {
            context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            appInstalled = true;
        } catch (Throwable t) {
            appInstalled = false;
        }
        return appInstalled;
    }

    public static int getAppVersionCode(Context context) {
        int appVersionCode = 0;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appVersionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Error retrieving the app version code.");
        }

        return appVersionCode;
    }
}
