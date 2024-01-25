package com.it.jobfinder.services;

import com.it.jobfinder.dtos.RegistrationDTO;
import com.it.jobfinder.entities.User;
import com.it.jobfinder.exceptions.NoSuchUserException;
import com.it.jobfinder.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService{

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        return optionalUser.orElseThrow(NoSuchUserException::new);
    }

    public void addUser(RegistrationDTO dto){

        userRepository.save(user);
    }
}
