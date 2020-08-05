package com.isao.mizurima.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.wear.widget.CircularProgressLayout;

import com.isao.mizurima.R;
import com.isao.mizurima.fragments.HistoryFragment;
import com.isao.mizurima.model.WaterSupplyHistory;
import com.isao.mizurima.utils.ConfirmationUtils;
import com.isao.mizurima.utils.Constants;

import java.text.ParseException;
import java.util.Date;

public class DeleteActivity extends WearableActivity
        implements CircularProgressLayout.OnTimerFinishedListener,
        View.OnClickListener {

    private CircularProgressLayout circularProgressLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);
        circularProgressLayout = findViewById(R.id.delayed_confirmation);
        circularProgressLayout.setOnTimerFinishedListener(this);
        circularProgressLayout.setOnClickListener(this);
        circularProgressLayout.setTotalTime(2000);
        circularProgressLayout.startTimer();

        TextView textView = findViewById(R.id.confirmation_history_textView);

        String deleteCreateTime = getIntent().getStringExtra(Constants.DELETE_HISTORY_CREATE_TIME);
        String deleteWaterAmount = getIntent().getStringExtra(Constants.DELETE_HISTORY_WATER_AMOUNT);

        Date deleteCreateDate = null;
        try {
            deleteCreateDate = WaterSupplyHistory.getSimpleDateFormat().parse(deleteCreateTime);
        } catch (ParseException e) {
            ConfirmationUtils.showFailureMessage(getString(
                    R.string.history_delete_failed_message), this);
        }

        StringBuilder sb = new StringBuilder(HistoryFragment.dateFormat.format(deleteCreateDate))
                .append("　")
                .append(deleteWaterAmount)
                .append("\n")
                .append(getString(R.string.confirmation_history_delete_message));
        textView.setText(sb.toString());
    }

    @Override
    public void onTimerFinished(CircularProgressLayout layout) {

        String deleteCreateTime = getIntent().getStringExtra(Constants.DELETE_HISTORY_CREATE_TIME);
        String deleteWaterAmount = getIntent().getStringExtra(Constants.DELETE_HISTORY_WATER_AMOUNT);

        Intent intent = new Intent();
        intent.putExtra(Constants.DELETE_HISTORY_CREATE_TIME, deleteCreateTime);
        intent.putExtra(Constants.DELETE_HISTORY_WATER_AMOUNT, deleteWaterAmount);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        ConfirmationUtils.showSuccessMessage(getString(R.string.cancel_message), this);
        circularProgressLayout.stopTimer();
        finish();
    }
}
