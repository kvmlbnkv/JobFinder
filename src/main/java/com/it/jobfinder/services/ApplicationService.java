package com.it.jobfinder.services;

import com.it.jobfinder.dtos.ApplicationDTO;
import com.it.jobfinder.dtos.IdDTO;
import com.it.jobfinder.entities.Application;
import com.it.jobfinder.entities.Job;
import com.it.jobfinder.entities.User;
import com.it.jobfinder.exceptions.AlreadyAppliedException;
import com.it.jobfinder.exceptions.IncorrectCredentialsException;
import com.it.jobfinder.repositories.ApplicationRepository;
import com.it.jobfinder.repositories.JobRepository;
import com.it.jobfinder.repositories.UserRepository;
import com.it.jobfinder.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final CustomUserDetailsService userDetailsService;

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

    public List<Application> getJobApplications(IdDTO dto, Principal principal) {
        User principalUser = (User) userDetailsService.loadUserByUsername(principal.getName());

        Job job = jobRepository.getReferenceById(dto.getId());

        //Jeśli praca nie należy do aktualnie zalogowanego employera to nie może zobaczyć do niej aplikacji kandydatów.
        if (!principalUser.getId().equals(job.getUser().getId())) throw new IncorrectCredentialsException("Can't show that user's applications");

        return this.applicationRepository.findByJob(job);
    }

    public List<Application> getUserApplications(IdDTO dto, Principal principal) {
        User principalUser = (User) userDetailsService.loadUserByUsername(principal.getName());

        //Sprawdzenie czy id usera wyszukującego równa się id usera wyszukiwanego. Jeśli nie to nie dopuszcza.
        if (!principalUser.getId().equals(dto.getId())) throw new IncorrectCredentialsException("Can't show that user's applications");

        User user = userRepository.getReferenceById(dto.getId());

        return this.applicationRepository.findByUser(user);
    }
}
