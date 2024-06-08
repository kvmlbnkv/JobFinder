package com.it.jobfinder.services;

import com.it.jobfinder.dtos.EmployerRegistrationDTO;
import com.it.jobfinder.dtos.LoginDTO;
import com.it.jobfinder.entities.EmployerDetails;
import com.it.jobfinder.entities.User;
import com.it.jobfinder.entities.UserRole;
import com.it.jobfinder.exceptions.UserDuplicateException;
import com.it.jobfinder.repositories.DetailsRepository;
import com.it.jobfinder.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EmployerService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DetailsRepository detailsRepository;

    public User addEmployer(EmployerRegistrationDTO dto) {
        Optional<User> userOptional = userRepository.findByUsername(dto.getUsername());
        if (userOptional.isPresent()) throw new UserDuplicateException("Username taken");

        Optional<User> email = userRepository.findByEmail(dto.getEmail());
        if (email.isPresent()) throw new UserDuplicateException("Email taken");

        EmployerDetails employerDetails = new EmployerDetails(dto.getName(), dto.getDescription());
        detailsRepository.save(employerDetails);

        User user = new User(dto.getUsername(), passwordEncoder.encode(dto.getPassword()), dto.getEmail(), UserRole.EMPLOYER, employerDetails);
        userRepository.save(user);

        return user;
    }

    public void deleteEmployer(LoginDTO dto) {
        User user = this.userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) throw new BadCredentialsException("Incorrect password");

        userRepository.delete(user);
    }
}
