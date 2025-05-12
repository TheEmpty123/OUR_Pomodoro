package com.mobile.pomodoro.controller;

import com.mobile.pomodoro.dto.request.PlanRequestDTO;
import com.mobile.pomodoro.dto.response.MessageResponseDTO;
import com.mobile.pomodoro.dto.response.PlanTaskResponeseDTO.PlanTaskResponeseDTO;
import com.mobile.pomodoro.dto.response.PlanResponseDTO.PlanResponseDTO;
import com.mobile.pomodoro.entities.User;
import com.mobile.pomodoro.services.IPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class PlanController {
    @Autowired
    private IPlanService planService;

    // Add your plan-related endpoints here
    @GetMapping("recent-plan")
    @ResponseBody
    public PlanResponseDTO getRecentPlan(@RequestAttribute(name = "user") User user) throws Exception{
        return planService.findRecentPlan(user.getUsername());
    }

    @PostMapping("/plan-test")
    public ResponseEntity<String> test(@RequestAttribute(name = "user") User user) {
        return ResponseEntity.ok("Test successful");
    }
    @PostMapping("/plan/save")
    public ResponseEntity<PlanResponseDTO> createPlan(@RequestBody PlanRequestDTO requestDTO, @RequestAttribute(name = "user") User user) {
        return new ResponseEntity<>(planService.createPlan(requestDTO, user), HttpStatus.CREATED);
    }


    @PostMapping("/plan/do-not-save")
    public ResponseEntity<PlanTaskResponeseDTO> processPlanWithoutSaving(@RequestBody PlanRequestDTO request,
                                                                         @RequestAttribute(name = "user") User user) {
        return new ResponseEntity<>(planService.processWithoutSaving(request,user), HttpStatus.CREATED);
    }

//    @GetMapping("/plan-to-edit/{id}")
//    public ResponseEntity<TaskToEditResponseDTO> convertToEdit(@PathVariable Long id,
//                                                               @RequestAttribute(name = "user") User user) {
//        TaskToEditResponseDTO response = planService.convertPlanToEditFormat(id);
//        return ResponseEntity.ok(response);
//    }

}
