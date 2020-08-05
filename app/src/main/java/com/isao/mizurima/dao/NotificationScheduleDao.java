package com.isao.mizurima.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.isao.mizurima.model.NotificationSchedule;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NotificationScheduleDao {

    public static String getSharedPreferencesKey() {
        return "notification_schedule_list";
    }

    public static boolean isExists(Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.contains(getSharedPreferencesKey());
    }

    /**
     * SharedPreferencesからNotificationScheduleListを取得する
     *
     * @param context
     * @return NotificationScheduleList
     */
    public static List<NotificationSchedule> find(Context context) throws ParseException {

        List<NotificationSchedule> result = null;
        String notExists = "";

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        String scsv = sharedPreferences.getString(getSharedPreferencesKey(), notExists);

        if (scsv.equals(notExists)) {
            result = new ArrayList<>();
        } else {
            result = from(scsv);
        }
        return result;
    }

    private static List<NotificationSchedule> from(@NonNull String scsv) throws ParseException {
        // ;(セミコロン)で分割してNotificationSchedule単位のString配列にする
        List<NotificationSchedule> result = new ArrayList<>();
        String[] scheduleByCsvArray = scsv.split(";");
        for (String scheduleByCsv : scheduleByCsvArray) {
            String[] schedule = scheduleByCsv.split(",");
            NotificationSchedule notificationSchedule = new NotificationSchedule(
                    NotificationSchedule.getSimpleDateFormat().parse(schedule[0]),
                    Integer.parseInt(schedule[1]), schedule[2]);
            result.add(notificationSchedule);
        }
        return result;
    }

    /**
     * SharedPreferencesにNotificationScheduleListを保存する
     *
     * @param scheduleList
     * @param context
     */
    public static void save(List<NotificationSchedule> scheduleList, Context context) {
        if (scheduleList != null) {
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getSharedPreferencesKey(), toScsv(scheduleList));
            editor.apply();
        }
    }

    private static String toScsv(@NonNull List<NotificationSchedule> scheduleList) {
        List<String> scheduleByCsvList = new ArrayList<>();
        for (NotificationSchedule schedule : scheduleList) {
            String scheduleByCsv = NotificationSchedule.getSimpleDateFormat().format(
                    schedule.getScheduleTime()) + "," +
                    schedule.getTotalWaterSupplyAmount() + "," +
                    schedule.getMessage();
            scheduleByCsvList.add(scheduleByCsv);
        }
        return TextUtils.join(";", scheduleByCsvList);
    }

    /**
     * 今日の次の通知予定を取得
     *
     * @param nowTotalAmount 現在総給水量
     * @param context
     * @return 次の通知予定※なければnull
     * @throws ParseException
     */
    public static NotificationSchedule findTodayNextNotificationSchedule(
            int nowTotalAmount, Context context) throws ParseException {

        // 現在時刻
        long nowMillis = Calendar.getInstance(Locale.JAPAN).getTimeInMillis();
        // 今日の23:59:59.999
        Calendar todayEndTime = Calendar.getInstance(Locale.JAPAN);
        todayEndTime.set(Calendar.HOUR_OF_DAY, 23);
        todayEndTime.set(Calendar.MINUTE, 59);
        todayEndTime.set(Calendar.SECOND, 59);
        todayEndTime.set(Calendar.MILLISECOND, 999);

        NotificationSchedule result = null;
        for (NotificationSchedule schedule : find(context)) {
            // 直近で通知予定時刻<=今日の最終時刻かつ通知予定時刻>現在時刻かつ予定給水量>現在総給水量
            if (schedule.getScheduleTime().getTime() <= todayEndTime.getTimeInMillis() &&
                    schedule.getScheduleTime().getTime() > nowMillis &&
                    schedule.getTotalWaterSupplyAmount() > nowTotalAmount) {
                result = schedule;
                break;
            }
        }
        return result;
    }

}
