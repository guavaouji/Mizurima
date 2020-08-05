package com.isao.mizurima.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.isao.mizurima.R;
import com.isao.mizurima.dao.AverageWaterSupplyDao;
import com.isao.mizurima.dao.DailyTargetDao;
import com.isao.mizurima.dao.NotificationEndTimeDao;
import com.isao.mizurima.dao.NotificationStartTimeDao;
import com.isao.mizurima.dao.WaterSupplyUnitDao;
import com.isao.mizurima.fragments.RegistrationFragment;
import com.isao.mizurima.fragments.SettingsFragment;

public class MainActivity extends FragmentActivity {

    private RegistrationFragment registrationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 通知チャンネル作成
        createNotificationChannel();

        if (isFirstRun()) {
            // 設定Fragment起動
            SettingsFragment settingsFragment = new SettingsFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, settingsFragment)
                    .commit();
        } else {
            // 登録Fragment起動
            if (registrationFragment == null) {
                registrationFragment = new RegistrationFragment();
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, registrationFragment)
                    .commit();
        }
    }

    /**
     * インストール後の初回起動判定
     *
     * @return true：初回、false：その他
     */
    public boolean isFirstRun() {
        return
                !(DailyTargetDao.isExists(getApplicationContext()) &&
                        AverageWaterSupplyDao.isExists(getApplicationContext()) &&
                        WaterSupplyUnitDao.isExists(getApplicationContext()) &&
                        NotificationStartTimeDao.isExists(getApplicationContext()) &&
                        NotificationEndTimeDao.isExists(getApplicationContext()));
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = getString(R.string.mizurima_channel_id);
            CharSequence name = getString(R.string.mizurima_channel_name);
            String description = getString(R.string.mizurima_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}