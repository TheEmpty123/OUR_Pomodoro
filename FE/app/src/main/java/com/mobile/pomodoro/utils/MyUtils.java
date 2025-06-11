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

    public static void saveBoolean(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Mobile_APP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Mobile_APP", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    /*
     * Dùng cho: timer duration (milliseconds)
     * vd lưu 1800000L (30 phút) cho focus time
     */
    public static void saveLong(Context context, String key, long value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Mobile_APP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    // lưu thì phải có đọc
    public static long getLong(Context context, String key, long defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Mobile_APP", Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, defaultValue);
    }

    // remove data ko cần thiết hoặc khi user logout
    public static void remove(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Mobile_APP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }
}
