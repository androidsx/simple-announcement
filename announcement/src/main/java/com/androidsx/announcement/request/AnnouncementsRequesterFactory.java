package com.androidsx.announcement.request;

import android.content.Context;

import java.util.Map;

/**
 * Created by Androidsx on 21/12/15.
 */
public class AnnouncementsRequesterFactory {
    private static final boolean MOCK_ANNOUNCEMENTS = false;

    private AnnouncementsRequesterFactory() {
        // Non-instantiate
    }

    public static AnnouncementRequester create(Context context, String url, Map params, Map<String, String> headers) {
        if (MOCK_ANNOUNCEMENTS || "".equals(url)) {
            return MockAnnouncementRequester.getInstance();
        } else {
            return ServerAnnouncementRequester.with(context).url(url).params(params).headers(headers);
        }
    }
}
