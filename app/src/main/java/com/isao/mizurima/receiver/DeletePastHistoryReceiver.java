package com.isao.mizurima.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.isao.mizurima.dao.WaterSupplyHistoryDao;
import com.isao.mizurima.model.WaterSupplyHistory;

import java.util.List;

public class DeletePastHistoryReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 給水履歴の今日の履歴のみを保存
        try {
            List<WaterSupplyHistory> histories = WaterSupplyHistoryDao.findToday(context);
            WaterSupplyHistoryDao.save(histories, context);
        } catch (Exception e) {
        }
    }
}
