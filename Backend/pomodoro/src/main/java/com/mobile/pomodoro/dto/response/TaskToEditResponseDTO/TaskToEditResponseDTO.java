package com.mobile.pomodoro.dto.response.TaskToEditResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class TaskToEditResponseDTO {
    private String title;
    private int s_break_duration;
    private int l_break_duration;
    private List<StepToEdit> steps;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StepToEdit {
        private String plan_title;
        private int plan_duration;
        private int order;
    }
}
