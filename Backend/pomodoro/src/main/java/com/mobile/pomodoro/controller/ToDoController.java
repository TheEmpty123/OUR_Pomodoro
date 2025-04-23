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

    @GetMapping()
    @ResponseBody
    public ToDoResponseDTO getRecentPlan(@RequestAttribute(name = "user") User user) throws Exception {
        return toDoService.getAllTodosByUserId(user.getUserId());
    }


    @PostMapping()
    public ResponseEntity<MessageResponseDTO> createPlan(@RequestBody ToDoRequestDTO requestDTO, @RequestHeader("username") String username) {
        return new ResponseEntity<>(toDoService.createToDo(requestDTO, username), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseDTO> updateToDo(@PathVariable("id") Long todoId, @RequestBody ToDoRequestDTO requestDTO, @RequestHeader("username") String username) {
        return new ResponseEntity<>(toDoService.updateToDo(todoId, requestDTO, username), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponseDTO> deleteToDo(@PathVariable("id") Long todoId, @RequestBody ToDoRequestDTO requestDTO, @RequestHeader("username") String username) {
        return new ResponseEntity<>(toDoService.deleteToDo(todoId, requestDTO, username), HttpStatus.OK);
    }

}
