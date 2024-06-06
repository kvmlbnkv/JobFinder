package com.it.jobfinder.repositories;

import com.it.jobfinder.entities.EmployeeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmployeeUserRepository extends JpaRepository<EmployeeUser, UUID> {
}
