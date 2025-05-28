package com.mobile.pomodoro.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DailyTaskRequestDTO {
    String daily_task_description;
    String title;
    int s_break_duration;
    int l_break_duration;
    List<StepRequest> steps;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StepRequest {
        String plan_title;
        int plan_duration;
        int order;
    }
}
