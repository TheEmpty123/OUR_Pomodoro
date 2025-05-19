package com.mobile.pomodoro.services;

import com.mobile.pomodoro.dto.request.DailyTaskRequestDTO;
import com.mobile.pomodoro.dto.response.DailyTaskResponeseDTO.DailyTaskResponeseDTO;
import com.mobile.pomodoro.dto.response.MessageResponseDTO;

public interface IDailyTaskService extends IInitializerData{
    DailyTaskResponeseDTO getAllDailyTaskByUser(Long userId);
    MessageResponseDTO createDailyTask(DailyTaskRequestDTO request, Long userId);
}
