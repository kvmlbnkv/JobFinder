package com.it.jobfinder.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "employeeSkills")
@NoArgsConstructor
public class EmployeeSkills {

    @Id
    private UUID userId;
    @OneToMany
    private List<Skill> skills;

    public EmployeeSkills(UUID userId, List<Skill> skills) {
        this.userId = userId;
        this.skills = skills;
    }
}
