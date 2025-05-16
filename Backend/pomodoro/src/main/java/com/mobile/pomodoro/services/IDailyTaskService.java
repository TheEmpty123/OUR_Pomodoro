package com.mobile.pomodoro.services;

import com.mobile.pomodoro.dto.response.DailyTaskResponeseDTO.DailyTaskResponeseDTO;
import com.mobile.pomodoro.dto.response.PlanToEditResponseDTO.PlanToEditResponseDTO;
import com.mobile.pomodoro.entities.User;

public interface IDailyTaskService extends IInitializerData{
    DailyTaskResponeseDTO getAllDailyTaskByUser(Long userId);
    PlanToEditResponseDTO getDailyTaskPlanDetails(Long dailyTaskId, User user) throws Exception;

}
