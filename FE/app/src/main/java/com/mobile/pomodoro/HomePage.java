package com.mobile.pomodoro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.pomodoro.request_dto.PlanRequestDTO;
import com.mobile.pomodoro.request_dto.PlanTaskDTO;

import java.lang.reflect.Type;
import java.util.List;

public class HomePage extends NavigateActivity {
    private TextView timerText;
    private TextView currentTaskText;
    private FloatingActionButton btnPlayPause;
    private ImageButton btnReset;
    private ImageButton btnSkip;
    private Button btnFocus;
    private Button btnShortBreak;
    private Button btnLongBreak;
    private BottomNavigationView bottomNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        timerText = findViewById(R.id.timerText);
        currentTaskText = findViewById(R.id.currentTaskText);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnReset = findViewById(R.id.btnReset);
        btnSkip = findViewById(R.id.btnSkip);
        btnFocus = findViewById(R.id.btnFocus);
        btnShortBreak = findViewById(R.id.btnShortBreak);
        btnLongBreak = findViewById(R.id.btnLongBreak);
        bottomNavView = findViewById(R.id.bottomNavigation);

        // Nhận dữ liệu từ Intent PlanActivity của button "save", "start"
        Intent intent = getIntent();
        String planTitle = intent.getStringExtra("plan_title");
        Long planId = intent.getLongExtra("plan_id", -1);
        String tasksJson = intent.getStringExtra("tasks_json");

        // Hiển thị log
        Log.d("HomePage", "Plan title: " + planTitle);
        Log.d("HomePage", "Plan Id: " + planId);
        Log.d("HomePage", "Tasks JSON: " + tasksJson);

        // Chuyển từ JSON -> List<PlanTask>
        Gson gson = new Gson();
        Type listType = new TypeToken<List<PlanTaskDTO>>() {}.getType();
        List<PlanTaskDTO> taskList = gson.fromJson(tasksJson, listType);

        showRecentPlan(planTitle, taskList);
    }

    private void showRecentPlan(String planTitle, List<PlanTaskDTO> taskList) {
        if (taskList != null && !taskList.isEmpty()) {
            // Hiển thị PlanTask đầu tiên
            PlanTaskDTO firstTask = taskList.get(0);
            currentTaskText.setText(firstTask.getPlan_title());
            double minutes = firstTask.getPlan_duration();
            timerText.setText(String.format("%02d:00", (int) Math.round(minutes)));
        } else {
            currentTaskText.setText("No task available");
            timerText.setText("00:00");
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
}