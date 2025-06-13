package com.mobile.pomodoro.utils.Settings;

import android.app.AlertDialog;
import android.content.Context;

import com.mobile.pomodoro.utils.LogObj;
import com.mobile.pomodoro.utils.MyUtils;
import com.mobile.pomodoro.utils.Timer.TimerManager;

// Quản lý cài đặt của user
public class SettingManager {

    private static final LogObj logger = new LogObj("SettingManager");
    private static final String LAST_SAVED_SUFFIX = "_last_saved";
    private static final String AUTO_SAVE_KEY = "auto_save_enabled";
    private static final String USER_PROFILE_SUFFIX = "_profile_type";

    // valid timer
    private static final int MIN_FOCUS_TIME = 5;
    private static final int MAX_FOCUS_TIME = 120;
    private static final int MIN_SHORT_BREAK = 1;
    private static final int MAX_SHORT_BREAK = 30;
    private static final int MIN_LONG_BREAK = 5;
    private static final int MAX_LONG_BREAK = 60;

    // thời gian cài pomodoro tốt nhất
    private static final int IDEAL_FOCUS_MIN = 20;
    private static final int IDEAL_FOCUS_MAX = 45;
    private static final int IDEAL_SHORT_BREAK_MIN = 3;
    private static final int IDEAL_SHORT_BREAK_MAX = 10;
    private static final int IDEAL_LONG_BREAK_MIN = 15;
    private static final int IDEAL_LONG_BREAK_MAX = 30;

    private Context context;
    private String currentUsername;

    public SettingManager(Context context) {
        this.context = context;
        logger.info("SettingManager initialized");
    }

    public void updateUsername(String username) {
        this.currentUsername = username;
        logger.info("updateUsername", "Username updated to: " + username);
    }

    // lưu lại toàn bộ cài đặt của user
    public void saveUserSettings(String username, int focusTime, int shortBreakTime, int longBreakTime) {
        logger.info("saveUserSettings",
                String.format("Saving settings for %s: Focus=%dm, Short=%dm, Long=%dm",
                        username, focusTime, shortBreakTime, longBreakTime));
        TimerManager.saveAllTimerSettings(context, focusTime, shortBreakTime, longBreakTime);
    }

    // kiểm tra auto save có đang bật không
    public boolean isAutoSaveEnabled() {
        return MyUtils.getBoolean(context, AUTO_SAVE_KEY, true);
    }

    // validate settings timer
    public boolean validateSettings(int focusTime, int shortBreakTime, int longBreakTime) {
        boolean isValid = validateFocusTime(focusTime) &&
                validateShortBreakTime(shortBreakTime) &&
                validateLongBreakTime(longBreakTime) &&
                validateRelativeTimings(focusTime, shortBreakTime, longBreakTime);

        logger.info("validateSettings", "Settings validation result: " + isValid);
        return isValid;
    }

    public boolean validateFocusTime(int focusTime) {
        return focusTime >= MIN_FOCUS_TIME && focusTime <= MAX_FOCUS_TIME;
    }

    public boolean validateShortBreakTime(int shortBreakTime) {
        return shortBreakTime >= MIN_SHORT_BREAK && shortBreakTime <= MAX_SHORT_BREAK;
    }

    public boolean validateLongBreakTime(int longBreakTime) {
        return longBreakTime >= MIN_LONG_BREAK && longBreakTime <= MAX_LONG_BREAK;
    }

    // validate giữa các chế độ thời gian theo nguyên tắc Pomodoro
    public boolean validateRelativeTimings(int focusTime, int shortBreakTime, int longBreakTime) {
        return shortBreakTime < longBreakTime &&
                focusTime > shortBreakTime &&
                longBreakTime <= focusTime * 0.8;
    }

    // thông báo lỗi validate
    public String getValidationErrorMessage(int focusTime, int shortBreakTime, int longBreakTime) {
        if (!validateFocusTime(focusTime)) {
            return String.format("Focus time must be between %d and %d minutes", MIN_FOCUS_TIME, MAX_FOCUS_TIME);
        }
        if (!validateShortBreakTime(shortBreakTime)) {
            return String.format("Short break must be between %d and %d minutes", MIN_SHORT_BREAK, MAX_SHORT_BREAK);
        }
        if (!validateLongBreakTime(longBreakTime)) {
            return String.format("Long break must be between %d and %d minutes", MIN_LONG_BREAK, MAX_LONG_BREAK);
        }
        if (shortBreakTime >= longBreakTime) {
            return "Short break must be shorter than long break";
        }
        if (focusTime <= shortBreakTime) {
            return "Focus time must be longer than short break";
        }
        if (longBreakTime > focusTime * 0.8) {
            return "Long break is too close to focus time duration";
        }
        return "Settings are valid";
    }

    // kiểm tra settings có tuân thủ best practices không

    public boolean followsBestPractices(int focusTime, int shortBreakTime, int longBreakTime) {
        // ktra theo tỉ lệ Pomodoro
        boolean classicRatio = (focusTime >= IDEAL_FOCUS_MIN && focusTime <= IDEAL_FOCUS_MAX) &&
                (shortBreakTime >= IDEAL_SHORT_BREAK_MIN && shortBreakTime <= IDEAL_SHORT_BREAK_MAX) &&
                (longBreakTime >= IDEAL_LONG_BREAK_MIN && longBreakTime <= IDEAL_LONG_BREAK_MAX);

        // theo tỉ lệ short break / focus time
        double shortBreakRatio = (double) shortBreakTime / focusTime;
        boolean goodShortBreakRatio = shortBreakRatio >= 0.1 && shortBreakRatio <= 0.3;

        // long break / focus time
        double longBreakRatio = (double) longBreakTime / focusTime;
        boolean goodLongBreakRatio = longBreakRatio >= 0.5 && longBreakRatio <= 1.0;

        boolean followsBest = classicRatio || (goodShortBreakRatio && goodLongBreakRatio);
        return followsBest;
    }

    // tạo thông báo gợi ý cải thiện setting
    public String getRecommendationMessage(int focusTime, int shortBreakTime, int longBreakTime) {
        if (followsBestPractices(focusTime, shortBreakTime, longBreakTime)) {
            return "✓ Your settings follow Pomodoro best practices!";
        }

        StringBuilder recommendation = new StringBuilder("💡 Recommendations:\n");

        if (focusTime < IDEAL_FOCUS_MIN) {
            recommendation.append("* Consider longer focus sessions (").append(IDEAL_FOCUS_MIN).append("-").append(IDEAL_FOCUS_MAX).append(" min) for better deep work\n");
        } else if (focusTime > IDEAL_FOCUS_MAX) {
            recommendation.append("* Consider shorter focus sessions (").append(IDEAL_FOCUS_MIN).append("-").append(IDEAL_FOCUS_MAX).append(" min) to maintain concentration\n");
        }

        double shortBreakRatio = (double) shortBreakTime / focusTime;
        if (shortBreakRatio < 0.1) {
            recommendation.append("* Short break might be too short for proper rest\n");
        } else if (shortBreakRatio > 0.3) {
            recommendation.append("* Short break might be too long, reducing productivity\n");
        }

        if (longBreakTime < focusTime * 0.5) {
            recommendation.append("* Consider a longer long break for better recovery\n");
        }

        return recommendation.toString().trim();
    }

    public void showResetConfirmationDialog(Context context, Runnable onConfirm) {
        new AlertDialog.Builder(context)
                .setTitle("Reset Settings")
                .setMessage("Are you sure you want to reset all timer settings to default values?\n\nThis action cannot be undone.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Reset", (dialog, which) -> {
                    if (onConfirm != null) {
                        onConfirm.run();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void showUnsavedChangesDialog(Context context, Runnable onSave, Runnable onDiscard) {
        new AlertDialog.Builder(context)
                .setTitle("Unsaved Changes")
                .setMessage("You have unsaved changes. What would you like to do?")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("Save & Exit", (dialog, which) -> {
                    if (onSave != null) {
                        onSave.run();
                    }
                })
                .setNegativeButton("Discard", (dialog, which) -> {
                    if (onDiscard != null) {
                        onDiscard.run();
                    }
                })
                .setNeutralButton("Cancel", null)
                .show();
    }
}