package com.mobile.pomodoro.services;

import com.mobile.pomodoro.CustomException.UserNotFoundException;
import com.mobile.pomodoro.dto.request.LoginRequestDTO;
import com.mobile.pomodoro.dto.request.RegisterRequestDTO;
import com.mobile.pomodoro.dto.response.MessageResponseDTO;
import com.mobile.pomodoro.entities.User;
import org.springframework.data.repository.query.Param;

public interface IUserService extends IInitializerData{
    // Define the methods for user-related operations
    // For example, you might have methods for user registration, login, etc.

    //TokenResponseDTO login(LoginRequestDTO requestDTO); -> For future login with token
    MessageResponseDTO login(LoginRequestDTO requestDTO);
    MessageResponseDTO register(RegisterRequestDTO requestDTO);

    User getUserByUsername(@Param("username") String username) throws UserNotFoundException;
}
