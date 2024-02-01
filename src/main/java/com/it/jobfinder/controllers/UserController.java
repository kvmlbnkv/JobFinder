package com.it.jobfinder.controllers;

import com.it.jobfinder.dtos.RegistrationDTO;
import com.it.jobfinder.entities.User;
import com.it.jobfinder.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("getAll")
    public List<User> getAllUsers(){
        return this.userService.getAllUsers();
    }

    @GetMapping("get")
    public User getUser(@RequestBody()String username){
        return this.userService.getUser(username);
    }

    @PostMapping("register")
    public ResponseEntity<User> registerUser(@RequestBody()RegistrationDTO dto){
        return new ResponseEntity<>(this.userService.addUser(dto), HttpStatus.OK);

    }
}
