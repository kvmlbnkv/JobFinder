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

    public Skill addSkill(SkillDTO dto) {
        String name = dto.getSkill();

        Optional<Skill> skill = skillRepository.findByName(name);
        if (skill.isPresent()) throw new SkillDuplicateException("Skill already in db");

        return skillRepository.save(new Skill(dto.getSkill()));
    }

    public void delete(SkillDTO dto) {
        Skill skill = this.skillRepository.findByName(dto.getSkill())
                .orElseThrow(() -> new NoSuchSkillException("No such skill"));

        this.skillRepository.delete(skill);
    }
}
