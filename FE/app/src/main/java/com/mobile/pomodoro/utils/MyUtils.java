package com.mobile.pomodoro.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class MyUtils {
    // 3 function dưới sử dụng để lưu các context cần thiết vào header sau đó gửi về backend
    public static void deleteKeyResponse(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Mobile_APP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public static void save(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Mobile_APP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String get(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Mobile_APP", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    } // Thêm các function cần thiết nếu cần

}
