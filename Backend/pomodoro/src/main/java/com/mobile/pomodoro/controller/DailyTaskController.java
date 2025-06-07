package com.mobile.pomodoro.controller;

import com.mobile.pomodoro.dto.request.DailyTaskRequestDTO;
import com.mobile.pomodoro.dto.response.DailyTaskResponeseDTO;
import com.mobile.pomodoro.dto.response.MessageResponseDTO;
import com.mobile.pomodoro.dto.response.PlanToEditResponseDTO;
import com.mobile.pomodoro.entities.User;
import com.mobile.pomodoro.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/daily-task")
public class DailyTaskController {
    @Autowired
    private IDailyTaskService dailyTaskService;

    @GetMapping()
    @ResponseBody
    public DailyTaskResponeseDTO getRecentPlan(@RequestAttribute(name = "user") User user) {
        return dailyTaskService.getAllDailyTaskByUser(user.getUserId());
    }

    @PostMapping
    public MessageResponseDTO createDailyTask(
            @RequestBody DailyTaskRequestDTO request,
            @RequestAttribute(name = "user") User user) {
        return dailyTaskService.createDailyTask(request, user.getUserId());
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getDailyTaskPlanDetails(
            @PathVariable Long id,
            @RequestAttribute(name = "user") User user) {
        try {
            PlanToEditResponseDTO response = dailyTaskService.getDailyTaskPlanDetails(id, user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    MessageResponseDTO.builder()
                            .message("Failed")
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

//    @PostMapping("/plan-to-edit")
//    public ResponseEntity<?> planToEdit(
//            @RequestBody PlanRequestDTO request,
//            @RequestAttribute(name = "user") User user) {
//        try {
//            return new ResponseEntity<>(dailyTaskService.planToEdit(request, user), HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

}
