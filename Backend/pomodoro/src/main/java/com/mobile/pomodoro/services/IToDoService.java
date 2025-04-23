package com.mobile.pomodoro.services;

import com.mobile.pomodoro.CustomException.UserNotFoundException;
import com.mobile.pomodoro.dto.request.ToDoRequestDTO;
import com.mobile.pomodoro.dto.response.MessageResponseDTO;
import com.mobile.pomodoro.dto.response.ToDoResponeseDTO.ToDoResponseDTO;

public interface IToDoService extends IInitializerData{

    ToDoResponseDTO getAllTodosByUserId(Long userId) throws UserNotFoundException;
    MessageResponseDTO createToDo(ToDoRequestDTO requestDTO,String userName);
    MessageResponseDTO updateToDo(Long userId,ToDoRequestDTO requestDTO,String userName);
    MessageResponseDTO deleteToDo(Long userId,ToDoRequestDTO requestDTO,String userName);
}

