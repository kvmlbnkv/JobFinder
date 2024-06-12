package com.it.jobfinder.dtos;

import com.it.jobfinder.entities.User;
import lombok.Data;

import java.util.UUID;

@Data
public class ApplicationDTO {

    private UUID userId;

    private UUID jobId;
}
