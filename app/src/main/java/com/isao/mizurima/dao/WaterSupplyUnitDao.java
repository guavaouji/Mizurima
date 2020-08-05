package com.isao.mizurima.dao;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.isao.mizurima.R;

import net.grandcentrix.tray.AppPreferences;

import java.util.ArrayList;
import java.util.List;

public class WaterSupplyUnitDao {

    public static String getSharedPreferencesKey() {
        return "water_supply_unit_list";
    }

    public static boolean isExists(Context context) {
        final AppPreferences appPreferences = new AppPreferences(context);
        return appPreferences.contains(getSharedPreferencesKey());
    }

    /**
     * SharedPreferencesからWaterSupplyUnitListを取得する
     *
     * @param context
     * @return WaterSupplyUnitList
     */
    public static List<Integer> find(Context context) {

        List<Integer> result = null;
        String notExists = "";

        final AppPreferences appPreferences = new AppPreferences(context);
        String csv = appPreferences.getString(getSharedPreferencesKey(), notExists);
        if (csv.equals(notExists)) {
            result = new ArrayList<Integer>();
        } else {
            result = from(csv);
        }
        return result;
    }

    private static List<Integer> from(String csv) {
        List<Integer> result = new ArrayList<Integer>();
        String[] waterSupplyUnits = csv.split(",");
        for (String waterSupplyUnit : waterSupplyUnits) {
            result.add(Integer.parseInt(waterSupplyUnit));
        }
        return result;
    }

    /**
     * SharedPreferencesにaverageWaterSupplyAmountと一番上に置き換えた
     * average_water_supply_arrayをWaterSupplyUnitListに保存する
     *
     * @param averageWaterSupplyAmount
     * @param context
     */
    public static void save(int averageWaterSupplyAmount, Context context) {

        List<Integer> currentList = find(context);
        if(currentList.isEmpty()){
            int[] defaultArrays = context.getResources()
                    .getIntArray(R.array.average_water_supply_array);
               for (int i = 0; i < defaultArrays.length; i++){
                   currentList.add(defaultArrays[i]);
            }
        }

        int averageWaterSupplyAmountPosition = 0;

        List<Integer> resultList = new ArrayList<>();
        for (int i = 0; i < currentList.size(); i++) {
            if (averageWaterSupplyAmount == currentList.get(i)) {
                averageWaterSupplyAmountPosition = i;
            }
            resultList.add(currentList.get(i));
        }
        resultList.remove(averageWaterSupplyAmountPosition);
        resultList.add(0, averageWaterSupplyAmount);

        final AppPreferences appPreferences = new AppPreferences(context);
        appPreferences.put(getSharedPreferencesKey(), toCsv(resultList));
    }

    private static String toCsv(@NonNull List<Integer> waterSupplyUnitList) {
        return TextUtils.join(",", waterSupplyUnitList);
    }


}
