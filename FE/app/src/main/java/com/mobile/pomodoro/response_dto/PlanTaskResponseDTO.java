package com.mobile.pomodoro.response_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanTaskResponseDTO {
    private String plan_title;
    private double plan_duration;
    private int order;
}
