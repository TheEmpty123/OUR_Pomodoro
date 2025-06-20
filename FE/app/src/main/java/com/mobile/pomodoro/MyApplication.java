package com.mobile.pomodoro;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
import com.mobile.pomodoro.utils.MyUtils;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        boolean isDarkMode = MyUtils.getBoolean(this, "dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}