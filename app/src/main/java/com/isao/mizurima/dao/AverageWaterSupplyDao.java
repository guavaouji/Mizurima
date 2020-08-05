package com.isao.mizurima.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AverageWaterSupplyDao {

    public static String getSharedPreferencesKey() {
        return "average_water_supply";
    }


    public static boolean isExists(Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.contains(getSharedPreferencesKey());
    }

    /**
     * SharedPreferencesからAverageWaterSupplyを取得する
     *
     * @param context
     * @return AverageWaterSupply※なければ0
     */
    public static int find(Context context) {
        int notExists = 0;
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(getSharedPreferencesKey(), notExists);
    }

    /**
     * SharedPreferencesにAverageWaterSupplyを保存する
     *
     * @param averageWaterSupply
     * @param context
     */
    public static void save(int averageWaterSupply, Context context) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getSharedPreferencesKey(), averageWaterSupply);
        editor.apply();
    }
}
