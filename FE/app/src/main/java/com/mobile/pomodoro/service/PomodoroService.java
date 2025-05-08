package com.mobile.pomodoro.service;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class PomodoroService {
    private static final String BASE_URL = "https://testrender-ax0w.onrender.com/";
    private static Retrofit retrofit;

    //khởi tạo retrofit singleton
    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static PomodoroAPI getClient(){
        return getRetrofitInstance().create(PomodoroAPI.class);
    }
}
