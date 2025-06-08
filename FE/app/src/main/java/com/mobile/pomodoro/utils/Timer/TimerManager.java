package com.mobile.pomodoro.utils.Timer;

import android.content.Context;

import com.mobile.pomodoro.enums.TimerMode;
import com.mobile.pomodoro.utils.MyUtils;

// lưu/load cài đặt thời gian của các mode
public class TimerManager {
    private static final String FOCUS_TIME_KEY = "focus_time";
    private static final String SHORT_BREAK_TIME_KEY = "short_break_time";
    private static final String LONG_BREAK_TIME_KEY = "long_break_time";
    private static final String TIMER_REMAINING_KEY = "timer_remaining";
    private static final String TIMER_RUNNING_KEY = "timer_running";

    public static void loadTimerPreferences(Context context) {
        String focusTimeStr = MyUtils.get(context, FOCUS_TIME_KEY);
        String shortBreakTimeStr = MyUtils.get(context, SHORT_BREAK_TIME_KEY);
        String longBreakTimeStr = MyUtils.get(context, LONG_BREAK_TIME_KEY);

        if (focusTimeStr != null) {
            TimerMode.FOCUS.updateDuration(Long.parseLong(focusTimeStr));
        }
        if (shortBreakTimeStr != null) {
            TimerMode.SHORT_BREAK.updateDuration(Long.parseLong(shortBreakTimeStr));
        }
        if (longBreakTimeStr != null) {
            TimerMode.LONG_BREAK.updateDuration(Long.parseLong(longBreakTimeStr));
        }
    }

    // lưu cài đặt thời gian vào shared preference
    public static void saveTimerPreferences(Context context) {
        MyUtils.save(context, FOCUS_TIME_KEY, String.valueOf(TimerMode.FOCUS.getDuration()));
        MyUtils.save(context, SHORT_BREAK_TIME_KEY, String.valueOf(TimerMode.SHORT_BREAK.getDuration()));
        MyUtils.save(context, LONG_BREAK_TIME_KEY, String.valueOf(TimerMode.LONG_BREAK.getDuration()));
    }

    // luu trạng thái thời gian
    public static void saveTimerState(Context context, long timeRemaining, boolean isRunning) {
        MyUtils.save(context, TIMER_REMAINING_KEY, String.valueOf(timeRemaining));
        MyUtils.save(context, TIMER_RUNNING_KEY, String.valueOf(isRunning));
    }

    public static long getSavedTimerRemaining(Context context, long defaultValue) {
        String savedTime = MyUtils.get(context, TIMER_REMAINING_KEY);
        return savedTime != null ? Long.parseLong(savedTime) : defaultValue;
    }

    public static boolean getSavedTimerRunning(Context context) {
        String savedState = MyUtils.get(context, TIMER_RUNNING_KEY);
        return savedState != null && Boolean.parseBoolean(savedState);
    }

    public static void updateTimerModeDuration(Context context, TimerMode mode, long duration) {
        mode.updateDuration(duration);
        saveTimerPreferences(context);
    }

    public static void updateTimerModeFromSeconds(Context context, TimerMode mode, double durationInSeconds) {
        if (durationInSeconds > 0) {
            long durationInMillis = (long) (durationInSeconds * 1000);
            updateTimerModeDuration(context, mode, durationInMillis);
        }
    }

    public static void setFocusTime(long minutes) {
        TimerMode.FOCUS.updateDurationFromMinutes(minutes);
    }

    public static void setShortBreakTime(long minutes) {
        TimerMode.SHORT_BREAK.updateDurationFromMinutes(minutes);
    }

    public static void setLongBreakTime(long minutes) {
        TimerMode.LONG_BREAK.updateDurationFromMinutes(minutes);
    }

    public static long getFocusTimeMinutes() {
        return TimerMode.FOCUS.getDurationInMinutes();
    }

    public static long getShortBreakTimeMinutes() {
        return TimerMode.SHORT_BREAK.getDurationInMinutes();
    }

    public static long getLongBreakTimeMinutes() {
        return TimerMode.LONG_BREAK.getDurationInMinutes();
    }
}
