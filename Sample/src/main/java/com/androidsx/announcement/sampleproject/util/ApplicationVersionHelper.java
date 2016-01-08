package com.androidsx.announcement.sampleproject.util;

import android.content.Context;
import android.util.Log;

import com.androidsx.announcement.util.SharedPreferencesHelper;

/**
 * Helper methods
 */
public class ApplicationVersionHelper {
    private static final String TAG = ApplicationVersionHelper.class.getSimpleName();

    private static final String NUM_USES_PREFS_NAME = "numUses";

    /**
     * @return number of times that the application has been opened
     */
    public static int getNumUses(Context context) {
        return SharedPreferencesHelper.getIntValue(context, NUM_USES_PREFS_NAME);
    }

    /** Should be executed once and only once in the application. */
    public static int saveNewUse(Context context) {
        int numUses = getNumUses(context) + 1;
        SharedPreferencesHelper.saveIntValue(context, NUM_USES_PREFS_NAME, numUses);

        Log.i(TAG, "Saving a new usage of the app: " + numUses);
        return numUses;
    }
}
