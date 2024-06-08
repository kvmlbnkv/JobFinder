package com.it.jobfinder.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.web.WebProperties;

import java.util.UUID;

@Entity
@Getter
@Setter
public class Details {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
}
