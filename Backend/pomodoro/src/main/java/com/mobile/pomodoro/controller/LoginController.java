package com.mobile.pomodoro.controller;

import com.mobile.pomodoro.dto.request.LoginRequestDTO;
import com.mobile.pomodoro.dto.request.RegisterRequestDTO;
import com.mobile.pomodoro.dto.response.MessageResponseDTO;
import com.mobile.pomodoro.services.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user/")
public class LoginController {
    @Autowired
    private IUserService userService;

    // Add your login-related endpoints here

    // Example endpoint
    @PostMapping("login")
    public ResponseEntity<MessageResponseDTO> login(@RequestBody LoginRequestDTO requestDTO) {
        return new ResponseEntity<>(userService.login(requestDTO), HttpStatus.OK);
    }

    @PostMapping("register")
    public ResponseEntity<MessageResponseDTO> register(@RequestBody RegisterRequestDTO requestDTO) {
        return new ResponseEntity<>(userService.register(requestDTO), HttpStatus.OK);
    }

    // Add more endpoints as needed
    @PostMapping("test")
    public ResponseEntity<MessageResponseDTO> test(@RequestBody RegisterRequestDTO requestDTO) {
        return new ResponseEntity<>(
                MessageResponseDTO
                        .builder()
                        .message("DJT ME TOAN LOZ DAY NHU CON CAC")
                        .build(),
                HttpStatus.OK
                );
    }
}
