package com.it.jobfinder.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "employees")
public class EmployeeUser{
    @Id
    private UUID userId;

    private String name;

    private String surname;

    private String description;

    @OneToMany
    private List<Skill> skills;
}
