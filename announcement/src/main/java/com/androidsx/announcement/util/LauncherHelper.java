package com.androidsx.announcement.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class LauncherHelper {
    private static final String TAG = LauncherHelper.class.getSimpleName();

    private static NotificationManager getNotificationManagerInstance(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static void sendNotification(Context context, int id, Intent openIntent, Intent dismissIntent, String title, String content, int smallIconId, Bitmap largeIconBitmap, boolean expanded, Bitmap contentImageBitmap) {
        // intent for cancel the last pending intent.
        openIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(context, id, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        cancelPendingIntent.cancel();

        PendingIntent contentIntent = PendingIntent.getBroadcast(context, id, openIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent deleteIntent = PendingIntent.getBroadcast(context, id + 1, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_LIGHTS)
                        .setSmallIcon(smallIconId)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setContentIntent(contentIntent)
                        .setDeleteIntent(deleteIntent)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                        .setAutoCancel(true);

        if (largeIconBitmap != null) {
            notificationBuilder.setLargeIcon(largeIconBitmap);
        }

        if (expanded && contentImageBitmap != null) {
            NotificationCompat.BigPictureStyle notificationStyle = new NotificationCompat.BigPictureStyle();
            notificationStyle.setBigContentTitle(title);
            notificationStyle.setSummaryText(content);
            notificationStyle.bigPicture(contentImageBitmap);

            notificationBuilder.setStyle(notificationStyle);
        } else {
            Log.v(TAG, "Push '" + id + "': Expanded mode with no content image, launching as a standard push");
        }

        try {
            getNotificationManagerInstance(context).notify(id, notificationBuilder.build());
        } catch (Throwable t) {
            Log.e(TAG, "Error while launching daily notification", t);
            throw t;
        }
    }

    public static void dismissNotification(Context context, int id) {
        getNotificationManagerInstance(context).cancel(id);
    }
}
