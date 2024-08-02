package com.it.jobfinder;

import com.it.jobfinder.entities.*;
import com.it.jobfinder.repositories.DetailsRepository;
import com.it.jobfinder.repositories.JobRepository;
import com.it.jobfinder.repositories.SkillRepository;
import com.it.jobfinder.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class Config {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner cml(DetailsRepository detailsRepository, UserRepository userRepository, SkillRepository skillRepository, JobRepository jobRepository){
        return args -> {
            AdminDetails adminDetails = new AdminDetails("test", "test");
            detailsRepository.save(adminDetails);
            User adminUser = new User("admin", passwordEncoder.encode( "admin"), "test@test.com", UserRole.ADMIN, adminDetails);
            userRepository.save(adminUser);

            EmployeeDetails employeeDetails = new EmployeeDetails("Em", "Ployee", "", List.of());
            detailsRepository.save(employeeDetails);
            User employeeUser = new User("employee", passwordEncoder.encode("password"), "employee@test.com", UserRole.EMPLOYEE, employeeDetails);
            userRepository.save(employeeUser);

            EmployerDetails employerDetails = new EmployerDetails("Employer", "");
            detailsRepository.save(employerDetails);
            User employerUser = new User("employer", passwordEncoder.encode("password"), "employer@test.com", UserRole.EMPLOYER, employerDetails);
            userRepository.save(employerUser);

            Skill java = new Skill("Java");
            skillRepository.save(java);

            Job job = new Job(adminUser, "test job", "this is a test job", List.of(java), LocalDate.of(2025, 7, 5), false);
            jobRepository.save(job);
        };
    }

}
