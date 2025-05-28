package com.mobile.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.mobile.pomodoro.entity.PlanTask;
import com.mobile.pomodoro.request_dto.PlanRequestDTO;
import com.mobile.pomodoro.response_dto.MessageResponseDTO;
import com.mobile.pomodoro.service.PomodoroService;
import com.mobile.pomodoro.utils.LogObj;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanActivity extends NavigateActivity implements AddPlanFragment.OnPlanAddedListener {
        private RecyclerView recyclerView;
        private MaterialButton  btnSave, btnStart, btnExport, btnImport;
        private PlanAdapter adapter;
        private List<PlanTask> planList;
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
            adapter = new PlanAdapter(planList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

            // các button
            btnAdd.setOnClickListener(v -> {
                log.info("Add button clicked");
                showAddPlanDialog();
            });

            //Button "Save"
            btnSave.setOnClickListener(v -> {
                log.info("Save button clicked");
                savePlan();
            });
            btnStart.setOnClickListener(v -> {
                log.info("Start button clicked");
                startPlanWithoutSaving();
            });
            btnImport.setOnClickListener(v -> {
                log.info("Import button clicked");
                showImportPopup();
            });
            btnExport.setOnClickListener(v ->{
                log.info("Export button clicked");
                showExportPopup();
            });


//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.plan), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        }

        @Override
        public void onPlanAdded(PlanTask newPlan, int shortBreak, int longBreak, boolean isFirstTask) {
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
            TextView titlePlan = findViewById(R.id.titlePlan);
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

//        đổi từ plantask qua plantaskDTO
            List<PlanRequestDTO.PlanTaskDTO> taskList = new ArrayList<>();
            for (int i = 0; i < planList.size(); i++) {
                PlanTask task = planList.get(i);
                PlanRequestDTO.PlanTaskDTO dto = new PlanRequestDTO.PlanTaskDTO();
                dto.setPlan_title(task.getPlanName());
                dto.setPlan_duration(task.getDuration()*60);
                dto.setOrder(i + 1);
                taskList.add(dto);
            }
//        b2: tạo request
            PlanRequestDTO request = new PlanRequestDTO();
            request.setTitle(title);
            request.setS_break_duration(globalShortBreak *60);
            request.setL_break_duration(globalLongBreak *60);
            request.setSteps(taskList);

            log.info("Sending savePlan API request");

//        b3:gọi api
            PomodoroService.getClient().savePlan(request).enqueue(new Callback<MessageResponseDTO>() {
                @Override
                public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                    if (response.isSuccessful()) {
                        MessageResponseDTO responseDTO = response.body();
                        String message = responseDTO != null && responseDTO.getMessage() != null
                                ? responseDTO.getMessage()
                                : "Plan saved successfully";
                        Toast.makeText(PlanActivity.this, message, Toast.LENGTH_SHORT).show();
//                        recreate();// làm mới
//                     chuyển trang #home
                         Intent intent = new Intent(PlanActivity.this, HomePage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Xóa stack activity
                         startActivity(intent);
                         finish();
                    }else {
                        log.warn("savePlan API failed with HTTP: " + response.code());
                        Toast.makeText(PlanActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<MessageResponseDTO> call, Throwable t) {
                    log.error("savePlan failed: " + t.getMessage());
                    Toast.makeText(PlanActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


        private void startPlanWithoutSaving() {
            if (planList.isEmpty()) {
                log.warn("Start clicked with empty task list");
                Toast.makeText(this, "Vui lòng thêm công việc", Toast.LENGTH_SHORT).show();
                return;
            }

            TextView titleView = findViewById(R.id.titlePlan);
            String planTitle = titleView.getText().toString();


//            PlanTask latestPlan = planList.get(planList.size() - 1);
//     tạo requestDTO
            PlanRequestDTO request = new PlanRequestDTO();
            request.setTitle(planTitle);
            request.setS_break_duration(globalShortBreak *60 );
            request.setL_break_duration(globalLongBreak *60 );
// danh sách các bước
            List<PlanRequestDTO.PlanTaskDTO> steps = new ArrayList<>();
            for (int i = 0; i < planList.size(); i++) {
                PlanTask task = planList.get(i);
                PlanRequestDTO.PlanTaskDTO dto = new PlanRequestDTO.PlanTaskDTO();
                dto.setPlan_title(task.getPlanName());
                dto.setPlan_duration(task.getDuration() * 60);
                dto.setOrder(i + 1);
                steps.add(dto);
            }
            request.setSteps(steps);
// api
            PomodoroService.getClient().startPlan(request).enqueue(new Callback<MessageResponseDTO>() {
                @Override
                public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                    if (response.isSuccessful()) {
                        // Xử lý khi thành công
                        MessageResponseDTO responseDTO = response.body();
                        String message = responseDTO != null && responseDTO.getMessage() != null
                                ? responseDTO.getMessage()
                                : "Start Plan";

                        Toast.makeText(PlanActivity.this, message, Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(PlanActivity.this, HomePage.class);// Chuyển sang view home
                            // Truyền toàn bộ thông tin plan
                            intent.putExtra("plan_title", planTitle);
                            intent.putExtra("short_break", globalShortBreak);
                            intent.putExtra("long_break", globalLongBreak);

                            // Truyền danh sách tasks

                            Gson gson = new Gson();
                            String tasksJson = gson.toJson(planList);
                            intent.putExtra("tasks_json", tasksJson);
//                        intent.putParcelableArrayListExtra("latestPlan_tasks", new ArrayList<>(planList));
                            startActivity(intent);
                            finish(); // Đóng

                    }
                    else {
                        log.warn("startPlan failed - HTTP code: " + response.code());
                        Toast.makeText(PlanActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                }
                @Override
                public void onFailure(Call<MessageResponseDTO> call, Throwable t) {
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
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_plan;
    }

    @Override
    protected int getCurrentMenuItemId() {
        return R.id.page_task;
    }

    }