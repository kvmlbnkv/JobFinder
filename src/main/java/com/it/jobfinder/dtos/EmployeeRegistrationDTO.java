package com.it.jobfinder.dtos;

import com.it.jobfinder.entities.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EmployeeRegistrationDTO {

    private String username;

    private String password;

    private String email;

    private String name;

    private String surname;

    private String description;
}
