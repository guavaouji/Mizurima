package com.isao.mizurima.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class NotificationEndTimeDao {

    public static String getSharedPreferencesKey() {
        return "notification_end_time";
    }

    public static boolean isExists(Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.contains(getSharedPreferencesKey());
    }

    /**
     * SharedPreferencesからNotificationEndTimeを取得する
     *
     * @param context
     * @return NotificationEndTime※なければ""
     */
    public static String find(Context context) {
        String notExists = "";
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(getSharedPreferencesKey(), notExists);
    }

    /**
     * SharedPreferencesにNotificationEndTimeを保存する
     *
     * @param notificationEndTime
     * @param context
     */
    public static void save(String notificationEndTime, Context context) {
        if (notificationEndTime != null) {
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getSharedPreferencesKey(), notificationEndTime);
            editor.apply();
        }
    }
}
