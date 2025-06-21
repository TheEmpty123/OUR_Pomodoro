package com.mobile.pomodoro.DailyTask;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.pomodoro.R;
import com.mobile.pomodoro.response_dto.DailyTaskResponseDTO;

import java.util.List;
import java.util.function.Consumer;

public class DailyTaskAdapter extends RecyclerView.Adapter<DailyTaskAdapter.DailyTaskViewHolder> {
    private List<DailyTaskResponseDTO> tasks;
    private Consumer<DailyTaskResponseDTO> onItemClickListener;
    public DailyTaskAdapter(List<DailyTaskResponseDTO> tasks, Consumer<DailyTaskResponseDTO> onItemClickListener) {
        this.tasks = tasks;
        this.onItemClickListener = onItemClickListener;
    }
    @NonNull
    @Override
    public DailyTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_daily_task, parent, false);
        return new DailyTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyTaskViewHolder holder, int position) {
        DailyTaskResponseDTO task = tasks.get(position);
        if (task == null) {
            Log.e("DailyTaskAdapter", "Task is null at position: " + position);
            holder.txtTitle.setText("Task null");
            holder.checkbox.setChecked(false);
            return;
        }
        holder.checkbox.setChecked(task.getIs_done() == 1);
        holder.txtTitle.setText(task.getTitle() != null ? task.getTitle() : "No title");
        holder.itemView.setOnClickListener(v -> onItemClickListener.accept(task));
    }

    @Override
    public int getItemCount() {return tasks != null ? tasks.size() : 0;}

    static class DailyTaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkbox;
        TextView txtTitle;

        public DailyTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkbox = itemView.findViewById(R.id.checkbox);
            txtTitle = itemView.findViewById(R.id.title);
            if (checkbox == null || txtTitle == null) {
                Log.e("DailyTaskAdapter", "View not found: checkbox=" + checkbox + ", txtTitle=" + txtTitle);
            }
        }
    }
    }

