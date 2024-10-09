package com.it.jobfinder.controllers;

import com.it.jobfinder.dtos.JobDTO;
import com.it.jobfinder.dtos.JobUpdateDTO;
import com.it.jobfinder.entities.Job;
import com.it.jobfinder.services.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("job")
public class JobController {

    private final JobService jobService;

    @GetMapping("getAll")
    public ResponseEntity<List<Job>> getAllJobs(){
        return new ResponseEntity<>(this.jobService.getAllJobs(), HttpStatus.OK);
    }

    @GetMapping("get")
    public ResponseEntity<Job> getJob(@RequestBody String id){
        return new ResponseEntity<>(this.jobService.getJob(id), HttpStatus.OK);
    }

    @PostMapping("add")
    public ResponseEntity<Job> addJob(@RequestBody JobDTO dto, Principal principal){
        return new ResponseEntity<>(this.jobService.addJob(dto, principal), HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<Job> updateJob(@RequestBody JobUpdateDTO dto, Principal principal){
        return new ResponseEntity<>(this.jobService.update(dto, principal), HttpStatus.OK);
    }

    @PutMapping("close")
    public ResponseEntity<Job> closeJob(@RequestBody String id, Principal principal){
        return new ResponseEntity<>(this.jobService.close(id, principal), HttpStatus.OK);
    }
}
