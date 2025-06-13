package com.mobile.pomodoro.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobile.pomodoro.response_dto.DailyTaskListResponseDTO;
import com.mobile.pomodoro.response_dto.DailyTaskResponseDTO;
import com.mobile.pomodoro.service.PomodoroService;
import com.mobile.pomodoro.ui.adapters.DailyTaskAdapter;
import com.mobile.pomodoro.NavigateActivity;
import com.mobile.pomodoro.PlanActivity;
import com.mobile.pomodoro.R;
import com.mobile.pomodoro.utils.LogObj;
import com.mobile.pomodoro.utils.MyUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DailyTaskActivity extends NavigateActivity {

    private RecyclerView recyclerView;
    private DailyTaskAdapter adapter;
    private List<DailyTaskResponseDTO> dailyTaskList;
    private FloatingActionButton btnAddDailyTask;
    private LogObj log;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log = new LogObj();
        log.setName(getClass().getSimpleName());
        log.info("onCreate - Initializing DailyTaskActivity");
        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_daily_task);

        recyclerView = findViewById(R.id.recyclerDailyTasks);
        btnAddDailyTask = findViewById(R.id.btnAddDailyTask);

        dailyTaskList = new ArrayList<>();
        adapter = new DailyTaskAdapter(dailyTaskList, task -> {
            log.info("Daily Task clicked: " + task.getTitle());
            openPlanScreenForEdit(task.getPlan_id());
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Nút thêm Daily Task
        btnAddDailyTask.setOnClickListener(v -> {
            log.info("Add Daily Task button clicked");
            Intent intent = new Intent(this, PlanActivity.class);
            intent.putExtra("isDailyTaskMode", true);
            startActivity(intent);
        });
        // Tải danh sách Daily Task
        loadDailyTasks();
    }
private void loadDailyTasks() {
    var username = MyUtils.get(this, "username");
    if (username == null || username.trim().isEmpty()) {
        log.error("Username is null or empty");
        Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
        return;
    }
    PomodoroService.getRetrofitInstance(username).getDailyTasks().enqueue(new Callback<DailyTaskListResponseDTO>() {
        @Override
        public void onResponse(Call<DailyTaskListResponseDTO> call, Response<DailyTaskListResponseDTO> response) {
            if (response.isSuccessful() && response.body() != null) {
                dailyTaskList.clear();
                dailyTaskList.addAll(response.body().getList());
                adapter.notifyDataSetChanged();
                log.info("Loaded " + dailyTaskList.size() + " daily tasks");
            } else {
                log.warn("Failed to load daily tasks");
                try {
                    String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                    Toast.makeText(DailyTaskActivity.this, "Không tải được danh sách: " + errorBody, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(DailyTaskActivity.this, "Lỗi không xác định", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<DailyTaskListResponseDTO> call, Throwable t) {
            log.error("getDailyTasks failed: " + t.getMessage());
            Toast.makeText(DailyTaskActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
}


    private void openPlanScreenForEdit(long planId) {
        Intent intent = new Intent(this, PlanActivity.class);
        intent.putExtra("isEditMode", true);
        intent.putExtra("planId", planId);
        startActivity(intent);
    }
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_daily_task;
    }

    @Override
    protected int getCurrentMenuItemId() {
        return R.id.page_calendar;
    }
}