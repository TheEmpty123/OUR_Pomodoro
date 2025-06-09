package com.mobile.pomodoro.mapper.response;

import com.mobile.pomodoro.dto.response.PlanResponseDTO;
import com.mobile.pomodoro.entities.Plan;
import com.mobile.pomodoro.entities.PlanTask;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class PlanResponseDTOMapper {

    @Mapping(target = "planId", source = "plan.planId")
    @Mapping(target = "planTitle", source = "plan.title")
    public abstract PlanResponseDTO mapToDTO(Plan plan);

    @Mapping(target = "task_name", source = "planTask.task_name")
    @Mapping(target = "duration", source = "planTask.duration")
    @Mapping(target = "task_order", source = "planTask.task_order")
    public abstract PlanResponseDTO.TaskDTO mapToDTO(PlanTask planTask);

}
