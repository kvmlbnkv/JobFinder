package com.it.jobfinder.services;

import com.it.jobfinder.dtos.ApplicationDTO;
import com.it.jobfinder.dtos.IdDTO;
import com.it.jobfinder.entities.Application;
import com.it.jobfinder.entities.Job;
import com.it.jobfinder.entities.User;
import com.it.jobfinder.entities.UserRole;
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

    public Application makeApplication(ApplicationDTO dto, Principal principal) {
        User principalUser = (User) userDetailsService.loadUserByUsername(principal.getName());

        User user = userRepository.getReferenceById(dto.getUserId());
        Job job = jobRepository.getReferenceById(dto.getJobId());

        if (!principalUser.getRole().name().equals(UserRole.ADMIN.name()) && !principalUser.getId().equals(dto.getUserId()))
            throw new IncorrectCredentialsException("Can't make applications for that user");

        Optional<Application> previousApplication = this.applicationRepository.findByUserAndJob(user, job);
        if (previousApplication.isPresent()) throw new AlreadyAppliedException("Already applied for this job");

        Application application = new Application(user, job);
        this.applicationRepository.save(application);

        return application;
    }

    public List<Application> getAll() {
        return this.applicationRepository.findAll();
    }

    public List<Application> getJobApplications(IdDTO dto, Principal principal) {
        User principalUser = (User) userDetailsService.loadUserByUsername(principal.getName());

        Job job = jobRepository.getReferenceById(dto.getId());

        // If logged employer didn't post searched job (and if he's not an admin) he cannot get applications to it
        // Jeśli praca nie należy do aktualnie zalogowanego employera (lub nie jest on adminem) to nie może zobaczyć do niej aplikacji kandydatów.
        if (!principalUser.getRole().name().equals(UserRole.ADMIN.name()) && !principalUser.getId().equals(job.getUser().getId()))
            throw new IncorrectCredentialsException("Can't show applications for this job");

        return this.applicationRepository.findByJob(job);
    }

    public List<Application> getUserApplications(IdDTO dto, Principal principal) {
        User principalUser = (User) userDetailsService.loadUserByUsername(principal.getName());

        //Check if searched user's id equals logged one's
        //Sprawdzenie czy id usera wyszukującego równa się id usera wyszukiwanego. Jeśli nie to nie dopuszcza.
        if (!principalUser.getRole().name().equals(UserRole.ADMIN.name()) && !principalUser.getId().equals(dto.getId()))
            throw new IncorrectCredentialsException("Can't show this user's applications");

        User user = userRepository.getReferenceById(dto.getId());

        return this.applicationRepository.findByUser(user);
    }
}
