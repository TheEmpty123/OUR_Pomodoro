package com.mobile.pomodoro.request_dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyTaskRequestDTO {
    private String daily_task_description;
    private String title;
    private int s_break_duration;
    private int l_break_duration;
    private List<PlanTaskDTO> steps;
}
