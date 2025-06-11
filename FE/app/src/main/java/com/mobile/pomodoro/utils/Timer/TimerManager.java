package com.mobile.pomodoro.utils.Timer;

import android.content.Context;

import com.mobile.pomodoro.enums.TimerMode;
import com.mobile.pomodoro.utils.LogObj;
import com.mobile.pomodoro.utils.MyUtils;

// lưu/load cài đặt thời gian của các mode
// đồng bộ setting giữa SettingActivity và HomePage, giúp cho 2 cái đều đồng nhất về thời gian giữa các mode
// Cache để tăng tốc độ xử lý

public class TimerManager {
    private static final LogObj logger = new LogObj("TimerManager");

    // các cài đặt được lưu vào shared preferences theo từng user
    // vd "tanhtrantest5_focus_time", "tanhtrantest1_focus_time" nên mỗi user sẽ có settings timer riêng
    private static final String FOCUS_TIME_SUFFIX = "_focus_time";
    private static final String SHORT_BREAK_TIME_SUFFIX = "_short_break_time";
    private static final String LONG_BREAK_TIME_SUFFIX = "_long_break_time";
    private static final String TIMER_REMAINING_SUFFIX = "_timer_remaining";
    private static final String TIMER_RUNNING_SUFFIX = "_timer_running";
    private static final String CURRENT_MODE_SUFFIX = "_current_mode";

    // cache theo user
    private static String currentUsername;
    private static Long cachedFocusTime;
    private static Long cachedShortBreakTime;
    private static Long cachedLongBreakTime;
    private static boolean cacheValid = false;

    public interface TimerSettingsChangeListener {
        void onTimerSettingsChanged(int focusTime, int shortBreakTime, int longBreakTime);
    }

    private static TimerSettingsChangeListener settingsChangeListener;

    // method này lấy ra username để load setting của user đó
    public static void setCurrentUsername(String username) {
        // username rỗng, không có thì dùng default name
        if (username == null || username.trim().isEmpty()) {
            username = "default_user";
        }

        // Chỉ thay đổi nếu user thật sự khác (tránh clear cache không cần thiết)
        if (!username.equals(currentUsername)) {
            logger.info("setCurrentUsername", "Chuyển user từ " + currentUsername + " sang " + username);
            currentUsername = username;
            invalidateCache(); // Xóa cache cũ để load settings user mới
        }
    }

    public static String getCurrentUsername() {
        return currentUsername != null ? currentUsername : "default_user"; // username rỗng, không có thì dùng default name
    }

    // tạo key cho SHARED PREFERENCES theo định dạng: "username_focus_time", "username_short_break_time"
    private static String getUserKey(String suffix) {
        return getCurrentUsername() + suffix;
    }

    // khi user thay đổi settings trong SettingsActivity, HomePage sẽ tự động cập nhật timer mà không cần restart app
    public static void setTimerSettingsChangeListener(TimerSettingsChangeListener listener) {
        settingsChangeListener = listener;
        logger.info("setListener", "Đã đăng ký listener cho thông báo settings thay đổi");
    }

    private static long getDefaultDuration(TimerMode mode) {
        return TimerMode.getDefaultDuration(mode);
    }

    /**
     * load các cài đặt timer từ shared preferences cho user hiện tại
     * Đọc từ SharedPreferences với key theo user (VD: "tanhtrantest5_focus_time")
     * Nếu không có thì dùng default từ TimerMode
     * Cập nhật TimerMode enum với giá trị đã load
     * Lưu vào cache để truy cập nhanh lần sau
     */
    public static void loadTimerPreferences(Context context) {
        try {
            String currentUser = getCurrentUsername();
            logger.info("loadTimerPreferences", "Đang load cài đặt cho user: " + currentUser);
            long focusTimeMillis = MyUtils.getLong(context,
                    getUserKey(FOCUS_TIME_SUFFIX),
                    getDefaultDuration(TimerMode.FOCUS));

            long shortBreakMillis = MyUtils.getLong(context,
                    getUserKey(SHORT_BREAK_TIME_SUFFIX),
                    getDefaultDuration(TimerMode.SHORT_BREAK));

            long longBreakMillis = MyUtils.getLong(context,
                    getUserKey(LONG_BREAK_TIME_SUFFIX),
                    getDefaultDuration(TimerMode.LONG_BREAK));

            // cập nhật timer với giá trị vừa load
            TimerMode.FOCUS.updateDuration(focusTimeMillis);
            TimerMode.SHORT_BREAK.updateDuration(shortBreakMillis);
            TimerMode.LONG_BREAK.updateDuration(longBreakMillis);

            // lưu vào cache
            updateCache(focusTimeMillis, shortBreakMillis, longBreakMillis);

            logger.info("loadTimerPreferences",
                    String.format("Đã load: Focus=%d phút, Nghỉ ngắn=%d phút, Nghỉ dài=%d phút",
                            focusTimeMillis / 60000, shortBreakMillis / 60000, longBreakMillis / 60000));

        } catch (Exception e) {
            logger.error("loadTimerPreferences", "Lỗi khi load cài đặt: " + e.getMessage());
            loadDefaultSettings(); //lỗi thì load default setting
        }
    }

    /**
     * lưu cài đặt timer vào shared preferences cho user hiện tại
     * lấy thời gian từ TimerMode và lưu vào shared preferences với key theo user
     */
    public static void saveTimerPreferences(Context context) {
        try {
            String currentUser = getCurrentUsername();

            // lưu duration từ TimerMode vào SharedPreferences
            MyUtils.saveLong(context, getUserKey(FOCUS_TIME_SUFFIX), TimerMode.FOCUS.getDuration());
            MyUtils.saveLong(context, getUserKey(SHORT_BREAK_TIME_SUFFIX), TimerMode.SHORT_BREAK.getDuration());
            MyUtils.saveLong(context, getUserKey(LONG_BREAK_TIME_SUFFIX), TimerMode.LONG_BREAK.getDuration());

            // cập nhật cache
            updateCache(TimerMode.FOCUS.getDuration(),
                    TimerMode.SHORT_BREAK.getDuration(),
                    TimerMode.LONG_BREAK.getDuration());

            logger.info("saveTimerPreferences", "Đã lưu cài đặt cho user: " + currentUser);

        } catch (Exception e) {
            logger.error("saveTimerPreferences", "Lỗi khi lưu cài đặt: " + e.getMessage());
        }
    }

    // luu trạng thái thời gian với kiểu dữ liệu là Long
    public static void saveTimerState(Context context, long timeRemaining, boolean isRunning) {
        MyUtils.saveLong(context, getUserKey(TIMER_REMAINING_SUFFIX), timeRemaining);
        MyUtils.saveBoolean(context, getUserKey(TIMER_RUNNING_SUFFIX), isRunning);
    }

    public static void saveTimerState(Context context, long timeRemaining, boolean isRunning, TimerMode currentMode) {
        saveTimerState(context, timeRemaining, isRunning);
        MyUtils.save(context, getUserKey(CURRENT_MODE_SUFFIX), currentMode.name());
    }

    // cập nhật thời gian cho một chế độ cụ thể nào đó và lưu lại
    public static void updateTimerModeDuration(Context context, TimerMode mode, long duration) {
        mode.updateDuration(duration);
        saveTimerPreferences(context);
        invalidateCache(); // xóa cache
        logger.info("updateTimerModeDuration",
                "Đã cập nhật " + mode.name() + " thành " + (duration / 60000) + " phút");
    }

    public static void updateTimerModeFromSeconds(Context context, TimerMode mode, double durationInSeconds) {
        if (durationInSeconds > 0) {
            long durationInMillis = (long) (durationInSeconds * 1000);
            updateTimerModeDuration(context, mode, durationInMillis);
        }
    }

    public static void setFocusTime(long minutes) {
        TimerMode.FOCUS.updateDurationFromMinutes(minutes);
        cachedFocusTime = minutes * 60000L;
    }

    public static void setShortBreakTime(long minutes) {
        TimerMode.SHORT_BREAK.updateDurationFromMinutes(minutes);
        cachedShortBreakTime = minutes * 60000L;
    }

    public static void setLongBreakTime(long minutes) {
        TimerMode.LONG_BREAK.updateDurationFromMinutes(minutes);
        cachedLongBreakTime = minutes * 60000L;
    }

    public static long getFocusTimeMinutes() {
        if (cacheValid && cachedFocusTime != null) {
            return cachedFocusTime / 60000L; // Đọc từ cache
        }
        return TimerMode.FOCUS.getDurationInMinutes();
    }

    public static long getShortBreakTimeMinutes() {
        if (cacheValid && cachedShortBreakTime != null) {
            return cachedShortBreakTime / 60000L;
        }
        return TimerMode.SHORT_BREAK.getDurationInMinutes();
    }

    public static long getLongBreakTimeMinutes() {
        if (cacheValid && cachedLongBreakTime != null) {
            return cachedLongBreakTime / 60000L;
        }
        return TimerMode.LONG_BREAK.getDurationInMinutes();
    }

    // lưu tất cả cài đặt timer và thông báo
    public static void saveAllTimerSettings(Context context, int focusMinutes, int shortBreakMinutes, int longBreakMinutes) {
        try {
            logger.info("saveAllTimerSettings",
                    String.format("Đang lưu tất cả cài đặt: Focus=%d phút, Nghỉ ngắn=%d phút, Nghỉ dài=%d phút",
                            focusMinutes, shortBreakMinutes, longBreakMinutes));

            // chuyển đổi sang milli giây và cập nhật lại thời gian trong TimerMode
            long focusMillis = focusMinutes * 60000L;
            long shortBreakMillis = shortBreakMinutes * 60000L;
            long longBreakMillis = longBreakMinutes * 60000L;

            TimerMode.FOCUS.updateDuration(focusMillis);
            TimerMode.SHORT_BREAK.updateDuration(shortBreakMillis);
            TimerMode.LONG_BREAK.updateDuration(longBreakMillis);

            // lưu vào SharedPreferences
            saveTimerPreferences(context);

            // cập nhật UI bên homepage
            if (settingsChangeListener != null) {
                settingsChangeListener.onTimerSettingsChanged(focusMinutes, shortBreakMinutes, longBreakMinutes);
            }

        } catch (Exception e) {
            logger.error("saveAllTimerSettings", "Lỗi khi lưu tất cả cài đặt: " + e.getMessage());
        }
    }

    // load cài đặt mặc định từ enum
    private static void loadDefaultSettings() {
        logger.info("loadDefaultSettings", "Đang load cài đặt mặc định từ TimerMode enum");

        // Reset TimerMode về giá trị gốc
        TimerMode.FOCUS.resetToOriginal();
        TimerMode.SHORT_BREAK.resetToOriginal();
        TimerMode.LONG_BREAK.resetToOriginal();
        updateCache(
                getDefaultDuration(TimerMode.FOCUS),
                getDefaultDuration(TimerMode.SHORT_BREAK),
                getDefaultDuration(TimerMode.LONG_BREAK)
        );
    }

    public static void resetToDefaults(Context context) {
        logger.info("resetToDefaults", "Đang reset về cài đặt mặc định cho user: " + getCurrentUsername());

        loadDefaultSettings();
        saveTimerPreferences(context);
        if (settingsChangeListener != null) {
            settingsChangeListener.onTimerSettingsChanged(
                    (int) (TimerMode.FOCUS.getOriginalDuration() / 60000L),
                    (int) (TimerMode.SHORT_BREAK.getOriginalDuration() / 60000L),
                    (int) (TimerMode.LONG_BREAK.getOriginalDuration() / 60000L)
            );
        }
    }

    // cập nhật cache
    private static void updateCache(long focusMillis, long shortBreakMillis, long longBreakMillis) {
        cachedFocusTime = focusMillis;
        cachedShortBreakTime = shortBreakMillis;
        cachedLongBreakTime = longBreakMillis;
        cacheValid = true;
    }

    // xoá cache
    public static void invalidateCache() {
        cacheValid = false;
        cachedFocusTime = null;
        cachedShortBreakTime = null;
        cachedLongBreakTime = null;
    }
}