package com.mobile.pomodoro;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.pomodoro.enums.ApplicationMode;
import com.mobile.pomodoro.enums.TimerMode;
import com.mobile.pomodoro.mapper.PlanMapper;
import com.mobile.pomodoro.response_dto.PlanResponseDTO;
import com.mobile.pomodoro.response_dto.PlanTaskResponseDTO;
import com.mobile.pomodoro.room.DatabaseClient;
import com.mobile.pomodoro.service.PomodoroAPI;
import com.mobile.pomodoro.service.PomodoroService;
import com.mobile.pomodoro.service.TimerService;
import com.mobile.pomodoro.utils.EditTitleDialogFragment;
import com.mobile.pomodoro.utils.LogObj;
import com.mobile.pomodoro.utils.MyUtils;
import com.mobile.pomodoro.utils.SessionManager;
import com.mobile.pomodoro.utils.Timer.TimerAnimationHelper;
import com.mobile.pomodoro.utils.Timer.TimerManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private LogObj log;

    private interface TaskCallback {
        void onTasksLoaded(PlanResponseDTO plan);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log = new LogObj();
        log.setName(getClass().getSimpleName());
        log.info("Initializing...");

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

        // Xử lý dữ liệu từ Intent (từ PlanActivity)
        handleIntentData();

        timerService.initializeTimer(TimerMode.FOCUS);
        updateModeUI();
        sessionManager.updateSessionIndicators(indicators);

        // call API lấy ra plan của user
//        fetchRecentPlan();
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

    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("plan_id") && intent.hasExtra("plan_title") && intent.hasExtra("tasks_json")) {
            // lấy dữ liệu từ Intent
            currentPlanId = intent.getLongExtra("plan_id", -1L);
            currentPlanTitle = intent.getStringExtra("plan_title");
            String tasksJson = intent.getStringExtra("tasks_json");

            // Parse tasks_json thành List
            Gson gson = new Gson();
            Type stepListType = new TypeToken<List<PlanTaskResponseDTO>>() {
            }.getType();
            List<PlanTaskResponseDTO> steps = gson.fromJson(tasksJson, stepListType);

            if (steps != null && !steps.isEmpty()) {
                // Cập nhật UI và SessionManager
                currentTaskText.setText(currentPlanTitle); // title plan
                sessionManager.initializeSession(new ArrayList<>(steps)); // chuyển sang List để SessionManager quản lý
                log.info("Steps loaded from intent: " + steps.size());

                // Cập nhật timer cho plan
                PlanTaskResponseDTO firstTask = steps.get(0);
                long firstDurationInMillis = firstTask.getPlan_duration() * 1000L;
                if (firstDurationInMillis > 0) {
                    TimerMode.FOCUS.updateDuration(firstDurationInMillis);
                    if (timerService.getCurrentMode() != TimerMode.FOCUS) {
                        timerService.switchToMode(TimerMode.FOCUS);
                    }
                    // Đặt thời gian cho timer
                    timerService.restoreTimerState(firstDurationInMillis, false);
                }

                sessionManager.updateSessionIndicators(indicators);
                if (log != null) {
                    log.info("Loaded plan from Intent: plan_id=" + currentPlanId + ", title=" + currentPlanTitle + ", steps=" + steps.size());
                }
            } else {
                // Fallback nếu steps rỗng
                showDefaultTask();
                fetchRecentPlan(); // Gọi API để lấy plan mặc định
            }
        } else {
            // Không có Intent thì lấy recent plan
            fetchRecentPlan();
        }
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
        boolean usernameChanged = currentStoredUsername != null && !currentStoredUsername.equals(TimerManager.getCurrentUsername());

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
        TimerManager.saveTimerState(this, timerService.getTimeLeftInMillis(), timerService.isTimerRunning(), timerService.getCurrentMode());

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
            Toast.makeText(this, String.format("Updated: Focus=%d minutes, Short Break=%d minutes, Long Break=%d minutes", focusTime, shortBreakTime, longBreakTime), Toast.LENGTH_SHORT).show();
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
            EditTitleDialogFragment dialog = new EditTitleDialogFragment(currentTaskText.getText().toString(), newTitle -> {
                currentTaskText.setText(newTitle);
                PlanTaskResponseDTO currentTask = sessionManager.getCurrentTask();
                if (currentTask != null) {
                    currentTask.setPlan_title(newTitle);
                }
                TimerAnimationHelper.animateTextChange(currentTaskText);
            });
            dialog.show(getSupportFragmentManager(), "EditTitleDialog");
        });

        timerText.setOnClickListener(v -> {
            Toast.makeText(this, "Chuyển sang Plan Screen", Toast.LENGTH_SHORT).show();
        });
    }

    // lấy recent plan từ API
    private void fetchRecentPlan() {
        // Check if our application is running online or offline
        // If online, call apis using retrofit
        // If offline, use application local storage
        if (MyUtils.applicationMode == ApplicationMode.ONLINE) {
            log.info("Application is ONLINE");

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

                        // cập nhật lại plan dc gọi từ API - USING FE_SavePlan format
                        String titleToShow = plan.getPlanTitle() != null ? plan.getPlanTitle() : "No Title";
                        Long idToShow = plan.getPlanId() != null ? plan.getPlanId() : -999L;

                        currentPlanTitle = titleToShow;
                        currentPlanId = idToShow;
                        currentTaskText.setText(titleToShow);

                        if (plan.getSteps() != null && !plan.getSteps().isEmpty()) {
                            sessionManager.initializeSession(plan.getSteps());

                            PlanTaskResponseDTO firstTask = plan.getSteps().get(0);

                            // nếu task có timer riêng thì cập nhật lại timer
                            if (firstTask.getPlan_duration() > 0) {
                                TimerManager.updateTimerModeFromSeconds(HomePage.this, TimerMode.FOCUS, firstTask.getPlan_duration());
                            }

                            if (timerService.getCurrentMode() == TimerMode.FOCUS && !timerService.isTimerRunning()) {
                                timerService.initializeTimer(TimerMode.FOCUS);
                            }

                            sessionManager.updateSessionIndicators(indicators);
                        } else {
                            showDefaultTask();
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
        } else {
            log.info("Application is OFFLINE");
            log.info("Loading recent task");
            /** 1. Get instance
             *  2. Fetch all saved plan
             *  3. Map to PlanResponseDTO
             *  4. Update UI
             */
            var app = DatabaseClient.getInstance(HomePage.this).getAppDatabase();

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    var plan = app.plan().getAll();
                    var mapper = PlanMapper.getInstance();
                    var dto = mapper.mapToDTO(plan.get(0));

                    runOnUiThread(() -> {
                        ((TaskCallback) this::updatePlan).onTasksLoaded(dto);
                    });
                } catch (Exception e) {
                    // If there is no task exist, load default
                    runOnUiThread(this::showDefaultTask);
                }
            });
            executor.shutdown();
        }
    }

    // đặt default name task là Work nếu không có plan/task từ API
    private void showDefaultTask() {
        log.warn("There is no task, show default task");
        currentTaskText.setText("Work");
    }

    private void updatePlan(PlanResponseDTO plan) {
        // USING FE_SavePlan format
        currentPlanTitle = plan.getPlanTitle();
        currentPlanId = plan.getPlanId();
        currentTaskText.setText(plan.getPlanTitle());

        if (plan.getSteps() != null && !plan.getSteps().isEmpty()) {
            sessionManager.initializeSession(plan.getSteps());

            PlanTaskResponseDTO firstTask = plan.getSteps().get(0);

            // nếu task có timer riêng thì cập nhật lại timer
            if (firstTask.getPlan_duration() > 0) {
                TimerManager.updateTimerModeFromSeconds(HomePage.this, TimerMode.FOCUS, firstTask.getPlan_duration());
            }

            if (timerService.getCurrentMode() == TimerMode.FOCUS && !timerService.isTimerRunning()) {
                timerService.initializeTimer(TimerMode.FOCUS);
            }

            sessionManager.updateSessionIndicators(indicators);
        } else {
            showDefaultTask(); // không có task
        }
    }

    // các nút nhấn dừng, hoàn thành, bắt đầu lại, tiếp tục
    private void showStopConfirmationDialog() {
        new AlertDialog.Builder(this).setTitle("Tạm dừng phiên làm việc?").setMessage("Bạn có muốn hoàn thành hoặc bắt đầu lại?").setPositiveButton("Hoàn thành", (dialog, which) -> {
            timerService.pauseTimer();
            sessionManager.completeCurrentSession();
            sessionManager.moveToNextTask();
        }).setNegativeButton("Bắt đầu lại", (dialog, which) -> {
            timerService.resetTimer();
            TimerAnimationHelper.animateReset(progressCircle);
        }).setNeutralButton("Tiếp tục", (dialog, which) -> {
        }).show();
    }

    private void showCompletionDialog() {
        new AlertDialog.Builder(this).setTitle("🎉 Hoàn thành!").setMessage("Bạn đã hoàn thành tất cả các task! Chúc mừng bạn!").setPositiveButton("Bắt đầu lại", (dialog, which) -> {
            sessionManager.resetSession();
            timerService.resetTimer();
            TimerAnimationHelper.animateReset(progressCircle);
        }).setNegativeButton("Đóng", null).show();
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
            // tự chuyển về tab focus khi có task
            if (timerService.getCurrentMode() != TimerMode.FOCUS) {
                timerService.switchToMode(TimerMode.FOCUS);
                updateModeUI(); // hiển thị tab active
                Toast.makeText(this, "Chuyển về Focus mode", Toast.LENGTH_SHORT).show();
            }

            // nếu task có timer riêng thì cập nhật lại timer duration
            if (task.getPlan_duration() > 0) {
                // Cập nhật duration cho FOCUS mode (vì đã switch về FOCUS rồi)
                TimerManager.updateTimerModeFromSeconds(this, TimerMode.FOCUS, task.getPlan_duration());

                // Khởi tạo lại timer với thời gian mới
                if (!timerService.isTimerRunning()) {
                    timerService.initializeTimer(TimerMode.FOCUS);
                }
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

    // xoá lun rồi nha ae :v
    @Override
    public void onSessionStatsUpdated(int completed, int current) {
        // chưa xài tới - hiển thị số session đã hoàn thành
    }

    @Override
    public void onModeAutoSwitch(TimerMode newMode) {
        // tự chuyển mode khi phát hiện task mới
        if (timerService.getCurrentMode() != newMode) {
            timerService.switchToMode(newMode);
            updateModeUI();

            // Khởi tạo lại timer với mode mới
            timerService.initializeTimer(newMode);

            // Hiển thị thông báo cho user biết đã chuyển mode
            String modeName = "";
            switch (newMode) {
                case FOCUS:
                    modeName = "Focus Time";
                    break;
                case SHORT_BREAK:
                    modeName = "Short Break";
                    break;
                case LONG_BREAK:
                    modeName = "Long Break";
                    break;
            }
            Toast.makeText(this, "Chuyển sang " + modeName, Toast.LENGTH_SHORT).show();
        }
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