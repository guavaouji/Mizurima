package com.isao.mizurima.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.isao.mizurima.R;
import com.isao.mizurima.dao.AverageWaterSupplyDao;
import com.isao.mizurima.dao.DailyTargetDao;
import com.isao.mizurima.dao.NotificationEndTimeDao;
import com.isao.mizurima.dao.NotificationScheduleDao;
import com.isao.mizurima.dao.NotificationStartTimeDao;
import com.isao.mizurima.dao.WaterSupplyHistoryDao;
import com.isao.mizurima.model.NotificationSchedule;
import com.isao.mizurima.receiver.NotificationReceiver;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationUtils {


    public static void update(Context context, int startAddDay, int endAddDay)
            throws ParseException {

        // 通知スケジュール作成
        List<NotificationSchedule> schedules = createSchedules(context, startAddDay, endAddDay);

        // 通知を更新
        updateNotification(schedules, context);

        // sharedに保存
        NotificationScheduleDao.save(schedules, context);
    }

    private static List<NotificationSchedule> createSchedules(Context context, int startAddDay,
                                                              int endAddDay) throws ParseException {

        // 通知予定リスト
        List<NotificationSchedule> result = new ArrayList<>();

        int dailyTarget = DailyTargetDao.find(context);
        int averageAmount = AverageWaterSupplyDao.find(context);
        // 通知回数
        int notificationCount = (int) Math.ceil((double) dailyTarget / (double) averageAmount);
        // 現在総給水量
        int nowTotalAmount = WaterSupplyHistoryDao.findTodayTotalWaterSupplyAmount(context);

        for (int addDay = startAddDay; addDay <= endAddDay; addDay++) {
            Calendar baseCalendar = Calendar.getInstance(Locale.JAPAN);
            baseCalendar.add(Calendar.DAY_OF_MONTH, addDay);
            long baseMillis = (addDay == 0) ?
                    baseCalendar.getTimeInMillis() : toMillis(baseCalendar, "00:00");
            long startMillis = toMillis(baseCalendar, NotificationStartTimeDao.find(context));
            long endMillis = toMillis(baseCalendar, NotificationEndTimeDao.find(context));

            // 差分のミリ秒
            long diffMillis = endMillis - startMillis;

            // 通知間隔※-1は通知開始時刻分を除くため
            long notificationInterval = diffMillis / (notificationCount - 1);

            for (int i = 1; i <= notificationCount; i++) {
                long scheduleTime = 0L;
                int scheduleAmount = 0;

                if (i != notificationCount) {
                    scheduleTime = startMillis + (notificationInterval * (i - 1));
                    scheduleAmount = averageAmount * i;
                } else {
                    scheduleTime = endMillis;
                    scheduleAmount = dailyTarget;
                }

                // 今日分で通知予定時刻>現在時刻かつ予定給水量>現在総給水量もしくは明日分なら通知予定リストに追加
                if ((addDay == 0 && scheduleTime > baseMillis && scheduleAmount > nowTotalAmount) ||
                        addDay == 1) {
                    NotificationSchedule schedule = new NotificationSchedule(new Date(scheduleTime),
                            scheduleAmount, context.getResources().getString(R.string.notification_message));
                    result.add(schedule);
                }
            }
        }
        return result;
    }

    private static long toMillis(Calendar baseCalendar, String time) {
        String hourMinute[] = time.split(":");
        Calendar calendar = Calendar.getInstance(Locale.JAPAN);
        calendar.set(baseCalendar.get(Calendar.YEAR),
                baseCalendar.get(Calendar.MONTH),
                baseCalendar.get(Calendar.DAY_OF_MONTH),
                Integer.parseInt(hourMinute[0]),
                Integer.parseInt(hourMinute[1]), 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private static void updateNotification(List<NotificationSchedule> schedules,
                                           Context context) throws ParseException {
        deleteNotification(context);
        addNotification(schedules, context);
    }

    private static void deleteNotification(Context context) throws ParseException {

        AlarmManager alarmManager = getAlarmManager(context);

        List<NotificationSchedule> schedules = NotificationScheduleDao.find(context);
        for (int position = 0; position < schedules.size(); position++) {
            NotificationSchedule schedule = schedules.get(position);

            Intent intent = new Intent(context, NotificationReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(
                    context, position, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (sender != null) {
                alarmManager.cancel(sender);
            }
        }
    }

    private static void addNotification(List<NotificationSchedule> schedules,
                                        Context context) {
        AlarmManager alarmManager = getAlarmManager(context);

        for (int position = 0; position < schedules.size(); position++) {
            NotificationSchedule schedule = schedules.get(position);

            Intent intent = new Intent(context, NotificationReceiver.class);

            PendingIntent sender = PendingIntent.getBroadcast(
                    context, position, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            long scheduleMillis = schedule.getScheduleTime().getTime();

            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, scheduleMillis, sender);
        }
    }

    private static AlarmManager alarmManager = null;

    private static AlarmManager getAlarmManager(Context context) {
        if (alarmManager == null) {
            return (AlarmManager) context.getSystemService
                    (Context.ALARM_SERVICE);
        } else {
            return alarmManager;
        }
    }
}
