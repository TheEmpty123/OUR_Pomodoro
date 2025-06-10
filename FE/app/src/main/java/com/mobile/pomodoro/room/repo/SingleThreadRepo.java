package com.mobile.pomodoro.room.repo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mobile.pomodoro.room.entity.TodoItem;
import com.mobile.pomodoro.ui.view_model.Resource;
import com.mobile.pomodoro.utils.LogObj;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleThreadRepo{
    private final TodoRepository repo;
    private final ExecutorService executor;
    private final LogObj log;

    public SingleThreadRepo(TodoRepository repo) {
        this.repo = repo;
        this.executor = Executors.newSingleThreadExecutor();
        this.log = new LogObj();
        log.setName(getClass().getSimpleName());
    }

    public LiveData<Resource<List<TodoItem>>> getAll(){

        MutableLiveData<Resource<List<TodoItem>>> res = new MutableLiveData<>();
        res.setValue(Resource.loading(null));

        executor.execute(() ->{
            try {
                List<TodoItem> items = repo.getAll();
                res.postValue(Resource.success(items));
            }
            catch (Exception e){
                res.setValue(Resource.error(null, "Failed to load plans: " + e.getMessage()));
                log.error("Error occurred while getting resources");
                log.error(e.getMessage());
            }
        });
        return res;
    }

    public void insert(TodoItem item){
        executor.execute(() -> {
            try {
                repo.insert(item);
                log.info("Todo successfully created");
            }
            catch (Exception e){
                log.error("Error occurred while inserting " + item);
                log.error(e.getMessage());
            }
        });
    }

    public void update(TodoItem item){
        executor.execute(() -> {
            try {
                repo.update(item);
            } catch (Exception e) {
                log.error("Error occurred while updating " + item);
                log.error(e.getMessage());
            }
        });
    }

    public void cleanUp(){
        if (executor != null && !executor.isShutdown()){
            log.info("Shutting down " + executor);
            executor.shutdown();
        }
    }
}
