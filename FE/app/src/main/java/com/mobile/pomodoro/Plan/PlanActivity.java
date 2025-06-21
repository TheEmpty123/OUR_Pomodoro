package com.mobile.pomodoro.Plan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.mobile.pomodoro.AddPlanFragment;
import com.mobile.pomodoro.HomePage;
import com.mobile.pomodoro.NavigateActivity;
import com.mobile.pomodoro.R;
import com.mobile.pomodoro.request_dto.PlanRequestDTO;
import com.mobile.pomodoro.request_dto.PlanTaskDTO;
import com.mobile.pomodoro.response_dto.PlanResponseDTO;
import com.mobile.pomodoro.service.PomodoroService;
import com.mobile.pomodoro.utils.LogObj;
import com.mobile.pomodoro.utils.MyUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanActivity extends NavigateActivity implements AddPlanFragment.OnPlanAddedListener {
    private RecyclerView recyclerView;
    private MaterialButton btnSave, btnStart, btnExport, btnImport;
    private PlanAdapter adapter;
    private List<PlanTaskDTO> planList;
    private LogObj log;
    private int globalShortBreak = 0;
    private int globalLongBreak = 0;
    private boolean hasBreakTimeSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log = new LogObj();
        log.setName(getClass().getSimpleName());
        log.info("onCreate - Initializing PlanActivity");
        EdgeToEdge.enable(this);
//            setContentView(R.layout.activity_plan);

        FloatingActionButton btnAdd = findViewById(R.id.btnAdd);
        btnSave = findViewById(R.id.btnSave);
        btnStart = findViewById(R.id.btnStart);
        btnImport = findViewById(R.id.btnImport);
        btnExport = findViewById(R.id.btnExport);
        recyclerView = findViewById(R.id.recyclerPlan);

        planList = new ArrayList<>();
        adapter = new PlanAdapter(planList); //  adapter kết ối dl với RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // button "Add"
        btnAdd.setOnClickListener(v -> {
            log.info("Add button clicked");
            showAddPlanDialog();
        });

        //Button "Save"
        btnSave.setOnClickListener(v -> {
            log.info("Save button clicked");
            savePlan();
        });
        // button "Start"
        btnStart.setOnClickListener(v -> {
            log.info("Start button clicked");
            startPlanWithoutSaving();
        });
        btnImport.setOnClickListener(v -> {
            log.info("Import button clicked");
            showImportPopup();
        });
        btnExport.setOnClickListener(v -> {
            log.info("Export button clicked");
            showExportPopup();
        });


//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.plan), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
    }

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

        planList.add(newPlan);
        adapter.notifyItemInserted(planList.size() - 1);
    }

    private void showAddPlanDialog() {
        boolean isFirstTask = !hasBreakTimeSet;
        AddPlanFragment fragment = AddPlanFragment.newInstance(isFirstTask, globalShortBreak, globalLongBreak);
        fragment.show(getSupportFragmentManager(), "AddPlanFragment");
    }


    private void savePlan() {
//        B1: ktr danh sách task
        if (planList.isEmpty()) {
            log.warn("Attempt to save empty plan list");
            Toast.makeText(this, "Please add at least one task", Toast.LENGTH_SHORT).show();
            return;
        }
//            Lấy title
        EditText titlePlan = findViewById(R.id.titlePlan);
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
            Toast.makeText(this, "Vui lòng đăng nhập để cập nhật todo", Toast.LENGTH_SHORT).show();
            return;
        }
        PomodoroService.getRetrofitInstance(username).savePlan(request).enqueue(new Callback<PlanResponseDTO>() {
            @Override
            public void onResponse(Call<PlanResponseDTO> call, Response<PlanResponseDTO> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    log.warn("Failed to receive recent plan");
                    Toast.makeText(PlanActivity.this, "Không nhận được kế hoạch", Toast.LENGTH_SHORT).show();
                    return;
                }
                PlanResponseDTO plan = response.body();
                Log.d("API Response", "Response body: " + new Gson().toJson(plan));
                Toast.makeText(PlanActivity.this, "Success", Toast.LENGTH_SHORT).show();

//                     chuyển trang #home
                Intent intent = new Intent(PlanActivity.this, HomePage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Truyền toàn bộ thông tin plan
                intent.putExtra("plan_id", plan.getId());
                intent.putExtra("plan_title", plan.getTitle());

                // Truyền danh sách tasks dưới dạng JSON
                Gson gson = new Gson();
                String tasksJson = gson.toJson(plan.getSteps());
                intent.putExtra("tasks_json", tasksJson);

                startActivity(intent);
                finish(); // Đóng
//
            }

            @Override
            public void onFailure(Call<PlanResponseDTO> call, Throwable t) {
                log.error("savePlan failed: " + t.getMessage());
                Toast.makeText(PlanActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void startPlanWithoutSaving() {
        // Kiểm tra ds
        if (planList.isEmpty()) {
            log.warn("Start clicked with empty task list");
            Toast.makeText(this, "Please add at least one task", Toast.LENGTH_SHORT).show();
            return;
        }
        // Lấy title
        EditText titleView = findViewById(R.id.titlePlan);
        String planTitle = titleView.getText().toString();
        if (planTitle.isEmpty()) {
            log.warn("Title is empty. Defaulting to 'My Plan'");
            planTitle = "My Plan";
        }

//            Thiết lập order
        for (int i = 0; i < planList.size(); i++) {
            planList.get(i).setOrder(i + 1);
        }
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
            Toast.makeText(this, "Vui lòng đăng nhập để cập nhật todo", Toast.LENGTH_SHORT).show();
            return;
        }
        PomodoroService.getRetrofitInstance(username).startPlan(request).enqueue(new Callback<PlanResponseDTO>() {
            @Override
            public void onResponse(Call<PlanResponseDTO> call, Response<PlanResponseDTO> response) {
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
                intent.putExtra("plan_title", startplan.getTitle());
                intent.putExtra("plan_id", startplan.getId());

//                            // Truyền danh sách tasks dưới dạng JSON
                Gson gson = new Gson();
                String tasksJson = gson.toJson(startplan.getSteps());
                intent.putExtra("tasks_json", tasksJson);

                startActivity(intent);
                finish(); // Đóng

            }

            @Override
            public void onFailure(Call<PlanResponseDTO> call, Throwable t) {
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