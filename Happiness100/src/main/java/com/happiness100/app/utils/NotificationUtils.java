package com.happiness100.app.utils;/**
 * Created by Administrator on 2016/9/2.
 */

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

/**
 * 作者：justin on 2016/9/2 12:07
 */
public class NotificationUtils {
    public static  void notifySimpleNotifycation(Context context, int id, String ticker, String title,
                                                 String content, int drawableId, boolean ongoing, boolean autoCancel, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context)
                .setTicker(ticker)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(autoCancel)
                .setOngoing(ongoing)
                .setOnlyAlertOnce(true)
                .setContentIntent(
                        PendingIntent.getActivity(context, 0, intent, 0))
                .setSmallIcon(drawableId);

        Notification notification = builder.build();
        NotificationManagerCompat.from(context).notify(id, notification);
    }
}
