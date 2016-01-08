package com.androidsx.announcement.model;

import android.content.Intent;

import java.util.Date;
import java.util.List;

/**
 * Created by Androidsx on 14/12/15.
 */
public interface Announcement {
    boolean launchAnnouncement(int icon);
    Intent getOpenIntent(boolean openAppIfNoOpenUrl);
    Intent getAnnouncementLaunchIntent();
    Intent getAnnouncementOpenIntent();
    Intent getAnnouncementDismissIntent();
    String getId();
    boolean isCancellable();
    String getTitle();
    String getContent();
    String getContentImageUrl();
    String getOpenUrl();
    String getNonInstalledPackage();
    Date getFromDate();
    Date getToDate();
    List<Integer> getDaysOfWeek();
    int getMinAppVersionCode();
    int getMaxAppVersionCode();
    boolean canLaunch();
}
