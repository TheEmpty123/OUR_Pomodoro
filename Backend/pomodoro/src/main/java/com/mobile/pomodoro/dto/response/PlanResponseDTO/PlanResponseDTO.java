package com.mobile.pomodoro.dto.response.PlanResponseDTO;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanResponseDTO {
    private Long planId;
    private String planTitle;
    private List<TaskDTO> steps;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TaskDTO {
        private String task_name;
        private int task_order;
        private double duration;
    }
}
