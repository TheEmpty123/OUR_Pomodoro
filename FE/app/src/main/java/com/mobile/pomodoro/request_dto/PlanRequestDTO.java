package com.mobile.pomodoro.request_dto;

import com.mobile.pomodoro.entity.PlanTask;

import java.util.List;

//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
public class PlanRequestDTO {
    private String title;
    private int s_break_duration;
    private int l_break_duration;
    private List<PlanTaskDTO> steps;
//    @Data
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
    public static class PlanTaskDTO {
        private String plan_title;
        private double plan_duration;
        private int order;
    public String getPlan_title() { return plan_title; }
    public void setPlan_title(String plan_title) { this.plan_title = plan_title; }

    public double getPlan_duration() { return plan_duration; }
    public void setPlan_duration(double plan_duration) { this.plan_duration = plan_duration; }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }
    }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getS_break_duration() { return s_break_duration; }
    public void setS_break_duration(int s_break_duration) { this.s_break_duration = s_break_duration; }

    public int getL_break_duration() { return l_break_duration; }
    public void setL_break_duration(int l_break_duration) { this.l_break_duration = l_break_duration; }

    public List<PlanTaskDTO> getSteps() { return steps; }
    public void setSteps(List<PlanTaskDTO> steps) { this.steps = steps; }
}
