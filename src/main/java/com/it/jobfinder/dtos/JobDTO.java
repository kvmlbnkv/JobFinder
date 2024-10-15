package com.it.jobfinder.dtos;

import com.it.jobfinder.entities.Skill;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class JobDTO {

    private String username;

    private String name;

    private String description;

    private List<SkillDTO> requirements;

    private LocalDate dueTo;
}
