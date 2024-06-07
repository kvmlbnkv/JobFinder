package com.it.jobfinder.dtos;

import com.it.jobfinder.entities.UserRole;
import lombok.Data;

import java.util.List;

@Data
public class EmployeeRegistrationDTO {

    private String username;

    private String password;

    private String email;

    private String name;

    private String surname;
}
