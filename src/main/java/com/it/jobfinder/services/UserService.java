package com.it.jobfinder.services;

import com.it.jobfinder.dtos.LoginDTO;
import com.it.jobfinder.dtos.RegistrationDTO;
import com.it.jobfinder.dtos.UserSkillDTO;
import com.it.jobfinder.entities.*;
import com.it.jobfinder.exceptions.NoSuchSkillException;
import com.it.jobfinder.exceptions.SkillAlreadyAcquiredException;
import com.it.jobfinder.exceptions.UserDuplicateException;
import com.it.jobfinder.repositories.EmployeeUserRepository;
import com.it.jobfinder.repositories.SkillRepository;
import com.it.jobfinder.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
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
    private final SkillRepository skillRepository;
    private final EmployeeUserRepository employeeUserRepository;

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

        User user = new User(dto.getUsername(), passwordEncoder.encode(dto.getPassword()), dto.getEmail(), dto.getRole());
        userRepository.save(user);

        if(user.getRole().equals(UserRole.EMPLOYEE)){
            List<Skill> employeeSkills = new ArrayList<>();

            EmployeeUser employeeUser = new EmployeeUser();
        }

        return user;
    }

    public void deleteUser(LoginDTO dto) {
        Optional<User> user = userRepository.findByUsername(dto.getUsername());
        if (user.isEmpty()) throw new UsernameNotFoundException("Incorrect username");

        if (!passwordEncoder.matches(dto.getPassword(),user.get().getPassword())) throw new BadCredentialsException("Incorrect password");

        this.userRepository.delete(user.get());
    }


    /*
    public List<Skill> addSkillToUser(UserSkillDTO dto){
        Optional<User> username = userRepository.findByUsername(dto.getUsername());
        if (username.isEmpty()) throw new UsernameNotFoundException("There's no such username in the db");

        Optional<Skill> skill = skillRepository.findByName(dto.getSkill());
        if (skill.isEmpty()) throw new NoSuchSkillException("There's no such skill in the db");

        if (username.get().getSkills().contains(skill.get())) throw new SkillAlreadyAcquiredException("User already has added such skill");

        username.get().getSkills().add(skill.get());
        userRepository.save(username.get());
        return username.get().getSkills();
    }


    public List<Skill> getUserSkills(String username){
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) throw new UsernameNotFoundException("There's no such username in the db");

        return user.get().getSkills();
    }

    public boolean deleteSkillFromUser(UserSkillDTO dto){
        Optional<User> username = userRepository.findByUsername(dto.getUsername());
        if (username.isEmpty()) throw new UsernameNotFoundException("There's no such username in the db");

        Optional<Skill> skill = skillRepository.findByName(dto.getSkill());
        if (skill.isEmpty()) throw new NoSuchSkillException("There's no such skill in the db");

        if (!username.get().getSkills().contains(skill.get())) throw new NoSuchSkillException("User doesn't have such skill");

        return username.get().getSkills().remove(skill.get());
    }


    public String loginUser(LoginDTO dto){
        Optional<User> user = userRepository.findByUsername(dto.getUsername());
        if (user.isEmpty()) throw new IncorrectCredentialsException("Incorrect username");

        if (!passwordEncoder.matches(dto.getPassword(),user.get().getPassword())) throw new IncorrectCredentialsException("Incorrect password");

        return "Login successful";
    }*/
}
