package com.isao.mizurima.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.isao.mizurima.R;
import com.isao.mizurima.dao.DailyTargetDao;
import com.isao.mizurima.dao.NotificationScheduleDao;
import com.isao.mizurima.dao.WaterSupplyHistoryDao;
import com.isao.mizurima.dao.WaterSupplyUnitDao;
import com.isao.mizurima.model.NotificationSchedule;
import com.isao.mizurima.model.WaterSupplyHistory;
import com.isao.mizurima.utils.ConfirmationUtils;
import com.isao.mizurima.utils.NotificationUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RegistrationFragment extends Fragment {

    public RegistrationFragment() {
    }

    private HistoryFragment historyFragment;
    private SettingsFragment settingsFragment;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.registration_fragment, container,
                false);

        updateUI(rootView);


        // 画面遷移：HistoryFragment
        ImageView toHistoryImageView = rootView.findViewById(
                R.id.from_registration_to_history_imageView);
        toHistoryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (historyFragment == null) {
                    historyFragment = new HistoryFragment();
                }

                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, historyFragment);
                    fragmentTransaction.commit();
                }
            }
        });

        // 画面遷移：SettingsFragment
        ImageView toSettingImageView = rootView.findViewById(
                R.id.from_registration_to_settings_imageView);
        toSettingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (settingsFragment == null) {
                    settingsFragment = new SettingsFragment();
                }

                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, settingsFragment);
                    fragmentTransaction.commit();
                }
            }
        });

        return rootView;
    }

    public static void addWaterSupplyHistoryAndUpdateNotification(int waterSupplyAmount, Context context) {
        try {
            WaterSupplyHistory history = new WaterSupplyHistory(
                    waterSupplyAmount, new Date());
            List<WaterSupplyHistory> historyList = WaterSupplyHistoryDao
                    .find(context);
            historyList.add(0, history);
            WaterSupplyHistoryDao.save(historyList, context);
        } catch (Exception e) {
            ConfirmationUtils.showFailureMessage(context.getString(
                    R.string.water_supply_registration_failed_message),
                    context);
        }
        ConfirmationUtils.showSuccessMessage(String.format(context.getString(
                R.string.history_amount_label), waterSupplyAmount) + "\n" +
                        context.getString(R.string.water_supply_registration_success_message),
                context);
        // 通知を更新
        try {
            // 今日、明日の通知のスケジュール更新
            NotificationUtils.update(context, 0, 1);
        } catch (Exception e) {
            ConfirmationUtils.showFailureMessage(context.getString(
                    R.string.notification_update_failed_message),
                    context);
        }
    }

    private void updateUI(View rootView) {
        int totalAmount = 0;
        NotificationSchedule nextSchedule = null;
        try {
            totalAmount = WaterSupplyHistoryDao.findTodayTotalWaterSupplyAmount(getContext());
            nextSchedule = NotificationScheduleDao.
                    findTodayNextNotificationSchedule(totalAmount, getContext());
        } catch (Exception e) {
            ConfirmationUtils.showFailureMessage(getContext().getString(
                    R.string.history_find_failed_message),
                    getContext());
        }

        TextView toNotificationTimeTextView = rootView.findViewById(
                R.id.to_notification_time_textView);
        if (nextSchedule == null) {
            toNotificationTimeTextView.setText("");
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.JAPAN);
            String toNotificationTimeLabel = String.format(getResources().getString(
                    R.string.to_notification_time_label), dateFormat.format(nextSchedule.
                    getScheduleTime()));
            toNotificationTimeTextView.setText(toNotificationTimeLabel);
        }

        TextView requiredWaterSupplyTextView = rootView.findViewById(
                R.id.required_water_supply_textView);
        if (nextSchedule == null) {
            int dailyTarget = DailyTargetDao.find(getContext());
            if (dailyTarget <= totalAmount) {
                requiredWaterSupplyTextView.setText(getResources().getString(
                        R.string.target_achievement_label));
            } else {
                String requiredWaterSupplyLabel = String.format(getResources().getString(
                        R.string.required_water_supply_label), dailyTarget - totalAmount);
                requiredWaterSupplyTextView.setText(requiredWaterSupplyLabel);
            }
        } else {
            String requiredWaterSupplyLabel = String.format(getResources().getString(
                    R.string.required_water_supply_label),
                    nextSchedule.getTotalWaterSupplyAmount() - totalAmount);
            requiredWaterSupplyTextView.setText(requiredWaterSupplyLabel);
        }

        // Button[]の設定
        LinearLayout buttonsLinearLayout = rootView.findViewById(R.id.buttons_linearLayout);
        buttonsLinearLayout.removeAllViews();
        List<Integer> waterSupplyUnits = WaterSupplyUnitDao.find(getContext());

        Button[] button = new Button[waterSupplyUnits.size()];
        for (int i = 0; i < button.length; i++) {
            button[i] = new Button(getContext());
            // Tagを設定
            button[i].setTag(waterSupplyUnits.get(i));
            String buttonLabel = String.format(getResources().getString(
                    R.string.water_supply_button_label), waterSupplyUnits.get(i));
            button[i].setText(buttonLabel);
            button[i].setAllCaps(false);
            button[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            buttonsLinearLayout.addView(button[i]);

            // OnClickListenerを設定
            button[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int waterSupplyAmount = (Integer) view.getTag();
                    // 履歴追加と通知更新
                    addWaterSupplyHistoryAndUpdateNotification(waterSupplyAmount, getContext());
                    // sharedのwater_supply_unit_listを更新
                    WaterSupplyUnitDao.save(waterSupplyAmount, getContext());
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateUI(getView());
                        }
                    }, 500);
                }
            });
        }

        TextView top = rootView.findViewById(R.id.registration_container_top_textView);
        top.clearFocus();
        top.requestFocus();
    }


}
