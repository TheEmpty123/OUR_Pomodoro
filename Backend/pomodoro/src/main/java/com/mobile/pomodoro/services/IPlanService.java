package com.mobile.pomodoro.services;

import com.mobile.pomodoro.CustomException.UserNotFoundException;
import com.mobile.pomodoro.dto.request.PlanRequestDTO;
import com.mobile.pomodoro.dto.response.PlanTaskResponeseDTO;
import com.mobile.pomodoro.dto.response.PlanResponseDTO;
import com.mobile.pomodoro.entities.User;

public interface IPlanService extends IInitializerData{

    // Define the methods for plan-related operations
    public PlanResponseDTO findPlan(String username) throws UserNotFoundException;
    PlanResponseDTO createPlan(PlanRequestDTO requestDTO, User user);
    PlanResponseDTO findRecentPlan(String username) throws UserNotFoundException;
    PlanTaskResponeseDTO processWithoutSaving(PlanRequestDTO requestDTO, User user);

}
