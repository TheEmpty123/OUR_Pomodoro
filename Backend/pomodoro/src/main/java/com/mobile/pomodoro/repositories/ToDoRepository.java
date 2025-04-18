package com.mobile.pomodoro.repositories;
import com.mobile.pomodoro.entities.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ToDoRepository extends JpaRepository<Todo, Long>{
    @Query("SELECT t FROM todo t WHERE t.userId = :userId")
    List<Todo> findByUserId(@Param("userId") Long userId);
}
