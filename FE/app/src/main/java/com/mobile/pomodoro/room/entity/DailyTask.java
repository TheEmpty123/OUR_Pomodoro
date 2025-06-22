package com.mobile.pomodoro.room.entity;

import androidx.room.Entity;
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
@Entity(tableName = "daily_task")
@FieldDefaults(level = AccessLevel.PUBLIC)
public class DailyTask {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String title;
    private int isDone;
    private Long planId;
}
