package com.isao.mizurima.dao;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.isao.mizurima.model.WaterSupplyHistory;

import net.grandcentrix.tray.AppPreferences;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WaterSupplyHistoryDao {

    public static String getSharedPreferencesKey() {
        return "water_supply_history_list";
    }

    public static boolean isExists(Context context) {

        final AppPreferences appPreferences = new AppPreferences(context);
        return appPreferences.contains(getSharedPreferencesKey());

    }

    /**
     * SharedPreferencesからWaterSupplyHistoryListを取得する
     *
     * @param context
     * @return WaterSupplyHistoryList
     */
    public static List<WaterSupplyHistory> find(Context context) throws ParseException {

        List<WaterSupplyHistory> result = null;
        String notExists = "";

        final AppPreferences appPreferences = new AppPreferences(context);
        String scsv = appPreferences.getString(getSharedPreferencesKey(), notExists);

        if (scsv.equals(notExists)) {
            result = new ArrayList<>();
        } else {
            result = from(scsv);
        }
        return result;
    }

    private static List<WaterSupplyHistory> from(@NonNull String scsv) throws ParseException {
        // ;(セミコロン)で分割してWaterSupplyHistory単位のString配列にする
        List<WaterSupplyHistory> result = new ArrayList<>();
        String[] historyByCsvArray = scsv.split(";");
        for (String historyByCsv : historyByCsvArray) {
            String[] history = historyByCsv.split(",");
            WaterSupplyHistory waterSupplyHistory = new WaterSupplyHistory(
                    Integer.parseInt(history[0]),
                    WaterSupplyHistory.getSimpleDateFormat().parse(history[1]));
            result.add(waterSupplyHistory);
        }
        return result;
    }

    /**
     * SharedPreferencesにWaterSupplyHistoryListを保存する
     *
     * @param historyList
     * @param context
     */
    public static void save(List<WaterSupplyHistory> historyList, Context context) {
        if (historyList != null) {

            final AppPreferences appPreferences = new AppPreferences(context);
            appPreferences.put(getSharedPreferencesKey(), toScsv(historyList));
        }
    }

    private static String toScsv(@NonNull List<WaterSupplyHistory> historyList) {
        List<String> historyByCsvList = new ArrayList<>();
        for (WaterSupplyHistory history : historyList) {
            String historyByCsv = String.valueOf(history.getWaterSupplyAmount()) + "," +
                    WaterSupplyHistory.getSimpleDateFormat().format(history.getCreateTime());
            historyByCsvList.add(historyByCsv);
        }
        return TextUtils.join(";", historyByCsvList);
    }

    public static int findTodayTotalWaterSupplyAmount(Context context) throws ParseException {
        int result = 0;
        Calendar todayStart = getTodayStart();
        Calendar todayEnd = getTodayEnd();

        final AppPreferences appPreferences = new AppPreferences(context);
        List<WaterSupplyHistory> histories = find(context);
        for (WaterSupplyHistory history : histories) {
            // 登録日時今日に限定する
            if (todayStart.getTimeInMillis() <= history.getCreateTime().getTime() &&
                    history.getCreateTime().getTime() <= todayEnd.getTimeInMillis()) {
                result = result + history.getWaterSupplyAmount();
            }
        }
        return result;
    }

    private static Calendar getTodayEnd() {
        return getToday(23, 59, 59, 999);
    }

    private static Calendar getTodayStart() {
        return getToday(0, 0, 0, 0);
    }

    private static Calendar getToday(int hour, int minute, int second, int millis) {
        Calendar today = Calendar.getInstance(Locale.JAPAN);
        today.set(Calendar.HOUR_OF_DAY, hour);
        today.set(Calendar.MINUTE, minute);
        today.set(Calendar.SECOND, second);
        today.set(Calendar.MILLISECOND, millis);
        return today;
    }

    public static List<WaterSupplyHistory> findToday(Context context) throws ParseException {
        List<WaterSupplyHistory> result = new ArrayList<>();
        Calendar todayStart = getTodayStart();
        Calendar todayEnd = getTodayEnd();
        List<WaterSupplyHistory> allHistories = find(context);

        for (WaterSupplyHistory history : allHistories) {
            if (todayStart.getTimeInMillis() <= history.getCreateTime().getTime() &&
                    history.getCreateTime().getTime() <= todayEnd.getTimeInMillis()) {
                result.add(history);
            }
        }
        return result;
    }


}
