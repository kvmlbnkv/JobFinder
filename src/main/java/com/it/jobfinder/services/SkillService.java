package com.it.jobfinder.services;

import com.it.jobfinder.dtos.SkillDTO;
import com.it.jobfinder.entities.Skill;
import com.it.jobfinder.exceptions.NoSuchSkillException;
import com.it.jobfinder.exceptions.SkillDuplicateException;
import com.it.jobfinder.repositories.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SkillService {

    SkillRepository skillRepository;

    @Autowired
    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public List<Skill> getAllSkills() {
        return this.skillRepository.findAll();
    }

    public Skill getSkill(String name) {
        return this.skillRepository.findByName(name).orElseThrow(() -> new NoSuchSkillException("There's no such skill in db"));
    }

    public Skill addSkill(SkillDTO dto) {
        String name = dto.getName();

        Optional<Skill> available = this.skillRepository.findByName(name);

        if (available.isPresent()) throw new SkillDuplicateException("Skill already in db");

        return this.skillRepository.save(new Skill(dto.getName()));
    }
}
