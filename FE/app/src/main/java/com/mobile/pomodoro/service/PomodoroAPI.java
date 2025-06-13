package com.mobile.pomodoro.service;

import com.mobile.pomodoro.request_dto.DailyTaskRequestDTO;
import com.mobile.pomodoro.request_dto.LoginRequestDTO;
import com.mobile.pomodoro.request_dto.PlanRequestDTO;
import com.mobile.pomodoro.request_dto.RegisterRequestDTO;
import com.mobile.pomodoro.request_dto.TodoRequestDTO;
import com.mobile.pomodoro.response_dto.DailyTaskDetailResponseDTO;
import com.mobile.pomodoro.response_dto.DailyTaskListResponseDTO;
import com.mobile.pomodoro.response_dto.MessageResponseDTO;
import com.mobile.pomodoro.response_dto.PlanResponseDTO;
import com.mobile.pomodoro.response_dto.TodoListResponseDTO;

import retrofit2.Call;
import retrofit2.http.*;

public interface PomodoroAPI {

    @POST("api/v1/user/login")
    Call<MessageResponseDTO> login(@Body LoginRequestDTO loginRequestDTO);

    @POST("api/v1/user/register")
    Call<MessageResponseDTO> register(@Body RegisterRequestDTO registerRequestDTO);

    @POST("api/v1/user/test")
    Call<MessageResponseDTO> testLogin(@Body LoginRequestDTO loginRequestDTO);

    @POST("/api/v1/plan/save")
    Call<PlanResponseDTO> savePlan(@Body PlanRequestDTO planRequest);

    @POST("/api/v1/plan/do-not-save")
    Call<PlanResponseDTO> startPlan(@Body PlanRequestDTO planRequest);

    @GET("/api/v1/todos")
    Call<TodoListResponseDTO> getTodos();

    @POST("/api/v1/todos")
    Call<MessageResponseDTO> createTodo(@Body TodoRequestDTO todoRequest);

    @PUT("/api/v1/todos/{id}")
    Call<MessageResponseDTO> updateTodo(@Path("id") long id, @Body TodoRequestDTO todoRequest);

    @DELETE("/api/v1/todos/{id}")
    Call<MessageResponseDTO> deleteTodo(@Path("id") long id);
    @GET("/api/v1/plan-to-edit/{id}")
    Call<DailyTaskDetailResponseDTO> getPlanToEdit(@Path("id") long id);
    @GET("api/v1/daily-task")
    Call<DailyTaskListResponseDTO> getDailyTasks();
    @GET("api/v1/daily-task/{id}")
    Call<PlanResponseDTO> getDailyTaskDetails(@Path("id") long id);
    @POST("api/v1/daily-task")
    Call<MessageResponseDTO> createDailyTask(@Body DailyTaskRequestDTO request);
    @PUT("api/v1/daily-task/{id}")
    Call<MessageResponseDTO> updateDailyTask(@Path("id") long id, @Body DailyTaskRequestDTO request);
    @PUT("api/v1/daily-task/complete/{id}")
    Call<MessageResponseDTO> completeDailyTask(@Path("id") long id);
    @DELETE("api/v1/daily-task/{id}")
    Call<MessageResponseDTO> deleteDailyTask(@Path("id") long id);
    @GET("/api/v1/recent-plan")
    Call<PlanResponseDTO> getRecentPlan();
}
