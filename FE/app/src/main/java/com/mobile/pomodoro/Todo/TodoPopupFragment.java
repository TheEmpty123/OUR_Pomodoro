package com.mobile.pomodoro.Todo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.mobile.pomodoro.R;
import com.mobile.pomodoro.enums.ApplicationMode;
import com.mobile.pomodoro.request_dto.TodoRequestDTO;
import com.mobile.pomodoro.response_dto.MessageResponseDTO;
import com.mobile.pomodoro.response_dto.TodoResponseDTO;
import com.mobile.pomodoro.room.AppDatabase;
import com.mobile.pomodoro.room.DatabaseClient;
import com.mobile.pomodoro.room.entity.TodoItem;
import com.mobile.pomodoro.room.repo.SingleThreadRepo;
import com.mobile.pomodoro.service.PomodoroService;
import com.mobile.pomodoro.utils.LogObj;
import com.mobile.pomodoro.utils.MyUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TodoPopupFragment extends DialogFragment {
    private static final String ARG_TODO_ID = "todo_id";
    private static final String ARG_TODO_TITLE = "todo_title";
    private static final String ARG_TODO_IS_DONE = "todo_is_done";
    private TodoResponseDTO todoItem;  // null hoặc todo cần sửa
    private OnTodoActionListener callback;
    private MaterialButton btnSave, btnDelete;
    private EditText etTitle;
    private LogObj log;
    public interface OnTodoActionListener {
        void onTodoSaved(TodoResponseDTO item);
        void onTodoDeleted(TodoResponseDTO item);
        void onTodoCreated(); // Callback thông báo tạo TODO thành công để reload
    }
    public void setListener(OnTodoActionListener callback) {
        this.callback = callback;
    }

    // tạo dialog mới, nếu có item truyền vào là chỉnh sửa (dùng bundle chứa key  và lấy Todo trong bundle đó
    public static TodoPopupFragment newInstance(TodoResponseDTO item) {
        TodoPopupFragment fragment = new TodoPopupFragment();
        Bundle args = new Bundle();
        if (item != null) {
            args.putLong(ARG_TODO_ID, item.getId());
            args.putString(ARG_TODO_TITLE, item.getTitle());
            args.putInt(ARG_TODO_IS_DONE, item.getIs_done());
        }
        fragment.setArguments(args);
        return fragment;
    }

    //Lây dl trong bundle khi tạo fragment
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log = new LogObj();
        log.setName(getClass().getSimpleName());
        if (getArguments() != null && getArguments().containsKey(ARG_TODO_ID)) {
            todoItem = TodoResponseDTO.builder()
                    .id(getArguments().getLong(ARG_TODO_ID))
                    .title(getArguments().getString(ARG_TODO_TITLE))
                    .is_done(getArguments().getInt(ARG_TODO_IS_DONE))
                    .build();
        }
    }

//    tạo gd popup
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.popup_todo, null);

        etTitle = view.findViewById(R.id.etTodoTitle);
        btnSave = view.findViewById(R.id.btnSaveTodo);
        btnDelete = view.findViewById(R.id.btnDeleteTodo);

        if (todoItem != null) {
            etTitle.setText(todoItem.getTitle()); // sửa thì hiển thị title
        } else {
            etTitle.setText("");
            btnDelete.setVisibility(View.GONE); // thêm mới thì ẩn nút xóa
        }

// Button Lưu ( thêm)
        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            if (title.isEmpty()) {
                log.warn("Todo title is empty");
                Toast.makeText(getContext(), "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
                return;
            }
            Context context = getContext();

            // ==================================OFFLINE==============================================
            if (MyUtils.applicationMode == ApplicationMode.OFFLINE){
                // If application running offline
                if (todoItem == null){
                    log.info("Creating new todo: " + title);
                    var item = TodoItem.builder()
                            .title(title)
                            .isDone(0)
                            .build();
                    newTodo(item);

                    if (callback != null) callback.onTodoCreated(); // gọi callback để reload
                    dismiss(); // đóng popup
                }
                return;
                // ==============================OFFLINE==============================================
            }

            var username = MyUtils.get(context, "username"); // Lấy username
            if (username == null || username.trim().isEmpty()) {
                log.error("Username is null or empty");
                Toast.makeText(context, "Vui lòng đăng nhập để thực hiện hành động", Toast.LENGTH_SHORT).show();
                return;
            }
            if (todoItem == null) {
                // API thêm
                log.info("Creating new todo: " + title);
                PomodoroService.getRetrofitInstance(username).createTodo(
                        TodoRequestDTO.builder()
                                .title(title)
                                .is_done(0)
                                .build()
                ).enqueue(new Callback<MessageResponseDTO>() {
                    @Override
                    public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            log.warn("Failed to create todo");
                            Toast.makeText(getContext(), "Không tạo được todo", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        log.info("Todo created: " + response.body().getMessage());
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        if (callback != null) callback.onTodoCreated(); // gọi callback để reload
                        dismiss(); // đóng popup
                    }

                    @Override
                    public void onFailure(Call<MessageResponseDTO> call, Throwable t) {
                        log.error("Create todo failed: " + t.getMessage());
                        Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // API sửa
                log.info("Updating todo: " + title);

                // ==================================OFFLINE==============================================
                if (MyUtils.applicationMode == ApplicationMode.OFFLINE){
                    // If application running offline
                    if (todoItem != null){
                        log.info("Updating todo: " + title);
                        var item = TodoItem.builder()
                                .id(todoItem.getId())
                                .title(title)
                                .isDone(todoItem.getIs_done())
                                .build();
                        udpateTodo(item);

                        if (callback != null) callback.onTodoSaved(todoItem); // gọi callback để reload
                        dismiss(); // đóng popup
                    }
                    return;
                    // ==============================OFFLINE==============================================
                }

                PomodoroService.getRetrofitInstance(username).updateTodo(todoItem.getId(),
                        TodoRequestDTO.builder()
                                .title(title)
                                .is_done(todoItem.getIs_done())
                                .build()
                        ).enqueue(new Callback<MessageResponseDTO>() {
                    @Override
                    public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            log.warn("Failed to update todo");
                            Toast.makeText(getContext(), "Không cập nhật được todo", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        log.info("Todo updated: " + response.body().getMessage());
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        todoItem.setTitle(title); // cập nhập title
                        if (callback != null) callback.onTodoSaved(todoItem);
                        dismiss();
                    }

                    @Override
                    public void onFailure(Call<MessageResponseDTO> call, Throwable t) {
                        log.error("Update todo failed: " + t.getMessage());
                        Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        // API xóa
        btnDelete.setOnClickListener(v -> {
            if (todoItem != null) {
                log.info("Deleting todo: " + todoItem.getTitle());
                Context context = getContext();
                var username = MyUtils.get(context, "username");
                if (username == null || username.trim().isEmpty()) {
                    log.error("Username is null or empty");
                    Toast.makeText(context, "Vui lòng đăng nhập để thực hiện hành động", Toast.LENGTH_SHORT).show();
                    return;
                }
                PomodoroService.getRetrofitInstance(username).deleteTodo(todoItem.getId()).enqueue(new Callback<MessageResponseDTO>() {
                    @Override
                    public void onResponse(Call<MessageResponseDTO> call, Response<MessageResponseDTO> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            log.warn("Failed to delete todo");
                            Toast.makeText(getContext(), "Không xóa được todo", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        log.info("Todo deleted: " + response.body().getMessage());
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        if (callback != null) callback.onTodoDeleted(todoItem);
                        dismiss();
                    }

                    @Override
                    public void onFailure(Call<MessageResponseDTO> call, Throwable t) {
                        log.error("Delete todo failed: " + t.getMessage());
                        Toast.makeText(getContext(), "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setView(view);
        return builder.create();
    }

    private void newTodo(TodoItem todo){
        // Create new todo
        // ==================================OFFLINE==============================================
        // Application is running offline
        log.info("Saving todo to local storage");
        /** Fetch todo using room storage
         * 1. Get AppDatabase
         * 2. Create new background thread
         * 3. Insert
         */
        AppDatabase db = DatabaseClient.getInstance(getContext()).getAppDatabase();    // 1.
        SingleThreadRepo repo = new SingleThreadRepo(db.todoItem());   // 2.
        repo.insert(todo);


        // ==================================OFFLINE==============================================
    }

    private void udpateTodo(TodoItem todo){
        // Update todo
        // ==================================OFFLINE==============================================
        // Application is running offline
        log.info("Updating todo");
        /** Fetch todo using room storage
         * 1. Get AppDatabase
         * 2. Create new background thread
         * 3. Update
         */
        AppDatabase db = DatabaseClient.getInstance(getContext()).getAppDatabase();    // 1.
        SingleThreadRepo repo = new SingleThreadRepo(db.todoItem());   // 2.
        repo.update(todo);
    }
}
