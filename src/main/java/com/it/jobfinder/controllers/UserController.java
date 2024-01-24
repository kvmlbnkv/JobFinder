package com.it.jobfinder.controllers;

import com.it.jobfinder.entities.User;
import com.it.jobfinder.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
