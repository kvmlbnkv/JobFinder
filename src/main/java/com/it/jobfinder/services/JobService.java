package com.it.jobfinder.services;

import com.it.jobfinder.dtos.IdDTO;
import com.it.jobfinder.dtos.JobDTO;
import com.it.jobfinder.dtos.JobUpdateDTO;
import com.it.jobfinder.dtos.SkillDTO;
import com.it.jobfinder.entities.Job;
import com.it.jobfinder.entities.Skill;
import com.it.jobfinder.entities.User;
import com.it.jobfinder.exceptions.NoSuchJobException;
import com.it.jobfinder.repositories.JobRepository;
import com.it.jobfinder.repositories.SkillRepository;
import com.it.jobfinder.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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

    public List<Job> getAllJobs(){
        return this.jobRepository.findAll();
    }

    public Job addJob(JobDTO dto){
        User user = this.userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("There's no user with such name in the database"));

        List<Skill> requirements = new ArrayList<>();
        for (SkillDTO skillDTO : dto.getRequirements()){
            this.skillRepository.findByName(skillDTO.getSkill()).ifPresent(requirements::add);
        }

        return this.jobRepository.save(new Job(user, dto.getName(), dto.getDescription(), requirements, dto.getDueTo(), false));
    }

    public Job update(JobUpdateDTO dto){
        Job job = this.jobRepository.findById(dto.getId())
                .orElseThrow(() -> new NoSuchJobException("Job not found"));

        job.setName(dto.getName());
        job.setDescription(dto.getDescription());

        List<Skill> requirements = new ArrayList<>();
        for (SkillDTO skillDTO : dto.getRequirements()){
            this.skillRepository.findByName(skillDTO.getSkill()).ifPresent(requirements::add);
        }
        job.setRequirements(requirements);
        job.setDueTo(dto.getDueTo());

        return jobRepository.save(job);
    }

    public Job close(IdDTO id) {
        Job job = this.jobRepository.getReferenceById(id.getId());

        job.setClosed(true);

        return this.jobRepository.save(job);
    }
}
