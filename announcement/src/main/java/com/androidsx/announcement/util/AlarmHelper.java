package com.androidsx.announcement.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.androidsx.announcement.AnnouncementManager;
import com.androidsx.announcement.service.AnnouncementService;

import java.util.Calendar;

/**
 * Created by Androidsx on 17/12/15.
 */
public class AlarmHelper {

    private AlarmHelper() {
        // Non-instantiate
    }

    public static void setDailyServiceFetchAlarm(Context context) {
        setDailyServiceFetchAlarm(context, AnnouncementManager.getSavedAnnouncementsUrl(context));
    }

    public static void setDailyServiceFetchAlarm(Context context, String url) {
        Intent intent = AnnouncementService.getFetchIntent(context, url, true);
        if (!AlarmHelper.isPendingIntentActive(context, intent, 0)) {
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent alarmPendingIntent = AlarmHelper.getServicePendingIntent(context, intent, 0);

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis() + AlarmManager.INTERVAL_DAY);
            cal.set(Calendar.HOUR_OF_DAY, 8); // Starts alarm tomorrow between 8 - 9 o'clock,
            // this is because we don't know the minutes of the actual time,
            // but with this can distribute all requests in an hour instead a
            // few seconds for all the users in one TimeZone.

            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmPendingIntent);
        } else {
            // Just set the alarm.
        }
    }

    public static boolean isPendingIntentActive(Context context, Intent intent, int id) {
        return (PendingIntent.getService(context, id,
                intent, PendingIntent.FLAG_NO_CREATE) != null);
    }

    public static PendingIntent getServicePendingIntent(Context context, Intent intent, int id) {
        return PendingIntent.getService(context, id, intent, 0);
    }
}
