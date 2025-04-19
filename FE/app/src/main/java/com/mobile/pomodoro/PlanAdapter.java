package com.mobile.pomodoro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.pomodoro.Model.PlanTask;

import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

    private List<PlanTask> planTasks;

    public PlanAdapter(List<PlanTask> planTasks) {
        this.planTasks = planTasks;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        PlanTask task = planTasks.get(position);
        holder.txtTitle.setText(task.getPlanName());
        holder.txtTime.setText((int) task.getDuration() + " min");
        holder.txtShortBreak.setText(task.getShortBreak() + " min");
        holder.txtLongBreak.setText(task.getLongBreak() + " min");
    }

    @Override
    public int getItemCount() {
        return planTasks.size();
    }

    public static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtTime, txtShortBreak, txtLongBreak;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtShortBreak = itemView.findViewById(R.id.txtShortBreak);
            txtLongBreak = itemView.findViewById(R.id.txtLongBreak);
        }
    }
}