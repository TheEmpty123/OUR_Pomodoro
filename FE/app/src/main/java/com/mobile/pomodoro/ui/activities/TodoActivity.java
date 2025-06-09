package com.mobile.pomodoro.ui.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobile.pomodoro.NavigateActivity;
import com.mobile.pomodoro.R;
import com.mobile.pomodoro.ui.adapters.TodoAdapter;
import com.mobile.pomodoro.ui.fragments.TodoPopupFragment;
import com.mobile.pomodoro.request_dto.TodoRequestDTO;
import com.mobile.pomodoro.response_dto.MessageResponseDTO;
import com.mobile.pomodoro.response_dto.TodoListResponseDTO;
import com.mobile.pomodoro.response_dto.TodoResponseDTO;
import com.mobile.pomodoro.service.PomodoroService;
import com.mobile.pomodoro.utils.LogObj;
import com.mobile.pomodoro.utils.MyUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TodoActivity extends NavigateActivity implements TodoAdapter.TodoItemListener {
    private RecyclerView recyclerView;
    private TodoAdapter adapter;
    private List<TodoResponseDTO> todoList = new ArrayList<>();
    private FloatingActionButton btnAdd;
    private LogObj log;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log = new LogObj();
        log.setName(getClass().getSimpleName());
        log.info("onCreate - Initializing TodoActivity");
//        setContentView(R.layout.activity_todo);
        // Khởi tạo
        recyclerView = findViewById(R.id.recyclerTodo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TodoAdapter(todoList, this);
        recyclerView.setAdapter(adapter);
//        Button thêm
        btnAdd = findViewById(R.id.btnAddTodo);
        btnAdd.setOnClickListener(v -> {
            log.info("Add button clicked");
            showTodoPopup(null);
        });

        loadTodos();
    }
    // Hiển thị #popups_todo
    private void showTodoPopup(TodoResponseDTO item) {
        TodoPopupFragment fragment = TodoPopupFragment.newInstance(item);
        fragment.setListener(new TodoPopupFragment.OnTodoActionListener() {
            @Override
            public void onTodoSaved(TodoResponseDTO newItem) {
                log.info("Todo saved: " + newItem.getTitle());
                boolean isUpdate = false;
                for (int i = 0; i < todoList.size(); i++) {
                    if (todoList.get(i).getId() == newItem.getId()) {
                        todoList.set(i, newItem);
                        adapter.notifyItemChanged(i);
                        return;
                    }
                }
            }

            @Override
            public void onTodoDeleted(TodoResponseDTO deletedItem) {
                log.info("Todo deleted: " + deletedItem.getTitle());
                for (int i = 0; i < todoList.size(); i++) {
                    if (todoList.get(i).getId() == deletedItem.getId()) {
                        todoList.remove(i);
                        adapter.notifyItemRemoved(i);
                        return;
                    }
                }
            }
            @Override
            public void onTodoCreated() {
                log.info("Todo mới được tạo, tải lại danh sách");
                loadTodos(); // Tải lại danh sách sau khi tạo TODO mới
            }
        });

        fragment.show(getSupportFragmentManager(), "TodoPopupFragment");
    }

    //    Check/uncheck , gọi updateTodo() để cập nhập trạng thái hoàn thành
    @Override
    public void onTodoChecked(TodoResponseDTO item, boolean isChecked) {
        log.info("Todo checked: " + item.getTitle() + ", isChecked: " + isChecked);
        item.setIs_done(isChecked ? 1 : 0);
        updateTodo(item);
    }

    //    Nhấn vào item_todo để mở -#popups_todo chỉnh sửa
    @Override
    public void onTodoClicked(TodoResponseDTO item) {
        log.info("Todo clicked: " + item.getTitle());
        showTodoPopup(item);
    }

    //    Tải danh sách todo
    private void loadTodos() {
        log.info("Loading todos from API");
        var username = MyUtils.get(this, "username");
        if (username == null || username.trim().isEmpty()) {
            log.error("Username is null or empty");
            Toast.makeText(this, "Vui lòng đăng nhập để tải danh sách todo", Toast.LENGTH_SHORT).show();
            return;
        }
        PomodoroService.getRetrofitInstance(username).getTodos().enqueue(new Callback<TodoListResponseDTO>() {
            @Override
            public void onResponse(Call<TodoListResponseDTO> call, Response<TodoListResponseDTO> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().getList() == null) {
                    log.warn("Failed to load todos");
                    Toast.makeText(TodoActivity.this, "Không tải được danh sách todo", Toast.LENGTH_SHORT).show();
                    return;
                }
                todoList.clear(); // xóa dl cũ
                todoList.addAll(response.body().getList()); // thêm mới
                adapter.notifyDataSetChanged(); // cập nhập giao diện
                log.info("Todos loaded: " + todoList.size() + " items");
            }

            @Override
            public void onFailure(Call<TodoListResponseDTO> call, Throwable t) {
                log.error("Load todos failed: " + t.getMessage());
                Toast.makeText(TodoActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //    Dùng để cập nhập trạng thái của checkbox
    private void updateTodo(TodoResponseDTO item) {
        log.info("Updating todo: " + item.getTitle());
        var username = MyUtils.get(this, "username"); // Lấy username
        if (username == null || username.trim().isEmpty()) {
            log.error("Username is null or empty");
            Toast.makeText(this, "Vui lòng đăng nhập để cập nhật todo", Toast.LENGTH_SHORT).show();
            return;
        }
        PomodoroService.getRetrofitInstance(username).updateTodo(item.getId(),  TodoRequestDTO.builder()
                                                    .title(item.getTitle())
                                                    .is_done(item.getIs_done())
                                                    .build()
                                    ).enqueue(new Callback<MessageResponseDTO>() {
            @Override
            public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    log.warn("Failed to update todo: " + item.getTitle());
                    Toast.makeText(TodoActivity.this, "Không cập nhật được todo", Toast.LENGTH_SHORT).show();
                    return;
                }
                log.info("Todo updated: " + response.body().getMessage());
                Toast.makeText(TodoActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<MessageResponseDTO> call, Throwable t) {
                log.error("Update todo failed: " + t.getMessage());
                Toast.makeText(TodoActivity.this, "ERROR " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_todo;
    }

    @Override
    protected int getCurrentMenuItemId() {
        return R.id.page_todo;
    }
}