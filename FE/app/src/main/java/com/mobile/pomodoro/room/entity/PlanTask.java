package com.mobile.pomodoro.room.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(tableName = "plan_task")
@FieldDefaults(level = AccessLevel.PUBLIC)
public class PlanTask {
    @PrimaryKey(autoGenerate = true)
    Long id;
    private String plan_title;
    private double plan_duration;
    private int order;
    Long plan_id;
}