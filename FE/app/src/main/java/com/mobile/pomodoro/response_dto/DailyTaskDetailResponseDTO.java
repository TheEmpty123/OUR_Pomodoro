package com.mobile.pomodoro.response_dto;

import com.mobile.pomodoro.request_dto.PlanTaskDTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyTaskDetailResponseDTO {
    private String daily_task_description;
    private String title;
    private int s_break_duration;
    private int l_break_duration;
    private List<PlanTaskResponseDTO> steps;
}
