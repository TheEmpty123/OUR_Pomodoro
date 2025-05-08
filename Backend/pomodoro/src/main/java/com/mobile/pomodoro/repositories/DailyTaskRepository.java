package com.mobile.pomodoro.repositories;

import com.mobile.pomodoro.entities.DailyTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface DailyTaskRepository extends JpaRepository<DailyTask, Long>{
    @Query("SELECT t FROM daily_task t WHERE t.userId = :userId")
    List<DailyTask> findByUserId(@Param("userId") Long userId);
}
