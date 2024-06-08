package com.it.jobfinder.controllers;

import com.it.jobfinder.dtos.*;
import com.it.jobfinder.entities.Skill;
import com.it.jobfinder.entities.User;
import com.it.jobfinder.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("register")
    public ResponseEntity<User> registerEmployee(@RequestBody() EmployeeRegistrationDTO dto){
        return new ResponseEntity<>(this.employeeService.addEmployee(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("delete")
    public ResponseEntity<Void> deleteEmployee(@RequestBody LoginDTO dto){
        this.employeeService.deleteEmployee(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("addSkill")
    public ResponseEntity<List<Skill>> addSkillToEmployee(@RequestBody UserSkillDTO dto){
        return new ResponseEntity<>(this.employeeService.addSkillToUser(dto), HttpStatus.OK);
    }

    @DeleteMapping("removeSkill")
    public ResponseEntity<List<Skill>> removeSkillFromEmployee(@RequestBody UserSkillDTO dto){
        return new ResponseEntity<>(this.employeeService.removeSkillFromEmployee(dto), HttpStatus.OK);
    }
}
