package com.isao.mizurima.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.isao.mizurima.R;
import com.isao.mizurima.utils.DailyTaskUtils;
import com.isao.mizurima.utils.NotificationUtils;

import java.text.ParseException;

public class SystemBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            try {
                // 今日、明日の通知のスケジュール更新
                NotificationUtils.update(context, 0, 1);
            } catch (ParseException e) {
                Toast.makeText(context,
                        R.string.notification_update_failed_message,
                        Toast.LENGTH_SHORT).show();
            }
            // 日次処理のスケジュール登録
            DailyTaskUtils.saveSchedule(context);
        }
    }
}
