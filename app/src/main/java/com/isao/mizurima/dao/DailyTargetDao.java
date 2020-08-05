package com.isao.mizurima.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DailyTargetDao {

    public static String getSharedPreferencesKey() {
        return "daily_target";
    }

    public static boolean isExists(Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.contains(getSharedPreferencesKey());
    }

    /**
     * SharedPreferencesからDailyTargetを取得する
     *
     * @param context
     * @return DailyTarget※なければ0
     */
    public static int find(Context context) {
        int notExists = 0;
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(getSharedPreferencesKey(), notExists);
    }

    /**
     * SharedPreferencesにDailyTargetを保存する
     *
     * @param dailyTarget
     * @param context
     */
    public static void save(int dailyTarget, Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getSharedPreferencesKey(), dailyTarget);
        editor.apply();

    }
}
