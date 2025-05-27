package com.mobile.pomodoro.controller;
import com.mobile.pomodoro.dto.request.DailyTaskRequestDTO;
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
    public ResponseEntity<PlanToEditResponseDTO> getDailyTaskPlanDetails(@PathVariable Long id,
                                                                         @RequestAttribute(name = "user") User user) {
        try {
            return new ResponseEntity<>(dailyTaskService.getDailyTaskPlanDetails(id, user), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
