package com.mobile.pomodoro.response_dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanResponseDTO {
    private Long planId;
    private String planTitle;
    private List<PlanTaskResponseDTO> steps;
}
