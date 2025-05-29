package com.mobile.pomodoro.request_dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanRequestDTO {
    private String title;
    private int s_break_duration;
    private int l_break_duration;
    private List<PlanTaskDTO> steps;
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanTaskDTO {
        private String plan_title;
        private double plan_duration;
        private int order;
        private transient int shortBreak;
        private transient int longBreak;
    }
}
