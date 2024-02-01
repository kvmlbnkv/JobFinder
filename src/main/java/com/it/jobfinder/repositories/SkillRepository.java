package com.it.jobfinder.repositories;

import com.it.jobfinder.entities.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillRepository extends JpaRepository<Skill, UUID> {

    public Optional<Skill> findByName(String name);
}
