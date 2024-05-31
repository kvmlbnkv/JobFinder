package com.it.jobfinder.controllers;

import com.it.jobfinder.dtos.LoginDTO;
import com.it.jobfinder.dtos.RegistrationDTO;
import com.it.jobfinder.entities.User;
import com.it.jobfinder.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    @GetMapping("getAll")
    public List<User> getAllUsers(){
        return this.userService.getAllUsers();
    }

    @GetMapping("get")
    public ResponseEntity<User> getUser(@RequestBody()String username){
        return new ResponseEntity<>(this.userService.getUser(username), HttpStatus.OK);
    }

    @PostMapping("register")
    public ResponseEntity<User> registerUser(@RequestBody()RegistrationDTO dto){
        return new ResponseEntity<>(this.userService.addUser(dto), HttpStatus.CREATED);
    }
    /*
    @PostMapping("login")
    public ResponseEntity<String> loginUser(@RequestBody() LoginDTO dto){
        return new ResponseEntity<>(this.userService.loginUser(dto), HttpStatus.OK);
    }*/
}
