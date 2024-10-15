package com.it.jobfinder.services;

import com.it.jobfinder.dtos.SkillDTO;
import com.it.jobfinder.entities.Skill;
import com.it.jobfinder.exceptions.NoSuchSkillException;
import com.it.jobfinder.exceptions.SkillDuplicateException;
import com.it.jobfinder.repositories.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public List<Skill> getAllSkills() {
        return this.skillRepository.findAll();
    }

    public Skill getSkill(String name) {
        return this.skillRepository.findByName(name).orElseThrow(() -> new NoSuchSkillException("There's no such skill in db"));
    }

    public Skill addSkill(String name) {
        if (skillRepository.findByName(name).isPresent()) throw new SkillDuplicateException("Skill already in db");

        return skillRepository.save(new Skill(name));
    }

    public void delete(String name) {
        Skill skill = this.skillRepository.findByName(name)
                .orElseThrow(() -> new NoSuchSkillException("No such skill"));

        this.skillRepository.delete(skill);
    }
}
