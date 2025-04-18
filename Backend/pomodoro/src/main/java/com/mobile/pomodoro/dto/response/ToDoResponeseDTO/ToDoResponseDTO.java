package com.mobile.pomodoro.dto.response.ToDoResponeseDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ToDoResponseDTO {
    List<SingleToDoDTO> list;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SingleToDoDTO {
        String title;
        int is_done;
    }
}
