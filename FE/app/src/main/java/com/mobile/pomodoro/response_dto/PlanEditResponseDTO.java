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
public class PlanEditResponseDTO {
    private Long id;
    private String title;
    private List<PlanTaskEditResponseDTO> steps;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class PlanTaskEditResponseDTO {
        private String plan_title;
        private int plan_duration;
        private int order;

    }
}