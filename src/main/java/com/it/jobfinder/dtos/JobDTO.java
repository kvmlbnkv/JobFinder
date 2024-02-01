package com.it.jobfinder.dtos;

import com.it.jobfinder.entities.Skill;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class JobDTO {
    private String username;

    private String name;

    private String description;

    private List<Skill> requirements;

    private LocalDate dueTo;

    private boolean closed;
}
