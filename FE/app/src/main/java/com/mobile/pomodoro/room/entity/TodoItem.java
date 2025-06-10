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
public class TodoItem implements BaseEntity{
    @PrimaryKey(autoGenerate = true)
    Long id;
    private String title;
    private int isDone;
}
