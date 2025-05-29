package com.mobile.pomodoro.dto.response.PlanToEditResponseDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanToEditResponseDTO {

    private String title;
    private Integer s_break_duration;
    private Integer l_break_duration;
    private List<Step> steps;
    @Builder
    @Data
    public static class Step {
        private String plan_title;
        private Integer plan_duration;
        private Integer order;
    }
}

