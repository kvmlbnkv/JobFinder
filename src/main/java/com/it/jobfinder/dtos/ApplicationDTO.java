package com.it.jobfinder.dtos;

import com.it.jobfinder.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ApplicationDTO {

    private UUID userId;

    private UUID jobId;
}
