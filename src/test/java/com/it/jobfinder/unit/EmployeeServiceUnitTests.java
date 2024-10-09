package com.it.jobfinder.unit;

import com.it.jobfinder.dtos.EmployeeRegistrationDTO;
import com.it.jobfinder.dtos.LoginDTO;
import com.it.jobfinder.dtos.UserSkillDTO;
import com.it.jobfinder.entities.*;
import com.it.jobfinder.exceptions.IncorrectCredentialsException;
import com.it.jobfinder.exceptions.UserDuplicateException;
import com.it.jobfinder.repositories.DetailsRepository;
import com.it.jobfinder.repositories.SkillRepository;
import com.it.jobfinder.repositories.UserRepository;
import com.it.jobfinder.security.CustomUserDetailsService;
import com.it.jobfinder.services.EmployeeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmployeeServiceUnitTests {

    @Mock
    Principal principal;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    CustomUserDetailsService userDetailsService;

    @Mock
    UserRepository userRepository;

    @Mock
    DetailsRepository detailsRepository;

    @Mock
    SkillRepository skillRepository;

    @InjectMocks
    EmployeeService employeeService;

    @BeforeEach
    void beforeEach(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addEmployeeTest(){
        EmployeeDetails employeeDetails = new EmployeeDetails("Em", "Ployee", "", List.of());
        User employeeUser = new User("employee", "password", "employee@mail.com", UserRole.EMPLOYEE, employeeDetails);

        EmployeeRegistrationDTO dto = EmployeeRegistrationDTO.builder()
                .name("Em").surname("Ployee").username("employee").password("password").email("employee@mail.com").description("")
                .build();

        when(userRepository.findByUsername("employee")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("employee@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        User createdUser = employeeService.addEmployee(dto);

        Assertions.assertNotNull(createdUser);
        Assertions.assertEquals(employeeUser.getUsername(), createdUser.getUsername());
        Assertions.assertTrue(passwordEncoder.matches(employeeUser.getPassword(), createdUser.getPassword()));
        Assertions.assertEquals(employeeUser.getEmail(), createdUser.getEmail());
        Assertions.assertEquals(employeeUser.getRole().name(), createdUser.getRole().name());
        Assertions.assertEquals(employeeDetails.getName(), ((EmployeeDetails) createdUser.getDetails()).getName());
        Assertions.assertEquals(employeeDetails.getSurname(), ((EmployeeDetails) createdUser.getDetails()).getSurname());
        Assertions.assertEquals(employeeDetails.getDescription(), ((EmployeeDetails) createdUser.getDetails()).getDescription());
        Assertions.assertIterableEquals(((EmployeeDetails) employeeUser.getDetails()).getSkills(), ((EmployeeDetails) createdUser.getDetails()).getSkills());
    }

    @Test
    void addEmployeeUsernameTakenTest(){
        EmployeeDetails employeeDetails = new EmployeeDetails("Em", "Ployee", "", List.of());
        User employeeUser = new User("employee", "password", "employee@mail.com", UserRole.EMPLOYEE, employeeDetails);

        EmployeeRegistrationDTO dto = EmployeeRegistrationDTO.builder()
                .name("Em").surname("Ployee").username("employee").password("password").email("employee@mail.com").description("")
                .build();

        when(userRepository.findByUsername("employee")).thenReturn(Optional.of(employeeUser));

        Assertions.assertThrows(UserDuplicateException.class, () -> employeeService.addEmployee(dto));
    }

    @Test
    void addEmployeeEmailTakenTest(){
        EmployeeDetails employeeDetails = new EmployeeDetails("Em", "Ployee", "", List.of());
        User employeeUser = new User("employee", "password", "employee@mail.com", UserRole.EMPLOYEE, employeeDetails);

        EmployeeRegistrationDTO dto = EmployeeRegistrationDTO.builder()
                .name("Em").surname("Ployee").username("employee").password("password").email("employee@mail.com").description("")
                .build();

        when(userRepository.findByUsername("employee")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("employee@mail.com")).thenReturn(Optional.of(employeeUser));

        Assertions.assertThrows(UserDuplicateException.class, () -> employeeService.addEmployee(dto));
    }

    @Test
    void deleteEmployeeTest(){
        UUID employeeId = UUID.randomUUID();

        EmployeeDetails employeeDetails = new EmployeeDetails("Em", "Ployee", "", List.of());
        User employeeUser = new User("employee", "password", "employee@mail.com", UserRole.EMPLOYEE, employeeDetails);
        employeeUser.setId(employeeId);

        when(userRepository.findByUsername("employee")).thenReturn(Optional.of(employeeUser));
        when(userDetailsService.loadUserByUsername("employee")).thenReturn(employeeUser);
        when(principal.getName()).thenReturn("employee");
        when(passwordEncoder.matches("password", "password")).thenReturn(true);

        LoginDTO dto = LoginDTO.builder()
                .username("employee").password("password")
                .build();

        employeeService.deleteEmployee(dto, principal);

        verify(userRepository).delete(employeeUser);
    }

    @Test
    void deleteEmployeeNotFoundTest(){
        when(userRepository.findByUsername("employee")).thenReturn(Optional.empty());

        LoginDTO dto = LoginDTO.builder()
                .username("employee").password("password")
                .build();

        Assertions.assertThrows(UsernameNotFoundException.class, () -> employeeService.deleteEmployee(dto, principal));
    }

    @Test
    void deleteEmployeeIncorrectCredentialsTest(){
        UUID employeeId = UUID.randomUUID();
        UUID otherEmployeeId = UUID.randomUUID();

        EmployeeDetails employeeDetails = new EmployeeDetails("Em", "Ployee", "", List.of());
        User employeeUser = new User("employee", "password", "employee@mail.com", UserRole.EMPLOYEE, employeeDetails);
        employeeUser.setId(employeeId);

        EmployeeDetails otherEmployeeDetails = new EmployeeDetails("Emm", "Ploye", "", List.of());
        User otherEmployeeUser = new User("Oemployee", "password", "Oemployee@mail.com", UserRole.EMPLOYEE, otherEmployeeDetails);
        otherEmployeeUser.setId(otherEmployeeId);

        when(userRepository.findByUsername("employee")).thenReturn(Optional.of(employeeUser));
        when(principal.getName()).thenReturn("Oemployee");
        when(userDetailsService.loadUserByUsername("Oemployee")).thenReturn(otherEmployeeUser);

        LoginDTO dto = LoginDTO.builder()
                .username("employee").password("password")
                .build();

        Assertions.assertThrows(IncorrectCredentialsException.class, () -> employeeService.deleteEmployee(dto, principal));
    }

    @Test
    void deleteEmployeeBadCredentialsTest(){
        UUID employeeId = UUID.randomUUID();

        EmployeeDetails employeeDetails = new EmployeeDetails("Em", "Ployee", "", List.of());
        User employeeUser = new User("employee", "password", "employee@mail.com", UserRole.EMPLOYEE, employeeDetails);
        employeeUser.setId(employeeId);

        when(userRepository.findByUsername("employee")).thenReturn(Optional.of(employeeUser));
        when(userDetailsService.loadUserByUsername("employee")).thenReturn(employeeUser);
        when(principal.getName()).thenReturn("employee");
        when(passwordEncoder.matches("password", "password")).thenReturn(false);

        LoginDTO dto = LoginDTO.builder()
                .username("employee").password("password")
                .build();

        Assertions.assertThrows(BadCredentialsException.class, () -> employeeService.deleteEmployee(dto, principal));
    }

    @Test
    void addSkillToUserTest(){
        UUID employeeId = UUID.randomUUID();
        EmployeeDetails employeeDetails = new EmployeeDetails("Em", "Ployee", "", new ArrayList<>());
        User employeeUser = new User("employee", "password", "employee@mail.com", UserRole.EMPLOYEE, employeeDetails);
        employeeUser.setId(employeeId);

        Skill skill = new Skill("skill");

        when(userRepository.findByUsername("employee")).thenReturn(Optional.of(employeeUser));
        when(userDetailsService.loadUserByUsername("employee")).thenReturn(employeeUser);
        when(principal.getName()).thenReturn("employee");
        when(skillRepository.findByName("skill")).thenReturn(Optional.of(skill));

        UserSkillDTO dto = UserSkillDTO.builder()
                .skill("skill").username("employee")
                .build();

        List<Skill> updatedList = employeeService.addSkillToUser(dto, principal);

        Assertions.assertIterableEquals(List.of(skill), updatedList);
    }
}
