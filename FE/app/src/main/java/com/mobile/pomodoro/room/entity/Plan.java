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
@Entity(tableName = "plan")
@FieldDefaults(level = AccessLevel.PUBLIC)
public class Plan implements BaseEntity{
    @PrimaryKey(autoGenerate = true)
    Long id;
    private String title;
}