package com.mobile.pomodoro.service;

import com.mobile.pomodoro.request_dto.LoginRequestDTO;
import com.mobile.pomodoro.request_dto.PlanRequestDTO;
import com.mobile.pomodoro.response_dto.MessageResponseDTO;

import retrofit2.Call;
import retrofit2.http.*;

public interface PomodoroAPI {

    @POST("api/v1/user/login")
    Call<MessageResponseDTO> login(@Body LoginRequestDTO loginRequestDTO);

    @POST("api/v1/user/test")
    Call<MessageResponseDTO> testLogin(@Body LoginRequestDTO loginRequestDTO);

    @POST("/api/v1/plan/save")
    Call<MessageResponseDTO> savePlan(@Body PlanRequestDTO planRequest);

    @POST("/api/v1/plan/do-not-save")
    Call<MessageResponseDTO> startPlan(@Body PlanRequestDTO planRequest);
}
