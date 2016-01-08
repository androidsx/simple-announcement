package com.androidsx.announcement.sampleproject;

import android.app.Application;

import com.androidsx.announcement.AnnouncementManager;
import com.androidsx.announcement.sampleproject.util.ApplicationVersionHelper;
import com.androidsx.announcements.sampleproject.R;

/**
 * Created by Androidsx on 8/1/16.
 */
public class ProjectApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationVersionHelper.saveNewUse(this);

        /** Empty URL force to use the mock Announcement data,
         * see {@link com.androidsx.announcement.request.MockAnnouncementRequester}
         * and update the JSON configuration.
         */
        AnnouncementManager.init(this, R.mipmap.ic_launcher, ApplicationVersionHelper.getNumUses(this), "");
    }
}
