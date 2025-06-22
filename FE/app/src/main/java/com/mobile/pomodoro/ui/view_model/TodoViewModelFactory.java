package com.mobile.pomodoro.ui.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mobile.pomodoro.room.repo.SingleThreadRepo;

public class TodoViewModelFactory  implements ViewModelProvider.Factory {
    private final Application app;
    private final SingleThreadRepo repo;

    public TodoViewModelFactory(SingleThreadRepo repo){
        this.repo = repo;
        this.app = null;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
//        System.out.println(modelClass.isAssignableFrom(TodoViewModel.class));
        if (modelClass.isAssignableFrom(TodoViewModel.class)){
            if (app != null){
                return (T) new TodoViewModel(app);
            } else {
                return (T) new TodoViewModel(repo);
            }
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
