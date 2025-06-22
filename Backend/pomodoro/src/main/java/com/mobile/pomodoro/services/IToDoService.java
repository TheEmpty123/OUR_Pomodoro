package com.mobile.pomodoro.services;
import com.mobile.pomodoro.dto.request.ToDoRequestDTO;
import com.mobile.pomodoro.dto.response.MessageResponseDTO;
import com.mobile.pomodoro.dto.response.ToDoResponseDTO;
import com.mobile.pomodoro.entities.User;

public interface IToDoService extends IInitializerData{

    ToDoResponseDTO getAllTodosByUserId(Long userId);
    MessageResponseDTO createToDo(ToDoRequestDTO requestDTO, User user);
    MessageResponseDTO updateToDo(Long userId,ToDoRequestDTO requestDTO,User user);
    MessageResponseDTO deleteToDo(Long userId,User user);
}

