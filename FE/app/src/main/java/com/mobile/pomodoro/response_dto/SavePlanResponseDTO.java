package com.mobile.pomodoro.response_dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SavePlanResponseDTO {
    // Getters and Setters
    @SerializedName("planId")
    private Long planId;

    @SerializedName("planTitle")
    private String planTitle;

    @SerializedName("steps")
    private List<SaveTaskDTO> steps;

    // Inner class for tasks
    public static class SaveTaskDTO {
        @SerializedName("task_name")
        private String task_name;

        @SerializedName("duration")
        private double duration;

        @SerializedName("task_order")
        private int task_order;
    }
}