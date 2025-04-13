package com.mobile.pomodoro.mapper.response;

import com.mobile.pomodoro.dto.response.PlanResponseDTO.PlanResponseDTO;
import com.mobile.pomodoro.entities.Plan;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class PlanResponseDTOMapper {

    @Mapping(target = "planId", source = "plan.id")
    @Mapping(target = "planTitle", source = "plan.title")
    @Mapping(target = "tasks", source = "plan", qualifiedByName = "mapTasks")
    public abstract PlanResponseDTO mapToDTO(Plan plan);

    @Named("mapTasks")
    public List<PlanResponseDTO.TaskDTO> mapTasks(Plan plan) {
        return null; // Implement the logic to map tasks from Plan entity to TaskDTO
    }
}
