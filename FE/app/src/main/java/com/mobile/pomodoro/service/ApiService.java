package com.mobile.pomodoro.service;

import com.mobile.pomodoro.Model.Plan;
import com.mobile.pomodoro.Model.PlanTask;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/v1/plan/save")
    Call<Void> savePlan(@Body List<PlanTask> planList);

    @POST("/api/v1/plan/do-not-save")
    Call<Plan> startPlan(@Body Plan plan);
}
