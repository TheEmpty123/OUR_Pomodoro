package com.mobile.pomodoro.room.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Entity(tableName = "users")
@FieldDefaults(level = AccessLevel.PUBLIC)
public class User implements BaseEntity{
    @PrimaryKey
    Long id;
    String username;
    String email;
}