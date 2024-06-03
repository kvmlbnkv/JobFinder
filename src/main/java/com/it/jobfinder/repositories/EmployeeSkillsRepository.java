package com.it.jobfinder.repositories;

import com.it.jobfinder.entities.EmployeeSkills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmployeeSkillsRepository extends JpaRepository<EmployeeSkills, UUID> {
}
