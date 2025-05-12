package com.mobile.pomodoro.dto.request;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("is_done")
    private int isDone;
}
