package com.androidsx.announcement.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.androidsx.announcement.AnnouncementManager;

/**
 * Created by Androidsx on 16/12/15.
 */
public class AnnouncementService extends IntentService {
    private static final String TAG = AnnouncementService.class.getSimpleName();

    private static final String EXTRA_FETCH = "fetch";
    private static final String EXTRA_FETCH_URL = "fetch_url";
    private static final String EXTRA_FETCH_FORCE_REQUEST = "fetch_forced_request";
    private static final String EXTRA_LAUNCH_PUSH = "launch_push";
    private static final String EXTRA_LAUNCH_PUSH_ID = "launch_push_id";

    public static void startServiceForFetch(Context context, String url, boolean forceRequest) {
        context.startService(getFetchIntent(context, url, forceRequest));
    }

    public static Intent getFetchIntent(Context context, String url, boolean forceRequest) {
        Intent intent = new Intent(context, AnnouncementService.class);
        intent.putExtra(EXTRA_FETCH, true);
        intent.putExtra(EXTRA_FETCH_URL, url);
        intent.putExtra(EXTRA_FETCH_FORCE_REQUEST, forceRequest);

        return intent;
    }

    public static Intent getLaunchPushIntent(Context context, String id) {
        Intent intent = new Intent(context, AnnouncementService.class);
        intent.putExtra(EXTRA_LAUNCH_PUSH, true);
        intent.putExtra(EXTRA_LAUNCH_PUSH_ID, id);

        return intent;
    }

    public AnnouncementService() {
        super(TAG);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public AnnouncementService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if (intent.hasExtra(EXTRA_FETCH) && intent.hasExtra(EXTRA_FETCH_URL)) {
                Log.v(TAG, "Fetch " + intent.getStringExtra(EXTRA_FETCH_URL));
                AnnouncementManager.with(this).fetch(intent.getStringExtra(EXTRA_FETCH_URL), false, intent.getBooleanExtra(EXTRA_FETCH_FORCE_REQUEST, false));
            } else if (intent.hasExtra(EXTRA_LAUNCH_PUSH) && intent.hasExtra(EXTRA_LAUNCH_PUSH_ID)) {
                Log.v(TAG, "LaunchPush " + intent.getStringExtra(EXTRA_LAUNCH_PUSH_ID));
                AnnouncementManager.with(this).fetch().launchPushAnnouncementIfApply(intent.getStringExtra(EXTRA_LAUNCH_PUSH_ID));
            }
        }
    }
}
