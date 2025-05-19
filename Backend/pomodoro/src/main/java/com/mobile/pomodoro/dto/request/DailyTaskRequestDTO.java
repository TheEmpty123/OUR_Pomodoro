package com.mobile.pomodoro.dto.request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyTaskRequestDTO {
    private Long userId;
    private Long planId;
    private String title;
    private int isDone;

}

