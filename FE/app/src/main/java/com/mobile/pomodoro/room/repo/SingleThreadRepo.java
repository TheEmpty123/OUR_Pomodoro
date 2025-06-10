package com.mobile.pomodoro.room.repo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mobile.pomodoro.room.entity.BaseEntity;
import com.mobile.pomodoro.ui.view_model.Resource;
import com.mobile.pomodoro.utils.LogObj;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleThreadRepo<K extends BaseRepository<V>, V extends BaseEntity> {
    private final BaseRepository<V> repo;
    private final ExecutorService executor;
    private final LogObj log;

    public SingleThreadRepo(BaseRepository<V> repo) {
        this.repo = repo;
        this.executor = Executors.newSingleThreadExecutor();
        this.log = new LogObj();
        log.setName(getClass().getSimpleName());
    }

    public LiveData<Resource<List<V>>> getAll(){

        MutableLiveData<Resource<List<V>>> res = new MutableLiveData<>();
        res.setValue(Resource.loading(null));

        try {
            List<V> items = repo.getAll();
            res.postValue(Resource.success(items));
        }
        catch (Exception e){
            res.setValue(Resource.error(null, "Failed to load plans: " + e.getMessage()));
            log.error("Error occurred while getting resources");
            log.error(e.getMessage());
        }
        return res;
    }

    public void insert(V item){
        try {
            repo.insert(item);
        }
        catch (Exception e){
            log.error("Error occurred while inserting " + item);
            log.error(e.getMessage());
        }
    }

    public void update(V item){
        try {
            repo.update(item);
        } catch (Exception e) {
            log.error("Error occurred while updating " + item);
            log.error(e.getMessage());
        }
    }

    public void cleanUp(){
        if (executor != null && !executor.isShutdown()){
            log.info("Shutting down " + executor);
            executor.shutdown();
        }
    }
}
