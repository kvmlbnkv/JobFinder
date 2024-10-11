package com.it.jobfinder.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EmployerUpdateDTO {

    private UUID id;

    private String username;

    private String password;

    private String email;

    private String name;

    private String description;
}
