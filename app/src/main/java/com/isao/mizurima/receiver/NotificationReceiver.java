package com.isao.mizurima.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.isao.mizurima.R;
import com.isao.mizurima.activity.MainActivity;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent viewIntent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = context.getString(R.string.mizurima_channel_id);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_notify_water_supply)
                        .setContentTitle(context.getString(R.string.notification_message))
                        .setCategory(Notification.CATEGORY_REMINDER)
                        .setPriority(NotificationManager.IMPORTANCE_HIGH)
                        .setContentIntent(pendingIntent)
                        .setSound(RingtoneManager.getDefaultUri
                                (RingtoneManager.TYPE_NOTIFICATION));
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
