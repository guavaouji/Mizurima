package com.isao.mizurima.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.isao.mizurima.R;
import com.isao.mizurima.utils.NotificationUtils;

public class CreateTomorrowNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            // 翌日の通知登録
            NotificationUtils.update(context, 0, 1);
        } catch (Exception e) {
            Toast.makeText(context,
                    R.string.notification_update_failed_message,
                    Toast.LENGTH_SHORT).show();
        }

    }
}
