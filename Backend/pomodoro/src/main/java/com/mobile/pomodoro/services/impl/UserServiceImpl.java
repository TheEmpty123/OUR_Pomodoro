package com.mobile.pomodoro.services.impl;

import com.mobile.pomodoro.CustomException.FailedToLoginException;
import com.mobile.pomodoro.CustomException.FailedToRegisterException;
import com.mobile.pomodoro.CustomException.UserNotFoundException;
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
        log.setName(this.getClass().getSimpleName());
        log.info("Initializing data");
    }

    @Override
    public MessageResponseDTO login(LoginRequestDTO requestDTO) {
        log.info("Login requested: " + requestDTO.getUsername());

        // Implement the login logic here

        try {
            Optional<User> checkUser = userRepository.findUsersByUsername(requestDTO.getUsername());
            if (checkUser.isPresent()) {
                User user = checkUser.get();
                if (user.getPassword().equals(requestDTO.getPassword())) {
                    // Generate a token and return it in the response
                    // String token = jwtService.generateToken(user);
                    // return new TokenResponseDTO(token);
                    userRepository.save(user);
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
        log.info("Registering user: " + requestDTO.getUsername());

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

    @Override
    public User getUserByUsername(String username) throws UserNotFoundException {
        // Implement the logic to retrieve a user by username
        Optional<User> userOptional = userRepository.findUsersByUsername(username);

        if (userOptional.isPresent()) {
            log.info("User found");
            return userOptional.get();
        }

        // Handle the case where the user is not found
        log.error("User not found");
        throw  new UserNotFoundException("Could not find user");
    }

    // Add any other user-related methods you need
    // For example, you might have methods for updating user profiles, changing passwords, etc.
}
