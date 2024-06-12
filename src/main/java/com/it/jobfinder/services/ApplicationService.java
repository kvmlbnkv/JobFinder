package com.it.jobfinder.services;

import com.it.jobfinder.dtos.ApplicationDTO;
import com.it.jobfinder.dtos.IdDTO;
import com.it.jobfinder.entities.Application;
import com.it.jobfinder.entities.Job;
import com.it.jobfinder.entities.User;
import com.it.jobfinder.exceptions.AlreadyAppliedException;
import com.it.jobfinder.repositories.ApplicationRepository;
import com.it.jobfinder.repositories.JobRepository;
import com.it.jobfinder.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public Application makeApplication(ApplicationDTO dto) {
        User user = userRepository.getReferenceById(dto.getUserId());
        Job job = jobRepository.getReferenceById(dto.getJobId());

        Optional<Application> previousApplication = this.applicationRepository.findByUserAndJob(user, job);
        if (previousApplication.isPresent()) throw new AlreadyAppliedException("Already applied to this job");

        return this.applicationRepository.save(new Application(user, job));
    }

    public List<Application> getAll() {
        return this.applicationRepository.findAll();
    }

    public List<Application> getJobApplications(IdDTO dto) {
        Job job = jobRepository.getReferenceById(dto.getId());

        return this.applicationRepository.findByJob(job);
    }

    public List<Application> getUserApplications(IdDTO dto) {
        User user = userRepository.getReferenceById(dto.getId());

        return this.applicationRepository.findByUser(user);
    }
}
