package com.it.jobfinder;

import com.it.jobfinder.entities.Skill;
import com.it.jobfinder.entities.User;
import com.it.jobfinder.entities.UserRole;
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
    CommandLineRunner cml(UserRepository userRepository, SkillRepository skillRepository){
        return args -> {
            User test = new User("test", passwordEncoder.encode( "test"), "test@test.com", UserRole.ADMIN);
            userRepository.save(test);

            Skill java = new Skill("Java");
            skillRepository.save(java);
        };
    }

}
