package com.mobile.pomodoro;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobile.pomodoro.entity.DailyTask;

import java.util.ArrayList;
import java.util.List;

public class DailyTaskActivity extends AppCompatActivity implements DailyTaskPopupFragment.OnDailyTaskListener {

    private RecyclerView recyclerView;
    private DailyTaskAdapter adapter;
    private List<DailyTask> taskList;
    private FloatingActionButton btnAdd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_task);

        recyclerView = findViewById(R.id.recyclerDailyTasks);
        btnAdd = findViewById(R.id.btnAddDailyTask);

        taskList = new ArrayList<>();
        adapter = new DailyTaskAdapter(taskList, new DailyTaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DailyTask task) {
                // Chuyển sang Plan Screen với id task
                Intent intent = new Intent(DailyTaskActivity.this, HomePage.class);
                intent.putExtra("task_id", task.getId());
                startActivity(intent);
            }

            @Override
            public void onCheckChanged(DailyTask task, boolean isChecked) {
                // Cập nhật trạng thái hoàn thành
                task.setDone(isChecked);
                //   API update trạng thái isDone lên server nếu cần
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener(v -> {
            DailyTaskPopupFragment popup = DailyTaskPopupFragment.newInstance(null); // Thêm mới
            popup.setListener(DailyTaskActivity.this);
            popup.show(getSupportFragmentManager(), "AddTask");
        });
        loadTestTasks();
    }

    private void loadTestTasks() {
        taskList.add(new DailyTask("id1", "Wake Up", false));
        taskList.add(new DailyTask("id2", "Sleep", true));
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onDailyTaskSaved(DailyTask newTask) {
        boolean isUpdate = false;
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).getId().equals(newTask.getId())) {
                taskList.set(i, newTask);
                adapter.notifyItemChanged(i);
                isUpdate = true;
                break;
            }
        }
        if (!isUpdate) {
            taskList.add(newTask);
            adapter.notifyItemInserted(taskList.size() - 1);
        }
    }

    @Override
    public void onDailyTaskDeleted(DailyTask task) {
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).getId().equals(task.getId())) {
                taskList.remove(i);
                adapter.notifyItemRemoved(i);
                break;
            }
        }
    }
}