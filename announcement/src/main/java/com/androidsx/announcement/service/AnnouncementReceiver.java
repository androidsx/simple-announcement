package com.androidsx.announcement.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.androidsx.announcement.AnnouncementManager;
import com.androidsx.announcement.model.Announcement;

/**
 * Created by Androidsx on 4/1/16.
 */
public abstract class AnnouncementReceiver extends BroadcastReceiver {
    private static final String TAG = AnnouncementReceiver.class.getSimpleName();

    public static final String ANNOUNCEMENT_ACTION = "com.androidsx.announcement.event";

    public static final String ANNOUNCEMENT_EXTRA = "announcement";
    public static final String ANNOUNCEMENT_TYPE = "announcement_type";

    public static final int PUSH_LAUNCH = 1<<1;
    public static final int PUSH_OPEN = 1<<2;
    public static final int PUSH_DISMISS = 1<<3;
    public static final int DIALOG_LAUNCH = 1<<4;
    public static final int DIALOG_OPEN = 1<<5;
    public static final int DIALOG_DISMISS = 1<<6;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "Received Intent");

        if (intent != null && intent.hasExtra(ANNOUNCEMENT_EXTRA)) {
            String id = intent.getStringExtra(ANNOUNCEMENT_EXTRA);
            switch(intent.getIntExtra(ANNOUNCEMENT_TYPE, 0)) {
                case PUSH_LAUNCH:
                    Log.v(TAG, "Push Launch");
                    onPushLaunch(context, id);
                    break;
                case PUSH_OPEN:
                    Log.v(TAG, "Push Open");
                    onPushOpen(context, id);
                    break;
                case PUSH_DISMISS:
                    Log.v(TAG, "Push Dismiss");
                    onPushDismiss(context, id);
                    break;
                case DIALOG_LAUNCH:
                    Log.v(TAG, "Dialog Launch");
                    onDialogLaunch(context, id);
                    break;
                case DIALOG_OPEN:
                    Log.v(TAG, "Dialog Open");
                    onDialogOpen(context, id);
                    break;
                case DIALOG_DISMISS:
                    Log.v(TAG, "Dialog Dismiss");
                    onDialogClose(context, id);
                    break;
                default:
                    Log.w(TAG, "Unhandled announcement type: " + id);
                    break;
            }
        }
    }

    protected abstract void onPushLaunch(Context context, String pushId);

    protected void onPushOpen(Context context, String pushId) {
        Announcement push = AnnouncementManager.with(context).fetch().getPushAnnouncementFromId(pushId);
        if (push != null) {
            context.startActivity(push.getOpenIntent(true));
        } else {
            Log.w(TAG, "Ouch! Something wrong, no push available with id " + pushId);
        }
    }

    protected abstract void onPushDismiss(Context context, String pushId);

    protected abstract void onDialogLaunch(Context context, String dialogId);

    protected void onDialogOpen(Context context, String dialogId) {
        Announcement dialog = AnnouncementManager.with(context).fetch().getDialogAnnouncementFromId(dialogId);
        if (dialog != null) {
            context.startActivity(dialog.getOpenIntent(false));
        } else {
            Log.w(TAG, "Ouch! Something wrong, no dialog available with id " + dialogId);
        }
    }

    protected abstract void onDialogClose(Context context, String dialogId);
}
