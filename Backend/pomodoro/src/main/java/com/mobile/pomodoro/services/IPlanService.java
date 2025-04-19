package com.mobile.pomodoro.services;

import com.mobile.pomodoro.CustomException.UserNotFoundException;
import com.mobile.pomodoro.dto.response.PlanRequestDTO.PlanRequestDTO;
import com.mobile.pomodoro.dto.response.PlanResponseDTO.PlanResponseDTO;
import com.mobile.pomodoro.dto.response.TaskToEditResponseDTO.TaskToEditResponseDTO;
import com.mobile.pomodoro.entities.Plan;
import com.mobile.pomodoro.entities.PlanTask;

import java.util.List;

public interface IPlanService extends IInitializerData{

    // Define the methods for plan-related operations
    public PlanResponseDTO findPlan(String username) throws UserNotFoundException;

    PlanResponseDTO findRecentPlan(String username) throws UserNotFoundException;

    TaskToEditResponseDTO convertPlanToEditFormat(Long planId);
    PlanResponseDTO processWithoutSaving(PlanRequestDTO request);

}
