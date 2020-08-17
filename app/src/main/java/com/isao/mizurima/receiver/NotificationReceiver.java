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
import com.isao.mizurima.dao.WaterSupplyUnitDao;
import com.isao.mizurima.utils.Constants;

import java.util.List;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String channelId = context.getString(R.string.mizurima_channel_id);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_notify_water_supply)
                        .setContentTitle(context.getString(R.string.notification_message))
                        .setCategory(Notification.CATEGORY_REMINDER)
                        .setPriority(NotificationManager.IMPORTANCE_HIGH)
                        .setSound(RingtoneManager.getDefaultUri
                                (RingtoneManager.TYPE_NOTIFICATION));

        List<Integer> waterSupplyUnits = WaterSupplyUnitDao.find(context);
        for (Integer waterSupplyUnit : waterSupplyUnits) {

            String title = String.format(context.getResources().getString(
                    R.string.history_amount_label), waterSupplyUnit);

            Intent sendIntent = new Intent(context, AddWaterSupplyAmountFromNotificationReceiver.class);
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sendIntent.putExtra(Constants.ADD_WATER_SUPPLY_AMOUNT, waterSupplyUnit);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    Constants.ADD_WATER_SUPPLY_AMOUNT_FROM_NOTIFICATION_REQUEST_CODE
                            + waterSupplyUnit, sendIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            notificationBuilder.addAction(
                    R.drawable.baseline_add_circle_white_18,
                    title, pendingIntent);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
