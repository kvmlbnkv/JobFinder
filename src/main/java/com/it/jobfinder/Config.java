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
            AdminDetails details = new AdminDetails("test", "test");
            detailsRepository.save(details);

            User test = new User("test", passwordEncoder.encode( "test"), "test@test.com", UserRole.ADMIN, details);
            userRepository.save(test);

            Skill java = new Skill("Java");
            skillRepository.save(java);

            Job job = new Job(test, "test job", "this is a test job", new ArrayList<>(List.of(java)), LocalDate.of(2024, 7, 5), false);
            jobRepository.save(job);
        };
    }

}
