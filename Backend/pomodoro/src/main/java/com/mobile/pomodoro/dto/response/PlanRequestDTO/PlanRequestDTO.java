package com.mobile.pomodoro.dto.response.PlanRequestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanRequestDTO {
    private String title;
    private int s_break_duration;
    private int l_break_duration;
    private List<StepDTO> steps;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StepDTO {
        private String plan_title;
        private int plan_duration;
        private int order;
    }
}
