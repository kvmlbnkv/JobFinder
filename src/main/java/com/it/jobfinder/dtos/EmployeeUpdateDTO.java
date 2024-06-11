package com.it.jobfinder.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class EmployeeUpdateDTO {

    private UUID id;

    private String username;

    private String password;

    private String email;

    private String name;

    private String surname;

    private String description;
}
