package com.it.jobfinder.services;

import com.it.jobfinder.dtos.EmployerRegistrationDTO;
import com.it.jobfinder.entities.EmployeeUser;
import com.it.jobfinder.entities.EmployerUser;
import com.it.jobfinder.entities.User;
import com.it.jobfinder.entities.UserRole;
import com.it.jobfinder.exceptions.UserDuplicateException;
import com.it.jobfinder.repositories.EmployerUserRepository;
import com.it.jobfinder.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EmployerService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmployerUserRepository employerUserRepository;

    public User addEmployer(EmployerRegistrationDTO dto) {
        Optional<User> username = userRepository.findByUsername(dto.getUsername());
        if (username.isPresent()) throw new UserDuplicateException("Username taken");

        Optional<User> email = userRepository.findByEmail(dto.getEmail());
        if (email.isPresent()) throw new UserDuplicateException("Email taken");

        User user = new User(dto.getUsername(), passwordEncoder.encode(dto.getPassword()), dto.getEmail(), UserRole.EMPLOYER);
        userRepository.save(user);

        EmployerUser employerUser = new EmployerUser(user.getId(), dto.getName(), "");
        employerUserRepository.save(employerUser);

        return user;
    }
}
