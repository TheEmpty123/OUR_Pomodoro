package com.mobile.pomodoro.repositories;

import com.mobile.pomodoro.entities.PlanTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface PlanTaskRepository extends JpaRepository<PlanTask, Long> {
//    @Query("SELECT pt FROM plan_task pt WHERE pt.plan.id = :planId ORDER BY pt.task_order")
//    List<PlanTask> findTasksByPlanId(@Param("planId") Long planId);

    @Query("SELECT pt FROM plan_task pt WHERE pt.plan.planId = :planId ORDER BY pt.task_order")
    List<PlanTask> findTaskByPlanId(@Param("planId") Long planId);
}
