package com.androidsx.announcement.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.androidsx.announcement.AnnouncementManager;
import com.androidsx.announcement.util.AlarmHelper;

/**
 * Created by Androidsx on 17/12/15.
 */
public class AnnouncementOnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                AlarmHelper.setDailyServiceFetchAlarm(context);
                AnnouncementManager.init(context);
            }
        }
    }
}
