package com.mobile.pomodoro.controller;

import com.mobile.pomodoro.dto.response.ToDoResponeseDTO.ToDoResponseDTO;
import com.mobile.pomodoro.entities.User;
import com.mobile.pomodoro.services.IToDoService;
import org.springframework.beans.factory.annotation.Autowired;
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


//    @PostMapping("create")
//    public MessageResponseDTO createTodo(@RequestAttribute(name = "user") User user,
//                                         @RequestBody ToDoRequestDTO todoRequestDTO) {
//        return toDoService.createTodo(user, todoRequestDTO);
//    }
}
