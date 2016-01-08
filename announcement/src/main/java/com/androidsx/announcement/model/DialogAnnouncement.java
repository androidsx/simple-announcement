package com.androidsx.announcement.model;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.androidsx.announcement.AnnouncementManager;
import com.androidsx.announcement.service.AnnouncementReceiver;
import com.androidsx.announcement.widget.DialogColors;
import com.androidsx.announcement.util.ApplicationHelper;
import com.androidsx.announcement.util.DateHelper;
import com.androidsx.announcement.util.SharedPreferencesHelper;
import com.androidsx.announcement.widget.DialogFullScreen;

import java.util.Date;
import java.util.List;

/**
 * Created by Androidsx on 14/12/15.
 */
public class DialogAnnouncement extends BaseAnnouncement {
    private static final String TAG = DialogAnnouncement.class.getSimpleName();

    private final int maxNumShows;
    private final int showEveryXOpenings;
    private final boolean stopShowingAfterOk;

    private Bitmap contentImageBitmap = null;

    private FragmentManager fragmentManager = null;
    private DialogColors dialogColors = new DialogColors.Builder().build();
    private int layoutResId = DialogFullScreen.DEFAULT_LAYOUT_RES_ID;

    DialogAnnouncement(Builder builder) {
        super(builder.context, builder.id, builder.cancellable,
                builder.title, builder.content, builder.contentImageUrl, builder.openUrl,
                builder.nonInstalledPackage, builder.fromDate, builder.toDate, builder.daysOfWeek,
                builder.minAppVersionCode, builder.maxAppVersionCode);
        this.maxNumShows = builder.maxNumShows;
        this.showEveryXOpenings = builder.showEveryXOpenings;
        this.stopShowingAfterOk = builder.stopShowingAfterOk;

        loadSavedImages();
    }

    public int getMaxNumShows() {
        return maxNumShows;
    }

    public int getShowEveryXOpenings() {
        return showEveryXOpenings;
    }

    public boolean getStopShowingAfterOk() {
        return stopShowingAfterOk;
    }

    @Override
    public boolean canLaunch() {
        int appVersionCode = ApplicationHelper.getAppVersionCode(context);

        if (contentImageBitmap == null) {
            Log.v(TAG, "Dialog '" + id + "': No content image");
            return false;
        }
        if (!nonInstalledPackage.equals("") && ApplicationHelper.isAppInstalled(context, nonInstalledPackage)) {
            Log.v(TAG, "Dialog '" + id + "': Package installed");
            return false;
        }
        if (maxNumShows > 0 && DialogFullScreen.getNumLaunches(context, id) > maxNumShows) {
            Log.v(TAG, "Dialog '" + id + "': Max shows reached");
            return false;
        }
        if (appVersionCode <= minAppVersionCode || appVersionCode > maxAppVersionCode) {
            Log.v(TAG, "Dialog '" + id + "': Not for this app version code");
            return false;
        }
        if (showEveryXOpenings > 0 && AnnouncementManager.getAppUsages(context) % showEveryXOpenings != 0) {
            Log.v(TAG, "Dialog '" + id + "': Not show this time");
            return false;
        }
        if (stopShowingAfterOk && DialogFullScreen.isPressedOkButton(context, id)) {
            Log.v(TAG, "Dialog '" + id + "': Ok just pressed");
            return false;
        }
        if (!DateHelper.isBetweenDates(fromDate, toDate)) {
            Log.v(TAG, "Dialog '" + id + "': Not between dates");
            return false;
        }
        if (fromDate == null && toDate == null && !DateHelper.isWeekDay(daysOfWeek)) {
            Log.v(TAG, "Dialog '" + id + "': Today is not a week day to show");
            return false;
        }

        return true;
    }

    @Override
    public boolean launchAnnouncement(int icon) {
        if (fragmentManager == null) {
            Log.w(TAG, "FragmentManager is null, call setFragmentManager before.");

            return false;
        }
        if (context == null) {
            Log.w(TAG, "Context is null, call setContext before.");

            return false;
        }
        try {
            new DialogFullScreen.Builder(context)
                    .id(id)
                    .message(content)
                    .openIntent(getAnnouncementOpenIntent())
                    .dismissIntent(getAnnouncementDismissIntent())
                    .fullScreenDialogBackgroundColor(context.getResources().getColor(dialogColors.getBackground()))
                    .cancellable(cancellable)
                    .messageTextColor(context.getResources().getColor(dialogColors.getContent()))
                    .actionButtonTextColor(context.getResources().getColor(dialogColors.getButtonText()))
                    .actionButtonBackgroundColor(context.getResources().getColor(dialogColors.getButtonBackground()))
                    .iconBitmap(contentImageBitmap)
                    .buttonTitle(title)
                    .layoutResId(layoutResId)
                    .build()
                    .show(fragmentManager, TAG);
            context.sendBroadcast(getAnnouncementLaunchIntent());

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Launch dialog error", e);

            return false;
        }
    }

    @Override
    public Intent getAnnouncementLaunchIntent() {
        Intent intent = getBaseIntent();
        intent.putExtra(AnnouncementReceiver.ANNOUNCEMENT_TYPE, AnnouncementReceiver.DIALOG_LAUNCH);

        return intent;
    }

    @Override
    public Intent getAnnouncementOpenIntent() {
        Intent intent = getBaseIntent();
        intent.putExtra(AnnouncementReceiver.ANNOUNCEMENT_TYPE, AnnouncementReceiver.DIALOG_OPEN);

        return intent;
    }

    @Override
    public Intent getAnnouncementDismissIntent() {
        Intent intent = getBaseIntent();
        intent.putExtra(AnnouncementReceiver.ANNOUNCEMENT_TYPE, AnnouncementReceiver.DIALOG_DISMISS);

        return intent;
    }

    private void loadSavedImages() {
        contentImageBitmap = loadSavedBitmap(contentImageUrl);
        if (contentImageBitmap == null) {
            downloadBitmap(contentImageUrl);
        }
    }

    public DialogAnnouncement setContext(Context context) {
        this.context = context;
        return this;
    }

    public DialogAnnouncement setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        return this;
    }

    public DialogAnnouncement setDialogColors(DialogColors dialogColors) {
        if (dialogColors != null) {
            this.dialogColors = dialogColors;
        }
        return this;
    }

    public DialogAnnouncement setLayoutResId(int layoutResId) {
        this.layoutResId = layoutResId;
        return this;
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
        private int maxNumShows;
        private int showEveryXOpenings;
        private Date fromDate;
        private Date toDate;
        private List<Integer> daysOfWeek;
        private int minAppVersionCode;
        private int maxAppVersionCode;
        private boolean stopShowingAfterOk;

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

        public Builder maxNumShows(int maxNumShows) {
            this.maxNumShows = maxNumShows;
            return this;
        }

        public Builder showEveryXOpenings(int showEveryXOpenings) {
            this.showEveryXOpenings = showEveryXOpenings;
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

        public Builder minAppVersionCode(int minAppVersionCode) {
            this.minAppVersionCode = minAppVersionCode;
            return this;
        }

        public Builder maxAppVersionCode(int maxAppVersionCode) {
            this.maxAppVersionCode = maxAppVersionCode;
            return this;
        }

        public Builder stopShowingAfterOk(boolean stopShowingAfterOk) {
            this.stopShowingAfterOk = stopShowingAfterOk;
            return this;
        }

        public Announcement build() {
            return new DialogAnnouncement(this);
        }
    }
}
