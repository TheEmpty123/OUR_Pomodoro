package com.mobile.pomodoro.Todo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.mobile.pomodoro.R;
import com.mobile.pomodoro.entity.TodoItem;

public class TodoPopupFragment extends DialogFragment {
    private static final String ARG_TODO = "todo";
    private TodoItem todoItem;
    private OnPlanAddedListener callback;
    private MaterialButton btnSave, btnDelete;
    private EditText etTitle;
    public interface OnPlanAddedListener {
        void onTodoSaved(TodoItem item);
        void onTodoDeleted(TodoItem item);
    }
    public void setListener(OnPlanAddedListener callback) {
        this.callback = callback;
    }
    // tạo dialog mới, nếu có item truyền vào là chỉnh sửa (dùng bundle chứa key (ARG_TODO) và lấy Todo trong bundle đó
    public static TodoPopupFragment newInstance(TodoItem item) {
        TodoPopupFragment fragment = new TodoPopupFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TODO, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            todoItem = getArguments().getParcelable(ARG_TODO);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.popup_todo, null);

        etTitle = view.findViewById(R.id.etTodoTitle);
        btnSave = view.findViewById(R.id.btnSaveTodo);
        btnDelete = view.findViewById(R.id.btnDeleteTodo);

        if (todoItem != null) {
            etTitle.setText(todoItem.getTitle());
        } else {
            btnDelete.setVisibility(View.GONE);
        }
// Button save
        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            if (!title.isEmpty()) {
                if (todoItem == null) {
                    todoItem = new TodoItem(String.valueOf(System.currentTimeMillis()), title, false);
//                API thêm
                } else {
                    todoItem.setTitle(title);
//                API sửa
                }
//            sau khi thêm hoặc chỉnh rồi gọi về lại activity
                if (callback != null) callback.onTodoSaved(todoItem);
                dismiss();
            }
        });

        btnDelete.setOnClickListener(v -> {
            if (callback != null && todoItem != null) {
                callback.onTodoDeleted(todoItem);
            }
            dismiss();
        });

        builder.setView(view);
        return builder.create();
    }


}
