package com.it.jobfinder.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class JobUpdateDTO {

    private UUID id;

    private String name;

    private String description;

    private List<SkillDTO> requirements;

    private LocalDate dueTo;
}
