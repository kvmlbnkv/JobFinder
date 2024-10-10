package com.it.jobfinder.unit;

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



    UUID employeeId = UUID.randomUUID();
    UUID otherEmployeeId = UUID.randomUUID();

    EmployeeDetails employeeDetails = new EmployeeDetails("Em", "Ployee", "", new ArrayList<>());
    User employeeUser = new User(employeeId, "employee", "password", "employee@mail.com", UserRole.EMPLOYEE, employeeDetails);

    EmployeeDetails otherEmployeeDetails = new EmployeeDetails("Emm", "Ploye", "", new ArrayList<>());
    User otherEmployeeUser = new User(otherEmployeeId, "Oemployee", "password", "Oemployee@mail.com", UserRole.EMPLOYEE, otherEmployeeDetails);

    Skill skill = new Skill("skill");



    @BeforeEach
    void beforeEach(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addEmployeeTest(){
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
        EmployeeRegistrationDTO dto = EmployeeRegistrationDTO.builder()
                .name("Em").surname("Ployee").username("employee").password("password").email("employee@mail.com").description("")
                .build();

        when(userRepository.findByUsername("employee")).thenReturn(Optional.of(employeeUser));

        Assertions.assertThrows(UserDuplicateException.class, () -> employeeService.addEmployee(dto));
    }

    @Test
    void addEmployeeEmailTakenTest(){
        EmployeeRegistrationDTO dto = EmployeeRegistrationDTO.builder()
                .name("Em").surname("Ployee").username("employee").password("password").email("employee@mail.com").description("")
                .build();

        when(userRepository.findByUsername("employee")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("employee@mail.com")).thenReturn(Optional.of(employeeUser));

        Assertions.assertThrows(UserDuplicateException.class, () -> employeeService.addEmployee(dto));
    }

    @Test
    void deleteEmployeeTest(){
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
    void addSkillToEmployeeTest(){
        when(userRepository.findByUsername("employee")).thenReturn(Optional.of(employeeUser));
        when(userDetailsService.loadUserByUsername("employee")).thenReturn(employeeUser);
        when(principal.getName()).thenReturn("employee");
        when(skillRepository.findByName("skill")).thenReturn(Optional.of(skill));

        UserSkillDTO dto = UserSkillDTO.builder()
                .skill("skill").username("employee")
                .build();

        List<Skill> updatedList = employeeService.addSkillToEmployee(dto, principal);

        verify(userRepository).save(employeeUser);
        Assertions.assertIterableEquals(List.of(skill), updatedList);
    }

    @Test
    void addSkillToEmployeeUsernameNotFoundTest(){
        when(userRepository.findByUsername("employee")).thenReturn(Optional.empty());

        UserSkillDTO dto = UserSkillDTO.builder()
                .skill("skill").username("employee")
                .build();

        Assertions.assertThrows(UsernameNotFoundException.class, () -> employeeService.addSkillToEmployee(dto, principal));
    }

    @Test
    void addSkillToEmployeeIncorrectCredentialsTest(){
        when(userRepository.findByUsername("employee")).thenReturn(Optional.of(employeeUser));
        when(principal.getName()).thenReturn("Oemployee");
        when(userDetailsService.loadUserByUsername("Oemployee")).thenReturn(otherEmployeeUser);

        UserSkillDTO dto = UserSkillDTO.builder()
                .skill("skill").username("employee")
                .build();

        Assertions.assertThrows(IncorrectCredentialsException.class, () -> employeeService.addSkillToEmployee(dto, principal));
    }

    @Test
    void addSkillToEmployeeNoSuchSkillTest(){
        when(userRepository.findByUsername("employee")).thenReturn(Optional.of(employeeUser));
        when(userDetailsService.loadUserByUsername("employee")).thenReturn(employeeUser);
        when(principal.getName()).thenReturn("employee");
        when(skillRepository.findByName("skill")).thenReturn(Optional.empty());

        UserSkillDTO dto = UserSkillDTO.builder()
                .skill("skill").username("employee")
                .build();

        Assertions.assertThrows(NoSuchSkillException.class, () -> employeeService.addSkillToEmployee(dto, principal));
    }

    @Test
    void addSkillToEmployeeSkillAlreadyAcquiredTest(){
        ((EmployeeDetails) employeeUser.getDetails()).getSkills().add(skill);

        when(userRepository.findByUsername("employee")).thenReturn(Optional.of(employeeUser));
        when(userDetailsService.loadUserByUsername("employee")).thenReturn(employeeUser);
        when(principal.getName()).thenReturn("employee");
        when(skillRepository.findByName("skill")).thenReturn(Optional.of(skill));

        UserSkillDTO dto = UserSkillDTO.builder()
                .skill("skill").username("employee")
                .build();

        Assertions.assertThrows(SkillAlreadyAcquiredException.class, () -> employeeService.addSkillToEmployee(dto, principal));
    }

    @Test
    void removeSkillFromEmployeeTest(){
        ((EmployeeDetails) employeeUser.getDetails()).getSkills().add(skill);

        when(userRepository.findByUsername("employee")).thenReturn(Optional.of(employeeUser));
        when(userDetailsService.loadUserByUsername("employee")).thenReturn(employeeUser);
        when(principal.getName()).thenReturn("employee");
        when(skillRepository.findByName("skill")).thenReturn(Optional.of(skill));

        UserSkillDTO dto = UserSkillDTO.builder()
                .skill("skill").username("employee")
                .build();

        List<Skill> updatedList = employeeService.removeSkillFromEmployee(dto, principal);

        verify(userRepository).save(employeeUser);
        Assertions.assertIterableEquals(List.of(), updatedList);
    }

    @Test
    void removeSkillFromEmployeeUsernameNotFoundTest(){
        when(userRepository.findByUsername("employee")).thenReturn(Optional.empty());

        UserSkillDTO dto = UserSkillDTO.builder()
                .skill("skill").username("employee")
                .build();

        Assertions.assertThrows(UsernameNotFoundException.class, () -> employeeService.removeSkillFromEmployee(dto, principal));
    }

    @Test
    void removeSkillFromEmployeeIncorrectCredentialsTest(){
        when(userRepository.findByUsername("employee")).thenReturn(Optional.of(employeeUser));
        when(principal.getName()).thenReturn("Oemployee");
        when(userDetailsService.loadUserByUsername("Oemployee")).thenReturn(otherEmployeeUser);

        UserSkillDTO dto = UserSkillDTO.builder()
                .skill("skill").username("employee")
                .build();

        Assertions.assertThrows(IncorrectCredentialsException.class, () -> employeeService.removeSkillFromEmployee(dto, principal));
    }

    @Test
    void removeSkillFromEmployeeNoSuchSkillTest(){
        when(userRepository.findByUsername("employee")).thenReturn(Optional.of(employeeUser));
        when(userDetailsService.loadUserByUsername("employee")).thenReturn(employeeUser);
        when(principal.getName()).thenReturn("employee");
        when(skillRepository.findByName("skill")).thenReturn(Optional.empty());

        UserSkillDTO dto = UserSkillDTO.builder()
                .skill("skill").username("employee")
                .build();

        Assertions.assertThrows(NoSuchSkillException.class, () -> employeeService.removeSkillFromEmployee(dto, principal));
    }

    @Test
    void removeSkillFromEmployeeSkillNotAcquiredTest(){
        when(userRepository.findByUsername("employee")).thenReturn(Optional.of(employeeUser));
        when(userDetailsService.loadUserByUsername("employee")).thenReturn(employeeUser);
        when(principal.getName()).thenReturn("employee");
        when(skillRepository.findByName("skill")).thenReturn(Optional.of(skill));

        UserSkillDTO dto = UserSkillDTO.builder()
                .skill("skill").username("employee")
                .build();

        Assertions.assertThrows(SkillNotAcquiredException.class, () -> employeeService.removeSkillFromEmployee(dto, principal));
    }

    @Test
    void getAllTest(){
        when(userRepository.findByRole(UserRole.EMPLOYEE)).thenReturn(List.of(employeeUser, otherEmployeeUser));

        Assertions.assertIterableEquals(List.of(employeeUser, otherEmployeeUser), employeeService.getAll());
    }

    @Test
    void getTest(){
        when(userRepository.findByRoleAndUsername(UserRole.EMPLOYEE, "employee")).thenReturn(Optional.of(employeeUser));
        when(passwordEncoder.matches("password", "password")).thenReturn(true);

        User returnedUser = employeeService.get("employee");

        Assertions.assertNotNull(returnedUser);
        Assertions.assertEquals(employeeUser.getUsername(), returnedUser.getUsername());
        Assertions.assertTrue(passwordEncoder.matches(employeeUser.getPassword(), returnedUser.getPassword()));
        Assertions.assertEquals(employeeUser.getEmail(), returnedUser.getEmail());
        Assertions.assertEquals(employeeUser.getRole().name(), returnedUser.getRole().name());
        Assertions.assertEquals(employeeDetails.getName(), ((EmployeeDetails) returnedUser.getDetails()).getName());
        Assertions.assertEquals(employeeDetails.getSurname(), ((EmployeeDetails) returnedUser.getDetails()).getSurname());
        Assertions.assertEquals(employeeDetails.getDescription(), ((EmployeeDetails) returnedUser.getDetails()).getDescription());
        Assertions.assertIterableEquals(((EmployeeDetails) employeeUser.getDetails()).getSkills(), ((EmployeeDetails) returnedUser.getDetails()).getSkills());
    }

    @Test
    void updateTest(){
        when(userRepository.getReferenceById(employeeId)).thenReturn(employeeUser);
        when(userDetailsService.loadUserByUsername("employee")).thenReturn(employeeUser);
        when(principal.getName()).thenReturn("employee");
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(passwordEncoder.matches("newPassword", "encodedNewPassword")).thenReturn(true);
        when(userRepository.save(employeeUser)).thenReturn(employeeUser);

        EmployeeUpdateDTO dto = EmployeeUpdateDTO.builder()
                .id(employeeId).name("Emm").surname("Ployeee").username("nemployee").email("nemployee@mail.com").description("hello").password("newPassword")
                .build();

        User updatedUser = employeeService.update(dto, principal);

        verify(userRepository).save(employeeUser);

        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(dto.getUsername(), updatedUser.getUsername());
        Assertions.assertTrue(passwordEncoder.matches(dto.getPassword(), updatedUser.getPassword()));
        Assertions.assertEquals(dto.getEmail(), updatedUser.getEmail());
        Assertions.assertEquals(dto.getName(), ((EmployeeDetails) updatedUser.getDetails()).getName());
        Assertions.assertEquals(dto.getSurname(), ((EmployeeDetails) updatedUser.getDetails()).getSurname());
        Assertions.assertEquals(dto.getDescription(), ((EmployeeDetails) updatedUser.getDetails()).getDescription());
    }

    @Test
    void updateNotAllValuesTest(){
        when(userRepository.getReferenceById(employeeId)).thenReturn(employeeUser);
        when(userDetailsService.loadUserByUsername("employee")).thenReturn(employeeUser);
        when(principal.getName()).thenReturn("employee");
        when(passwordEncoder.matches("password", "password")).thenReturn(true);
        when(userRepository.save(employeeUser)).thenReturn(employeeUser);

        EmployeeUpdateDTO dto = EmployeeUpdateDTO.builder()
                .id(employeeId).surname("Ployeee").username("nemployee").email("nemployee@mail.com")
                .build();

        User updatedUser = employeeService.update(dto, principal);

        verify(userRepository).save(employeeUser);

        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(dto.getUsername(), updatedUser.getUsername());
        Assertions.assertTrue(passwordEncoder.matches("password", updatedUser.getPassword()));
        Assertions.assertEquals(dto.getEmail(), updatedUser.getEmail());
        Assertions.assertEquals("Em", ((EmployeeDetails) updatedUser.getDetails()).getName());
        Assertions.assertEquals(dto.getSurname(), ((EmployeeDetails) updatedUser.getDetails()).getSurname());
        Assertions.assertEquals("", ((EmployeeDetails) updatedUser.getDetails()).getDescription());
    }

    @Test
    void updateIncorrectCredentialsTest(){
        when(userRepository.getReferenceById(employeeId)).thenReturn(employeeUser);
        when(principal.getName()).thenReturn("Oemployee");
        when(userDetailsService.loadUserByUsername("Oemployee")).thenReturn(otherEmployeeUser);

        EmployeeUpdateDTO dto = EmployeeUpdateDTO.builder()
                .id(employeeId).name("Emm").surname("Ployeee").username("nemployee").email("nemployee@mail.com").description("hello").password("newPassword")
                .build();

        Assertions.assertThrows(IncorrectCredentialsException.class, () -> employeeService.update(dto, principal));
    }
}
