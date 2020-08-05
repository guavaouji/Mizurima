package com.isao.mizurima.utils;

import android.content.Context;
import android.content.Intent;
import androidx.wear.activity.ConfirmationActivity;

public class ConfirmationUtils {
    public static void showSuccessMessage(String message, Context context) {
        Intent intent = new Intent(context, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.SUCCESS_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, message);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_DURATION_MILLIS, 750);
        context.startActivity(intent);
    }

    public static void showFailureMessage(String message, Context context) {
        Intent intent = new Intent(context, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.FAILURE_ANIMATION);
        intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, message);
        context.startActivity(intent);
    }
}
