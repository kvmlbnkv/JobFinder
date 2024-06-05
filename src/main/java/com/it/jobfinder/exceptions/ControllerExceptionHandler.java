package com.it.jobfinder.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> usernameNotFoundHandler(UsernameNotFoundException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserDuplicateException.class)
    public ResponseEntity<String> userDuplicateHandler(UserDuplicateException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NoSuchJobException.class)
    public ResponseEntity<String> noSuchJobHandler(NoSuchJobException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoSuchSkillException.class)
    public ResponseEntity<String> noSuchSkillHandler(NoSuchSkillException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IncorrectCredentialsException.class)
    public ResponseEntity<String> incorrectCredentialsHandler(IncorrectCredentialsException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(SkillAlreadyAcquiredException.class)
    public ResponseEntity<String> skillalreadyAcquiredHandler(SkillAlreadyAcquiredException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(SkillDuplicateException.class)
    public ResponseEntity<String> skillDuplicateHandler(SkillDuplicateException e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }
}
