package com.androidsx.announcement.request;

/**
 * Created by Androidsx on 19/12/15.
 */
public interface AnnouncementRequester {
    String execute(boolean forceCache, boolean forceRequest);
}
