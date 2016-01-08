package com.androidsx.announcement.request;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.androidsx.announcement.service.AnnouncementService;
import com.androidsx.announcement.util.AlarmHelper;
import com.androidsx.announcement.util.SharedPreferencesHelper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Androidsx on 15/12/15.
 */
public class ServerAnnouncementRequester implements AnnouncementRequester {
    private static final String TAG = ServerAnnouncementRequester.class.getSimpleName();

    private static final String SHARED_ANNOUNCEMENTS_DATA = "announcements_data";
    private static final long TIMEOUT_CONNECTION_MILLIS = 3000;

    static volatile ServerAnnouncementRequester singleton;
    private final Context context;

    private String url;
    private Map params;
    private Map<String, String> headers;

    ServerAnnouncementRequester(Context context) {
        this.context = context;
    }

    public static ServerAnnouncementRequester with(Context context) {
        if (singleton == null) {
            synchronized (ServerAnnouncementRequester.class) {
                if (singleton == null) {
                    singleton = new ServerAnnouncementRequester(context);
                }
            }
        }
        return singleton;
    }

    public ServerAnnouncementRequester url(String url) {
        this.url = url;
        return this;
    }

    public ServerAnnouncementRequester params(Map params) {
        this.params = params;
        return this;
    }

    public ServerAnnouncementRequester headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    @Override
    public String execute(boolean forceCache, boolean forceRequest) {
        String announcementsData = SharedPreferencesHelper.getStringValue(context, SHARED_ANNOUNCEMENTS_DATA, "");
        if (!forceCache || forceRequest) {
            try {
                if (forceRequest || announcementsData == null || announcementsData.equals("")) {
                    announcementsData = request();
                    SharedPreferencesHelper.saveStringValue(context, SHARED_ANNOUNCEMENTS_DATA, announcementsData);
                    cancelScheduleRetryExecution();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching Announcements from server", e);
                if (forceRequest) {
                    announcementsData = "";
                    SharedPreferencesHelper.saveStringValue(context, SHARED_ANNOUNCEMENTS_DATA, "");
                    scheduleRetryExecution();
                } else {
                    // Maintain the stored data.
                }
            }
        }

        return announcementsData;
    }

    private String request() throws IOException {
        OkHttpClient httpClient = new OkHttpClient();
        Request.Builder httpRequest;

        // appending params to url
        if (params != null) {
            boolean firstParameter = true;
            for (Object key : params.keySet().toArray()) {
                Object value = params.get(key);
                if (key instanceof String && value instanceof String) {
                    if (!firstParameter) {
                        url += "&";
                    } else {
                        url += "?";
                    }

                    url += (String) key;
                    url += "=";
                    url += URLEncoder.encode((String) value, "UTF-8");

                    firstParameter = false;
                }
            }
        }
        httpRequest = new Request.Builder().url(url).get();

        httpClient.setConnectTimeout(TIMEOUT_CONNECTION_MILLIS, TimeUnit.MILLISECONDS);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpRequest.header(entry.getKey(), entry.getValue());
            }
        } else {
            // Skip headers
        }

        Response httpResponse = httpClient.newCall(httpRequest.build()).execute();
        if (httpResponse != null && httpResponse.body() != null) {
            return httpResponse.body().string();
        } else {
            throw new IllegalStateException("Null response or empty body, cannot create the Announcements.");
        }
    }

    private void cancelScheduleRetryExecution() {
        Intent intent = AnnouncementService.getFetchIntent(context, url, true);
        if (AlarmHelper.isPendingIntentActive(context, intent, 1)) {
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent alarmPendingIntent = AlarmHelper.getServicePendingIntent(context, intent, 1);

            alarmMgr.cancel(alarmPendingIntent);
        } else {
            // Alarm doesn't exists.
        }
    }

    private void scheduleRetryExecution() {
        Intent intent = AnnouncementService.getFetchIntent(context, url, true);
        if (!AlarmHelper.isPendingIntentActive(context, intent, 1)) {
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent alarmPendingIntent = AlarmHelper.getServicePendingIntent(context, intent, 1);

            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + AlarmManager.INTERVAL_HALF_HOUR, AlarmManager.INTERVAL_HALF_HOUR,
                    alarmPendingIntent);
        } else {
            // Just set the alarm.
        }
    }
}
