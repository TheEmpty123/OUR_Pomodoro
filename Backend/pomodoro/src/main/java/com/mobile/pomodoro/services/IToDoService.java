package com.mobile.pomodoro.services;

import com.mobile.pomodoro.CustomException.UserNotFoundException;
import com.mobile.pomodoro.dto.response.ToDoResponeseDTO.ToDoResponseDTO;

public interface IToDoService extends IInitializerData{

    ToDoResponseDTO getAllTodosByUserId(Long userId) throws UserNotFoundException;
}

