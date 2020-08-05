package com.isao.mizurima.utils;

import android.content.Context;

public class LayoutUtils {

    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
