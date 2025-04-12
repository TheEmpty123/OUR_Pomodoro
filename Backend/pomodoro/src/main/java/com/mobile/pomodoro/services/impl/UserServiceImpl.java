package com.mobile.pomodoro.services.impl;

import com.mobile.pomodoro.CustomException.FailedToLoginException;
import com.mobile.pomodoro.CustomException.FailedToRegisterException;
import com.mobile.pomodoro.dto.request.LoginRequestDTO;
import com.mobile.pomodoro.dto.request.RegisterRequestDTO;
import com.mobile.pomodoro.dto.response.MessageResponseDTO;
import com.mobile.pomodoro.entities.User;
import com.mobile.pomodoro.repositories.UserRepository;
import com.mobile.pomodoro.services.IUserService;
import com.mobile.pomodoro.utils.MyUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl extends AService implements IUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private MyUtils myUtils;

    //    @Autowired
    //    private JwtService jwtService;

    @Override
    public void initData() {
        // Implement the method to initialize data
        // This could involve setting up default users, roles, etc.
    }

    @Override
    public MessageResponseDTO login(LoginRequestDTO requestDTO) {
        // Implement the login logic here

        try {
            Optional<User> checkUser = userRepository.findUsersByUsername(requestDTO.getUsername());
            if (checkUser.isPresent()) {
                User user = checkUser.get();
                if (user.getPassword().equals(requestDTO.getPassword())) {
                    // Generate a token and return it in the response
                    // String token = jwtService.generateToken(user);
                    // return new TokenResponseDTO(token);
                    return new MessageResponseDTO("Login successful");
                } else {
                    return new MessageResponseDTO("Invalid password");
                }
            } else {
                return new MessageResponseDTO("User not found");
            }
        }
        catch (Exception e) {
            Exception failedToLoginException = new FailedToLoginException("Something went wrong while logging in");
            return new MessageResponseDTO(failedToLoginException.getMessage());
        }

    }

    @Override
    public MessageResponseDTO register(RegisterRequestDTO requestDTO) {
        // Implement the registration logic here

        try {
            // Check if the user already exists
            Optional<User> checkUser = userRepository.findUsersByUsername(requestDTO.getUsername());
            if (checkUser.isPresent()) {
                return MessageResponseDTO
                        .builder()
                        .message("User already exists")
                        .build();
            }
            else {
                // Create a new user entity
                User user = modelMapper.map(requestDTO, User.class);

                user.setPassword(requestDTO.getPassword());
                user.setUsername(requestDTO.getUsername());

                userRepository.save(user);

                // Optionally, you can generate a token for the new user
                // String token = jwtService.generateToken(user);
                return MessageResponseDTO
                        .builder()
                        .message("User registered successfully")
                        .build();
            }

        }
        catch (Exception e) {
            Exception e2 = new FailedToRegisterException("Something went wrong while registering");
            return new MessageResponseDTO(e2.getMessage());
        }
    }

    // Add any other user-related methods you need
    // For example, you might have methods for updating user profiles, changing passwords, etc.
}
