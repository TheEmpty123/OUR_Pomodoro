package com.mobile.pomodoro.dto.request;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanRequestDTO {
    private String title;
    private int s_break_duration;
    private int l_break_duration;
    private List<StepRequest> steps;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class StepRequest {
        private String plan_title;
        private int plan_duration;
        private int order;
    }
}
