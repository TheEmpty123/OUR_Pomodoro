package com.mobile.pomodoro.controller;

import com.mobile.pomodoro.dto.request.ToDoRequestDTO;
import com.mobile.pomodoro.dto.response.MessageResponseDTO;
import com.mobile.pomodoro.dto.response.ToDoResponeseDTO.ToDoResponseDTO;
import com.mobile.pomodoro.entities.User;
import com.mobile.pomodoro.services.IToDoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/todos")
public class ToDoController {
    @Autowired
    private IToDoService toDoService;

    @GetMapping("get")
    @ResponseBody
    public ToDoResponseDTO getRecentPlan(@RequestAttribute(name = "user") User user) throws Exception{
        return toDoService.getAllTodosByUserId(user.getUserId());
    }


    @PostMapping("create")
    public ResponseEntity<MessageResponseDTO> register(@RequestBody ToDoRequestDTO requestDTO,@RequestHeader("username") String username) {
        return new ResponseEntity<>(toDoService.createToDo(requestDTO,username), HttpStatus.CREATED);
    }
}
