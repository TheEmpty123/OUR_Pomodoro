package com.mobile.pomodoro.ui.view_model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
public class Resource<T> {
    public enum  Status { SUCCESS, ERROR, LOADING}

    private final Status status;
    private final T data;
    private final String message;

    public static <T> Resource<T> success(T data){
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(T data, String message){
        return new Resource<>(Status.ERROR, data, message);
    }

    public static <T> Resource<T> loading(T data){
        return new Resource<>(Status.LOADING, data, null);
    }
}
