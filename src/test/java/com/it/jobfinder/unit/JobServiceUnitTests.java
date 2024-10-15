package com.it.jobfinder.unit;

import com.it.jobfinder.dtos.JobDTO;
import com.it.jobfinder.dtos.JobUpdateDTO;
import com.it.jobfinder.dtos.SkillDTO;
import com.it.jobfinder.entities.*;
import com.it.jobfinder.exceptions.IncorrectCredentialsException;
import com.it.jobfinder.exceptions.NoSuchJobException;
import com.it.jobfinder.repositories.JobRepository;
import com.it.jobfinder.repositories.SkillRepository;
import com.it.jobfinder.repositories.UserRepository;
import com.it.jobfinder.security.CustomUserDetailsService;
import com.it.jobfinder.services.JobService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class JobServiceUnitTests {

    @Mock
    JobRepository jobRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    SkillRepository skillRepository;

    @Mock
    CustomUserDetailsService userDetailsService;

    @Mock
    Principal principal;

    @InjectMocks
    JobService jobService;

    Skill skill = new Skill("skill");

    UUID employerId = UUID.randomUUID();
    EmployerDetails employerDetails = new EmployerDetails("Employer", "");
    User employerUser = new User(employerId, "employer", "password", "employer@mail.com", UserRole.EMPLOYER, employerDetails);

    UUID otherEmployerId = UUID.randomUUID();
    EmployerDetails otherEmployerDetails = new EmployerDetails("oEmployer", "");
    User otherEmployerUser = new User(otherEmployerId, "oemployer", "password", "oemployer@mail.com", UserRole.EMPLOYER, otherEmployerDetails);

    UUID adminId = UUID.randomUUID();
    AdminDetails adminDetails = new AdminDetails("Ad", "Min");
    User adminUser = new User(adminId, "admin", "admin", "admin@admin.com", UserRole.ADMIN, adminDetails);

    UUID jobId = UUID.randomUUID();
    Job job = new Job(jobId, employerUser, "job", "", List.of(skill), LocalDate.of(2025, 12, 31), false);

    UUID otherJobId = UUID.randomUUID();
    Job otherJob = new Job(otherJobId, employerUser, "otherJob", "", List.of(), LocalDate.of(2025, 12, 31), false);



    @BeforeEach
    void beforeEach(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllTest(){
        when(jobRepository.findAll()).thenReturn(List.of(job, otherJob));

        List<Job> jobs = jobService.getAllJobs();

        Assertions.assertNotNull(jobs);
        Assertions.assertEquals(2, jobs.size());
        Assertions.assertEquals(job, jobs.get(0));
        Assertions.assertEquals(otherJob, jobs.get(1));
    }

    @Test
    void addJobTest(){
        when(userRepository.findByUsername("employer")).thenReturn(Optional.of(employerUser));
        when(principal.getName()).thenReturn("employer");
        when(userDetailsService.loadUserByUsername("employer")).thenReturn(employerUser);
        when(skillRepository.findByName("skill")).thenReturn(Optional.of(skill));
        when(jobRepository.save(any(Job.class))).thenAnswer(a -> a.getArgument(0));

        JobDTO dto = JobDTO.builder()
                .username("employer").name("job").description("").requirements(List.of(new SkillDTO("skill"))).dueTo(LocalDate.of(2025, 12, 31))
                .build();

        Job createdJob = jobService.addJob(dto, principal);

        Assertions.assertNotNull(createdJob);
        Assertions.assertEquals(employerUser, createdJob.getUser());
        Assertions.assertEquals(dto.getName(), createdJob.getName());
        Assertions.assertEquals(dto.getDescription(), createdJob.getDescription());
        Assertions.assertIterableEquals(List.of(skill), createdJob.getRequirements());
        Assertions.assertEquals(LocalDate.of(2025, 12, 31), createdJob.getDueTo());
    }

    @Test
    void addJobByAdminTest(){
        when(userRepository.findByUsername("employer")).thenReturn(Optional.of(employerUser));
        when(principal.getName()).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(adminUser);
        when(skillRepository.findByName("skill")).thenReturn(Optional.of(skill));
        when(jobRepository.save(any(Job.class))).thenAnswer(a -> a.getArgument(0));

        JobDTO dto = JobDTO.builder()
                .username("employer").name("job").description("").requirements(List.of(new SkillDTO("skill"))).dueTo(LocalDate.of(2025, 12, 31))
                .build();

        Job createdJob = jobService.addJob(dto, principal);

        Assertions.assertNotNull(createdJob);
        Assertions.assertEquals(employerUser, createdJob.getUser());
        Assertions.assertEquals(dto.getName(), createdJob.getName());
        Assertions.assertEquals(dto.getDescription(), createdJob.getDescription());
        Assertions.assertIterableEquals(List.of(skill), createdJob.getRequirements());
        Assertions.assertEquals(LocalDate.of(2025, 12, 31), createdJob.getDueTo());
    }

    @Test
    void addJobUsernameNotFoundTest(){
        when(userRepository.findByUsername("employer")).thenReturn(Optional.empty());

        JobDTO dto = JobDTO.builder()
                .username("employer").name("job").description("").requirements(List.of(new SkillDTO("skill"))).dueTo(LocalDate.of(2025, 12, 31))
                .build();

        Assertions.assertThrows(UsernameNotFoundException.class, () -> jobService.addJob(dto, principal));
    }

    @Test
    void addJobIncorrectCredentialsTest(){
        when(userRepository.findByUsername("employer")).thenReturn(Optional.of(employerUser));
        when(principal.getName()).thenReturn("oemployer");
        when(userDetailsService.loadUserByUsername("oemployer")).thenReturn(otherEmployerUser);

        JobDTO dto = JobDTO.builder()
                .username("employer").name("job").description("").requirements(List.of(new SkillDTO("skill"))).dueTo(LocalDate.of(2025, 12, 31))
                .build();

        Assertions.assertThrows(IncorrectCredentialsException.class, () -> jobService.addJob(dto, principal));
    }

    @Test
    void updateTest(){
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(principal.getName()).thenReturn("employer");
        when(userDetailsService.loadUserByUsername("employer")).thenReturn(employerUser);
        when(jobRepository.save(job)).thenReturn(job);

        JobUpdateDTO dto = JobUpdateDTO.builder()
                .id(jobId).name("changedJob").description("hello").dueTo(LocalDate.of(2036, 12, 31)).requirements(List.of())
                .build();

        Job updatedJob = jobService.update(dto, principal);

        Assertions.assertNotNull(updatedJob);
        Assertions.assertEquals(employerUser, updatedJob.getUser());
        Assertions.assertEquals(dto.getName(), updatedJob.getName());
        Assertions.assertEquals(dto.getDescription(), updatedJob.getDescription());
        Assertions.assertIterableEquals(List.of(), updatedJob.getRequirements());
        Assertions.assertEquals(LocalDate.of(2036, 12, 31), updatedJob.getDueTo());
    }

    @Test
    void updateByAdminTest(){
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(principal.getName()).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(adminUser);
        when(jobRepository.save(job)).thenReturn(job);

        JobUpdateDTO dto = JobUpdateDTO.builder()
                .id(jobId).name("changedJob").description("hello").dueTo(LocalDate.of(2036, 12, 31)).requirements(List.of())
                .build();

        Job updatedJob = jobService.update(dto, principal);

        Assertions.assertNotNull(updatedJob);
        Assertions.assertEquals(employerUser, updatedJob.getUser());
        Assertions.assertEquals(dto.getName(), updatedJob.getName());
        Assertions.assertEquals(dto.getDescription(), updatedJob.getDescription());
        Assertions.assertIterableEquals(List.of(), updatedJob.getRequirements());
        Assertions.assertEquals(LocalDate.of(2036, 12, 31), updatedJob.getDueTo());
    }

    @Test
    void updateNotAllTest(){
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(principal.getName()).thenReturn("employer");
        when(userDetailsService.loadUserByUsername("employer")).thenReturn(employerUser);
        when(jobRepository.save(job)).thenReturn(job);

        JobUpdateDTO dto = JobUpdateDTO.builder()
                .id(jobId).description("hello").dueTo(LocalDate.of(2036, 12, 31))
                .build();

        Job updatedJob = jobService.update(dto, principal);

        Assertions.assertNotNull(updatedJob);
        Assertions.assertEquals(employerUser, updatedJob.getUser());
        Assertions.assertEquals(job.getName(), updatedJob.getName());
        Assertions.assertEquals(dto.getDescription(), updatedJob.getDescription());
        Assertions.assertIterableEquals(List.of(skill), updatedJob.getRequirements());
        Assertions.assertEquals(LocalDate.of(2036, 12, 31), updatedJob.getDueTo());
    }

    @Test
    void updateNoSuchJobTest(){
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        JobUpdateDTO dto = JobUpdateDTO.builder()
                .id(jobId).name("changedJob").description("hello").dueTo(LocalDate.of(2036, 12, 31)).requirements(List.of())
                .build();

        Assertions.assertThrows(NoSuchJobException.class, () -> jobService.update(dto, principal));
    }

    @Test
    void updateIncorrectCredentialsTest(){
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(principal.getName()).thenReturn("oemployer");
        when(userDetailsService.loadUserByUsername("oemployer")).thenReturn(otherEmployerUser);
        when(jobRepository.save(job)).thenReturn(job);

        JobUpdateDTO dto = JobUpdateDTO.builder()
                .id(jobId).name("changedJob").description("hello").dueTo(LocalDate.of(2036, 12, 31)).requirements(List.of())
                .build();

        Assertions.assertThrows(IncorrectCredentialsException.class, () -> jobService.update(dto, principal));
    }

    @Test
    void closeTest(){
        when(jobRepository.getReferenceById(jobId)).thenReturn(job);
        when(principal.getName()).thenReturn("employer");
        when(userDetailsService.loadUserByUsername("employer")).thenReturn(employerUser);
        when(jobRepository.save(job)).thenReturn(job);

        Job closedJob = jobService.close(jobId.toString(), principal);

        Assertions.assertNotNull(closedJob);
        Assertions.assertTrue(closedJob.isClosed());
    }

    @Test
    void closeByAdminTest(){
        when(jobRepository.getReferenceById(jobId)).thenReturn(job);
        when(principal.getName()).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(adminUser);
        when(jobRepository.save(job)).thenReturn(job);

        Job closedJob = jobService.close(jobId.toString(), principal);

        Assertions.assertNotNull(closedJob);
        Assertions.assertTrue(closedJob.isClosed());
    }

    @Test
    void closeIncorrectCredentialsTest(){
        when(jobRepository.getReferenceById(jobId)).thenReturn(job);
        when(principal.getName()).thenReturn("oemployer");
        when(userDetailsService.loadUserByUsername("oemployer")).thenReturn(otherEmployerUser);
        when(jobRepository.save(job)).thenReturn(job);

        Assertions.assertThrows(IncorrectCredentialsException.class, () -> jobService.close(jobId.toString(), principal));
    }

    @Test
    void getTest(){
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));

        Assertions.assertEquals(job, jobService.getJob(jobId.toString()));
    }

    @Test
    void getNoSuchJobTest(){
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NoSuchJobException.class,() -> jobService.getJob(jobId.toString()));
    }
}
