package com.isao.mizurima.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class NotificationStartTimeDao {

    public static String getSharedPreferencesKey() {
        return "notification_start_time";
    }

    public static boolean isExists(Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.contains(getSharedPreferencesKey());
    }

    /**
     * SharedPreferencesからNotificationStartTimeを取得する
     *
     * @param context
     * @return NotificationStartTime※なければ""
     */
    public static String find(Context context) {
        String notExists = "";
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(getSharedPreferencesKey(), notExists);
    }

    /**
     * SharedPreferencesにNotificationStartTimeを保存する
     *
     * @param notificationStartTime
     * @param context
     */
    public static void save(String notificationStartTime, Context context) {
        if (notificationStartTime != null) {
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getSharedPreferencesKey(), notificationStartTime);
            editor.apply();
        }
    }
}
