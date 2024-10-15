package com.it.jobfinder.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class JobUpdateDTO {

    private UUID id;

    private String name;

    private String description;

    private List<SkillDTO> requirements;

    private LocalDate dueTo;
}
