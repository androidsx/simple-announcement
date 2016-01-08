package com.androidsx.announcement;

import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.androidsx.announcement.model.Announcement;
import com.androidsx.announcement.model.AnnouncementParams;
import com.androidsx.announcement.model.DialogAnnouncement;
import com.androidsx.announcement.model.PushAnnouncement;
import com.androidsx.announcement.request.AnnouncementsRequesterFactory;
import com.androidsx.announcement.service.AnnouncementService;
import com.androidsx.announcement.util.AlarmHelper;
import com.androidsx.announcement.util.SharedPreferencesHelper;
import com.androidsx.announcement.widget.DialogColors;
import com.androidsx.announcement.widget.DialogFullScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Androidsx on 15/12/15.
 */
public class AnnouncementManager {
    private static final String TAG = AnnouncementManager.class.getSimpleName();
    private static final String SHARED_APP_ICON = "app_icon";
    private static final String SHARED_APP_USAGES = "app_usages";
    private static final String SHARED_ANNOUNCEMENTS_URL = "announcements_url";

    private static final String DEFAULT_LANG = "en";

    static volatile AnnouncementManager singleton = null;

    private final Context context;
    private String announcementsJSONString;
    private String version;
    private Map<String, List<Announcement>> announcementsMap = new HashMap<>();

    public AnnouncementManager(Context context) {
        this.context = context;
    }

    public static AnnouncementManager with(Context context) {
        if (singleton == null) {
            synchronized (AnnouncementManager.class) {
                if (singleton == null) {
                    singleton = new Builder(context).build();
                }
            }
        }
        return singleton;
    }

    public static void init(Context context) {
        init(context,
                getSavedAppIcon(context),
                getAppUsages(context),
                getSavedAnnouncementsUrl(context));
    }

    /**
     * Initializes the announcement manager. It schedules a periodic alarm to refresh all data from the
     * server without the user's intervention, and starts a service to fetch all remote announcements.

     * Call this method from your Application class of your launcher activity.
     *
     * @return
     */
    public static void init(Context context, int icon, int appUsages, String url) {
        Log.i(TAG, "AnnouncementsManager initialization.\n" +
                "AppUsages: " + appUsages + "\n" +
                "Url: " + url);

        SharedPreferencesHelper.saveIntValue(context, SHARED_APP_ICON, icon);
        SharedPreferencesHelper.saveIntValue(context, SHARED_APP_USAGES, appUsages);
        SharedPreferencesHelper.saveStringValue(context, SHARED_ANNOUNCEMENTS_URL, url);
        AlarmHelper.setDailyServiceFetchAlarm(context, url);
        AnnouncementService.startServiceForFetch(context, url, false);
    }

    public Announcement getPushAnnouncementFromId(String id) {
        Announcement announcement = null;
        for (Announcement a : announcementsMap.get(AnnouncementParams.PUSHES)) {
            if (a.getId().equals(id)) {
                announcement = a;
                break;
            }
        }

        return announcement;
    }

    public Announcement getDialogAnnouncementFromId(String id) {
        Announcement announcement = null;
        for (Announcement a : announcementsMap.get(AnnouncementParams.DIALOGS)) {
            if (a.getId().equals(id)) {
                announcement = a;
                break;
            }
        }

        return announcement;
    }

    public static int getSavedAppIcon(Context context) {
        return SharedPreferencesHelper.getIntValue(context, SHARED_APP_ICON);
    }

    public static String getSavedAnnouncementsUrl(Context context) {
        return SharedPreferencesHelper.getStringValue(context, SHARED_ANNOUNCEMENTS_URL);
    }

    public static int getAppUsages(Context context) {
        return SharedPreferencesHelper.getIntValue(context, SHARED_APP_USAGES, -1);
    }

    public String getVersion() {
        return version;
    }

    /**
     * Fetch all local Announcements.
     *
     * @return
     */
    public synchronized AnnouncementManager fetch() {
        return fetch("", true, false);
    }

    /**
     * Fetch all remote Announcements if needed,
     * must be called only by the Announcement Service or in a non main thread.
     *
     * @return
     */
    public synchronized AnnouncementManager fetch(String url, boolean forceCache, boolean forceRequest) {
        return fetch(url, null, null, forceCache, forceRequest);
    }

    public synchronized AnnouncementManager fetch(String url, Map params, Map<String, String> headers, boolean forceCache, boolean forceRequest) {
        announcementsJSONString = AnnouncementsRequesterFactory.create(context, url, params, headers).execute(forceCache, forceRequest);
        try {
            if (announcementsJSONString != null && !announcementsJSONString.equals("")) {
                synchronized (announcementsMap) {
                    announcementsMap.clear();
                    announcementsMap.putAll(computeAnnouncements(announcementsJSONString));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching Announcements", e);
        }
        return this;
    }

    private Map<String, List<Announcement>> computeAnnouncements(String announcementsJSONString) {
        Map<String, List<Announcement>> announcementsMap = new HashMap<>();
        List<Announcement> pushesList = new ArrayList<>();
        List<Announcement> dialogsList = new ArrayList<>();
        try {
            final String lang = Locale.getDefault().getLanguage();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

            JSONObject announcementsJSON = new JSONObject(announcementsJSONString);
            version = announcementsJSON.getString(AnnouncementParams.VERSION);

            try {
                JSONArray pushesAnnouncements = (JSONArray) announcementsJSON.get(AnnouncementParams.PUSHES);
                for (int i = 0; i < pushesAnnouncements.length(); i++) {
                    try {
                        JSONObject push = (JSONObject) pushesAnnouncements.get(i);
                        if (!push.getBoolean(AnnouncementParams.Data.ENABLED)) {
                            continue;
                        } else {
                            JSONObject pushConfig = (JSONObject) push.get(AnnouncementParams.Data.CONFIG);
                            JSONObject pushTitle = (JSONObject) push.get(AnnouncementParams.Data.TITLE);
                            JSONObject pushContent = (JSONObject) push.get(AnnouncementParams.Data.CONTENT);
                            JSONArray pushDaysOfWeek = (JSONArray) pushConfig.get(AnnouncementParams.Data.DAYS_OF_WEEK);
                            List<Integer> daysOfWeekList = new ArrayList<>();
                            for (int j = 0; j < pushDaysOfWeek.length(); j++) {
                                daysOfWeekList.add(pushDaysOfWeek.getInt(j));
                            }
                            Date fromDate = null;
                            Date toDate = null;
                            try {
                                fromDate = sdf.parse(pushConfig.getString(AnnouncementParams.Data.FROM_DATE));
                            } catch (Exception e) {
                            }
                            try {
                                toDate = sdf.parse(pushConfig.getString(AnnouncementParams.Data.TO_DATE));
                            } catch (Exception e) {
                            }

                            Announcement pushAnnouncement = new PushAnnouncement.Builder(context)
                                    .id(push.getString(AnnouncementParams.Data.ID))
                                    .cancellable(push.getBoolean(AnnouncementParams.Data.CANCELLABLE))
                                    .title(pushTitle.getString(pushTitle.has(lang) ? lang : DEFAULT_LANG))
                                    .content(pushContent.getString(pushContent.has(lang) ? lang : DEFAULT_LANG))
                                    .expanded(pushConfig.getBoolean(AnnouncementParams.Data.Push.EXPANDED))
                                    .largeIconUrl(pushConfig.getString(AnnouncementParams.Data.Push.LARGE_ICON_URL))
                                    .contentImageUrl(pushConfig.getString(AnnouncementParams.Data.CONTENT_IMAGE_URL))
                                    .openUrl(pushConfig.getString(AnnouncementParams.Data.OPEN_URL))
                                    .nonInstalledPackage(pushConfig.getString(AnnouncementParams.Data.NON_INSTALLED_APP_PACKAGE))
                                    .fromDate(fromDate)
                                    .toDate(toDate)
                                    .daysOfWeek(daysOfWeekList)
                                    .hourOfDay(pushConfig.getInt(AnnouncementParams.Data.Push.HOUR_OF_DAY))
                                    .minAppVersionCode(pushConfig.getInt(AnnouncementParams.Data.MIN_APP_VERSION_CODE))
                                    .maxAppVersionCode(pushConfig.getInt(AnnouncementParams.Data.MAX_APP_VERSION_CODE))
                                    .build();

                            pushesList.add(pushAnnouncement);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error computing push " + i, e);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error computing pushes", e);
            }

            try {
                JSONArray dialogsAnnouncements = (JSONArray) announcementsJSON.get(AnnouncementParams.DIALOGS);
                for (int i = 0; i < dialogsAnnouncements.length(); i++) {
                    try {
                        JSONObject dialog = (JSONObject) dialogsAnnouncements.get(i);
                        if (!dialog.getBoolean(AnnouncementParams.Data.ENABLED)) {
                            continue;
                        } else {
                            JSONObject dialogConfig = (JSONObject) dialog.get(AnnouncementParams.Data.CONFIG);
                            JSONObject dialogTitle = (JSONObject) dialog.get(AnnouncementParams.Data.TITLE);
                            JSONObject dialogContent = (JSONObject) dialog.get(AnnouncementParams.Data.CONTENT);
                            JSONArray dialogDaysOfWeek = (JSONArray) dialogConfig.get(AnnouncementParams.Data.DAYS_OF_WEEK);
                            List<Integer> daysOfWeekList = new ArrayList<>();
                            for (int j = 0; j < dialogDaysOfWeek.length(); j++) {
                                daysOfWeekList.add(dialogDaysOfWeek.getInt(j));
                            }
                            Date fromDate = null;
                            Date toDate = null;
                            try {
                                fromDate = sdf.parse(dialogConfig.getString(AnnouncementParams.Data.FROM_DATE));
                            } catch (Exception e) {
                            }
                            try {
                                toDate = sdf.parse(dialogConfig.getString(AnnouncementParams.Data.TO_DATE));
                            } catch (Exception e) {
                            }

                            Announcement dialogAnnouncement = new DialogAnnouncement.Builder(context)
                                    .id(dialog.getString(AnnouncementParams.Data.ID))
                                    .cancellable(dialog.getBoolean(AnnouncementParams.Data.CANCELLABLE))
                                    .title(dialogTitle.getString(dialogTitle.has(lang) ? lang : DEFAULT_LANG))
                                    .content(dialogContent.getString(dialogContent.has(lang) ? lang : DEFAULT_LANG))
                                    .contentImageUrl(dialogConfig.getString(AnnouncementParams.Data.CONTENT_IMAGE_URL))
                                    .openUrl(dialogConfig.getString(AnnouncementParams.Data.OPEN_URL))
                                    .nonInstalledPackage(dialogConfig.getString(AnnouncementParams.Data.NON_INSTALLED_APP_PACKAGE))
                                    .maxNumShows(dialogConfig.getInt(AnnouncementParams.Data.Dialog.MAX_NUM_SHOWS))
                                    .showEveryXOpenings(dialogConfig.getInt(AnnouncementParams.Data.Dialog.SHOW_EVERY_X_OPENINGS))
                                    .stopShowingAfterOk(dialogConfig.getBoolean(AnnouncementParams.Data.Dialog.STOP_SHOWING_AFTER_OK))
                                    .fromDate(fromDate)
                                    .toDate(toDate)
                                    .daysOfWeek(daysOfWeekList)
                                    .minAppVersionCode(dialogConfig.getInt(AnnouncementParams.Data.MIN_APP_VERSION_CODE))
                                    .maxAppVersionCode(dialogConfig.getInt(AnnouncementParams.Data.MAX_APP_VERSION_CODE))
                                    .build();

                            dialogsList.add(dialogAnnouncement);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error computing dialog " + i, e);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error computing dialogs", e);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error computing Announcements", e);
        }

        announcementsMap.put(AnnouncementParams.PUSHES, pushesList);
        announcementsMap.put(AnnouncementParams.DIALOGS, dialogsList);
        return announcementsMap;
    }

    private static class Builder {
        private final Context context;

        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            }
            this.context = context.getApplicationContext();
        }

        /** Create the {@link AnnouncementManager} instance. */
        public AnnouncementManager build() {
            Context context = this.context;

            return new AnnouncementManager(context);
        }
    }

    public void launchPushAnnouncement(String id) {
        if (!announcementsMap.isEmpty() && announcementsMap.containsKey(AnnouncementParams.PUSHES)) {
            Log.v(TAG, "Announcements Pushes: " + announcementsMap.get(AnnouncementParams.PUSHES).size());
            for (Announcement a : announcementsMap.get(AnnouncementParams.PUSHES)) {
                if (a.getId().equals(id) && a instanceof PushAnnouncement) {
                    if (a.canLaunch()) { // This is to avoid possible changes between schedule and effective launch.
                        Log.v(TAG, "Launching Push " + a.getId());
                        boolean launched = a.launchAnnouncement(getSavedAppIcon(context));
                        Log.v(TAG, "Push " + a.getId() + " launched: " + launched);
                    } else {
                        return;
                    }
                } else {
                    continue;
                }
            }
        } else {
            return;
        }
    }

    public void launchDialogAnnouncementIfApply(Context context, FragmentManager fragmentManager) {
        launchDialogAnnouncementIfApply(context, null, fragmentManager, null, DialogFullScreen.DEFAULT_LAYOUT_RES_ID);
    }

    public void launchDialogAnnouncementIfApply(Context context, FragmentManager fragmentManager, DialogColors dialogColors) {
        launchDialogAnnouncementIfApply(context, null, fragmentManager, dialogColors, DialogFullScreen.DEFAULT_LAYOUT_RES_ID);
    }

    public void launchDialogAnnouncementIfApply(Context context, FragmentManager fragmentManager, int layoutResId) {
        launchDialogAnnouncementIfApply(context, null, fragmentManager, null, layoutResId);
    }

    public void launchDialogAnnouncementIfApply(Context context, String id, FragmentManager fragmentManager) {
        launchDialogAnnouncementIfApply(context, id, fragmentManager, null, DialogFullScreen.DEFAULT_LAYOUT_RES_ID);
    }

    public void launchDialogAnnouncementIfApply(Context context, String id, FragmentManager fragmentManager, DialogColors dialogColors) {
        launchDialogAnnouncementIfApply(context, id, fragmentManager, dialogColors, DialogFullScreen.DEFAULT_LAYOUT_RES_ID);
    }

    public void launchDialogAnnouncementIfApply(Context context, String id, FragmentManager fragmentManager, int layoutResId) {
        launchDialogAnnouncementIfApply(context, id, fragmentManager, null, layoutResId);
    }

    public void launchDialogAnnouncementIfApply(final Context context, final String id, final FragmentManager fragmentManager, final DialogColors dialogColors, final int layoutResId) {
        new AsyncTask<Void, Void, Object>() {
            @Override
            protected Object doInBackground(Void... params) {
                fetch(); // Fetching all local announcements and downloading not downloaded images.
                synchronized (announcementsMap) {
                    if (!announcementsMap.isEmpty() && announcementsMap.containsKey(AnnouncementParams.DIALOGS)) {
                        Log.v(TAG, "Announcements Dialogs: " + announcementsMap.get(AnnouncementParams.DIALOGS).size());
                        for (Announcement a : announcementsMap.get(AnnouncementParams.DIALOGS)) {
                            if (a instanceof DialogAnnouncement) {
                                DialogAnnouncement da = (DialogAnnouncement) a;
                                /** Launch the first dialog that can be launched or the specific dialog using its id */
                                if (da.canLaunch() && (id == null || id.equals(da.getId()))) {
                                    da.setFragmentManager(fragmentManager);
                                    da.setContext(context);
                                    da.setLayoutResId(layoutResId);
                                    da.setDialogColors(dialogColors);

                                    return da;
                                } else {
                                    return null;
                                }
                            } else {
                                continue;
                            }
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object aObject) {
                super.onPostExecute(aObject);
                if (aObject != null) {
                    DialogAnnouncement da = (DialogAnnouncement) aObject;
                    Log.v(TAG, "Launching Dialog " + da.getId());
                    boolean launched =  da.launchAnnouncement(getSavedAppIcon(context));
                    Log.v(TAG, "Dialog " + da.getId() + " launched: " + launched);
                } else {
                    // No dialog to launch.
                }
            }
        }.execute();
    }
}
