package com.mobile.pomodoro.controller;

import com.mobile.pomodoro.dto.request.BaodailyTaskRequestDTO;
import com.mobile.pomodoro.dto.response.DailyTaskResponeseDTO.DailyTaskResponeseDTO;
import com.mobile.pomodoro.dto.response.MessageResponseDTO;
import com.mobile.pomodoro.dto.response.PlanToEditResponseDTO.PlanToEditResponseDTO;
import com.mobile.pomodoro.entities.User;
import com.mobile.pomodoro.services.IDailyTaskService;
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
    public DailyTaskResponeseDTO getRecentPlan(@RequestAttribute(name = "user") User user) throws Exception {
        return dailyTaskService.getAllDailyTaskByUser(user.getUserId());
    }
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseDTO> updateDailyTask(
            @PathVariable Long id,
            @RequestBody PlanToEditResponseDTO request,
            @RequestAttribute(name = "user") User user) throws Exception {
        return new ResponseEntity<>(dailyTaskService.updateDailyTask(id, request, user), HttpStatus.OK);
    }

    @PutMapping("/complete/{id}")
    public ResponseEntity<MessageResponseDTO> completeDailyTask(
            @PathVariable Long id,
            @RequestAttribute(name = "user") User user) throws Exception {
        return new ResponseEntity<>(dailyTaskService.completeDailyTask(id, user), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponseDTO> deleteDailyTask(
            @PathVariable Long id,
            @RequestAttribute(name = "user") User user) throws Exception {
        return new ResponseEntity<>(dailyTaskService.deleteDailyTask(id, user), HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<PlanToEditResponseDTO> getDailyTaskPlanDetails(@PathVariable Long id,
                                                                         @RequestAttribute(name = "user") User user) throws Exception {
        return new  ResponseEntity<>(dailyTaskService.getDailyTaskPlanDetails(id, user), HttpStatus.OK);

    }
@PostMapping()
    public ResponseEntity<MessageResponseDTO> createDailyTask(@RequestBody BaodailyTaskRequestDTO request,
                                                              @RequestAttribute(name = "user") User user) {
        try{
            return new ResponseEntity<>(dailyTaskService.createDailyTask(request, user), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


}
