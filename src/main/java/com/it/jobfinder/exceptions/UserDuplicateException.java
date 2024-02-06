package com.it.jobfinder.exceptions;

public class UserDuplicateException extends RuntimeException{

    public UserDuplicateException(String message) {
        super(message);
    }
}
