package com.it.jobfinder.services;

import com.it.jobfinder.dtos.JobDTO;
import com.it.jobfinder.dtos.JobUpdateDTO;
import com.it.jobfinder.dtos.SkillDTO;
import com.it.jobfinder.entities.Job;
import com.it.jobfinder.entities.Skill;
import com.it.jobfinder.entities.User;
import com.it.jobfinder.entities.UserRole;
import com.it.jobfinder.exceptions.IncorrectCredentialsException;
import com.it.jobfinder.exceptions.NoSuchJobException;
import com.it.jobfinder.repositories.JobRepository;
import com.it.jobfinder.repositories.SkillRepository;
import com.it.jobfinder.repositories.UserRepository;
import com.it.jobfinder.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class JobService {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final CustomUserDetailsService userDetailsService;

    public List<Job> getAllJobs(){
        return this.jobRepository.findAll();
    }

    public Job addJob(JobDTO dto, Principal principal){
        User user = this.userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("There's no user with such name in the database"));

        User principalUser = (User) userDetailsService.loadUserByUsername(principal.getName());

        if (!principalUser.getRole().name().equals(UserRole.ADMIN.name()) && !principalUser.getId().equals(user.getId()))
            throw new IncorrectCredentialsException("Can't add job");

        List<Skill> requirements = new ArrayList<>();
        for (SkillDTO skillDTO : dto.getRequirements()){
            this.skillRepository.findByName(skillDTO.getSkill()).ifPresent(requirements::add);
        }

        return this.jobRepository.save(new Job(user, dto.getName(), dto.getDescription(), requirements, dto.getDueTo(), false));
    }

    public Job update(JobUpdateDTO dto, Principal principal){
        Job job = this.jobRepository.findById(dto.getId())
                .orElseThrow(() -> new NoSuchJobException("Job not found"));

        User principalUser = (User) userDetailsService.loadUserByUsername(principal.getName());

        if (!principalUser.getRole().name().equals(UserRole.ADMIN.name()) && !principalUser.getId().equals(job.getUser().getId()))
            throw new IncorrectCredentialsException("Can't update this job");

        if(dto.getName() != null) job.setName(dto.getName());
        if(dto.getDescription() != null) job.setDescription(dto.getDescription());

        if(dto.getRequirements() != null){
            List<Skill> requirements = new ArrayList<>();
            for (SkillDTO skillDTO : dto.getRequirements()){
                this.skillRepository.findByName(skillDTO.getSkill()).ifPresent(requirements::add);
            }
            job.setRequirements(requirements);
        }

        if(dto.getDueTo() != null) job.setDueTo(dto.getDueTo());

        return jobRepository.save(job);
    }

    public Job close(String id, Principal principal) {
        Job job = this.jobRepository.getReferenceById(UUID.fromString(id));

        User principalUser = (User) userDetailsService.loadUserByUsername(principal.getName());

        if (!principalUser.getRole().name().equals(UserRole.ADMIN.name()) && !principalUser.getId().equals(job.getUser().getId()))
            throw new IncorrectCredentialsException("Can't close this job");

        job.setClosed(true);

        return this.jobRepository.save(job);
    }

    public Job getJob(String id) {
        return this.jobRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NoSuchJobException("Job not found"));
    }
}
