package com.androidsx.announcement.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.androidsx.announcement.service.AnnouncementReceiver;
import com.androidsx.announcement.service.AnnouncementService;
import com.androidsx.announcement.util.AlarmHelper;
import com.androidsx.announcement.util.ApplicationHelper;
import com.androidsx.announcement.util.DateHelper;
import com.androidsx.announcement.util.LauncherHelper;
import com.androidsx.announcement.util.SharedPreferencesHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Androidsx on 14/12/15.
 */
public class PushAnnouncement extends BaseAnnouncement {
    private static final String TAG = PushAnnouncement.class.getSimpleName();

    private static final String SHARED_NUM_RETRIES = "num_retries";
    private static final int MAX_NUM_RETIRES = 8;

    private final boolean expanded;
    private final String largeIconUrl;
    private final int hourOfDay;

    private Bitmap largeIconBitmap = null;
    private Bitmap contentImageBitmap = null;

    PushAnnouncement(Builder builder) {
        super(builder.context, builder.id, builder.cancellable, builder.title,
                builder.content, builder.contentImageUrl, builder.openUrl, builder.nonInstalledPackage,
                builder.fromDate, builder.toDate, builder.daysOfWeek, builder.minAppVersionCode, builder.maxAppVersionCode);
        this.expanded = builder.expanded;
        this.largeIconUrl = builder.largeIconUrl;
        this.hourOfDay = builder.hourOfDay;

        loadSavedImages();

        if (canLaunch()) {
            scheduleAlarm();
        } else {
            Log.v(TAG, "Cannot schedule push: '" + id + "'");
            cancelAlarm();
        }
    }

    public boolean isExpanded() {
        return expanded;
    }

    public String getLargeIconUrl() {
        return largeIconUrl;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    @Override
    public boolean canLaunch() {
        int appVersionCode = ApplicationHelper.getAppVersionCode(context);

        if (!nonInstalledPackage.equals("") && ApplicationHelper.isAppInstalled(context, nonInstalledPackage)) {
            Log.v(TAG, "Push '" + id + "': Package installed");
            return false;
        }
        if (appVersionCode <= minAppVersionCode || appVersionCode > maxAppVersionCode) {
            Log.v(TAG, "Push '" + id + "': Not for this app version code");
            return false;
        }
        if (!DateHelper.isBetweenDates(fromDate, toDate)) {
            Log.v(TAG, "Push '" + id + "': Not between dates");
            return false;
        }
        if (fromDate == null && toDate == null && !DateHelper.isWeekDay(daysOfWeek)) {
            Log.v(TAG, "Push '" + id + "': Today is not a week day to show");
            return false;
        }

        return true;
    }

    @Override
    public boolean launchAnnouncement(int icon) {
        try {
            if (largeIconBitmap == null) {
                largeIconBitmap = BitmapFactory.decodeResource(context.getResources(), icon);
                Log.v(TAG, "Push '" + id + "': No large icon, using app icon");
            }
            LauncherHelper.sendNotification(context, id.hashCode(),
                    getAnnouncementOpenIntent(), getAnnouncementDismissIntent(),
                    title, content, icon, largeIconBitmap,
                    expanded, contentImageBitmap);

            cancelAlarm();
            context.sendBroadcast(getAnnouncementLaunchIntent());
            return true;
        } catch (Exception e) {
            int numRetries = SharedPreferencesHelper.getIntValue(context, SHARED_NUM_RETRIES, 0);
            if (numRetries >= MAX_NUM_RETIRES) {
                cancelAlarm();
            }
            return false;
        }
    }

    @Override
    public Intent getAnnouncementLaunchIntent() {
        Intent intent = getBaseIntent();
        intent.putExtra(AnnouncementReceiver.ANNOUNCEMENT_TYPE, AnnouncementReceiver.PUSH_LAUNCH);

        return intent;
    }

    @Override
    public Intent getAnnouncementOpenIntent() {
        Intent intent = getBaseIntent();
        intent.putExtra(AnnouncementReceiver.ANNOUNCEMENT_TYPE, AnnouncementReceiver.PUSH_OPEN);

        return intent;
    }

    @Override
    public Intent getAnnouncementDismissIntent() {
        Intent intent = getBaseIntent();
        intent.putExtra(AnnouncementReceiver.ANNOUNCEMENT_TYPE, AnnouncementReceiver.PUSH_DISMISS);

        return intent;
    }

    private void loadSavedImages() {
        largeIconBitmap = loadSavedBitmap(largeIconUrl);
        contentImageBitmap = loadSavedBitmap(contentImageUrl);
        if (largeIconBitmap == null) {
            downloadBitmap(largeIconUrl);
        }
        if (contentImageBitmap == null) {
            downloadBitmap(contentImageUrl);
        }
    }

    private void cancelAlarm() {
        Intent intent = AnnouncementService.getLaunchPushIntent(context, id);
        if (AlarmHelper.isPendingIntentActive(context, intent, id.hashCode())) {
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent alarmPendingIntent = AlarmHelper.getServicePendingIntent(context, intent, id.hashCode());
            alarmMgr.cancel(alarmPendingIntent);

            Log.v(TAG, "Alarm push id " + id + " canceled.");
        }
    }

    private void scheduleAlarm() {
        Intent intent = AnnouncementService.getLaunchPushIntent(context, id);
        if (!AlarmHelper.isPendingIntentActive(context, intent, id.hashCode())) {
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent alarmPendingIntent = AlarmHelper.getServicePendingIntent(context, intent, id.hashCode());

            Calendar cal = Calendar.getInstance();
            Calendar nowCal = Calendar.getInstance();
            cal.setTimeInMillis(nowCal.get(Calendar.HOUR_OF_DAY) >= hourOfDay ? /** If push hour is before now, schedule for tomorrow */
                    System.currentTimeMillis() + AlarmManager.INTERVAL_DAY :
                    System.currentTimeMillis());
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);

            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmPendingIntent);

            Log.v(TAG, "Scheduled push at " + cal.getTime().toString());
        } else {
            // Just set the alarm before.
        }
    }

    public static class Builder {
        private final Context context;
        private String id;
        private boolean enabled;
        private boolean cancellable;
        private String title;
        private String content;
        private String contentImageUrl;
        private String openUrl;
        private String nonInstalledPackage;
        private Date toDate;
        private Date fromDate;
        private List<Integer> daysOfWeek;
        private boolean expanded;
        private String largeIconUrl;
        private int hourOfDay;
        private int minAppVersionCode;
        private int maxAppVersionCode;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder cancellable(boolean cancellable) {
            this.cancellable = cancellable;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder expanded(boolean expanded) {
            this.expanded = expanded;
            return this;
        }

        public Builder largeIconUrl(String largeIconUrl) {
            this.largeIconUrl = largeIconUrl;
            return this;
        }

        public Builder contentImageUrl(String contentImageUrl) {
            this.contentImageUrl = contentImageUrl;
            return this;
        }

        public Builder openUrl(String openUrl) {
            this.openUrl = openUrl;
            return this;
        }

        public Builder nonInstalledPackage(String nonInstalledPackage) {
            this.nonInstalledPackage = nonInstalledPackage;
            return this;
        }

        public Builder fromDate(Date fromDate) {
            this.fromDate = fromDate;
            return this;
        }

        public Builder toDate(Date toDate) {
            this.toDate = toDate;
            return this;
        }

        public Builder daysOfWeek(List<Integer> daysOfWeek) {
            this.daysOfWeek = daysOfWeek;
            return this;
        }

        public Builder hourOfDay(int hourOfDay) {
            this.hourOfDay = hourOfDay;
            return this;
        }

        public Builder minAppVersionCode(int minAppVersionCode) {
            this.minAppVersionCode = minAppVersionCode;
            return this;
        }

        public Builder maxAppVersionCode(int maxAppVersionCode) {
            this.maxAppVersionCode = maxAppVersionCode;
            return this;
        }

        public Announcement build() {
            return new PushAnnouncement(this);
        }
    }
}
