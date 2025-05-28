package com.mobile.pomodoro;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mobile.pomodoro.entity.PlanTask;


public class AddPlanFragment extends DialogFragment {
    private TextInputEditText inputTitle, inputTime, inputShortBreak, inputLongBreak;
    private OnPlanAddedListener callback;
    private boolean isFirstTask;

    public interface OnPlanAddedListener {
        void onPlanAdded(PlanTask newPlan, int shortBreak, int longBreak,  boolean isFirstTask);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnPlanAddedListener) {
            callback = (OnPlanAddedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnPlanAddedListener");
        }
    }
    public static AddPlanFragment newInstance(boolean isFirstTask, int shortBreak, int longBreak) {
        AddPlanFragment fragment = new AddPlanFragment();
        Bundle args = new Bundle();
        args.putBoolean("isFirstTask", isFirstTask);
        args.putInt("shortBreak", shortBreak);
        args.putInt("longBreak", longBreak);
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_plan, container, false);

        inputTitle = view.findViewById(R.id.inputTitle);
        inputTime = view.findViewById(R.id.inputTime);
        inputShortBreak = view.findViewById(R.id.inputShortBreak);
        inputLongBreak = view.findViewById(R.id.inputLongBreak);
        MaterialButton btnSubmit = view.findViewById(R.id.btnSubmit);

        // ẩnn/hiện các trường break time
        if (!isFirstTask) {
            inputShortBreak.setVisibility(View.GONE);
            inputLongBreak.setVisibility(View.GONE);
        }

        btnSubmit.setOnClickListener(v -> {
            String title = inputTitle.getText().toString().trim();
            int time = parseIntSafe(inputTime.getText().toString());
            int shortBreak = isFirstTask ? parseIntSafe(inputShortBreak.getText().toString()) : 0;
            int longBreak = isFirstTask ? parseIntSafe(inputLongBreak.getText().toString()) : 0;


            // kiểm tra cacs input
            if (!title.isEmpty() && time > 0) {
                if (isFirstTask && (shortBreak <= 0 || longBreak <= 0)) {
                    Toast.makeText(getContext(), "Vui lòng nhập thời gian nghỉ", Toast.LENGTH_SHORT).show();
                    return;
                }


                PlanTask plan = new PlanTask();
                plan.setPlanName(title);
                plan.setDuration(time);
//                plan.setShortBreak(shortBreak);
//                plan.setLongBreak(longBreak);

                if (callback != null) {
                    callback.onPlanAdded(plan, shortBreak, longBreak, isFirstTask);
                }
                dismiss();
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

@Override
public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
        isFirstTask = getArguments().getBoolean("isFirstTask", true);
    }
}

    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

}