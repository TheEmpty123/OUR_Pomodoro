package com.mobile.pomodoro.service;

import com.mobile.pomodoro.entity.Plan;
import com.mobile.pomodoro.entity.PlanTask;
import com.mobile.pomodoro.request_dto.PlanRequestDTO;
import com.mobile.pomodoro.response_dto.PlanResponseDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PomodoroAPI {
    @POST("/api/v1/plan/save")
    Call<PlanResponseDTO> savePlan(@Body PlanRequestDTO planRequest);

    @POST("/api/v1/plan/do-not-save")
    Call<PlanResponseDTO> startPlan(@Body PlanRequestDTO planRequest);
}
