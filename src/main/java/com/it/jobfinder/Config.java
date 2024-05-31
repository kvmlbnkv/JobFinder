package com.it.jobfinder;

import com.it.jobfinder.entities.User;
import com.it.jobfinder.entities.UserRole;
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
    CommandLineRunner cml(UserRepository userRepository){
        return args -> {
            User test = new User("test", passwordEncoder.encode( "test"), "test@test.com", UserRole.ADMIN);

            userRepository.save(test);
        };
    }

}
