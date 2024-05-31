package com.it.jobfinder.controllers;

import com.it.jobfinder.dtos.SkillDTO;
import com.it.jobfinder.entities.Skill;
import com.it.jobfinder.services.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("skill")
public class SkillController {

    private final SkillService skillService;

    @GetMapping("getAll")
    public ResponseEntity<List<Skill>> getAllSkills(){
        return new ResponseEntity<>(this.skillService.getAllSkills(), HttpStatus.OK);
    }

    @GetMapping("get")
    public ResponseEntity<Skill> getSkill(@RequestBody()String name){
        return new ResponseEntity<>(this.skillService.getSkill(name), HttpStatus.OK);
    }

    @PostMapping("add")
    public ResponseEntity<Skill> addSkill(@RequestBody()SkillDTO dto){
        return new ResponseEntity<>(this.skillService.addSkill(dto), HttpStatus.OK);
    }
}
