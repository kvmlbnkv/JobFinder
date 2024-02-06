package com.it.jobfinder.controllers;

import com.it.jobfinder.exceptions.UserDuplicateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> usernameNotFoundHandler(UsernameNotFoundException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
    }

    @ExceptionHandler(UserDuplicateException.class)
    public ResponseEntity<String> userDuplicateHandler(UserDuplicateException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
    }
}
