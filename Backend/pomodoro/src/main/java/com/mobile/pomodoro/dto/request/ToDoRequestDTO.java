package com.mobile.pomodoro.dto.request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ToDoRequestDTO {
    private Long userId;
    private String title;
    private int isDone;
}
