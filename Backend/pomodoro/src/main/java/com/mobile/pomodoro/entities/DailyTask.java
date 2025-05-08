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
@Entity(name = "daily_task")
public class DailyTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "plan_id")
    private Long planId;
    @Column(name = "title")
    private String title;
    @Column(name = "is_done")
    private int isDone;
    @Column(name = "created_date")
    private String created_At;
}
