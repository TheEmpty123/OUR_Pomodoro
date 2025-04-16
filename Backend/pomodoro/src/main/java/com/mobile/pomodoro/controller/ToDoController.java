package com.mobile.pomodoro.controller;

import com.mobile.pomodoro.dto.response.ToDoResponeseDTO.ToDoResponseDTO;

import com.mobile.pomodoro.entities.Todo;
import com.mobile.pomodoro.repositories.ToDoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/todos")
public class ToDoController {
    @Autowired
    private ToDoRepository todoRepository;

    @GetMapping("/{userId}")
    public ToDoResponseDTO getTodos(@PathVariable Long userId) {
        List<Todo> todos = todoRepository.findByUserId(userId);

        List<ToDoResponseDTO.SingleToDoDTO> list = todos.stream()
                .map(todo -> ToDoResponseDTO.SingleToDoDTO.builder()
                        .title(todo.getTitle())
                        .is_done(todo.getIsDone())
                        .build())
                .toList();

        return ToDoResponseDTO.builder().list(list).build();
    }

    @PostMapping
    public Todo createTodo(@RequestBody Todo todo) {
        return todoRepository.save(todo);
    }
}
