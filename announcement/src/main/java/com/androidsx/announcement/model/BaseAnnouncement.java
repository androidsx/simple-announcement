package com.androidsx.announcement.model;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.androidsx.announcement.service.AnnouncementReceiver;
import com.androidsx.announcement.util.SharedPreferencesHelper;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * Created by Androidsx on 15/12/15.
 */
public abstract class BaseAnnouncement implements Announcement {
    private static final String TAG = BaseAnnouncement.class.getSimpleName();

    protected Context context;

    protected final String id;
    protected final boolean cancellable;
    protected final String title;
    protected final String content;
    protected final String contentImageUrl;
    protected final String openUrl;
    protected final String nonInstalledPackage;
    protected final Date fromDate;
    protected final Date toDate;
    protected final List<Integer> daysOfWeek;
    protected final int minAppVersionCode;
    protected final int maxAppVersionCode;

    BaseAnnouncement(Context context, String id, boolean cancellable,
                     String title, String content, String contentImageUrl, String openUrl,
                     String nonInstalledPackage, Date fromDate, Date toDate, List<Integer> daysOfWeek,
                     int minAppVersionCode, int maxAppVersionCode) {
        this.context = context;
        this.id = id;
        this.cancellable = cancellable;
        this.title = title;
        this.content = content;
        this.contentImageUrl = contentImageUrl;
        this.openUrl = openUrl;
        this.nonInstalledPackage = nonInstalledPackage;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.daysOfWeek = daysOfWeek;
        this.minAppVersionCode = minAppVersionCode;
        this.maxAppVersionCode = maxAppVersionCode;

        Log.v(TAG, "Created Announcement: '" + id + "'");
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isCancellable() {
        return cancellable;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String getContentImageUrl() {
        return contentImageUrl;
    }

    @Override
    public String getOpenUrl() {
        return openUrl;
    }

    @Override
    public String getNonInstalledPackage() {
        return nonInstalledPackage;
    }

    @Override
     public Date getFromDate() {
        return fromDate;
    }

    @Override
    public Date getToDate() {
        return toDate;
    }

    @Override
    public List<Integer> getDaysOfWeek() {
        return daysOfWeek;
    }

    @Override
    public int getMinAppVersionCode() {
        return minAppVersionCode;
    }

    @Override
    public int getMaxAppVersionCode() {
        return maxAppVersionCode;
    }

    @Override
    public Intent getOpenIntent(boolean openAppIfNoOpenUrl) {
        if (openUrl == null || openUrl.equals("")) {
            PackageManager manager = context.getPackageManager();
            Intent intent = manager.getLaunchIntentForPackage(context.getApplicationContext().getPackageName());
            if (!openAppIfNoOpenUrl || intent == null) {
                return null;
            } else {
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                return intent;
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(openUrl));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            return intent;
        }
    }

    protected Intent getBaseIntent() {
        Intent intent = new Intent();
        intent.setAction(AnnouncementReceiver.ANNOUNCEMENT_ACTION);
        intent.putExtra(AnnouncementReceiver.ANNOUNCEMENT_EXTRA, id);

        return intent;
    }

    /**
     * Download the url images and save it as a Base64 string.
     * Note: this method cannot be called by MainThread.
     * @param imageUrl
     */
    protected void downloadBitmap(String imageUrl){
        if (!Looper.getMainLooper().equals(Looper.myLooper())) {
            if (imageUrl != null && !imageUrl.equals("")) {
                try {
                    Log.v(TAG, "Downloading image from url " + imageUrl);

                    URL url = new URL(imageUrl);
                    Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    if (bitmap != null) {
                        saveLoadedBitmap(imageUrl, bitmap);
                    } else {
                        Log.w(TAG, "Error downloading the image from " + imageUrl);
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Error downloading the image from " + imageUrl, e);
                }
            } else {
                Log.w(TAG, "Announcement " + id + ", wrong URL: " + imageUrl);
            }
        } else {
            Log.w(TAG, "Ignoring, this method can't be called by the MainThread.");
        }
    }

    protected void saveLoadedBitmap(String url, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] b = byteArrayOutputStream.toByteArray();

        String encodedImageBase64 = Base64.encodeToString(b, Base64.DEFAULT);
        SharedPreferencesHelper.saveStringValue(context, url, encodedImageBase64);
        Log.v(TAG, "Saved image of Announcement '" + id + "' into sharedPrefs " + url + " -> " + (encodedImageBase64 != null && !encodedImageBase64.equals("")));
    }

    protected Bitmap loadSavedBitmap(String url) {
        Bitmap bitmap = null;
        String previouslyEncodedImage = SharedPreferencesHelper.getStringValue(context, url);
        if (!previouslyEncodedImage.equals("")){
            byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        }

        Log.v(TAG, "Loading image of Announcement '" + id + "' from sharedPrefs " + url + " -> " + (bitmap != null));
        return bitmap;
    }
}
