package com.mobile.pomodoro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobile.pomodoro.Model.PlanTask;

import java.util.ArrayList;
import java.util.List;

public class PlanActivity extends AppCompatActivity {
    private static final int REQUEST_ADD_TASK = 100;

    private RecyclerView recyclerView;
    private PlanAdapter adapter;
    private List<PlanTask> planList;

    private int shortBreak = 25;
    private int longBreak = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_plan);

        FloatingActionButton btnAdd = findViewById(R.id.btnAdd);
        MaterialButton btnSave = findViewById(R.id.btnSave);
        MaterialButton btnStart = findViewById(R.id.btnStart);
        MaterialButton btnImport = findViewById(R.id.btnImport);
        MaterialButton btnExport = findViewById(R.id.btnExport);
        recyclerView = findViewById(R.id.recyclerPlan);

        planList = new ArrayList<>();
        adapter = new PlanAdapter(planList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        // Xử lý  các button
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(PlanActivity.this, PlanActivityAdd.class);
            startActivityForResult(intent, REQUEST_ADD_TASK);
        });
        //Button "Save"
        btnSave.setOnClickListener(v -> {
            savePlanToBackend();
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

//Nhận kết quả ở Plan Add
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_TASK && resultCode == Activity.RESULT_OK && data != null) {
            String title = data.getStringExtra("planName");
            double time = data.getIntExtra("duration", 0);
            int shortB = data.getIntExtra("shortBreak", shortBreak);
            int longB = data.getIntExtra("longBreak", longBreak);

            shortBreak = shortB; // cập nhật giá trị
            longBreak = longB;

            PlanTask newTask = new PlanTask(title, time , shortBreak, longBreak);
            planList.add(newTask);
            adapter.notifyItemInserted(planList.size() - 1);

        }
    }

//     Gửi planList lên serve
    private void savePlanToBackend() {

    }

    private void startPlanWithoutSaving() {

    }
    private void showExportPopup() {
    }

    private void showImportPopup() {
    }
}