package com.it.jobfinder.unit;

import com.it.jobfinder.dtos.ApplicationDTO;
import com.it.jobfinder.entities.*;
import com.it.jobfinder.exceptions.AlreadyAppliedException;
import com.it.jobfinder.exceptions.IncorrectCredentialsException;
import com.it.jobfinder.repositories.ApplicationRepository;
import com.it.jobfinder.repositories.JobRepository;
import com.it.jobfinder.repositories.UserRepository;
import com.it.jobfinder.security.CustomUserDetailsService;
import com.it.jobfinder.services.ApplicationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import javax.management.remote.JMXPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ApplicationServiceUnitTests {

    @Mock
    Principal principal;

    @Mock
    CustomUserDetailsService userDetailsService;

    @Mock
    UserRepository userRepository;

    @Mock
    JobRepository jobRepository;

    @Mock
    ApplicationRepository applicationRepository;

    @InjectMocks
    ApplicationService applicationService;


    UUID jobId = UUID.randomUUID();
    UUID job1Id = UUID.randomUUID();
    UUID job2Id = UUID.randomUUID();
    UUID employeeId = UUID.randomUUID();
    UUID otherEmployeeId = UUID.randomUUID();
    UUID employee1Id = UUID.randomUUID();
    UUID employee2Id = UUID.randomUUID();
    UUID employerId = UUID.randomUUID();
    UUID otherEmployerId = UUID.randomUUID();
    UUID adminId = UUID.randomUUID();
    UUID application1Id = UUID.randomUUID();
    UUID application2Id = UUID.randomUUID();

    EmployeeDetails employeeDetails = new EmployeeDetails("Em", "Ployee", "", List.of());
    User employeeUser = new User(employeeId, "employee", "password", "employee@test.com", UserRole.EMPLOYEE, employeeDetails);

    EmployeeDetails otherEmployeeDetails = new EmployeeDetails("Emm", "Ployee", "", List.of());
    User otherEmployeeUser = new User(otherEmployeeId, "otherEmployee", "password", "oemployee@test.com", UserRole.EMPLOYEE, otherEmployeeDetails);

    EmployeeDetails employee1Details = new EmployeeDetails("Em", "Ployee", "", List.of());
    User employee1User = new User(employee1Id, "employee1", "password", "employee1@test.com", UserRole.EMPLOYEE, employee1Details);

    EmployeeDetails employee2Details = new EmployeeDetails("Emm", "Ployee", "", List.of());
    User employee2User = new User(employee2Id, "employee2", "password", "employee2@test.com", UserRole.EMPLOYEE, employee2Details);

    EmployerDetails employerDetails = new EmployerDetails("Employer", "");
    User employerUser = new User(employerId, "employer", "password", "employer@test.com", UserRole.EMPLOYER, employerDetails);

    EmployerDetails otherEmployerDetails = new EmployerDetails("oEmployer", "");
    User otherEmployerUser = new User(otherEmployerId, "oemployer", "password", "oemployer@test.com", UserRole.EMPLOYER, otherEmployerDetails);

    AdminDetails adminDetails = new AdminDetails("Ad", "Min");
    User adminUser = new User(adminId, "admin", "admin", "admin@admin.com", UserRole.ADMIN, adminDetails);

    Skill skill = new Skill("Skill");

    Job job = new Job(jobId, employerUser, "job", "", List.of(skill), LocalDate.of(2050, 12, 31), false);

    Job job1 = new Job(job1Id, employerUser, "job1", "", List.of(skill), LocalDate.of(2050, 12, 31), false);

    Job job2 = new Job(job2Id, employerUser, "job2", "", List.of(skill), LocalDate.of(2050, 12, 31), false);

    Application application = new Application(employeeUser, job);

    Application application1 = new Application(application1Id, employeeUser, job1);
    Application application2 = new Application(application2Id, employeeUser, job2);


    @BeforeEach
    void beforeEach(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void makeApplicationTest(){
        when(principal.getName()).thenReturn("employee");
        when(userDetailsService.loadUserByUsername("employee")).thenReturn(employeeUser);
        when(userRepository.getReferenceById(employeeId)).thenReturn(employeeUser);
        when(jobRepository.getReferenceById(jobId)).thenReturn(job);
        when(applicationRepository.findByUserAndJob(employeeUser, job)).thenReturn(Optional.empty());

        ApplicationDTO dto = ApplicationDTO.builder()
                .jobId(jobId).userId(employeeId)
                .build();

        Application createdApplication = applicationService.makeApplication(dto, principal);

        verify(applicationRepository).save(any(Application.class));

        Assertions.assertNotNull(createdApplication);

        assertThat(createdApplication.getUser()).usingRecursiveComparison().isEqualTo(employeeUser);
        assertThat(createdApplication.getJob()).usingRecursiveComparison().isEqualTo(job);

        Assertions.assertEquals(employeeUser.getId(), createdApplication.getUser().getId());
        Assertions.assertEquals(employeeUser.getUsername(), createdApplication.getUser().getUsername());
        Assertions.assertEquals(employeeUser.getPassword(), createdApplication.getUser().getPassword());
        Assertions.assertEquals(employeeUser.getEmail(), createdApplication.getUser().getEmail());
        Assertions.assertEquals(employeeUser.getRole().name(), createdApplication.getUser().getRole().name());
        Assertions.assertEquals(((EmployeeDetails) employeeUser.getDetails()).getName(), ((EmployeeDetails) createdApplication.getUser().getDetails()).getName());
        Assertions.assertEquals(((EmployeeDetails) employeeUser.getDetails()).getSurname(), ((EmployeeDetails) createdApplication.getUser().getDetails()).getSurname());
        Assertions.assertEquals(((EmployeeDetails) employeeUser.getDetails()).getDescription(), ((EmployeeDetails) createdApplication.getUser().getDetails()).getDescription());
        Assertions.assertIterableEquals(((EmployeeDetails) employeeUser.getDetails()).getSkills(), ((EmployeeDetails) createdApplication.getUser().getDetails()).getSkills());

        Assertions.assertEquals(job.getId(), createdApplication.getJob().getId());
        Assertions.assertEquals(job.getUser().getId(), createdApplication.getJob().getUser().getId());
        Assertions.assertEquals(job.getName(), createdApplication.getJob().getName());
        Assertions.assertEquals(job.getDescription(), createdApplication.getJob().getDescription());
        Assertions.assertEquals(job.getDueTo(), createdApplication.getJob().getDueTo());
        Assertions.assertIterableEquals(job.getRequirements(), createdApplication.getJob().getRequirements());
    }

    @Test
    void makeApplicationByAdminTest(){
        when(principal.getName()).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(adminUser);
        when(userRepository.getReferenceById(employeeId)).thenReturn(employeeUser);
        when(jobRepository.getReferenceById(jobId)).thenReturn(job);
        when(applicationRepository.findByUserAndJob(employeeUser, job)).thenReturn(Optional.empty());

        ApplicationDTO dto = ApplicationDTO.builder()
                .jobId(jobId).userId(employeeId)
                .build();

        Application createdApplication = applicationService.makeApplication(dto, principal);

        verify(applicationRepository).save(any(Application.class));

        Assertions.assertNotNull(createdApplication);

        assertThat(createdApplication.getUser()).usingRecursiveComparison().isEqualTo(employeeUser);
        assertThat(createdApplication.getJob()).usingRecursiveComparison().isEqualTo(job);

        Assertions.assertEquals(employeeUser.getId(), createdApplication.getUser().getId());
        Assertions.assertEquals(employeeUser.getUsername(), createdApplication.getUser().getUsername());
        Assertions.assertEquals(employeeUser.getPassword(), createdApplication.getUser().getPassword());
        Assertions.assertEquals(employeeUser.getEmail(), createdApplication.getUser().getEmail());
        Assertions.assertEquals(employeeUser.getRole().name(), createdApplication.getUser().getRole().name());
        Assertions.assertEquals(((EmployeeDetails) employeeUser.getDetails()).getName(), ((EmployeeDetails) createdApplication.getUser().getDetails()).getName());
        Assertions.assertEquals(((EmployeeDetails) employeeUser.getDetails()).getSurname(), ((EmployeeDetails) createdApplication.getUser().getDetails()).getSurname());
        Assertions.assertEquals(((EmployeeDetails) employeeUser.getDetails()).getDescription(), ((EmployeeDetails) createdApplication.getUser().getDetails()).getDescription());
        Assertions.assertIterableEquals(((EmployeeDetails) employeeUser.getDetails()).getSkills(), ((EmployeeDetails) createdApplication.getUser().getDetails()).getSkills());

        Assertions.assertEquals(job.getId(), createdApplication.getJob().getId());
        Assertions.assertEquals(job.getUser().getId(), createdApplication.getJob().getUser().getId());
        Assertions.assertEquals(job.getName(), createdApplication.getJob().getName());
        Assertions.assertEquals(job.getDescription(), createdApplication.getJob().getDescription());
        Assertions.assertEquals(job.getDueTo(), createdApplication.getJob().getDueTo());
        Assertions.assertIterableEquals(job.getRequirements(), createdApplication.getJob().getRequirements());
    }

    @Test
    void makeApplicationCredentialsErrorTest(){
        when(principal.getName()).thenReturn("employee");
        when(userDetailsService.loadUserByUsername("employee")).thenReturn(employeeUser);
        when(userRepository.getReferenceById(otherEmployeeId)).thenReturn(otherEmployeeUser);
        when(jobRepository.getReferenceById(jobId)).thenReturn(job);
        when(applicationRepository.findByUserAndJob(otherEmployeeUser, job)).thenReturn(Optional.empty());

        ApplicationDTO dto = ApplicationDTO.builder()
                .jobId(jobId).userId(otherEmployeeId)
                .build();

        Assertions.assertThrows(IncorrectCredentialsException.class, () -> applicationService.makeApplication(dto, principal));
    }

    @Test
    void makeApplicationAlreadyAppliedTest() {
        when(principal.getName()).thenReturn("employee");
        when(userDetailsService.loadUserByUsername("employee")).thenReturn(employeeUser);
        when(userRepository.getReferenceById(employeeId)).thenReturn(employeeUser);
        when(jobRepository.getReferenceById(jobId)).thenReturn(job);
        when(applicationRepository.findByUserAndJob(employeeUser, job)).thenReturn(Optional.of(application));

        ApplicationDTO dto = ApplicationDTO.builder()
                .jobId(jobId).userId(employeeId)
                .build();

        Assertions.assertThrows(AlreadyAppliedException.class, () -> applicationService.makeApplication(dto, principal));
    }

    @Test
    void getAllTest(){
        when(applicationRepository.findAll()).thenReturn(List.of(application1, application2));

        List<Application> applications = applicationService.getAll();

        Assertions.assertEquals(2, applications.size());

        Assertions.assertEquals(application1.getId(), applications.get(0).getId());
        Assertions.assertEquals(application2.getId(), applications.get(1).getId());
    }

    @Test
    void getAllEmptyTest(){
        when(applicationRepository.findAll()).thenReturn(List.of());

        List<Application> applications = applicationService.getAll();

        Assertions.assertEquals(0, applications.size());
    }

    @Test
    void getJobApplicationsTest(){
        when(principal.getName()).thenReturn("employer");
        when(userDetailsService.loadUserByUsername("employer")).thenReturn(employerUser);
        when(jobRepository.getReferenceById(jobId)).thenReturn(job);
        when(applicationRepository.findByJob(job)).thenReturn(List.of(application1, application2));

        List<Application> applications = applicationService.getJobApplications(jobId.toString(), principal);

        Assertions.assertEquals(2, applications.size());

        Assertions.assertEquals(application1.getId(), applications.get(0).getId());
        Assertions.assertEquals(application2.getId(), applications.get(1).getId());
    }

    @Test
    void getJobApplicationByAdminTest(){
        when(principal.getName()).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(adminUser);
        when(jobRepository.getReferenceById(jobId)).thenReturn(job);
        when(applicationRepository.findByJob(job)).thenReturn(List.of(application1, application2));

        List<Application> applications = applicationService.getJobApplications(jobId.toString(), principal);

        Assertions.assertEquals(2, applications.size());

        Assertions.assertEquals(application1.getId(), applications.get(0).getId());
        Assertions.assertEquals(application2.getId(), applications.get(1).getId());
    }

    @Test
    void getJobApplicationCredentialsErrorTest(){
        when(principal.getName()).thenReturn("oemployer");
        when(userDetailsService.loadUserByUsername("oemployer")).thenReturn(otherEmployerUser);
        when(jobRepository.getReferenceById(jobId)).thenReturn(job);
        when(applicationRepository.findByJob(job)).thenReturn(List.of(application1, application2));

        Assertions.assertThrows(IncorrectCredentialsException.class, () -> applicationService.getJobApplications(jobId.toString(), principal));
    }

    @Test
    void getUserApplicationsTest(){
        when(principal.getName()).thenReturn("employee");
        when(userDetailsService.loadUserByUsername("employee")).thenReturn(employeeUser);
        when(userRepository.findByUsername("employee")).thenReturn(Optional.of(employeeUser));
        when(applicationRepository.findByUser(employeeUser)).thenReturn(List.of(application1, application2));

        List<Application> applications = applicationService.getUserApplications("employee", principal);

        Assertions.assertEquals(2, applications.size());

        Assertions.assertEquals(application1.getId(), applications.get(0).getId());
        Assertions.assertEquals(application2.getId(), applications.get(1).getId());
    }

    @Test
    void getUserApplicationsByAdminTest(){
        when(principal.getName()).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(adminUser);
        when(userRepository.findByUsername("employee")).thenReturn(Optional.of(employeeUser));
        when(applicationRepository.findByUser(employeeUser)).thenReturn(List.of(application1, application2));

        List<Application> applications = applicationService.getUserApplications("employee", principal);

        Assertions.assertEquals(2, applications.size());

        Assertions.assertEquals(application1.getId(), applications.get(0).getId());
        Assertions.assertEquals(application2.getId(), applications.get(1).getId());
    }

    @Test
    void getUserApplicationsCredentialsErrorTest(){
        when(principal.getName()).thenReturn("employee2");
        when(userDetailsService.loadUserByUsername("employee2")).thenReturn(employee2User);
        when(userRepository.getReferenceById(employee1Id)).thenReturn(employee1User);
        when(applicationRepository.findByUser(employee1User)).thenReturn(List.of(application1, application2));

        Assertions.assertThrows(IncorrectCredentialsException.class, () -> applicationService.getUserApplications("employee", principal));
    }
}
