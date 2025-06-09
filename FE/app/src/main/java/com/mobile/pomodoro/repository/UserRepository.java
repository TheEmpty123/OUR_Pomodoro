package com.mobile.pomodoro.repository;

import android.content.Context;

import com.mobile.pomodoro.utils.*;

import lombok.Getter;

public class UserRepository {
    // Lấy username
    @Getter
    private String username;
    private static UserRepository instance;
    private Context context;

    public UserRepository(Context context) {
        this.context = context;
        this.username = MyUtils.get(this.context, "username");
    }

    //    singleton
    public static UserRepository getInstance(Context context) {
        if (instance == null) {
            instance = new UserRepository(context);
        }
        return instance;
    }
}
