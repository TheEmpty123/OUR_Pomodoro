package com.mobile.pomodoro.room.repo;

import androidx.room.*;
import com.mobile.pomodoro.room.entity.User;

@Dao
public interface UserRepository {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(User user);

    @Update
    void update(User user);

    @Query("SELECT * FROM users WHERE username = :username")
    User getUserByUsername(String username);
}