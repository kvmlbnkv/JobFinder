package com.it.jobfinder.services;

import com.it.jobfinder.dtos.EmployeeRegistrationDTO;
import com.it.jobfinder.dtos.EmployeeUpdateDTO;
import com.it.jobfinder.dtos.LoginDTO;
import com.it.jobfinder.dtos.UserSkillDTO;
import com.it.jobfinder.entities.*;
import com.it.jobfinder.exceptions.*;
import com.it.jobfinder.repositories.DetailsRepository;
import com.it.jobfinder.repositories.SkillRepository;
import com.it.jobfinder.repositories.UserRepository;
import com.it.jobfinder.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EmployeeService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DetailsRepository detailsRepository;
    private final SkillRepository skillRepository;
    private final CustomUserDetailsService userDetailsService;

    public User addEmployee(EmployeeRegistrationDTO dto) {
        Optional<User> userOptional = userRepository.findByUsername(dto.getUsername());
        if (userOptional.isPresent()) throw new UserDuplicateException("Username taken");

        Optional<User> email = userRepository.findByEmail(dto.getEmail());
        if (email.isPresent()) throw new UserDuplicateException("Email taken");

        EmployeeDetails employeeDetails = new EmployeeDetails(dto.getName(), dto.getSurname(), dto.getDescription(), new ArrayList<>());
        detailsRepository.save(employeeDetails);

        User user = new User(dto.getUsername(), passwordEncoder.encode(dto.getPassword()), dto.getEmail(), UserRole.EMPLOYEE, employeeDetails);
        userRepository.save(user);

        return user;
    }

    public void deleteEmployee(LoginDTO dto, Principal principal) {
        User user = this.userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        User principalUser = (User) userDetailsService.loadUserByUsername(principal.getName());

        if (!principalUser.getRole().name().equals(UserRole.ADMIN.name()) && !principalUser.getId().equals(user.getId()))
            throw new IncorrectCredentialsException("Can't delete this user");

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) throw new BadCredentialsException("Incorrect password");

        userRepository.delete(user);
    }

    public List<Skill> addSkillToEmployee(UserSkillDTO dto, Principal principal) {
        User user = this.userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        User principalUser = (User) userDetailsService.loadUserByUsername(principal.getName());

        if (!principalUser.getRole().name().equals(UserRole.ADMIN.name()) && !principalUser.getId().equals(user.getId()))
            throw new IncorrectCredentialsException("Can't update this user");

        Skill skill = this.skillRepository.findByName(dto.getSkill())
                .orElseThrow(() -> new NoSuchSkillException("No such skill"));

        List<Skill> userSkills = ((EmployeeDetails) user.getDetails()).getSkills();
        if (userSkills.contains(skill)) throw new SkillAlreadyAcquiredException("Skill already acquired");

        userSkills.add(skill);
        userRepository.save(user);

        return userSkills;
    }

    public List<Skill> removeSkillFromEmployee(UserSkillDTO dto, Principal principal){
        User user = this.userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        User principalUser = (User) userDetailsService.loadUserByUsername(principal.getName());

        if (!principalUser.getRole().name().equals(UserRole.ADMIN.name()) && !principalUser.getId().equals(user.getId()))
            throw new IncorrectCredentialsException("Can't update this user");

        Skill skill = this.skillRepository.findByName(dto.getSkill())
                .orElseThrow(() -> new NoSuchSkillException("No such skill"));

        List<Skill> userSkills = ((EmployeeDetails) user.getDetails()).getSkills();
        if(!userSkills.contains(skill)) throw new SkillNotAcquiredException("Skill not acquired");

        userSkills.remove(skill);
        userRepository.save(user);

        return userSkills;
    }

    public List<User> getAll() {
        return this.userRepository.findByRole(UserRole.EMPLOYEE);
    }

    public User get(String username) {
        return this.userRepository.findByRoleAndUsername(UserRole.EMPLOYEE, username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    public User update(EmployeeUpdateDTO dto, Principal principal) {
        User user = this.userRepository.getReferenceById(dto.getId());

        User principalUser = (User) userDetailsService.loadUserByUsername(principal.getName());

        if (!principalUser.getRole().name().equals(UserRole.ADMIN.name()) && !principalUser.getId().equals(user.getId()))
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

        EmployeeDetails employeeDetails = (EmployeeDetails) user.getDetails();
        if (dto.getName() != null) employeeDetails.setName(dto.getName());
        if (dto.getSurname() != null) employeeDetails.setSurname(dto.getSurname());
        if (dto.getDescription() != null) employeeDetails.setDescription(dto.getDescription());

        return this.userRepository.save(user);
    }
}
