package com.mobile.pomodoro.entities;

import com.mobile.pomodoro.enums.ETaskDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "plan_task")
public class PlanTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long planId;

    @Column(name = "task_name")
    private String w_title;

    @Column(name = "duration")
    private double duration;

    @Column(name = "task_order")
    private int order;

    @OneToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;
}
