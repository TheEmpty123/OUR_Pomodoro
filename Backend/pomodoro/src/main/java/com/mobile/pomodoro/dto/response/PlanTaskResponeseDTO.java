package com.mobile.pomodoro.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanTaskResponeseDTO {
    private String title;
    private List<PlanTaskDTO> steps;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PlanTaskDTO {
        private String task_name;
        private double duration;
        private int task_order;
    }
}
