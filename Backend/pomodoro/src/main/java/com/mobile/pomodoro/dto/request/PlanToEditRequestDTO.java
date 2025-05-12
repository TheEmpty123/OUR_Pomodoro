package com.mobile.pomodoro.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanToEditRequestDTO {
    private String title;
    private Long plan_id;
    private List<Step> steps;

    @Data
    public static class Step {
        private Integer order;
        private String w_tittle;
        private Integer duration;
    }
}
