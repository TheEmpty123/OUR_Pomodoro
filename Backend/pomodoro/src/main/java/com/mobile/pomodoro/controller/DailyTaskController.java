package com.mobile.pomodoro.controller;

import com.mobile.pomodoro.dto.response.DailyTaskResponeseDTO.DailyTaskResponeseDTO;
import com.mobile.pomodoro.entities.User;
import com.mobile.pomodoro.services.IDailyTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/daily-task")
public class DailyTaskController {
    @Autowired
    private IDailyTaskService dailyTaskService;

    @GetMapping()
    @ResponseBody
    public DailyTaskResponeseDTO getRecentPlan(@RequestAttribute(name = "user") User user) throws Exception {
        return dailyTaskService.getAllDailyTaskByUser(user.getUserId());
    }
}
