package com.it.jobfinder.controllers;

import com.it.jobfinder.dtos.AdminRegistrationDTO;
import com.it.jobfinder.dtos.LoginDTO;
import com.it.jobfinder.entities.User;
import com.it.jobfinder.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("register")
    public ResponseEntity<User> registerAdmin(@RequestBody AdminRegistrationDTO dto){
        return new ResponseEntity<>(this.adminService.addAdmin(dto), HttpStatus.OK);
    }

    @DeleteMapping("delete")
    public ResponseEntity<Void> deleteAdmin(@RequestBody LoginDTO dto){
        this.adminService.deleteAdmin(dto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
