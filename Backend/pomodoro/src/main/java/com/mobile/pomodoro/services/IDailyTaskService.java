package com.mobile.pomodoro.services;

import com.mobile.pomodoro.dto.response.DailyTaskResponeseDTO.DailyTaskResponeseDTO;

public interface IDailyTaskService extends IInitializerData{
    DailyTaskResponeseDTO getAllDailyTaskByUser(Long userId);
}
