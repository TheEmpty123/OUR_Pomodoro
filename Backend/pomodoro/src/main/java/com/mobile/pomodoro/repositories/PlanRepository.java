package com.mobile.pomodoro.repositories;

import com.mobile.pomodoro.entities.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    // Add custom query methods if needed

    @Query("SELECT p FROM plan p WHERE p.user.username = :username ORDER BY p.createdAt DESC LIMIT 1")
    Optional<Plan> findRecentPlanByUsername(@Param("username") String username);

    @Query("SELECT p.planId FROM plan p order by p.createdAt ASC LIMIT 1")
    long findbyid();

}
