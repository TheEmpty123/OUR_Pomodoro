package com.mobile.pomodoro.response_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyTaskResponseDTO {
    private long plan_id;
    private String title;
    private int is_done;
}
