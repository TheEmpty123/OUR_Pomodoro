package com.mobile.pomodoro.entities;

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
    private Long id;

    @Column(name = "task_name")
    private String task_name;

    @Column(name = "duration")
    private double duration;

    @Column(name = "task_order")
    private int task_order;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Plan plan;
    public PlanTask(Plan plan, String taskName, double duration, int taskOrder) {
        this.plan = plan;
        this.task_name = taskName;
        this.duration = duration;
        this.task_order = taskOrder;
    }
}
