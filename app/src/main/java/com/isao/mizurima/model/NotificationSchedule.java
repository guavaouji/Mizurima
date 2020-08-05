package com.isao.mizurima.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationSchedule {

    public static SimpleDateFormat getSimpleDateFormat() {
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS", Locale.JAPAN);
        return sdf;
    }

    private Date scheduleTime = null;
    private int totalWaterSupplyAmount = 0;
    private String message = "";

    public NotificationSchedule(Date scheduleTime, int totalWaterSupplyAmount, String message) {
        this.scheduleTime = scheduleTime;
        this.totalWaterSupplyAmount = totalWaterSupplyAmount;
        this.message = message;
    }

    public Date getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(Date scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public int getTotalWaterSupplyAmount() {
        return totalWaterSupplyAmount;
    }

    public void setTotalWaterSupplyAmount(int totalWaterSupplyAmount) {
        this.totalWaterSupplyAmount = totalWaterSupplyAmount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
