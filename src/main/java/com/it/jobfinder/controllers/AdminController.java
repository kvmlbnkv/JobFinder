package com.it.jobfinder.controllers;

import com.it.jobfinder.dtos.AdminRegistrationDTO;
import com.it.jobfinder.entities.User;
import com.it.jobfinder.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin")
public class AdminController {

    private final AdminService adminService;

    public ResponseEntity<User> registerAdmin(@RequestBody AdminRegistrationDTO dto){
        return new ResponseEntity<>(this.adminService.addAdmin(dto), HttpStatus.OK);
    }

}
