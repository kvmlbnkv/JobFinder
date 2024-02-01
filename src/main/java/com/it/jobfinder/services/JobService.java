package com.it.jobfinder.services;

import com.it.jobfinder.dtos.JobDTO;
import com.it.jobfinder.entities.Job;
import com.it.jobfinder.entities.User;
import com.it.jobfinder.exceptions.NoSuchJobException;
import com.it.jobfinder.repositories.JobRepository;
import com.it.jobfinder.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JobService {
    private JobRepository jobRepository;

    private UserRepository userRepository;

    @Autowired
    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public List<Job> getAllJobs(){
        return this.jobRepository.findAll();
    }

    public Job addJob(JobDTO dto){
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("There's no user with such name in the database"));

        return this.jobRepository.save(new Job(user, dto.getName(), dto.getDescription(), dto.getRequirements(), dto.getDueTo(), dto.isClosed()));
    }

    public Job updateJobName(UUID id, String name){
        Job job = jobRepository.findById(id).orElseThrow(() -> new NoSuchJobException("Job not found"));

        job.setName(name);
        return jobRepository.save(job);
    }
}
