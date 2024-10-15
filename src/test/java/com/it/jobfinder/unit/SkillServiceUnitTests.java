package com.it.jobfinder.unit;

import com.it.jobfinder.entities.Skill;
import com.it.jobfinder.exceptions.NoSuchSkillException;
import com.it.jobfinder.exceptions.SkillDuplicateException;
import com.it.jobfinder.repositories.SkillRepository;
import com.it.jobfinder.services.SkillService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SkillServiceUnitTests {

    @Mock
    SkillRepository skillRepository;

    @InjectMocks
    SkillService skillService;

    Skill skill = new Skill("skill");

    Skill otherSkill = new Skill("otherSkill");

    @BeforeEach
    void beforeEach(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllTest(){
        when(skillRepository.findAll()).thenReturn(List.of(skill, otherSkill));

        List<Skill> skills = skillService.getAllSkills();

        Assertions.assertNotNull(skills);
        Assertions.assertEquals(2, skills.size());
        Assertions.assertEquals(skill, skills.get(0));
        Assertions.assertEquals(otherSkill, skills.get(1));
    }

    @Test
    void getTest(){
        when(skillRepository.findByName("skill")).thenReturn(Optional.of(skill));

        Assertions.assertEquals(skill, skillService.getSkill("skill"));
    }

    @Test
    void getNoSuchSkillTest(){
        when(skillRepository.findByName("skill")).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchSkillException.class, () -> skillService.getSkill("skill"));
    }

    @Test
    void addTest(){
        when(skillRepository.findByName("skill")).thenReturn(Optional.empty());
        when(skillRepository.save(any(Skill.class))).thenAnswer(a -> a.getArgument(0));

        Assertions.assertEquals("skill", skillService.addSkill("skill").getName());
    }

    @Test
    void addSkillDuplicateTest(){
        when(skillRepository.findByName("skill")).thenReturn(Optional.of(skill));

        Assertions.assertThrows(SkillDuplicateException.class, () -> skillService.addSkill("skill"));
    }

    @Test
    void deleteTest(){
        when(skillRepository.findByName("skill")).thenReturn(Optional.of(skill));

        skillService.delete("skill");

        verify(skillRepository).delete(skill);
    }

    @Test
    void deleteNoSuchSkillTest(){
        when(skillRepository.findByName("skill")).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchSkillException.class, () -> skillService.delete("skill"));
    }
}
