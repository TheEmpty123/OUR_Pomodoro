package com.mobile.pomodoro;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobile.pomodoro.enums.TimerMode;
import com.mobile.pomodoro.response_dto.PlanResponseDTO;
import com.mobile.pomodoro.response_dto.PlanTaskResponseDTO;
import com.mobile.pomodoro.service.PomodoroAPI;
import com.mobile.pomodoro.service.PomodoroService;
import com.mobile.pomodoro.service.TimerService;
import com.mobile.pomodoro.utils.EditTitleDialogFragment;
import com.mobile.pomodoro.utils.MyUtils;
import com.mobile.pomodoro.utils.SessionManager;
import com.mobile.pomodoro.utils.Timer.TimerAnimationHelper;
import com.mobile.pomodoro.utils.Timer.TimerManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * Homepage: Màn hình chính của ứng dụng
 * Chức năng: Điều khiển timer, hiển thị task hiện tại, quản lý session
 */


public class HomePage extends NavigateActivity implements TimerService.TimerCallback, SessionManager.SessionCallback, TimerManager.TimerSettingsChangeListener {
    private TextView timerText;
    private TextView timerSubText;
    private TextView currentTaskText;
    private FloatingActionButton btnPlayPause;
    private ImageButton btnReset;
    private ImageButton btnSkip;
    private Button btnFocus;
    private Button btnShortBreak;
    private Button btnLongBreak;
    private BottomNavigationView bottomNavView;
    private ProgressBar progressCircle;

    private View indicator1, indicator2, indicator3, indicator4;
    private View[] indicators;

    private TimerService timerService;
    private SessionManager sessionManager;

    private String currentPlanTitle = "Work Session";
    private Long currentPlanId = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // khởi tạo user session
        initializeUserSession();

        // khi user thay đổi cài đặt timer, HomePage sẽ tự cập nhật
        TimerManager.setTimerSettingsChangeListener(this);

        timerService = new TimerService(this);
        sessionManager = new SessionManager(this);

        // load cài đặt timer hiện tại của user
        TimerManager.loadTimerPreferences(this);

        initializeViews();
        setupClickListeners();

        timerService.initializeTimer(TimerMode.FOCUS);
        updateModeUI();
        sessionManager.updateSessionIndicators(indicators);

        // call API lấy ra plan của user
        fetchRecentPlan();
    }

    private void initializeViews() {
        timerText = findViewById(R.id.timerText);
        timerSubText = findViewById(R.id.timerSubText);
        currentTaskText = findViewById(R.id.currentTaskText);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnReset = findViewById(R.id.btnReset);
        btnSkip = findViewById(R.id.btnSkip);
        btnFocus = findViewById(R.id.btnFocus);
        btnShortBreak = findViewById(R.id.btnShortBreak);
        btnLongBreak = findViewById(R.id.btnLongBreak);
        bottomNavView = findViewById(R.id.bottomNavigation);
        progressCircle = findViewById(R.id.progressCircle);
        progressCircle.setMax(100);
        indicator1 = findViewById(R.id.indicator1);
        indicator2 = findViewById(R.id.indicator2);
        indicator3 = findViewById(R.id.indicator3);
        indicator4 = findViewById(R.id.indicator4);
        indicators = new View[]{indicator1, indicator2, indicator3, indicator4};
    }

    // khởi tạo session cho user, lấy ra username để load đúng setting của user đó
    private void initializeUserSession() {
        // lấy username đã đăng nhập
        String username = MyUtils.get(this, "username");

        if (username == null || username.trim().isEmpty()) {
            // nếu lần đầu sử dụng app thì tạo username tự động
            username = "user_" + System.currentTimeMillis() % 10000; // VD: user_1234
            MyUtils.save(this, "username", username);

            Toast.makeText(this, "Create new session: " + username, Toast.LENGTH_SHORT).show();
        }
        // load settings cho user này
        TimerManager.setCurrentUsername(username);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // kiểm tra user nào đang sử dụng
        String currentStoredUsername = MyUtils.get(this, "username");
        boolean usernameChanged = currentStoredUsername != null &&
                !currentStoredUsername.equals(TimerManager.getCurrentUsername());

        if (usernameChanged) {
            // nếu không phải user hiện tại, reload lại tất cả setting
            TimerManager.setCurrentUsername(currentStoredUsername);
            TimerManager.loadTimerPreferences(this); // Chỉ reload khi user change
            Toast.makeText(this, "Change user: " + currentStoredUsername, Toast.LENGTH_SHORT).show();

            // reset timer khi đổi user vì mỗi user có mỗi setting timer riêng
            if (!timerService.isTimerRunning()) {
                timerService.initializeTimer(timerService.getCurrentMode());
                updateModeUI();
            }

            fetchRecentPlan(); // reload recent plan cho user mới
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // lưu trạng thái timer hiện tại
        TimerManager.saveTimerState(this,
                timerService.getTimeLeftInMillis(),
                timerService.isTimerRunning(),
                timerService.getCurrentMode());

        // lưu cài đặt timer
        TimerManager.saveTimerPreferences(this);
    }

    // cập nhật setting timer mà không cần phải restart lại ứng dụng
    @Override
    public void onTimerSettingsChanged(int focusTime, int shortBreakTime, int longBreakTime) {
        runOnUiThread(() -> {
            // chỉ cập nhật timer khi không chạy
            if (!timerService.isTimerRunning()) {
                timerService.initializeTimer(timerService.getCurrentMode());
                updateModeUI();
            }

            // hiển thị thông báo cập nhật cho user
            Toast.makeText(this,
                    String.format("Updated: Focus=%d minutes, Short Break=%d minutes, Long Break=%d minutes",
                            focusTime, shortBreakTime, longBreakTime),
                    Toast.LENGTH_SHORT).show();
        });
    }

    // xử lí sự kiện các nút nhấn button
    private void setupClickListeners() {
        //  gọi sự kiện các button
        btnPlayPause.setOnClickListener(v -> {
            if (!timerService.isTimerRunning()) {
                timerService.startTimer();
            } else {
                showStopConfirmationDialog();
            }
        });
        btnReset.setOnClickListener(v -> {
            TimerAnimationHelper.animateButton(v);
            timerService.resetTimer();
            TimerAnimationHelper.animateReset(progressCircle);
        });
        btnSkip.setOnClickListener(v -> {
            TimerAnimationHelper.animateButton(v);
            sessionManager.moveToNextTask();
        });

        btnFocus.setOnClickListener(v -> {
            if (timerService.getCurrentMode() != TimerMode.FOCUS) {
                timerService.switchToMode(TimerMode.FOCUS);
                updateModeUI();
            }
            TimerAnimationHelper.animateButton(v);
        });

        btnShortBreak.setOnClickListener(v -> {
            if (timerService.getCurrentMode() != TimerMode.SHORT_BREAK) {
                timerService.switchToMode(TimerMode.SHORT_BREAK);
                updateModeUI();
            }
            TimerAnimationHelper.animateButton(v);
        });

        btnLongBreak.setOnClickListener(v -> {
            if (timerService.getCurrentMode() != TimerMode.LONG_BREAK) {
                timerService.switchToMode(TimerMode.LONG_BREAK);
                updateModeUI();
            }
            TimerAnimationHelper.animateButton(v);
        });
        // sửa tên task
        currentTaskText.setOnClickListener(v -> {
            EditTitleDialogFragment dialog = new EditTitleDialogFragment(
                    currentTaskText.getText().toString(),
                    newTitle -> {
                        currentTaskText.setText(newTitle);
                        PlanTaskResponseDTO currentTask = sessionManager.getCurrentTask();
                        if (currentTask != null) {
                            currentTask.setPlan_title(newTitle);
                        }
                        TimerAnimationHelper.animateTextChange(currentTaskText);
                    }
            );
            dialog.show(getSupportFragmentManager(), "EditTitleDialog");
        });

        timerText.setOnClickListener(v -> {
            Toast.makeText(this, "Chuyển sang Plan Screen", Toast.LENGTH_SHORT).show();
        });
    }

    // lấy recent plan từ API
    private void fetchRecentPlan() {
        String username = MyUtils.get(this, "username");
        if (username == null) {
            showDefaultTask(); //không tìm thấy user thì hiển thị task mặc định là "Work"
            return;
        }

        PomodoroAPI api = PomodoroService.getRetrofitInstance(username);
        Call<PlanResponseDTO> call = api.getRecentPlan();

        call.enqueue(new Callback<PlanResponseDTO>() {
            @Override
            public void onResponse(Call<PlanResponseDTO> call, Response<PlanResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PlanResponseDTO plan = response.body();
                    // cập nhật lại plan dc gọi từ API
                    currentPlanTitle = plan.getTitle();
                    currentPlanId = plan.getId();
                    currentTaskText.setText(plan.getTitle());

                    if (plan.getSteps() != null && !plan.getSteps().isEmpty()) {
                        sessionManager.initializeSession(plan.getSteps());

                        PlanTaskResponseDTO firstTask = plan.getSteps().get(0);

                        // nếu task có timer riêng thì cập nhật lại timer
                        if (firstTask.getPlan_duration() > 0) {
                            TimerManager.updateTimerModeFromSeconds(HomePage.this,
                                    TimerMode.FOCUS, firstTask.getPlan_duration());
                        }

                        // khởi tạo lại timer nếu đang ở Focus mode mà không chạy
                        if (timerService.getCurrentMode() == TimerMode.FOCUS
                                && !timerService.isTimerRunning()) {
                            timerService.initializeTimer(TimerMode.FOCUS);
                        }

                        sessionManager.updateSessionIndicators(indicators);
                    } else {
                        showDefaultTask(); // không có task
                    }
                } else {
                    showDefaultTask();
                }
            }

            @Override
            public void onFailure(Call<PlanResponseDTO> call, Throwable t) {
                showDefaultTask();
            }
        });
    }

    // đặt default name task là Work nếu không có plan/task từ API
    private void showDefaultTask() {
        currentTaskText.setText("Work");
    }

    // các nút nhấn dừng, hoàn thành, bắt đầu lại, tiếp tục
    private void showStopConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Tạm dừng phiên làm việc?")
                .setMessage("Bạn có muốn hoàn thành hoặc bắt đầu lại?")
                .setPositiveButton("Hoàn thành", (dialog, which) -> {
                    timerService.pauseTimer();
                    sessionManager.completeCurrentSession();
                    sessionManager.moveToNextTask();
                })
                .setNegativeButton("Bắt đầu lại", (dialog, which) -> {
                    timerService.resetTimer();
                    TimerAnimationHelper.animateReset(progressCircle);
                })
                .setNeutralButton("Tiếp tục", (dialog, which) -> {
                })
                .show();
    }

    private void showCompletionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("🎉 Hoàn thành!")
                .setMessage("Bạn đã hoàn thành tất cả các task! Chúc mừng bạn!")
                .setPositiveButton("Bắt đầu lại", (dialog, which) -> {
                    sessionManager.resetSession();
                    timerService.resetTimer();
                    TimerAnimationHelper.animateReset(progressCircle);
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void updateModeUI() {
        resetButtonStyle(btnFocus);
        resetButtonStyle(btnShortBreak);
        resetButtonStyle(btnLongBreak);

        Button activeButton;
        switch (timerService.getCurrentMode()) {
            case FOCUS:
                activeButton = btnFocus;
                timerSubText.setText("FOCUS TIME");
                break;
            case SHORT_BREAK:
                activeButton = btnShortBreak;
                timerSubText.setText("SHORT BREAK");
                break;
            case LONG_BREAK:
                activeButton = btnLongBreak;
                timerSubText.setText("LONG BREAK");
                break;
            default:
                activeButton = btnFocus;
                timerSubText.setText("FOCUS TIME");
        }

        setActiveButtonStyle(activeButton);
    }

    private void setActiveButtonStyle(Button button) {
        button.setBackgroundResource(R.drawable.rounded_button_primary);
        button.setTextColor(getResources().getColor(R.color.white, null));
        button.setElevation(4f);
        TimerAnimationHelper.animateButton(button);
    }

    private void resetButtonStyle(Button button) {
        button.setBackgroundResource(R.drawable.rounded_button_secondary);
        button.setTextColor(getResources().getColor(R.color.text_secondary, null));
        button.setElevation(1f);
    }

    // cập nhật UI mỗi giây
    @Override
    public void onTick(long millisUntilFinished, String formattedTime, int progressPercentage) {
        timerText.setText(formattedTime);
        progressCircle.setProgress(progressPercentage);
    }

    @Override
    public void onFinish() {
        if (progressCircle != null) {
            progressCircle.setProgress(0);
        }

        if (btnPlayPause != null) {
            btnPlayPause.setImageResource(R.drawable.ic_play);
            if (progressCircle != null) {
                TimerAnimationHelper.animateCompletion(progressCircle, btnPlayPause);
            }
        }

        // khi chạy xong 1 session thì tự chuyển sang task tiếp theo
        sessionManager.completeCurrentSession(); // đánh dấu complete
        sessionManager.moveToNextTask();
    }

    // cập nhật hình ảnh nút play/pause theo trạng thái
    @Override
    public void onTimerStateChanged(boolean isRunning) {
        btnPlayPause.setImageResource(isRunning ? R.drawable.ic_pause : R.drawable.ic_play);
    }

    @Override
    public void onTaskChanged(PlanTaskResponseDTO task) {
        if (task != null) {
            // có task mới thì cập nhật UI
            currentTaskText.setText(task.getPlan_title());

            // nếu task có timer riêng thì cập nhật lại timer duration
            if (task.getPlan_duration() > 0) {
                TimerManager.updateTimerModeFromSeconds(this,
                        TimerMode.FOCUS, task.getPlan_duration());
            }

            // khởi tạo lại timer nếu đang ở Focus mode mà không chạy
            if (timerService.getCurrentMode() == TimerMode.FOCUS
                    && !timerService.isTimerRunning()) {
                timerService.initializeTimer(TimerMode.FOCUS);
            }

            TimerAnimationHelper.animateTaskTransition(currentTaskText);
        } else {
            showDefaultTask();
        }

        sessionManager.updateSessionIndicators(indicators);
        Toast.makeText(this, "Chuyển sang task tiếp theo", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TimerManager.setTimerSettingsChangeListener(null);
        timerService.destroy();
        TimerAnimationHelper.clearAnimations(progressCircle, currentTaskText);
    }

    @Override
    public void onAllTasksCompleted() {
        // xong hết task thì popup noti chúc mừng
        showCompletionDialog();
    }

    @Override
    public void onSessionStatsUpdated(int completed, int current) {
        // chưa xài tới - hiển thị số session đã hoàn thành
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_homepage;
    }

    @Override
    protected int getCurrentMenuItemId() {
        return R.id.page_home;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}