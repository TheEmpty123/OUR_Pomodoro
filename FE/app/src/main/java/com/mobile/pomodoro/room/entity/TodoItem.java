package com.mobile.pomodoro.room.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(tableName = "todo")
@FieldDefaults(level = AccessLevel.PUBLIC)
public class TodoItem {
    @PrimaryKey
    Long id;
    private String title;
    private boolean isDone;
}
