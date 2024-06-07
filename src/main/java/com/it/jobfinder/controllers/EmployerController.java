package com.it.jobfinder.controllers;

import com.it.jobfinder.dtos.EmployerRegistrationDTO;
import com.it.jobfinder.entities.User;
import com.it.jobfinder.services.EmployerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("employer")
public class EmployerController {

    private final EmployerService employerService;

    @PostMapping("register")
    public ResponseEntity<User> registerEmployer(@RequestBody EmployerRegistrationDTO dto){
        return new ResponseEntity<>(this.employerService.addEmployer(dto), HttpStatus.OK);
    }
}
