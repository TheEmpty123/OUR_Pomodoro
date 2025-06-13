package com.mobile.pomodoro;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.mobile.pomodoro.LoginSignup.LoginActivity;
import com.mobile.pomodoro.utils.MyUtils;
import com.mobile.pomodoro.utils.Settings.SettingManager;
import com.mobile.pomodoro.utils.Settings.TimeSelection;
import com.mobile.pomodoro.utils.Timer.TimerAnimationHelper;
import com.mobile.pomodoro.utils.Timer.TimerManager;
/*
- Cho phép user điều chỉnh thời gian
- Hiển thị thông tin user
- Lưu/khôi phục cài đặt
- Xử lý logout khi long press vào user info
- Đồng bộ thời gian với HomePage thông qua TimerManager
 */

public class SettingsActivity extends NavigateActivity {
    private TextView txtPomodoroTime;
    private TextView txtShortBreakTime;
    private TextView txtLongBreakTime;
    private TextView txtUsername;
    private TextView txtUserInitial;
    private CardView cardPomodoroTime;
    private CardView cardShortBreakTime;
    private CardView cardLongBreakTime;
    private CardView btnSaveCard;
    private CardView btnResetCard;
    private SettingManager settingsManager;
    private TimeSelection timeDialog;
    private String currentUsername;
    private int pomodoroTime;
    private int shortBreakTime;
    private int longBreakTime;
    private int originalPomodoroTime;
    private int originalShortBreakTime;
    private int originalLongBreakTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeComponents();    // khởi tạo UI
        setupUI();
        loadCurrentSettings();    // load setting hiện tại từ TimerManager
        updateDisplay();
        setupUserInfoLongPress(); // chức năng logout bằng cách nhấn giữ info user
    }

    private void initializeViews() {
        // Timer value TextViews - hiển thị số phút
        txtPomodoroTime = findViewById(R.id.txtPomodoroTime);
        txtShortBreakTime = findViewById(R.id.txtShortBreakTime);
        txtLongBreakTime = findViewById(R.id.txtLongBreakTime);

        // User info TextViews - hiển thị thông tin user
        txtUsername = findViewById(R.id.txtUsername);
        txtUserInitial = findViewById(R.id.txtUserInitial);

        // Time selection cards - user click để chọn thời gian
        cardPomodoroTime = findViewById(R.id.cardPomodoroTime);
        cardShortBreakTime = findViewById(R.id.cardShortBreakTime);
        cardLongBreakTime = findViewById(R.id.cardLongBreakTime);

        // Action cards - nút Save và Reset
        btnSaveCard = findViewById(R.id.btnSaveCard);
        btnResetCard = findViewById(R.id.btnResetCard);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String newUsername = MyUtils.get(this, "username");
        if (newUsername != null && !newUsername.equals(currentUsername)) {
            // user thay đổi thì cập nhật lại toàn bộ setting
            currentUsername = newUsername;
            TimerManager.setCurrentUsername(currentUsername);
            settingsManager.updateUsername(currentUsername);
            setupUserInfo();
            loadCurrentSettings();
            updateDisplay();

            Toast.makeText(this, "Loaded Settings For " + currentUsername, Toast.LENGTH_SHORT).show();
        } else {
            TimerManager.setCurrentUsername(currentUsername);
            loadCurrentSettings();
            updateDisplay();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // tự động lưu nếu có thay đổi
        if (hasUnsavedChanges() && settingsManager.isAutoSaveEnabled()) {
            handleSaveSettings();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // huỷ dialog chọn thời gian
        if (timeDialog != null) {
            timeDialog.setOnTimeSelectedListener(null);
            timeDialog = null;
        }
        settingsManager = null;
    }

    private void initializeComponents() {
        // khởi tạo UI
        initializeViews();

        // khởi tạo username và setup TimerManager
        initializeUserForSettings();
        settingsManager = new SettingManager(this);
        settingsManager.updateUsername(currentUsername);
        timeDialog = new TimeSelection(this);
        setupUserInfo();
    }

    private void initializeUserForSettings() {
        // lấy username từ shared preferences
        currentUsername = MyUtils.get(this, "username");

        if (currentUsername == null || currentUsername.trim().isEmpty()) {
            // Chưa có username thì tạo user guest
            currentUsername = "guest";
            MyUtils.save(this, "username", currentUsername);
        }
        // TimerManager sẽ load/save settings cho user này
        TimerManager.setCurrentUsername(currentUsername);

        Log.d("SettingsActivity", "Initialized settings for user: " + currentUsername);
    }

    // hiển thị thông tin user
    private void setupUserInfo() {
        txtUsername.setText(currentUsername);
        // chữ cái đầu của username làm avt
        txtUserInitial.setText(currentUsername.substring(0, 1).toUpperCase());
    }

    private void setupUI() {
        setupClickListeners();           // Thiết lập click events
        setupTimeSelectionCallbacks();  // Thiết lập time dialog callbacks
    }

    // các nút sự kiện
    private void setupClickListeners() {
        btnSaveCard.setOnClickListener(v -> {
            TimerAnimationHelper.animateButton(v);
            handleSaveSettings();
        });
        btnResetCard.setOnClickListener(v -> {
            TimerAnimationHelper.animateButton(v);
            handleResetSettings();
        });
        cardPomodoroTime.setOnClickListener(v -> {
            TimerAnimationHelper.animateButton(v);
            timeDialog.showFocusTimeDialog(pomodoroTime);
        });
        cardShortBreakTime.setOnClickListener(v -> {
            TimerAnimationHelper.animateButton(v);
            timeDialog.showShortBreakTimeDialog(shortBreakTime);
        });
        cardLongBreakTime.setOnClickListener(v -> {
            TimerAnimationHelper.animateButton(v);
            timeDialog.showLongBreakTimeDialog(longBreakTime);
        });
    }

    // cập nhật giá trị trong list thời gian selection
    private void setupTimeSelectionCallbacks() {
        timeDialog.setOnTimeSelectedListener(new TimeSelection.OnTimeSelectedListener() {
            @Override
            public void onFocusTimeSelected(int minutes) {
                pomodoroTime = minutes;
                updateTimeDisplay();
                animateTimeChange(txtPomodoroTime);
                updateSaveButtonState();
                // thông báo cho user
                Toast.makeText(SettingsActivity.this,
                        "Focus Time set to: " + minutes + " minutes",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onShortBreakTimeSelected(int minutes) {
                shortBreakTime = minutes;
                updateTimeDisplay();
                animateTimeChange(txtShortBreakTime);
                updateSaveButtonState();

                Toast.makeText(SettingsActivity.this,
                        "Short Break set to: " + minutes + " minutes",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongBreakTimeSelected(int minutes) {
                longBreakTime = minutes;
                updateTimeDisplay();
                animateTimeChange(txtLongBreakTime);
                updateSaveButtonState();

                Toast.makeText(SettingsActivity.this,
                        "Long Break set to: " + minutes + " minutes",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCurrentSettings() {
        // đảm bảo TimerManager có username đúng
        TimerManager.setCurrentUsername(currentUsername);

        // load setting cho user hiện tại
        TimerManager.loadTimerPreferences(this);

        // lấy các giá trị đã load (ép kiểu sang phút)
        pomodoroTime = (int) TimerManager.getFocusTimeMinutes();
        shortBreakTime = (int) TimerManager.getShortBreakTimeMinutes();
        longBreakTime = (int) TimerManager.getLongBreakTimeMinutes();

        // lưu vào original values
        originalPomodoroTime = pomodoroTime;
        originalShortBreakTime = shortBreakTime;
        originalLongBreakTime = longBreakTime;

        Log.d("SettingsActivity", "Loaded settings for user " + currentUsername +
                ": " + pomodoroTime + "/" + shortBreakTime + "/" + longBreakTime);
    }

    private void updateDisplay() {
        updateTimeDisplay();      // Cập nhật hiển thị thời gian
        updateSaveButtonState();  // Cập nhật trạng thái nút Save
    }

    private void updateTimeDisplay() {
        txtPomodoroTime.setText(String.valueOf(pomodoroTime));
        txtShortBreakTime.setText(String.valueOf(shortBreakTime));
        txtLongBreakTime.setText(String.valueOf(longBreakTime));
    }

    // cập nhật UI trạng thái chi nút lưu
    private void updateSaveButtonState() {
        boolean hasChanges = hasUnsavedChanges();
        btnSaveCard.setAlpha(hasChanges ? 1.0f : 0.6f);

        // Enable/disable click
        btnSaveCard.setClickable(hasChanges);
    }

    private void animateTimeChange(TextView textView) {
        TimerAnimationHelper.animateTextChange(textView);
    }

    // xử lý lưu setting
    private void handleSaveSettings() {
        if (settingsManager.validateSettings(pomodoroTime, shortBreakTime, longBreakTime)) {
            try {
                // lưu settings qua SettingManager
                settingsManager.saveUserSettings(currentUsername, pomodoroTime, shortBreakTime, longBreakTime);

                // cập nhật lại original value
                originalPomodoroTime = pomodoroTime;
                originalShortBreakTime = shortBreakTime;
                originalLongBreakTime = longBreakTime;

                updateSaveButtonState();

                String summary = String.format("Setting saved: Focus Time: %d, Short Break %d, Long Break %d",
                        pomodoroTime, shortBreakTime, longBreakTime);
                Toast.makeText(this, summary, Toast.LENGTH_LONG).show();

                // pop up recommendations nếu settings không optimize
                String recommendation = settingsManager.getRecommendationMessage(pomodoroTime, shortBreakTime, longBreakTime);
                if (!recommendation.startsWith("✓")) {
                    Toast.makeText(this, recommendation, Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Toast.makeText(this, "Error saving settings: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            String errorMessage = settingsManager.getValidationErrorMessage(pomodoroTime, shortBreakTime, longBreakTime);
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void handleResetSettings() {
        settingsManager.showResetConfirmationDialog(this, () -> {
            resetToDefaults();
            Toast.makeText(this, "Reset To Default Settings", Toast.LENGTH_SHORT).show();
        });
    }

    private void resetToDefaults() {
        TimerManager.resetToDefaults(this);

        // lấy giá trị từ TimerManager
        pomodoroTime = (int) TimerManager.getFocusTimeMinutes();
        shortBreakTime = (int) TimerManager.getShortBreakTimeMinutes();
        longBreakTime = (int) TimerManager.getLongBreakTimeMinutes();

        updateTimeDisplay();

        animateTimeChange(txtPomodoroTime);
        animateTimeChange(txtShortBreakTime);
        animateTimeChange(txtLongBreakTime);

        updateSaveButtonState();
    }

    private boolean hasUnsavedChanges() {
        return pomodoroTime != originalPomodoroTime ||
                shortBreakTime != originalShortBreakTime ||
                longBreakTime != originalLongBreakTime;
    }

    // cho phép user logout bằng cách nhấn giữ vào user info card, hoặc tên của user
    // nói chung cái phần nằm riêng
    private void setupUserInfoLongPress() {
        // long press trên toàn bộ card
        CardView userInfoCard = findViewById(R.id.userInfoCard);

        View.OnLongClickListener logoutLongPress = v -> {
            showLogoutDialog();
            return true;
        };

        userInfoCard.setOnLongClickListener(logoutLongPress);

        CardView avatarCard = findViewById(R.id.avatarCard);
        avatarCard.setOnLongClickListener(logoutLongPress);

        txtUsername.setOnLongClickListener(logoutLongPress);
    }

    // dialog xác nhận logout
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?\n\nYour timer settings will be saved automatically.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Logout", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Không làm gì, chỉ đóng dialog
                })
                .show();
    }

    // khi người dùng xác nhận logout, method này tự xử lý:
    // tự lưu setting hiện tại
    // xoá username ra khỏi shared preference
    // hiển thị thông báo logout
    // chuyển về login
    private void performLogout() {
        try {
            if (hasUnsavedChanges()) {
                handleSaveSettings();
            }
            MyUtils.remove(this, "username");
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            Toast.makeText(this, "Error during logout: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("SettingsActivity", "Logout error", e);
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_settings;
    }

    @Override
    protected int getCurrentMenuItemId() {
        return R.id.page_setting;
    }
}