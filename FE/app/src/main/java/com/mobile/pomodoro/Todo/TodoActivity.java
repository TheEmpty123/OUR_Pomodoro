package com.mobile.pomodoro.Todo;

import android.os.Bundle;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobile.pomodoro.NavigateActivity;
import com.mobile.pomodoro.R;
import com.mobile.pomodoro.enums.ApplicationMode;
import com.mobile.pomodoro.room.AppDatabase;
import com.mobile.pomodoro.room.DatabaseClient;
import com.mobile.pomodoro.room.entity.TodoItem;
import com.mobile.pomodoro.room.repo.SingleThreadRepo;
import com.mobile.pomodoro.room.repo.TodoRepository;
import com.mobile.pomodoro.request_dto.TodoRequestDTO;
import com.mobile.pomodoro.response_dto.MessageResponseDTO;
import com.mobile.pomodoro.response_dto.TodoListResponseDTO;
import com.mobile.pomodoro.response_dto.TodoResponseDTO;
import com.mobile.pomodoro.service.PomodoroService;
import com.mobile.pomodoro.ui.view_model.TodoViewModel;
import com.mobile.pomodoro.ui.view_model.TodoViewModelFactory;
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
    private TodoViewModel viewModel;

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
                // Save todo
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
                // Create new todo
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
        // Check Application Status
        if (MyUtils.applicationMode == ApplicationMode.ONLINE) {
            // =======================================================================================
            // If application running online
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
        } else {// ===================================================================================
            // If application running offline
            log.info("Loading todos from local storage");
            /** Fetch todo using room storage
             * 1. Get AppDatabase
             * 2. Create new background thread
             * 3. Initialize ViewModel (using factory)
             * 4. Assign observer
             */
            AppDatabase db = DatabaseClient.getInstance(TodoActivity.this).getAppDatabase();    // 1.
            SingleThreadRepo repo = new SingleThreadRepo(db.todoItem());   // 2.

            // 3. Init
            TodoViewModelFactory factory = new TodoViewModelFactory(repo);
            viewModel = new ViewModelProvider(this, factory).get(TodoViewModel.class);

            // 4. Assign observer
            viewModel.getLiveData().observe(this, resource ->{
                if (resource != null){
                    switch (resource.getStatus()){
                        // 1. Resources are loading, wait (maybe put a lazy mark here)
                        case LOADING:
                            break;

                        // 2. Resources are completed to be loaded
                        // 2.1 Stop lazy mark (if any)
                        // 2.2 Check resources whether it's null -> shows nothing
                        // 2.3 Check resources whether it's not null -> shows up
                        case SUCCESS:
                            // 2.1

                            // 2.2
                            if (resource.getData() == null || resource.getData().isEmpty()){
                                return;
                            }

                            // 2.3
                            todoList.clear(); // xóa dl cũ
                            var data = resource.getData();  // Fetch resources
                            data.forEach(item -> { // thêm mới
                                todoList.add(TodoResponseDTO
                                                .builder()
                                                .title(item.getTitle())
                                                .id(item.getId())
                                                .is_done(item.getIsDone())
                                                .build());
                            });

                            adapter.notifyDataSetChanged(); // cập nhập giao diện
                            log.info("Todos loaded: " + todoList.size() + " items");
                            break;

                        // 3. Error while loading todos
                        // Use Toast to shows message
                        case ERROR:
                            Toast.makeText(TodoActivity.this, resource.getMessage(), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });

            // Start load todos
            viewModel.loadTodos();
            // =======================================================================================
        }
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