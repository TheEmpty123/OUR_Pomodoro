package com.mobile.pomodoro.response_dto;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanTaskResponseDTO {
    @SerializedName("task_name")
    private String plan_title;
    @SerializedName("duration")
    private int plan_duration;
    @SerializedName("task_order")
    private int order;
}
