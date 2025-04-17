package com.mobile.pomodoro.services.impl;
import com.mobile.pomodoro.CustomException.UserNotFoundException;
import com.mobile.pomodoro.dto.response.ToDoResponeseDTO.ToDoResponseDTO;
import com.mobile.pomodoro.dto.response.ToDoResponeseDTO.ToDoResponseDTO.SingleToDoDTO;
import com.mobile.pomodoro.entities.Todo;
import com.mobile.pomodoro.repositories.ToDoRepository;
import com.mobile.pomodoro.services.IToDoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class ToDoServiceImpl extends AService implements IToDoService {
    @Autowired
    private ToDoRepository toDoRepository;


    @Override
    public void initData() {
        log.setName(this.getClass().getSimpleName());
        log.info("Initializing data");
    }

    @Override
    public ToDoResponseDTO getAllTodosByUserId(Long userId) throws UserNotFoundException {
        List<Todo> todos = toDoRepository.findByUserId(userId);

        List<SingleToDoDTO> todoDTOs = todos.stream()
                .map(todo -> SingleToDoDTO.builder()
                        .title(todo.getTitle())
                        .is_done(todo.getIsDone())
                        .build())
                .collect(Collectors.toList());

        return ToDoResponseDTO.builder()
                .list(todoDTOs)
                .build();
    }
}
