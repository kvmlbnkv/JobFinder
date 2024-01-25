package com.it.jobfinder.exceptions;

public class NoSuchUserException extends RuntimeException{

    public NoSuchUserException(){
        super("There's no user with such name in the database");
    }
}
