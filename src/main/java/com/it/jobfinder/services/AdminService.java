package com.it.jobfinder.services;

import com.it.jobfinder.dtos.AdminRegistrationDTO;
import com.it.jobfinder.dtos.LoginDTO;
import com.it.jobfinder.entities.AdminDetails;
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

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DetailsRepository detailsRepository;

    public User addAdmin(AdminRegistrationDTO dto) {
        Optional<User> userOptional = userRepository.findByUsername(dto.getUsername());
        if (userOptional.isPresent()) throw new UserDuplicateException("Username taken");

        Optional<User> email = userRepository.findByEmail(dto.getEmail());
        if (email.isPresent()) throw new UserDuplicateException("Email taken");

        AdminDetails adminDetails = new AdminDetails(dto.getName(), dto.getSurname());
        detailsRepository.save(adminDetails);
        User user = new User(dto.getUsername(), passwordEncoder.encode(dto.getPassword()), dto.getEmail(), UserRole.ADMIN, adminDetails);
        userRepository.save(user);

        return user;
    }

    public void deleteAdmin(LoginDTO dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) throw new BadCredentialsException("Incorrect password");

        userRepository.delete(user);
    }

    public List<User> getAll() {
        return this.userRepository.findByRole(UserRole.ADMIN);
    }

    public User get(String username) {
        return this.userRepository.findByRoleAndUsername(UserRole.ADMIN, username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    public User update(AdminRegistrationDTO dto) {
        User user = this.userRepository.findByRoleAndUsername(UserRole.ADMIN, dto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        user.setEmail(dto.getEmail());
        user.setPassword(this.passwordEncoder.encode(dto.getPassword()));
        AdminDetails adminDetails = (AdminDetails) user.getDetails();
        adminDetails.setName(dto.getName());
        adminDetails.setSurname(dto.getSurname());

        return this.userRepository.save(user);
    }
}
