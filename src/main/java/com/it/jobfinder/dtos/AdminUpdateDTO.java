package com.it.jobfinder.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AdminUpdateDTO {

    private UUID id;

    private String username;

    private String password;

    private String email;

    private String name;

    private String surname;
}
