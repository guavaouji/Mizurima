package com.isao.mizurima.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.isao.mizurima.R;
import com.isao.mizurima.activity.DeleteActivity;
import com.isao.mizurima.dao.WaterSupplyHistoryDao;
import com.isao.mizurima.model.WaterSupplyHistory;
import com.isao.mizurima.utils.ConfirmationUtils;
import com.isao.mizurima.utils.Constants;
import com.isao.mizurima.utils.LayoutUtils;
import com.isao.mizurima.utils.NotificationUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class HistoryFragment extends Fragment {

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.JAPAN);

    private RegistrationFragment registrationFragment;
    private SettingsFragment settingsFragment;
    private static final int REQUEST_CODE = 1001;

    public HistoryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.history_fragment, container,
                false);
        // 総量
        List<WaterSupplyHistory> historyList = null;
        try {
            historyList = WaterSupplyHistoryDao.findToday(getContext());
        } catch (Exception e) {
            ConfirmationUtils.showFailureMessage(getContext().getString(
                    R.string.history_find_failed_message),
                    getContext());
        }

        updateTotalAmountTextView(rootView, historyList);

        // 履歴の設定
        LinearLayout baseHistoryLinearLayout = rootView.findViewById(R.id.base_histories_linearLayout);
        LinearLayout[] historyLinearLayout = new LinearLayout[historyList.size()];
        AppCompatTextView[] createTimeTextView = new AppCompatTextView[historyList.size()];
        AppCompatTextView[] amountTextView = new AppCompatTextView[historyList.size()];
        ImageView[] deleteImageView = new ImageView[historyList.size()];


        for (int i = 0; i < historyList.size(); i++) {
            // 1レコード分のLinearLayout（横並べ）
            historyLinearLayout[i] = new LinearLayout(getContext());
            historyLinearLayout[i].setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            layoutParams.topMargin = 5;
            layoutParams.bottomMargin = 5;
            historyLinearLayout[i].setLayoutParams(layoutParams);

            // 作成時刻テキスト
            // TODO:layout.xmlから取得
            createTimeTextView[i] = new AppCompatTextView(getContext());
            String createTimeText = dateFormat.format(historyList.get(i).getCreateTime());
            createTimeTextView[i].setText(createTimeText);
            layoutTextView(createTimeTextView[i], 25, 30);

            // 給水量テキスト
            amountTextView[i] = new AppCompatTextView(getContext());
            String amountText = String.format(getResources().getString(
                    R.string.history_amount_label), historyList.get(i).getWaterSupplyAmount());
            amountTextView[i].setText(amountText);
            layoutTextView(amountTextView[i], 45, 30);

            // 削除イメージ
            deleteImageView[i] = new ImageView(getContext());
            // Tagを設定
            String tag = new StringBuilder(WaterSupplyHistory.getSimpleDateFormat().format(
                    historyList.get(i).getCreateTime()))
                    .append(";")
                    .append(amountText).toString();
            deleteImageView[i].setTag(tag);
            deleteImageView[i].setImageResource(R.drawable.baseline_delete_white_24);

            LinearLayout.LayoutParams deleteImageLayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f);
            deleteImageLayoutParams.gravity = Gravity.CENTER;
            deleteImageView[i].setLayoutParams(deleteImageLayoutParams);

            // 画面の一番上に移動
            focusTop(rootView);

            // OnClickListenerを設定
            deleteImageView[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 遅延確認
                    Intent intent = new Intent(getContext(),
                            DeleteActivity.class);

                    String[] tagParts = ((String) view.getTag()).split(";");

                    intent.putExtra(Constants.DELETE_HISTORY_CREATE_TIME, tagParts[0]);
                    intent.putExtra(Constants.DELETE_HISTORY_WATER_AMOUNT, tagParts[1]);

                    startActivityForResult(intent, REQUEST_CODE);
                }
            });

            historyLinearLayout[i].addView(createTimeTextView[i]);
            historyLinearLayout[i].addView(amountTextView[i]);
            historyLinearLayout[i].addView(deleteImageView[i]);
            baseHistoryLinearLayout.addView(historyLinearLayout[i]);
        }


        // 画面遷移：RegistrationFragment
        ImageView toRegistrationImageView = rootView.findViewById(
                R.id.from_history_to_registration_imageView);
        toRegistrationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (registrationFragment == null) {
                    registrationFragment = new RegistrationFragment();
                }

                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, registrationFragment);
                    fragmentTransaction.commit();
                }
            }
        });

        // 画面遷移：SettingsFragment
        ImageView toSettingsImageView = rootView.findViewById(
                R.id.from_history_to_settings_imageView);
        toSettingsImageView.setOnClickListener(new View.OnClickListener() {
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

    private void layoutTextView(AppCompatTextView textView, int widthDp, int heightDp) {
        textView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LayoutUtils.dpToPx(widthDp, getContext()),
                LayoutUtils.dpToPx(heightDp, getContext()),
                1f);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.leftMargin = 5;
        layoutParams.rightMargin = 5;
        textView.setLayoutParams(layoutParams);
        textView.setAutoSizeTextTypeUniformWithConfiguration(
                12, 22, 2, TypedValue.COMPLEX_UNIT_SP);
    }

    private void updateTotalAmountTextView(View rootView, List<WaterSupplyHistory> historyList) {
        TextView totalAmountTextView = rootView.findViewById(
                R.id.total_amount_textView);
        int totalAmount = 0;
        for (WaterSupplyHistory history : historyList) {
            totalAmount = totalAmount + history.getWaterSupplyAmount();
        }
        totalAmountTextView.setText(String.format(getResources().getString(
                R.string.history_amount_label), totalAmount));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_CODE || resultCode != RESULT_OK ||
                data == null || !data.hasExtra(Constants.DELETE_HISTORY_CREATE_TIME) ||
                !data.hasExtra(Constants.DELETE_HISTORY_WATER_AMOUNT)) {
            return;
        }

        String deleteCreateTime = data.getStringExtra(Constants.DELETE_HISTORY_CREATE_TIME);
        String deleteWaterAmount = data.getStringExtra(Constants.DELETE_HISTORY_WATER_AMOUNT);

        int index = 0;
        final List<WaterSupplyHistory> historyList = new ArrayList<>();
        try {
            historyList.addAll(WaterSupplyHistoryDao.find(getContext()));

            for (WaterSupplyHistory history : historyList) {
                String createTime = WaterSupplyHistory.getSimpleDateFormat().
                        format(history.getCreateTime());
                if (deleteCreateTime.equals(createTime)) {
                    historyList.remove(index);
                    break;
                }
                index = index + 1;
            }
            WaterSupplyHistoryDao.save(historyList, getContext());
        } catch (Exception e) {
            ConfirmationUtils.showFailureMessage(getContext().getString(
                    R.string.history_delete_failed_message),
                    getContext());
        }
        final int deleteIndex = index;

        Date deleteCreateDate = null;
        try {
            deleteCreateDate = WaterSupplyHistory.getSimpleDateFormat().parse(deleteCreateTime);
        } catch (ParseException e) {
            ConfirmationUtils.showFailureMessage(getString(
                    R.string.history_delete_failed_message), getContext());
        }
        StringBuilder sb = new StringBuilder(HistoryFragment.dateFormat.format(deleteCreateDate))
                .append("　")
                .append(deleteWaterAmount)
                .append("\n")
                .append(getString(R.string.history_delete_success_message));

        ConfirmationUtils.showSuccessMessage(sb.toString(), getContext());
        // 通知を更新
        try {
            NotificationUtils.update(getContext(), 0, 1);
        } catch (Exception e) {
            ConfirmationUtils.showFailureMessage(getContext().getString(
                    R.string.notification_update_failed_message),
                    getContext());
        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // 画面表示を更新
                LinearLayout baseHistoryLinearLayout = getView().findViewById(
                        R.id.base_histories_linearLayout);
                baseHistoryLinearLayout.removeViewAt(deleteIndex);
                try {
                    updateTotalAmountTextView(getView(), WaterSupplyHistoryDao.findToday(getContext()));
                    focusTop(getView());
                } catch (Exception e) {
                    ConfirmationUtils.showFailureMessage(getContext().getString(
                            R.string.history_find_failed_message),
                            getContext());
                }
            }
        }, 500);
    }

    private void focusTop(View view) {
        TextView top = view.findViewById(R.id.history_container_top_textView);
        top.clearFocus();
        top.requestFocus();
    }

}
