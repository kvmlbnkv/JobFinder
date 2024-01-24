package com.it.jobfinder;

import com.it.jobfinder.entities.User;
import com.it.jobfinder.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    CommandLineRunner cml(UserRepository userRepository){
        return args -> {
            User test = new User("test", "test", "test@test.com");

            userRepository.save(test);
        };
    }

}
