package com.it.jobfinder.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    private User user;

    private String name;

    private String description;

    @ManyToMany
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

    public Job(UUID id, User user, String name, String description, List<Skill> requirements, LocalDate dueTo, boolean closed) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.description = description;
        this.requirements = requirements;
        this.dueTo = dueTo;
        this.closed = closed;
    }
}
