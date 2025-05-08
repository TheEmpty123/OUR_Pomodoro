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

    public interface OnPlanAddedListener {
        void onPlanAdded(PlanTask newPlan);
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

        btnSubmit.setOnClickListener(v -> {
            String title = inputTitle.getText().toString().trim();
            int time = parseIntSafe(inputTime.getText().toString());
            int shortBreak = parseIntSafe(inputShortBreak.getText().toString());
            int longBreak = parseIntSafe(inputLongBreak.getText().toString());

            if (!title.isEmpty() && time > 0) {
                PlanTask plan = new PlanTask();
                plan.setPlanName(title);
//                plan.setPlantasks(Collections.singletonList(new PlanTask(title, time)));
                plan.setDuration(time);
                plan.setShortBreak(shortBreak);
                plan.setLongBreak(longBreak);

                callback.onPlanAdded(plan);
                dismiss();
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
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