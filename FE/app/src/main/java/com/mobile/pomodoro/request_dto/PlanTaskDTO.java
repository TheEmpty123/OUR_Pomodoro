package com.mobile.pomodoro.request_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanTaskDTO {
        private String plan_title;
        private double plan_duration;
        private int order;
        private transient int shortBreak;
        private transient int longBreak;
}

