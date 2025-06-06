package com.mobile.pomodoro.services;

import com.mobile.pomodoro.dto.request.DailyTaskRequestDTO;
import com.mobile.pomodoro.dto.request.PlanToEditRequestDTO;
import com.mobile.pomodoro.dto.response.DailyTaskResponeseDTO.DailyTaskResponeseDTO;
import com.mobile.pomodoro.dto.response.MessageResponseDTO;
import com.mobile.pomodoro.dto.response.PlanToEditResponseDTO.PlanToEditResponseDTO;
import com.mobile.pomodoro.entities.User;

public interface IDailyTaskService extends IInitializerData{
    DailyTaskResponeseDTO getAllDailyTaskByUser(Long userId);
    MessageResponseDTO createDailyTask(DailyTaskRequestDTO request, Long userId);
    PlanToEditResponseDTO getDailyTaskPlanDetails(Long id, User user);
    PlanToEditResponseDTO planToEdit(PlanToEditRequestDTO request, User user);

    MessageResponseDTO completeDailyTask(Long id, User user);
    MessageResponseDTO deleteDailyTask(Long id, User user);
}
