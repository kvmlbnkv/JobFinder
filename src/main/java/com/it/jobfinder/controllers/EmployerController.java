package com.it.jobfinder.controllers;

import com.it.jobfinder.dtos.EmployerRegistrationDTO;
import com.it.jobfinder.dtos.LoginDTO;
import com.it.jobfinder.entities.User;
import com.it.jobfinder.services.EmployerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("employer")
public class EmployerController {

    private final EmployerService employerService;

    @PostMapping("register")
    public ResponseEntity<User> registerEmployer(@RequestBody EmployerRegistrationDTO dto){
        return new ResponseEntity<>(this.employerService.addEmployer(dto), HttpStatus.OK);
    }

    @DeleteMapping("delete")
    public ResponseEntity<Void> deleteEmployer(@RequestBody LoginDTO dto){
        this.employerService.deleteEmployer(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("getAll")
    public ResponseEntity<List<User>> getAllEmployers(){
        return new ResponseEntity<>(this.employerService.getAll(), HttpStatus.OK);
    }

    @GetMapping("get")
    public ResponseEntity<User> getEmployer(@RequestBody String username){
        return new ResponseEntity<>(this.employerService.get(username), HttpStatus.OK);
    }
}
