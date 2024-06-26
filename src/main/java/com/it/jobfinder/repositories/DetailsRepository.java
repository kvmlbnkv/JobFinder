package com.it.jobfinder.repositories;

import com.it.jobfinder.entities.Details;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DetailsRepository extends JpaRepository<Details, UUID> {
}
