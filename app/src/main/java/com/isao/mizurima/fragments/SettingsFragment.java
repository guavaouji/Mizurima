package com.isao.mizurima.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.isao.mizurima.R;
import com.isao.mizurima.dao.AverageWaterSupplyDao;
import com.isao.mizurima.dao.DailyTargetDao;
import com.isao.mizurima.dao.NotificationEndTimeDao;
import com.isao.mizurima.dao.NotificationStartTimeDao;
import com.isao.mizurima.dao.WaterSupplyUnitDao;
import com.isao.mizurima.utils.ConfirmationUtils;
import com.isao.mizurima.utils.DailyTaskUtils;
import com.isao.mizurima.utils.NotificationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsFragment extends Fragment
        implements AdapterView.OnItemSelectedListener {

    private RegistrationFragment registrationFragment;
    private HistoryFragment historyFragment;

    public SettingsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.settings_fragment, container, false);

        // sharedになければinit.xmlからsharedに追加
        // 1日の目標量
        int dailyTarget;
        if (DailyTargetDao.isExists(getContext())) {
            dailyTarget = DailyTargetDao.find(getContext());
        } else {
            dailyTarget = getResources().getInteger(R.integer.daily_target_init);
            DailyTargetDao.save(dailyTarget, getContext());
        }

        // 1度に飲む平均量
        int averageWaterSupply;
        if (AverageWaterSupplyDao.isExists(getContext())) {
            averageWaterSupply = AverageWaterSupplyDao.find(getContext());
        } else {
            averageWaterSupply = getResources().getInteger(R.integer.average_water_supply_init);
            AverageWaterSupplyDao.save(averageWaterSupply, getContext());
        }

        // 給水ボタンリストを更新
        WaterSupplyUnitDao.save(averageWaterSupply, getContext());
        List<Integer> unitList = WaterSupplyUnitDao.find(getContext());

        // 通知開始時刻
        String startTime;
        if (NotificationStartTimeDao.isExists(getContext())) {
            startTime = NotificationStartTimeDao.find(getContext());
        } else {
            startTime = getResources().getString(R.string.notification_start_time_init);
            NotificationStartTimeDao.save(startTime, getContext());
        }

        // 通知最終時刻
        String endTime;
        if (NotificationEndTimeDao.isExists(getContext())) {
            endTime = NotificationEndTimeDao.find(getContext());
        } else {
            endTime = getResources().getString(R.string.notification_end_time_init);
            NotificationEndTimeDao.save(endTime, getContext());
        }

        // 各UIコンポーネント設定
        // 1日の目標量[mL]
        createSpinnerForIntItemArray(rootView, R.id.daily_target_spinner,
                R.array.daily_target_array, dailyTarget);

        // 1度に飲む平均量[mL]
        createSpinnerForIntItemArray(rootView, R.id.average_water_supply_spinner,
                R.array.average_water_supply_array, averageWaterSupply);

        // 通知開始時刻
        createSpinnerForNotificationStartTime(rootView,
                R.id.notification_start_time_spinner, R.array.notification_time_array,
                startTime, endTime);

        // 通知最終時刻
        createSpinnerForNotificationEndTime(rootView,
                R.id.notification_end_time_spinner, R.array.notification_time_array,
                startTime, endTime);

        // 画面遷移：RegistrationFragment
        ImageView toRegistrationImageView = rootView.findViewById(
                R.id.from_settings_to_registration_imageView);
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

        // 画面の一番上に移動
        focusTop(rootView);

        // 画面遷移：HistoryFragment
        ImageView toHistoryImageView = rootView.findViewById(
                R.id.from_settings_to_history_imageView);
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

        return rootView;
    }

    private Spinner createSpinnerForNotificationStartTime(
            View rootView, int spinnerId, int stringTimeArrayId,
            String selectStartValue, String selectEndValue) {

        String[] stringTimeArray = getResources().getStringArray(stringTimeArrayId);
        String startValue = stringTimeArray[0];

        return createSpinnerForNotificationTime(
                rootView, spinnerId, stringTimeArrayId,
                startValue, 0,
                selectEndValue, -18, selectStartValue);
    }

    private Spinner createSpinnerForNotificationEndTime(
            View rootView, int spinnerId, int stringTimeArrayId,
            String selectStartValue, String selectEndValue) {

        String[] stringTimeArray = getResources().getStringArray(stringTimeArrayId);
        String endValue = stringTimeArray[stringTimeArray.length - 1];

        return createSpinnerForNotificationTime(
                rootView, spinnerId, stringTimeArrayId,
                selectStartValue, 18,
                endValue, 0, selectEndValue);
    }

    /**
     * intItemArrayIdからSpinnerを作成する
     *
     * @param rootView
     * @param spinnerId
     * @param intItemArrayId
     * @param selectValue
     * @return
     */
    private Spinner createSpinnerForIntItemArray(View rootView, int spinnerId,
                                                 int intItemArrayId, int selectValue) {

        // itemArray→3桁ごとにカンマ区切りのList<String>
        int[] itemArray = getResources().getIntArray(intItemArrayId);
        List<String> items = new ArrayList<>();
        for (int item : itemArray) {
            items.add(String.format("%,d", item));
        }

        int selectPosition = 0;
        for (; selectPosition < itemArray.length; selectPosition++) {
            if (selectValue == itemArray[selectPosition]) {
                break;
            }
        }

        return CreateSpinner(rootView, spinnerId, items.toArray(new String[items.size()]), selectPosition);
    }

    /**
     * stringItemArrayからSpinnerを作成する
     *
     * @param rootView
     * @param spinnerId
     * @param stringItemArray
     * @param selectPosition
     * @return
     */
    private Spinner CreateSpinner(View rootView, int spinnerId,
                                  final String[] stringItemArray, int selectPosition) {
        Spinner spinner = rootView.findViewById(spinnerId);
        // Create an ArrayAdapter using the int array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                R.layout.spinner_item
                , stringItemArray) {
            // スピナー内のリストのテキストの中央で揃える
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setGravity(Gravity.CENTER);
                return v;
            }
        };

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(
                R.layout.spinner_dropdown_item
        );
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setSelection(selectPosition);
        spinner.setOnItemSelectedListener(this);
        return spinner;
    }

    /**
     * stringItemArrayIdからSpinnerを作成する
     *
     * @param rootView
     * @param spinnerId
     * @param stringTimeArrayId
     * @param startValue
     * @param startPositionOffset
     * @param endValue
     * @param endPositionOffset
     * @param selectValue
     * @return
     */
    private Spinner createSpinnerForNotificationTime(
            View rootView, int spinnerId, int stringTimeArrayId,
            String startValue, int startPositionOffset,
            String endValue, int endPositionOffset, String selectValue) {

        String[] fullTimeArray = getResources().getStringArray(stringTimeArrayId);
        List<String> fullTimes = Arrays.asList(fullTimeArray);

        int startPosition = 0;
        for (; startPosition < fullTimes.size(); startPosition++) {
            if (startValue.equals(fullTimes.get(startPosition))) {
                startPosition = startPosition + startPositionOffset;
                break;
            }
        }

        int endPosition = 0;
        for (; endPosition < fullTimes.size(); endPosition++) {
            if (endValue.equals(fullTimes.get(endPosition))) {
                endPosition = endPosition + endPositionOffset;
                break;
            }
        }

        List<String> times = fullTimes.subList(startPosition, endPosition + 1);

        int selectPosition = 0;
        for (; selectPosition < times.size(); selectPosition++) {
            if (selectValue.equals(times.get(selectPosition))) {
                break;
            }
        }

        return CreateSpinner(rootView, spinnerId, times.toArray(
                new String[times.size()]), selectPosition);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        switch (adapterView.getId()) {
            case R.id.daily_target_spinner: {
                int[] dailyTargetArray = getResources().getIntArray(R.array.daily_target_array);
                int prevDailyTarget = DailyTargetDao.find(getContext());
                if (prevDailyTarget != dailyTargetArray[pos]) {
                    // sharedのdaily_targetを更新
                    int dailyTarget = dailyTargetArray[pos];
                    DailyTargetDao.save(dailyTarget, getContext());
                }
            }
            break;
            case R.id.average_water_supply_spinner: {
                int[] averageWaterSupplyArray = getResources().getIntArray(R.array.average_water_supply_array);
                int prevAverageWaterSupply = AverageWaterSupplyDao.find(getContext());
                if (prevAverageWaterSupply != averageWaterSupplyArray[pos]) {
                    // sharedのaverage_water_supplyを更新
                    int averageWaterSupply = averageWaterSupplyArray[pos];
                    AverageWaterSupplyDao.save(averageWaterSupply, getContext());
                }
            }
            break;
            case R.id.notification_start_time_spinner: {
                String startTime = adapterView.getAdapter().getItem(pos).toString();
                String prevStartTime = NotificationStartTimeDao.find(getContext());
                if (!prevStartTime.equals(startTime)) {
                    // sharedのnotification_start_timeを更新
                    NotificationStartTimeDao.save(startTime, getContext());
                    // 通知最終時刻の更新
                    String prevEndTime = NotificationEndTimeDao.find(getContext());
                    createSpinnerForNotificationEndTime(getView(), R.id.notification_end_time_spinner,
                            R.array.notification_time_array,
                            startTime,
                            prevEndTime);
                }
            }
            break;
            case R.id.notification_end_time_spinner: {
                String endTime = adapterView.getAdapter().getItem(pos).toString();
                String prevEndTime = NotificationEndTimeDao.find(getContext());
                if (!prevEndTime.equals(endTime)) {
                    // sharedのnotification_end_timeを更新
                    NotificationEndTimeDao.save(endTime, getContext());
                    // 通知開始時刻の更新
                    String prevStartTime = NotificationStartTimeDao.find(getContext());
                    createSpinnerForNotificationStartTime(getView(), R.id.notification_start_time_spinner,
                            R.array.notification_time_array,
                            prevStartTime,
                            endTime);
                }
            }
            break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onPause() {
        super.onPause();
        // 通知を更新
        try {
            // 今日、明日の通知のスケジュール更新
            NotificationUtils.update(getContext(),0,1);
        } catch (Exception e) {
            ConfirmationUtils.showFailureMessage(getContext().getString(
                    R.string.notification_update_failed_message),
                    getContext());
        }

        // 日次処理のスケジュールを内容作成
        DailyTaskUtils.saveSchedule(getContext());
    }
    private void focusTop(View view) {
        TextView top = view.findViewById(R.id.settings_container_top_textView);
        top.clearFocus();
        top.requestFocus();
    }
}
