package com.mobile.pomodoro.Todo;

import android.os.Bundle;


import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobile.pomodoro.NavigateActivity;
import com.mobile.pomodoro.R;
import com.mobile.pomodoro.entity.TodoItem;

import java.util.ArrayList;
import java.util.List;

public class TodoActivity extends NavigateActivity implements TodoAdapter.TodoItemListener {
    private RecyclerView recyclerView;
    private TodoAdapter adapter;
    private List<TodoItem> todoList = new ArrayList<>();
    private FloatingActionButton btnAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_todo);
        // Khởi tạo
        recyclerView = findViewById(R.id.recyclerTodo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TodoAdapter(todoList, this);
        recyclerView.setAdapter(adapter);
//        Button thêm
        btnAdd = findViewById(R.id.btnAddTodo);
        btnAdd.setOnClickListener(v -> showTodoPopup(null));


        loadTodos();
    }
    // Hiển thị #popups_todo
    private void showTodoPopup(TodoItem item) {
        TodoPopupFragment fragment = TodoPopupFragment.newInstance(item);
        fragment.setListener(new TodoPopupFragment.OnPlanAddedListener() {
            @Override
            public void onTodoSaved(TodoItem newItem) {
                boolean isUpdate = false;
                for (int i = 0; i < todoList.size(); i++) {
                    if (todoList.get(i).getId().equals(newItem.getId())) {
                        todoList.set(i, newItem);
                        adapter.notifyItemChanged(i);
                        isUpdate = true;
                        break;
                    }
                }
                if (!isUpdate) {
                    todoList.add(newItem);
                    adapter.notifyItemInserted(todoList.size() - 1);
                }
            }

            @Override
            public void onTodoDeleted(TodoItem deletedItem) {
                for (int i = 0; i < todoList.size(); i++) {
                    if (todoList.get(i).getId().equals(deletedItem.getId())) {
                        todoList.remove(i);
                        adapter.notifyItemRemoved(i);
                        break;
                    }
                }
            }
        });
        fragment.show(getSupportFragmentManager(), "TodoPopupFragment");
    }

    //    Check/uncheck , gọi updateTodo() để cập nhập trạng thái hoàn thành
    @Override
    public void onTodoChecked(TodoItem item, boolean isChecked) {
        item.setDone(isChecked);
        updateTodo(item);
    }

    //    Nhấn vào item_todo để mở -#popups_todo chỉnh sửa
    @Override
    public void onTodoClicked(TodoItem item) {
        showTodoPopup(item);
    }
    //    Tải danh sách todo
    private void loadTodos() {

    }
    //    Dùng để cập nhập trạng thái của checkbox
    private void updateTodo(TodoItem item) {

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