package com.mobile.pomodoro;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.mobile.pomodoro.entity.DailyTask;

public class DailyTaskPopupFragment extends DialogFragment {

    private static final String ARG_TASK = "daily_task";
    private DailyTask task;
    private EditText etTitle;
    private MaterialButton btnSave, btnDelete;
    private OnDailyTaskListener listener;

    public interface OnDailyTaskListener {
        void onDailyTaskSaved(DailyTask task);
        void onDailyTaskDeleted(DailyTask task);
    }

    public void setListener(OnDailyTaskListener listener) {
        this.listener = listener;
    }

    public static DailyTaskPopupFragment newInstance(@Nullable DailyTask task) {
        DailyTaskPopupFragment fragment = new DailyTaskPopupFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TASK, task);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            task = getArguments().getParcelable(ARG_TASK);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.popup_add_daily_task, null);
        etTitle = view.findViewById(R.id.etTaskTitle);
        btnSave = view.findViewById(R.id.btnSaveTask);
        btnDelete = view.findViewById(R.id.btnDeleteTask);

        if (task != null) {
            etTitle.setText(task.getTitle());
        } else {
            btnDelete.setVisibility(View.GONE);
        }

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            if (!title.isEmpty()) {
                if (task == null) {
                    task = new DailyTask(String.valueOf(System.currentTimeMillis()), title, false);
                } else {
                    task.setTitle(title);
                }
                if (listener != null) listener.onDailyTaskSaved(task);
                dismiss();
            }
//            api
        });

        btnDelete.setOnClickListener(v -> {
            if (listener != null && task != null) {
                listener.onDailyTaskDeleted(task);
            }
            dismiss();
//            api
        });

        return new AlertDialog.Builder(requireContext()).setView(view).create();
    }
}
