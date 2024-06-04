package com.it.jobfinder.services;

import com.it.jobfinder.dtos.RegistrationDTO;
import com.it.jobfinder.dtos.UserSkillDTO;
import com.it.jobfinder.entities.EmployeeSkills;
import com.it.jobfinder.entities.Skill;
import com.it.jobfinder.entities.User;
import com.it.jobfinder.entities.UserRole;
import com.it.jobfinder.exceptions.*;
import com.it.jobfinder.repositories.EmployeeSkillsRepository;
import com.it.jobfinder.repositories.SkillRepository;
import com.it.jobfinder.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeSkillsRepository employeeSkillsRepository;
    private final SkillRepository skillRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("There's no such username in the db"));
    }

    public User addUser(RegistrationDTO dto){
        Optional<User> username = userRepository.findByUsername(dto.getUsername());
        if (username.isPresent()) throw new UserDuplicateException("Username taken");

        Optional<User> email = userRepository.findByEmail(dto.getEmail());
        if (email.isPresent()) throw new UserDuplicateException("Email taken");

        User user = userRepository.save(new User(dto.getUsername(), passwordEncoder.encode(dto.getPassword()), dto.getEmail(), dto.getRole()));


        if(user.getRole().equals(UserRole.EMPLOYEE)){
            EmployeeSkills employeeSkills = new EmployeeSkills(user.getId(), new ArrayList<>());
            employeeSkillsRepository.save(employeeSkills);
        }

        return user;
    }

    public EmployeeSkills addSkillToUser(UserSkillDTO dto){
        Optional<User> username = userRepository.findByUsername(dto.getUsername());
        if (username.isEmpty()) throw new UsernameNotFoundException("There's no such username in the db");

        Optional<Skill> skill = skillRepository.findByName(dto.getSkill());
        if (skill.isEmpty()) throw new NoSuchSkillException("There's no such skill in the db");

        Optional<EmployeeSkills> employeeSkills = employeeSkillsRepository.findById(username.get().getId());
        if (employeeSkills.isEmpty()) throw new UsernameNotFoundException("There's no such username in the db");

        if (employeeSkills.get().getSkills().contains(skill.get())) throw new SkillAlreadyAcquiredException("User already has added such skill");

        employeeSkills.get().getSkills().add(skill.get());
        return employeeSkillsRepository.save(employeeSkills.get());
    }


    public EmployeeSkills getUserSkills(String username){
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) throw new UsernameNotFoundException("There's no such username in the db");
        Optional<EmployeeSkills> employeeSkills = employeeSkillsRepository.findById(user.get().getId());
        if (employeeSkills.isEmpty()) throw new UsernameNotFoundException("There's no such username in the db");

        return employeeSkills.get();
    }

    public boolean deleteSkillFromUser(UserSkillDTO dto){
        Optional<User> username = userRepository.findByUsername(dto.getUsername());
        if (username.isEmpty()) throw new UsernameNotFoundException("There's no such username in the db");

        Optional<Skill> skill = skillRepository.findByName(dto.getSkill());
        if (skill.isEmpty()) throw new NoSuchSkillException("There's no such skill in the db");

        Optional<EmployeeSkills> employeeSkills = employeeSkillsRepository.findById(username.get().getId());
        if (employeeSkills.isEmpty()) throw new UsernameNotFoundException("There's no such username in the db");

        if (!employeeSkills.get().getSkills().contains(skill.get())) throw new NoSuchSkillException("User doesn't have such skill");

        return employeeSkills.get().getSkills().remove(skill.get());
    }
    /*
    public String loginUser(LoginDTO dto){
        Optional<User> user = userRepository.findByUsername(dto.getUsername());
        if (user.isEmpty()) throw new IncorrectCredentialsException("Incorrect username");

        if (!passwordEncoder.matches(dto.getPassword(),user.get().getPassword())) throw new IncorrectCredentialsException("Incorrect password");

        return "Login successful";
    }*/
}
