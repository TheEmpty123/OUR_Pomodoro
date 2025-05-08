package com.mobile.pomodoro.dto.response.DailyTaskResponeseDTO;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DailyTaskResponeseDTO {
    List<SingleDailyTaskDTO> list;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SingleDailyTaskDTO {
        String title;
        int is_done;
    }
}
