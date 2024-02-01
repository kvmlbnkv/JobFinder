package com.it.jobfinder.entities;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "jobs")
@NoArgsConstructor
@Setter
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    private User user;

    private String name;

    private String description;

    @OneToMany
    private List<Skill> requirements;

    private LocalDate dueTo;

    private boolean closed;

    public Job(User user, String name, String description, List<Skill> requirements, LocalDate dueTo, boolean closed) {
        this.user = user;
        this.name = name;
        this.description = description;
        this.requirements = requirements;
        this.dueTo = dueTo;
        this.closed = closed;
    }
}
