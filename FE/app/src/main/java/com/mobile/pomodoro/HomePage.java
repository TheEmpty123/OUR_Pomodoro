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

        // Nhận dữ liệu từ Intent PlanActivity của button "Start"
        Intent intent = getIntent();
        String planTitle = intent.getStringExtra("plan_title");
        int shortBreak = intent.getIntExtra("short_break", 300);
        int longBreak = intent.getIntExtra("long_break", 900);
        String tasksJson = intent.getStringExtra("tasks_json");

        // Hiển thị log
        Log.d("HomePage", "Plan title: " + planTitle);
        Log.d("HomePage", "Short break: " + shortBreak);
        Log.d("HomePage", "Long break: " + longBreak);
        Log.d("HomePage", "Tasks JSON: " + tasksJson);

        // Chuyển từ JSON -> List<PlanTask>
        Gson gson = new Gson();
        Type listType = new TypeToken<List<PlanRequestDTO.PlanTaskDTO>>() {}.getType();
        List<PlanRequestDTO.PlanTaskDTO> taskList = gson.fromJson(tasksJson, listType);
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