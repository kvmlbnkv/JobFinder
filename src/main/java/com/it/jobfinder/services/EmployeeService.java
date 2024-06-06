package com.it.jobfinder.services;

import com.it.jobfinder.dtos.RegistrationDTO;
import com.it.jobfinder.entities.EmployeeUser;
import com.it.jobfinder.entities.User;
import com.it.jobfinder.exceptions.UserDuplicateException;
import com.it.jobfinder.repositories.EmployeeUserRepository;
import com.it.jobfinder.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EmployeeService {

    private final EmployeeUserRepository employeeUserRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User addEmployee(RegistrationDTO dto) {
        Optional<User> username = userRepository.findByUsername(dto.getUsername());
        if (username.isPresent()) throw new UserDuplicateException("Username taken");

        Optional<User> email = userRepository.findByEmail(dto.getEmail());
        if (email.isPresent()) throw new UserDuplicateException("Email taken");

        User user = new User(dto.getUsername(), passwordEncoder.encode(dto.getPassword()), dto.getEmail(), dto.getRole());
        userRepository.save(user);

        EmployeeUser employeeUser = new EmployeeUser();

        return user;
    }
}
