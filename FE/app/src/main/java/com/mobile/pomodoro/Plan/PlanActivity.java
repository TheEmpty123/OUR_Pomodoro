package com.mobile.pomodoro.Plan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.mobile.pomodoro.HomePage;
import com.mobile.pomodoro.NavigateActivity;
import com.mobile.pomodoro.R;
import com.mobile.pomodoro.request_dto.DailyTaskRequestDTO;
import com.mobile.pomodoro.request_dto.DailyTaskUpdateRequestDTO;
import com.mobile.pomodoro.request_dto.PlanRequestDTO;
import com.mobile.pomodoro.request_dto.PlanTaskDTO;
import com.mobile.pomodoro.response_dto.DailyTaskDetailResponseDTO;
import com.mobile.pomodoro.response_dto.MessageResponseDTO;
import com.mobile.pomodoro.response_dto.PlanEditResponseDTO;
import com.mobile.pomodoro.response_dto.PlanResponseDTO;
import com.mobile.pomodoro.response_dto.SavePlanResponseDTO;
import com.mobile.pomodoro.service.PomodoroService;
import com.mobile.pomodoro.utils.LogObj;
import com.mobile.pomodoro.utils.MyUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanActivity extends NavigateActivity implements AddPlanFragment.OnPlanAddedListener {
    private RecyclerView recyclerView;
    private MaterialButton btnSave, btnStart, btnExport, btnImport;
    private MaterialButton btnAdd;
    private EditText titlePlan;
    private PlanAdapter adapter;
    private List<PlanTaskDTO> planList;
    private LogObj log;
    private int globalShortBreak = 0;
    private int globalLongBreak = 0;
    private boolean hasBreakTimeSet = false;
    private boolean isDailyTaskMode;
    private boolean isEditMode;
    private long planId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log = new LogObj();
        log.setName(getClass().getSimpleName());
        log.info("onCreate - Initializing PlanActivity");
        EdgeToEdge.enable(this);
//            setContentView(R.layout.activity_plan);
//            setContentView(getLayoutResourceId());

        btnAdd = findViewById(R.id.btnAdd);
        btnSave = findViewById(R.id.btnSave);
        btnStart = findViewById(R.id.btnStart);
        btnImport = findViewById(R.id.btnImport);
        btnExport = findViewById(R.id.btnExport);
        recyclerView = findViewById(R.id.recyclerPlan);
        titlePlan = findViewById(R.id.titlePlan);

        planList = new ArrayList<>();
        adapter = new PlanAdapter(planList); //  adapter kết nối dl với RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Lấy Intent extras
        Intent intent = getIntent();
        isDailyTaskMode = intent.getBooleanExtra("isDailyTaskMode", false);
        isEditMode = intent.getBooleanExtra("isEditMode", false);
        planId = intent.getLongExtra("planId", -1);
        log.info("Intent extras: isDailyTaskMode=" + isDailyTaskMode + ", isEditMode=" + isEditMode + ", planId=" + planId);

        // các button
        configureButtons();

        // Tải dữ liệu nếu ở chế độ chỉnh sửa
        if (isEditMode && planId != -1) {
            loadPlanForEdit();
        } else if (isEditMode) {
            log.warn("Edit mode enabled but planId is invalid: " + planId);
            Toast.makeText(this, "ID kế hoạch không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void configureButtons() {
        if (isDailyTaskMode && !isEditMode) { //thêm dailytask mới
            btnSave.setText("Add Daily");
            btnSave.setVisibility(View.VISIBLE);
            btnStart.setVisibility(View.GONE);
            btnImport.setVisibility(View.GONE);
            btnExport.setVisibility(View.GONE);
            btnSave.setOnClickListener(v -> showAddDailyTaskPopup());
        } else if (isDailyTaskMode && isEditMode) { // cập nhập dailytask
            btnSave.setText("Save");
            btnStart.setText("Start");
            btnImport.setText("Delete");
            btnExport.setText("Complete");
            btnSave.setOnClickListener(v -> updateDailyTask());
            btnStart.setOnClickListener(v -> startPlanWithoutSaving());
            btnImport.setOnClickListener(v -> deleteDailyTask());
            btnExport.setOnClickListener(v -> completeDailyTask());
        } else { // khi mở trang plan
            btnSave.setText("Save");
            btnStart.setText("Start");
            btnImport.setText("Import");
            btnExport.setText("Export");
            btnSave.setOnClickListener(v -> savePlan());
            btnStart.setOnClickListener(v -> startPlanWithoutSaving());
            btnImport.setOnClickListener(v -> showImportPopup());
            btnExport.setOnClickListener(v -> showExportPopup());
        }

        btnAdd.setOnClickListener(v -> {
            log.info("Add button clicked");
            showAddPlanDialog();
        });
    }
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.plan), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//        }

    //Callback khi thêm plan
    @Override
    public void onPlanAdded(PlanTaskDTO newPlan, int shortBreak, int longBreak, boolean isFirstTask) {
        if (isFirstTask) {
            globalShortBreak = shortBreak;
            globalLongBreak = longBreak;
            hasBreakTimeSet = true;
        }

        // Áp dụng break time cho các task sau
        newPlan.setShortBreak(globalShortBreak);
        newPlan.setLongBreak(globalLongBreak);
        newPlan.setOrder(planList.size() + 1);
        planList.add(newPlan);
        adapter.notifyItemInserted(planList.size() - 1);
    }

    private void showAddPlanDialog() {
        boolean isFirstTask = !hasBreakTimeSet;
        AddPlanFragment fragment = AddPlanFragment.newInstance(isFirstTask, globalShortBreak, globalLongBreak);
        fragment.show(getSupportFragmentManager(), "AddPlanFragment");
    }

    //api save plan
    private void savePlan() {
//        B1: ktr danh sách task
        if (planList.isEmpty()) {
            log.warn("Attempt to save empty plan list");
            Toast.makeText(this, "Please add at least one task", Toast.LENGTH_SHORT).show();
            return;
        }
//            Lấy title
        String title = titlePlan.getText().toString().trim();
        if (title.isEmpty()) {
            log.warn("Title is empty. Defaulting to 'My Plan'");
            title = "My Plan";
        }

        if (globalShortBreak <= 0 || globalLongBreak <= 0) {
            log.warn("Break time invalid: short=" + globalShortBreak + ", long=" + globalLongBreak);
            Toast.makeText(this, "Please set valid break times", Toast.LENGTH_SHORT).show();
            return;
        }
//            Thiết lập order
        for (int i = 0; i < planList.size(); i++) {
            planList.get(i).setOrder(i + 1);
            planList.get(i).setPlan_duration(planList.get(i).getPlan_duration() * 60);
        }
//        b2: tạo requestDTO
        PlanRequestDTO request = new PlanRequestDTO();
        request.setTitle(title);
        request.setS_break_duration(globalShortBreak * 60);
        request.setL_break_duration(globalLongBreak * 60);
        request.setSteps(planList);
//  gọi api gửi cho BE và nhận lại recent_plan
        var username = MyUtils.get(this, "username"); // Lấy username
        if (username == null || username.trim().isEmpty()) {
            log.error("Username is null or empty");
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }
        PomodoroService.getRetrofitInstance(username).savePlan(request).enqueue(new Callback<SavePlanResponseDTO>() {
            @Override
            public void onResponse(Call<SavePlanResponseDTO> call, Response<SavePlanResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SavePlanResponseDTO result = response.body();
                    Log.d("SAVE_PLAN_SUCCESS", "Plan saved with ID: " + result.getPlanId());
                    runOnUiThread(() -> {
                        Toast.makeText(PlanActivity.this, "Plan saved successfully!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(PlanActivity.this, HomePage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    Log.w("SAVE_PLAN_FAILURE", "Response not successful or body is null");
                    Toast.makeText(PlanActivity.this, "Failed to save plan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SavePlanResponseDTO> call, Throwable t) {
                Log.e("API_FAILURE", "Request failed: " + t.getMessage());
                log.error("savePlan failed: " + t.getMessage());
                Toast.makeText(PlanActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // api start plan
    private void startPlanWithoutSaving() {
        // Kiểm tra ds
        if (planList.isEmpty()) {
            log.warn("Start clicked with empty task list");
            Toast.makeText(this, "Please add at least one task", Toast.LENGTH_SHORT).show();
            return;
        }
        // Lấy title
        String planTitle = titlePlan.getText().toString();
        if (planTitle.isEmpty()) {
            log.warn("Title is empty. Defaulting to 'My Plan'");
            planTitle = "My Plan";
        }

//            Thiết lập order
        for (int i = 0; i < planList.size(); i++) {
            PlanTaskDTO task = planList.get(i);
            int duration = task.getPlan_duration() * 60;
            task.setOrder(i + 1);
            task.setPlan_duration(duration);

            log.info("After: Task " + i + " - order=" + task.getOrder() + ", duration=" + task.getPlan_duration());
        }
        log.info("Plan list size before sending: " + planList.size());
//     tạo requestDTO
        PlanRequestDTO request = new PlanRequestDTO();
        request.setTitle(planTitle);
        request.setS_break_duration(globalShortBreak * 60);
        request.setL_break_duration(globalLongBreak * 60);
        request.setSteps(planList);
        log.info("Sending startPlanWithoutSaving API request");

// api : nhận dl từ BE và hiển thị sang home
        var username = MyUtils.get(this, "username"); // Lấy username
        if (username == null || username.trim().isEmpty()) {
            log.error("Username is null or empty");
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }
        PomodoroService.getRetrofitInstance(username).startPlan(request).enqueue(new Callback<PlanResponseDTO>() {
            @Override
            public void onResponse(Call<PlanResponseDTO> call, Response<PlanResponseDTO> response) {
                Log.d("START_API_RESPONSE", "Status Code: " + response.code());

                if (response.body() != null) {
                    Log.d("START_API_RESPONSE", "Response: " + new Gson().toJson(response.body()));
                }

                // Log error nếu có
                if (!response.isSuccessful() && response.errorBody() != null) {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("START_API_ERROR", "Error Body: " + errorBody);
                    } catch (IOException e) {
                        Log.e("START_API_ERROR", "Cannot read error body: " + e.getMessage());
                    }
                }
                log.info("onResponse called");
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(PlanActivity.this, "Failed to load plan", Toast.LENGTH_SHORT).show();
                    return;
                }
                log.info("Response is successful");
                Toast.makeText(PlanActivity.this, "Success", Toast.LENGTH_SHORT).show();

                PlanResponseDTO startplan = response.body();

                Intent intent = new Intent(PlanActivity.this, HomePage.class);// Chuyển sang view home
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // Truyền toàn bộ thông tin plan
                intent.putExtra("plan_title", startplan.getPlanTitle());
                intent.putExtra("plan_id", startplan.getPlanId());

//                            // Truyền danh sách tasks dưới dạng JSON
                Gson gson = new Gson();
                String tasksJson = gson.toJson(startplan.getSteps());
                intent.putExtra("tasks_json", tasksJson);
                log.info("Sending to Home: " + tasksJson);
                startActivity(intent);
                finish(); // Đóng

            }

            @Override
            public void onFailure(Call<PlanResponseDTO> call, Throwable t) {
                Log.e("START_API_FAILURE", "Request failed: " + t.getMessage());
                log.error("startPlan failed: " + t.getMessage());
                Toast.makeText(PlanActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showExportPopup() {
    }

    private void showImportPopup() {
    }

    //fragment nhập mô tả cho dailytask
    private void showAddDailyTaskPopup() {
        new AlertDialog.Builder(this)
                .setTitle("Enter Daily Task description")
                .setView(R.layout.popup_add_daily_task)
                .setPositiveButton("Confiirm", (dialog, which) -> {
                    EditText input = ((AlertDialog) dialog).findViewById(R.id.inputDescription);
                    String description = input.getText().toString().trim();
                    if (description.isEmpty()) {
                        Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    saveDailyTask(description);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // api save dailytask
    private void saveDailyTask(String description) {
        if (planList.isEmpty()) {
            log.warn("Attempt to save empty plan list");
            Toast.makeText(this, "Vui lòng thêm ít nhất một công việc", Toast.LENGTH_SHORT).show();
            return;
        }
        String title = titlePlan.getText().toString().trim();
        if (title.isEmpty()) {
            log.warn("Title is empty. Defaulting to 'My Plan'");
            title = "My Plan";
        }
        if (globalShortBreak <= 0 || globalLongBreak <= 0) {
            log.warn("Break time invalid: short=" + globalShortBreak + ", long=" + globalLongBreak);
            Toast.makeText(this, "Vui lòng đặt thời gian nghỉ hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < planList.size(); i++) {
            planList.get(i).setOrder(i + 1);
            planList.get(i).setPlan_duration(planList.get(i).getPlan_duration() * 60);
        }
        DailyTaskRequestDTO request = DailyTaskRequestDTO.builder()
                .daily_task_description(description)
                .title(title)
                .s_break_duration(globalShortBreak * 60)
                .l_break_duration(globalLongBreak * 60)
                .steps(planList)
                .build();

        var username = MyUtils.get(this, "username");
        if (username == null || username.trim().isEmpty()) {
            log.error("Username is null or empty");
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }
        log.info("Sending DailyTaskRequestDTO: " + new Gson().toJson(request));
        PomodoroService.getRetrofitInstance(username).createDailyTask(request).enqueue(new Callback<MessageResponseDTO>() {
            @Override
            public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMessage().equals("Succeed")) {
                    log.info("Daily Task saved successfully");
                    Toast.makeText(PlanActivity.this, "Added Daily Task successfully", Toast.LENGTH_SHORT).show();
                    //Hoàn thành chuyển lại trang DailyTask
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    log.warn("Failed to save Daily Task");
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "error";
                        Toast.makeText(PlanActivity.this, "FAILED: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(PlanActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MessageResponseDTO> call, Throwable t) {
                log.error("saveDailyTask failed: " + t.getMessage());
                Toast.makeText(PlanActivity.this, "ERROR: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // api load dailytask for edit
    private void loadPlanForEdit() {
        var username = MyUtils.get(this, "username");
        if (username == null || username.trim().isEmpty()) {
            log.error("Username is null or empty");
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (planId <= 0) {
            log.error("Invalid planId: " + planId);
            Toast.makeText(this, "ID kế hoạch không hợp lệ", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        log.info("Loading plan for edit, planId: " + planId);
        PomodoroService.getRetrofitInstance(username).getPlanToEdit(planId).enqueue(new Callback<DailyTaskDetailResponseDTO>() {
            @Override
            public void onResponse(Call<DailyTaskDetailResponseDTO> call, Response<DailyTaskDetailResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DailyTaskDetailResponseDTO plan = response.body();
                    titlePlan.setText(plan.getTitle() != null ? plan.getTitle() : "");
                    globalShortBreak = plan.getS_break_duration() / 60;
                    globalLongBreak = plan.getL_break_duration() / 60;
                    hasBreakTimeSet = true;
                    planList.clear();
                    List<PlanEditResponseDTO.PlanTaskEditResponseDTO> steps = plan.getSteps();
                    if (steps != null) {
                        for (PlanEditResponseDTO.PlanTaskEditResponseDTO responseTask : steps) {
                            PlanTaskDTO task = PlanTaskDTO.builder()
                                    .plan_title(responseTask.getPlan_title())
                                    .plan_duration(responseTask.getPlan_duration() / 60) // Giây sang phút
                                    .order(responseTask.getOrder())
                                    .shortBreak(globalShortBreak)
                                    .longBreak(globalLongBreak)
                                    .build();
                            planList.add(task);
                            log.info("ResponseTask: " + new Gson().toJson(responseTask));

                        }
                    }
                    adapter.notifyDataSetChanged();
                    log.info("Loaded plan successfully: " + new Gson().toJson(plan));
                    Toast.makeText(PlanActivity.this, "SUCCESS", Toast.LENGTH_SHORT).show();
                } else {
                    log.warn("Failed to load plan for edit " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "error";
                        Toast.makeText(PlanActivity.this, " LOAD FAILED " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(PlanActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
            }

            @Override
            public void onFailure(Call<DailyTaskDetailResponseDTO> call, Throwable t) {
                log.error("loadPlanForEdit failed: " + t.getMessage());
                Toast.makeText(PlanActivity.this, "ERROR: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // api cập nhập dailytask
    private void updateDailyTask() {
        if (planList.isEmpty()) {
            log.warn("Attempt to save empty plan list");
            Toast.makeText(this, "Vui lòng thêm ít nhất một công việc", Toast.LENGTH_SHORT).show();
            return;
        }
        String title = titlePlan.getText().toString().trim();
        if (title.isEmpty()) {
            log.warn("Title is empty. Defaulting to 'My Plan'");
            title = "My Plan";
        }
        if (globalShortBreak <= 0 || globalLongBreak <= 0) {
            log.warn("Break time invalid: short=" + globalShortBreak + ", long=" + globalLongBreak);
            Toast.makeText(this, "Vui lòng đặt thời gian nghỉ hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < planList.size(); i++) {
            planList.get(i).setOrder(i + 1);
            planList.get(i).setPlan_duration(planList.get(i).getPlan_duration() * 60);
        }
        DailyTaskUpdateRequestDTO request = DailyTaskUpdateRequestDTO.builder()
//                .daily_task_description(title)
                .title(title)
                .s_break_duration(globalShortBreak * 60)
                .l_break_duration(globalLongBreak * 60)
                .steps(planList)
                .build();
        log.info("Sending DailyTaskRequestDTO: " + new Gson().toJson(request));
        var username = MyUtils.get(this, "username");
        PomodoroService.getRetrofitInstance(username).updateDailyTask(planId, request).enqueue(new Callback<MessageResponseDTO>() {
            @Override
            public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMessage().equals("Succeed")) {
                    log.info("Daily Task updated successfully");
                    Toast.makeText(PlanActivity.this, "UPDATE SUCCESS", Toast.LENGTH_SHORT).show();
                    //Hoàn thành chuyển lại trang DailyTask
                    Intent resultIntent = new Intent();
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    log.warn("Failed to update Daily Task");
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Toast.makeText(PlanActivity.this, "Update Failed: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(PlanActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MessageResponseDTO> call, Throwable t) {
                log.error("updateDailyTask failed: " + t.getMessage());
                Toast.makeText(PlanActivity.this, "ERROR: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Xóa dailytassk
    private void deleteDailyTask() {
        var username = MyUtils.get(this, "username");
        PomodoroService.getRetrofitInstance(username).deleteDailyTask(planId).enqueue(new Callback<MessageResponseDTO>() {
            @Override
            public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                if (response.isSuccessful()) {
                    MessageResponseDTO body = response.body();
                    if (body != null && body.getMessage().toLowerCase().contains("thành công")) {
                        log.info("Daily Task deleted successfully");
                        Toast.makeText(PlanActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                        //Hoàn thành chuyển lại trang DailyTask
                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        log.warn("Delete failed with message: " + (body != null ? body.getMessage() : "null"));
                        Toast.makeText(PlanActivity.this, "Delete failed: " + (body != null ? body.getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PlanActivity.this, "Delete failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponseDTO> call, Throwable t) {
                log.error("deleteDailyTask failed: " + t.getMessage());
                Toast.makeText(PlanActivity.this, "ERROR: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //api đánh dấu hoàn thành
    private void completeDailyTask() {
        var username = MyUtils.get(this, "username");
        PomodoroService.getRetrofitInstance(username).completeDailyTask(planId).enqueue(new Callback<MessageResponseDTO>() {
            @Override
            public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                if (response.isSuccessful()) {
                    MessageResponseDTO body = response.body();
                    if (body != null && body.getMessage().toLowerCase().contains("hoàn thành")) {
                        log.info("Daily Task marked as completed");
                        Toast.makeText(PlanActivity.this, "Marked as completed", Toast.LENGTH_SHORT).show();
                        //Hoàn thành chuyển lại trang DailyTask
                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        log.warn("Complete failed with message: " + (body != null ? body.getMessage() : "null"));
                        Toast.makeText(PlanActivity.this, "Mark complete failed: " + (body != null ? body.getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PlanActivity.this, "Mark complete failed:  " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponseDTO> call, Throwable t) {
                log.error("completeDailyTask failed: " + t.getMessage());
                Toast.makeText(PlanActivity.this, "ERROR: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // lấy chi tiết 1 dailytask cho trang home
    private void startDailyTaskEdit() {
        if (planId <= 0) {
            log.error("Invalid planId: " + planId);
            Toast.makeText(this, "ID kế hoạch không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy username
        var username = MyUtils.get(this, "username");
        if (username == null || username.trim().isEmpty()) {
            log.error("Username is null or empty");
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi API
        log.info("Loading dailytask " + planId);
        PomodoroService.getRetrofitInstance(username).getDailyTaskDetails(planId).enqueue(new Callback<PlanResponseDTO>() {
            @Override
            public void onResponse(Call<PlanResponseDTO> call, Response<PlanResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PlanResponseDTO plan = response.body();
                    log.info("Received dailytask details: " + new Gson().toJson(plan));

                    // Chuyển sang HomePage
                    Intent intent = new Intent(PlanActivity.this, HomePage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("plan_id", plan.getPlanId());
                    intent.putExtra("plan_title", plan.getPlanTitle());


                    // Truyền danh sách steps dưới dạng JSON
                    Gson gson = new Gson();
                    String stepsJson = gson.toJson(plan.getSteps());
                    intent.putExtra("tasks_json", stepsJson);
                    startActivity(intent);
                    finish(); // Đóng PlanActivity
                } else {
                    log.warn("Failed to load daily task details, HTTP code: " + response.code());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "error";
                        Toast.makeText(PlanActivity.this, "Failed to load dailytask: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(PlanActivity.this, "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<PlanResponseDTO> call, Throwable t) {
                log.error("getDailyTaskDetails failed: " + t.getMessage());
                Toast.makeText(PlanActivity.this, "ERROR: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // dùng cho Navbar
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_plan;
    }

    @Override
    protected int getCurrentMenuItemId() {
        return R.id.page_plan;
    }

}