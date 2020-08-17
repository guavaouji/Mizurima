package com.isao.mizurima.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.isao.mizurima.fragments.RegistrationFragment;
import com.isao.mizurima.utils.Constants;

public class AddWaterSupplyAmountFromNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int waterSupplyAmount = intent.getIntExtra(Constants.ADD_WATER_SUPPLY_AMOUNT, 0);
        Log.d("intent", "waterSupplyAmount:" + waterSupplyAmount);
        if (waterSupplyAmount > 0) {
            RegistrationFragment.addWaterSupplyHistoryAndUpdateNotification(waterSupplyAmount, context);
        }
    }
}
