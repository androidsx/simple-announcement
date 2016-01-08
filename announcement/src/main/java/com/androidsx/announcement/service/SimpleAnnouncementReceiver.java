package com.androidsx.announcement.service;

import android.content.Context;
import android.util.Log;

/**
 * Created by Androidsx on 4/1/16.
 */
public class SimpleAnnouncementReceiver extends AnnouncementReceiver {
    private static final String TAG = SimpleAnnouncementReceiver.class.getSimpleName();

    @Override
    protected void onPushLaunch(Context context, String pushId) {
        Log.v(TAG, "Push launched: " + pushId);
    }

    @Override
    protected void onPushOpen(Context context, String pushId) {
        Log.v(TAG, "Push opened: " + pushId);
        super.onPushOpen(context, pushId);
    }

    @Override
    protected void onPushDismiss(Context context, String pushId) {
        Log.v(TAG, "Push dismiss: " + pushId);
    }

    @Override
    protected void onDialogLaunch(Context context, String dialogId) {
        Log.v(TAG, "Dialog launched: " + dialogId);
    }

    @Override
    protected void onDialogOpen(Context context, String dialogId) {
        Log.v(TAG, "Dialog opened: " + dialogId);
        super.onDialogOpen(context, dialogId);
    }

    @Override
    protected void onDialogClose(Context context, String dialogId) {
        Log.v(TAG, "Dialog close: " + dialogId);
    }
}
