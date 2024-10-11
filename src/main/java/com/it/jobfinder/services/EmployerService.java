package com.it.jobfinder.services;

import com.it.jobfinder.dtos.EmployerRegistrationDTO;
import com.it.jobfinder.dtos.EmployerUpdateDTO;
import com.it.jobfinder.dtos.LoginDTO;
import com.it.jobfinder.entities.EmployeeDetails;
import com.it.jobfinder.entities.EmployerDetails;
import com.it.jobfinder.entities.User;
import com.it.jobfinder.entities.UserRole;
import com.it.jobfinder.exceptions.IncorrectCredentialsException;
import com.it.jobfinder.exceptions.UserDuplicateException;
import com.it.jobfinder.repositories.DetailsRepository;
import com.it.jobfinder.repositories.UserRepository;
import com.it.jobfinder.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EmployerService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DetailsRepository detailsRepository;
    private final CustomUserDetailsService userDetailsService;

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

    public void deleteEmployer(LoginDTO dto, Principal principal) {
        User user = this.userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        User principalUser = (User) userDetailsService.loadUserByUsername(principal.getName());
        if (!principalUser.getRole().name().equals(UserRole.ADMIN.name()) && !principalUser.getId().equals(user.getId()))
            throw new IncorrectCredentialsException("Can't delete this user");

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) throw new BadCredentialsException("Incorrect password");

        userRepository.delete(user);
    }

    public List<User> getAll() {
        return this.userRepository.findByRole(UserRole.EMPLOYER);
    }

    public User get(String username) {
        return this.userRepository.findByRoleAndUsername(UserRole.EMPLOYER, username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    public User update(EmployerUpdateDTO dto, Principal principal) {
        User user = this.userRepository.getReferenceById(dto.getId());

        User principalUser = (User) userDetailsService.loadUserByUsername(principal.getName());
        if (!principalUser.getRole().name().equals(UserRole.ADMIN.name()) && !principalUser.getId().equals(dto.getId()))
            throw new IncorrectCredentialsException("Can't update this user");

        if (dto.getUsername() != null){
            Optional<User> userOptional = userRepository.findByUsername(dto.getUsername());
            if (userOptional.isPresent()) throw new UserDuplicateException("Username taken");
            else user.setUsername(dto.getUsername());
        }

        if (dto.getEmail() != null){
            Optional<User> email = userRepository.findByEmail(dto.getEmail());
            if (email.isPresent()) throw new UserDuplicateException("Email taken");
            user.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null) user.setPassword(this.passwordEncoder.encode(dto.getPassword()));

        EmployerDetails employerDetails = (EmployerDetails) user.getDetails();
        if (dto.getName() != null) employerDetails.setName(dto.getName());
        if (dto.getDescription() != null) employerDetails.setDescription(dto.getDescription());

        return this.userRepository.save(user);
    }
}
