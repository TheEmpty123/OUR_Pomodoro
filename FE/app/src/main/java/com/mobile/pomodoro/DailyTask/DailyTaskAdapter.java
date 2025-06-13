package com.mobile.pomodoro.DailyTask;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.pomodoro.R;
import com.mobile.pomodoro.entity.DailyTask;

import java.util.List;

public class DailyTaskAdapter extends RecyclerView.Adapter<DailyTaskAdapter.TaskViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(DailyTask task);
        void onCheckChanged(DailyTask task, boolean isChecked);
    }

    private List<DailyTask> taskList;
    private OnItemClickListener listener;

    public DailyTaskAdapter(List<DailyTask> taskList, OnItemClickListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_daily_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        DailyTask task = taskList.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView tvTitle;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            tvTitle = itemView.findViewById(R.id.title);
        }

        void bind(DailyTask task) {
            tvTitle.setText(task.getTitle());
            checkBox.setChecked(task.isDone());

            // Khi nhấn checkbox thay đổi trạng thái hoàn thành
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onCheckChanged(task, isChecked);
                }
            });

            // Khi nhấn vào toàn bộ item thì mở Plan Screen
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(task);
                }
            });
        }
    }
}
