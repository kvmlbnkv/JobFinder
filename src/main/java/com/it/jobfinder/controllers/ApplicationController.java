package com.it.jobfinder.controllers;

import com.it.jobfinder.dtos.ApplicationDTO;
import com.it.jobfinder.dtos.IdDTO;
import com.it.jobfinder.entities.Application;
import com.it.jobfinder.entities.User;
import com.it.jobfinder.services.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("application")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("make")
    public ResponseEntity<Application> makeApplication(@RequestBody ApplicationDTO dto, Principal principal){
        return new ResponseEntity<>(this.applicationService.makeApplication(dto, principal), HttpStatus.OK);
    }

    @GetMapping("getAll")
    public ResponseEntity<List<Application>> getAllApplications(){
        return new ResponseEntity<>(this.applicationService.getAll(), HttpStatus.OK);
    }

    @GetMapping("job")
    public ResponseEntity<List<Application>> getJobApplications(@RequestBody IdDTO dto, Principal principal){
        return new ResponseEntity<>(this.applicationService.getJobApplications(dto, principal), HttpStatus.OK);
    }

    @GetMapping("user")
    public ResponseEntity<List<Application>> getUserApplications(@RequestBody IdDTO dto, Principal principal){
        return new ResponseEntity<>(this.applicationService.getUserApplications(dto, principal), HttpStatus.OK);
    }
}
