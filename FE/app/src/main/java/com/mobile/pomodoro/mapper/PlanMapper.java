package com.mobile.pomodoro.mapper;

import com.mobile.pomodoro.response_dto.PlanResponseDTO;
import com.mobile.pomodoro.response_dto.PlanTaskResponseDTO;
import com.mobile.pomodoro.room.entity.relation.PlanWithTasks;

import java.util.List;
import java.util.stream.Collectors;

public class PlanMapper {
    public static PlanMapper instance;

    public static PlanMapper getInstance(){
        if (instance == null){
            instance = new PlanMapper();
        }
        return instance;
    }

    public PlanResponseDTO mapToDTO(PlanWithTasks planWithTask) {
        List<PlanTaskResponseDTO> stepDTOs = planWithTask.tasks.stream()
                .map(task -> PlanTaskResponseDTO.builder()
                        .plan_title(task.getPlan_title())
                        .plan_duration((int) task.getPlan_duration())
                        .order(task.getOrder())
                        .build())
                .collect(Collectors.toList());

        return PlanResponseDTO.builder()
                .id(planWithTask.plan.getId())
                .title(planWithTask.plan.getTitle())
                .steps(stepDTOs)
                .build();
    }
}
