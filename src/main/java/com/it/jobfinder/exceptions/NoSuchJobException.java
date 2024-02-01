package com.it.jobfinder.exceptions;

public class NoSuchJobException extends RuntimeException{

    public NoSuchJobException(String message) {
        super(message);
    }
}
