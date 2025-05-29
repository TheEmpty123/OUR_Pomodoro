package com.mobile.pomodoro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.pomodoro.request_dto.PlanRequestDTO;

import java.util.List;

//Hiển thị danh sách
public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

    private List<PlanRequestDTO.PlanTaskDTO> planTasks;

    public PlanAdapter(List<PlanRequestDTO.PlanTaskDTO> planTasks) {
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
        PlanRequestDTO.PlanTaskDTO task = planTasks.get(position);
        holder.txtTitle.setText(task.getPlan_title());
        holder.txtTime.setText((int) task.getPlan_duration()  + " min");
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