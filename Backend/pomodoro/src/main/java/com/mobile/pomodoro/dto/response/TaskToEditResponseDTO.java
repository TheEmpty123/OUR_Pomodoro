package com.mobile.pomodoro.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskToEditResponseDTO {
    String title;
    int sBreakDuration;
    int lBreakDuration;
    List<TaskStep> steps;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class TaskStep {
        int order;
        String planTitle;
        int planDuration;
    }
}


