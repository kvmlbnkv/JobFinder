package com.it.jobfinder.dtos;

import com.it.jobfinder.entities.Skill;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class EmployeeDTO {
    
    private String name;

    private String surname;

    private String description;

    private List<SkillDTO> skills;
}
