package com.mobile.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.mobile.pomodoro.entity.PlanTask;
import com.mobile.pomodoro.request_dto.PlanRequestDTO;
import com.mobile.pomodoro.response_dto.PlanResponseDTO;
import com.mobile.pomodoro.service.PomodoroAPI;
import com.mobile.pomodoro.service.PomodoroService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanActivity extends AppCompatActivity implements AddPlanFragment.OnPlanAddedListener {
        private RecyclerView recyclerView;
        private MaterialButton  btnSave, btnStart, btnExport, btnImport;
        private PlanAdapter adapter;
        private List<PlanTask> planList;
    private int globalShortBreak = 0;
    private int globalLongBreak = 0;
    private boolean hasBreakTimeSet = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_plan);

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
                showAddPlanDialog();
            });

            //Button "Save"
            btnSave.setOnClickListener(v -> {
                savePlan();
            });
            btnStart.setOnClickListener(v -> {
                startPlanWithoutSaving();
            });
            btnImport.setOnClickListener(v -> {
                showImportPopup();
            });
            btnExport.setOnClickListener(v ->{
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
                Toast.makeText(this, "Please add at least one task", Toast.LENGTH_SHORT).show();
                return;
            }
//            Lấy title
            TextView titlePlan = findViewById(R.id.titlePlan);
            String title = titlePlan.getText().toString().trim();
            if (title.isEmpty()) {
                title = "My Plan";
            }

            if (globalShortBreak <= 0 || globalLongBreak <= 0) {
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
//        b3:gọi api
            PomodoroService.getClient().savePlan(request).enqueue(new Callback<PlanResponseDTO>() {
                @Override
                public void onResponse(Call<PlanResponseDTO> call, Response<PlanResponseDTO> response) {
                    if (response.isSuccessful()) {
                        PlanResponseDTO responseDTO = response.body();
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
                        Toast.makeText(PlanActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PlanResponseDTO> call, Throwable t) {
//                log.error(t.getMessage());
                    Toast.makeText(PlanActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


        private void startPlanWithoutSaving() {
            if (planList.isEmpty()) {
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
            PomodoroService.getClient().startPlan(request).enqueue(new Callback<PlanResponseDTO>() {
                @Override
                public void onResponse(Call<PlanResponseDTO> call, Response<PlanResponseDTO> response) {
                    if (response.isSuccessful()) {
                        // Xử lý khi thành công
                        PlanResponseDTO responseDTO = response.body();
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
                        Toast.makeText(PlanActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                }
                @Override
                public void onFailure(Call<PlanResponseDTO> call, Throwable t) {
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
    }