package com.it.jobfinder.exceptions;

public class SkillAlreadyAcquiredException extends RuntimeException{
    public SkillAlreadyAcquiredException(String message) {
        super(message);
    }
}
