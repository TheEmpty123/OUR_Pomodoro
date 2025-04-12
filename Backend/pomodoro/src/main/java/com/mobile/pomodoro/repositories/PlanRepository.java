package com.mobile.pomodoro.repositories;

import com.mobile.pomodoro.entities.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    // Add custom query methods if needed
}
