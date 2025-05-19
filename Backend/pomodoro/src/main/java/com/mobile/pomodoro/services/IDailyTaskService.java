package com.mobile.pomodoro.services;

import com.mobile.pomodoro.dto.request.BaodailyTaskRequestDTO;
import com.mobile.pomodoro.dto.response.DailyTaskResponeseDTO.DailyTaskResponeseDTO;
import com.mobile.pomodoro.dto.response.MessageResponseDTO;
import com.mobile.pomodoro.dto.response.PlanToEditResponseDTO.PlanToEditResponseDTO;
import com.mobile.pomodoro.entities.User;

public interface IDailyTaskService extends IInitializerData{
    DailyTaskResponeseDTO getAllDailyTaskByUser(Long userId);
    PlanToEditResponseDTO getDailyTaskPlanDetails(Long dailyTaskId, User user) throws Exception;

    MessageResponseDTO updateDailyTask(Long id, PlanToEditResponseDTO request, User user) throws Exception;
    MessageResponseDTO completeDailyTask(Long id, User user) throws Exception;
    MessageResponseDTO deleteDailyTask(Long id, User user) throws Exception;

    MessageResponseDTO createDailyTask(BaodailyTaskRequestDTO request, User user);

}
