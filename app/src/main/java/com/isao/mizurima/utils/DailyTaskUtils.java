package com.isao.mizurima.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.isao.mizurima.R;
import com.isao.mizurima.receiver.CreateTomorrowNotificationReceiver;
import com.isao.mizurima.receiver.DeletePastHistoryReceiver;

import java.util.Calendar;
import java.util.Locale;

public class DailyTaskUtils {
    public static void saveSchedule(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(
                Context.ALARM_SERVICE);

        // 翌日の通知作成通知
        createTomorrowNotification(context, alarmManager);

        // 過去の給水履歴の削除
        deletePastHistory(context, alarmManager);
    }


    private static void createTomorrowNotification(Context context, AlarmManager alarmManager) {
        Intent intent = new Intent(context, CreateTomorrowNotificationReceiver.class);

        PendingIntent sender = PendingIntent.getBroadcast(
                context, Constants.CREATE_TOMORROW_NOTIFICATION_REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar alarmCalender = Calendar.getInstance(Locale.JAPAN);

        alarmCalender.set(Calendar.HOUR_OF_DAY, context.getResources().getInteger(
                R.integer.create_tomorrow_notification_execute_hour));
        alarmCalender.set(Calendar.MINUTE, context.getResources().getInteger(
                R.integer.create_tomorrow_notification_execute_minute));
        alarmCalender.set(Calendar.SECOND, context.getResources().getInteger(
                R.integer.create_tomorrow_notification_execute_second));
        alarmCalender.set(Calendar.MILLISECOND, context.getResources().getInteger(
                R.integer.create_tomorrow_notification_execute_millisecond));
        long alarmMillis = alarmCalender.getTimeInMillis();

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                alarmMillis, AlarmManager.INTERVAL_DAY, sender);
    }

    private static void deletePastHistory(Context context, AlarmManager alarmManager) {

        Intent intent = new Intent(context, DeletePastHistoryReceiver.class);

        PendingIntent sender = PendingIntent.getBroadcast(
                context, Constants.DELETE_PAST_HISTORY_NOTIFICATION_REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar alarmCalender = Calendar.getInstance(Locale.JAPAN);
        alarmCalender.add(Calendar.DAY_OF_MONTH, 1);
        alarmCalender.set(Calendar.HOUR_OF_DAY, context.getResources().getInteger(
                R.integer.delete_past_history_execute_hour));
        alarmCalender.set(Calendar.MINUTE, context.getResources().getInteger(
                R.integer.delete_past_history_execute_minute));
        alarmCalender.set(Calendar.SECOND, context.getResources().getInteger(
                R.integer.delete_past_history_execute_second));
        alarmCalender.set(Calendar.MILLISECOND, context.getResources().getInteger(
                R.integer.delete_past_history_execute_millisecond));
        long alarmMillis = alarmCalender.getTimeInMillis();

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                alarmMillis, AlarmManager.INTERVAL_DAY, sender);
    }

}
