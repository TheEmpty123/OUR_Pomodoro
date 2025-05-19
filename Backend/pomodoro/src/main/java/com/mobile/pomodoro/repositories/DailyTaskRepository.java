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

    void deleteByPlanId(Long planId);

    @Query(value = "INSERT INTO daily_task (user_id, plan_id, title, is_done) VALUES (:userId, :planId, :title, :isDone)", nativeQuery = true)
    Optional<DailyTask> saveD(long userId, long planId, String title, int isDone);


}

