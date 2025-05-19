package com.mobile.pomodoro.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaodailyTaskRequestDTO {
     private String title;
     private int s_break_duration;
     private int l_break_duration;
     private  String daily_task_description;
    private List<PlanRequestDTO.StepRequest> steps;
}
