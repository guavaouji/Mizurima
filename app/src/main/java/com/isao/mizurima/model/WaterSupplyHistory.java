package com.isao.mizurima.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WaterSupplyHistory {

    public static SimpleDateFormat getSimpleDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS",
                Locale.JAPAN);
        return dateFormat;
    }

    private int waterSupplyAmount = 0;
    private Date createTime = null;

    public WaterSupplyHistory(int waterSupplyAmount, Date createTime) {
        this.waterSupplyAmount = waterSupplyAmount;
        this.createTime = createTime;
    }

    public int getWaterSupplyAmount() {
        return waterSupplyAmount;
    }

    public void setWaterSupplyAmount(int waterSupplyAmount) {
        this.waterSupplyAmount = waterSupplyAmount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
