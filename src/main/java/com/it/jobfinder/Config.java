package com.it.jobfinder;

import com.it.jobfinder.entities.AdminDetails;
import com.it.jobfinder.entities.Skill;
import com.it.jobfinder.entities.User;
import com.it.jobfinder.entities.UserRole;
import com.it.jobfinder.repositories.DetailsRepository;
import com.it.jobfinder.repositories.SkillRepository;
import com.it.jobfinder.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class Config {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner cml(DetailsRepository detailsRepository, UserRepository userRepository, SkillRepository skillRepository){
        return args -> {
            AdminDetails details = new AdminDetails("test", "test");
            detailsRepository.save(details);

            User test = new User("test", passwordEncoder.encode( "test"), "test@test.com", UserRole.ADMIN, details);
            userRepository.save(test);

            Skill java = new Skill("Java");
            skillRepository.save(java);
        };
    }

}
