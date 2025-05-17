package com.mobile.pomodoro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends NavigateActivity {
    private TextView txtPomodoroTime, txtShortBreakTime, txtLongBreakTime;
    private ImageButton btnClose, btnRefresh;
    private Button btnSaveSettings;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "PomodoroSettings";
    private static final String KEY_POMODORO_TIME = "pomodoro_time";
    private static final String KEY_SHORT_BREAK_TIME = "short_break_time";
    private static final String KEY_LONG_BREAK_TIME = "long_break_time";
    // Time default
    private int pomodoroTime = 25;
    private int shortBreakTime = 5;
    private int longBreakTime = 20;

    // Time options arrays
    private final Integer[] pomodoroTimeOptions = {15, 20, 25, 30, 35, 40, 45, 50, 55, 60};
    private final Integer[] shortBreakTimeOptions = {5, 6, 7, 8, 9, 10};
    private final Integer[] longBreakTimeOptions = {5, 10, 15, 20, 25, 30};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        txtPomodoroTime = findViewById(R.id.txtPomodoroTime);
        txtShortBreakTime = findViewById(R.id.txtShortBreakTime);
        txtLongBreakTime = findViewById(R.id.txtLongBreakTime);
        btnClose = findViewById(R.id.btnClose);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnSaveSettings = findViewById(R.id.btnSaveSettings);


        // khởi tạo share preference để lưu setting của người dùng
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        setupListeners();
        loadSettings();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_settings;
    }

    @Override
    protected int getCurrentMenuItemId() {
        return R.id.page_setting;
    }

    private void setupListeners() {
        // nút đóng
        btnClose.setOnClickListener(v -> finish());

        // nút lưu cài đặt
        btnSaveSettings.setOnClickListener(v -> saveSettings());

        // reset về cài đặt ban đầu
        btnRefresh.setOnClickListener(v -> {
            resetToDefaults();
            Toast.makeText(this, "Reset to default", Toast.LENGTH_SHORT).show();
        });

        // tăng/giảm thời gian
        setupTimeSelectionListeners();
    }

    // hiển thị danh sách thời gian
    private void setupTimeSelectionListeners() {
        // pomodoro time change
        findViewById(R.id.btnChangePomodoroTime).setOnClickListener(v -> showTimeSelectionDialog("Pomodoro Time", pomodoroTimeOptions, value -> {
            pomodoroTime = value;
            updateTimeDisplay();
        }));

        // Short break time change
        findViewById(R.id.btnChangeShortBreakTime).setOnClickListener(v -> showTimeSelectionDialog("Short Break Time", shortBreakTimeOptions, value -> {
            shortBreakTime = value;
            updateTimeDisplay();
        }));

        // Long break time change
        findViewById(R.id.btnChangeLongBreakTime).setOnClickListener(v -> showTimeSelectionDialog("Long Break Time", longBreakTimeOptions, value -> {
            longBreakTime = value;
            updateTimeDisplay();
        }));
    }

    // Interface để xử lý callback khi chọn giá trị từ dialog
    interface TimeValueCallback {
        void onTimeSelected(int value);
    }

    // Hiển thị dialog để chọn thời gian
    private void showTimeSelectionDialog(String title, Integer[] timeOptions, TimeValueCallback callback) {
        //  hiển thị chuỗi số kèm theo "minutes"
        String[] displayOptions = new String[timeOptions.length];
        for (int i = 0; i < timeOptions.length; i++) {
            displayOptions[i] = timeOptions[i] + " minutes";
        }

        int currentValue;
        if (title.contains("Pomodoro")) {
            currentValue = pomodoroTime;
        } else if (title.contains("Short Break")) {
            currentValue = shortBreakTime;
        } else {
            currentValue = longBreakTime;
        }

        int defaultSelection = 0;
        for (int i = 0; i < timeOptions.length; i++) {
            if (timeOptions[i] == currentValue) {
                defaultSelection = i;
                break;
            }
        }

        // hiển thị dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setSingleChoiceItems(displayOptions, defaultSelection, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Gọi callback với giá trị đã chọn
                callback.onTimeSelected(timeOptions[which]);
                dialog.dismiss();
            }
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void updateTimeDisplay() {
        txtPomodoroTime.setText(String.format("%02d", pomodoroTime));
        txtShortBreakTime.setText(String.format("%02d", shortBreakTime));
        txtLongBreakTime.setText(String.format("%02d", longBreakTime));
    }

    // Đặt lại về giá trị mặc định
    private void resetToDefaults() {
        pomodoroTime = 25;
        shortBreakTime = 5;
        longBreakTime = 20;
        updateTimeDisplay();
    }

    private void loadSettings() {
        pomodoroTime = sharedPreferences.getInt(KEY_POMODORO_TIME, 25);
        shortBreakTime = sharedPreferences.getInt(KEY_SHORT_BREAK_TIME, 5);
        longBreakTime = sharedPreferences.getInt(KEY_LONG_BREAK_TIME, 20);
        updateTimeDisplay();
    }

    // Lưu cài đặt vào SharedPreferences
    private void saveSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_POMODORO_TIME, pomodoroTime);
        editor.putInt(KEY_SHORT_BREAK_TIME, shortBreakTime);
        editor.putInt(KEY_LONG_BREAK_TIME, longBreakTime);
        editor.apply();

        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}