package com.it.jobfinder.controllers;

import com.it.jobfinder.dtos.RegistrationDTO;
import com.it.jobfinder.entities.User;
import com.it.jobfinder.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("employee")
public class EmployeeController {

    private final EmployeeService employeeService;


    @PostMapping("register")
    public ResponseEntity<User> registerEmployee(@RequestBody() RegistrationDTO dto){
        return new ResponseEntity<>(this.employeeService.addEmployee(dto), HttpStatus.CREATED);
    }
}
