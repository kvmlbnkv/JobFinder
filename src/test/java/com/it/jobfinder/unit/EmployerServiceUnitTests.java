package com.it.jobfinder.unit;

import com.it.jobfinder.dtos.EmployerRegistrationDTO;
import com.it.jobfinder.dtos.EmployerUpdateDTO;
import com.it.jobfinder.dtos.LoginDTO;
import com.it.jobfinder.entities.AdminDetails;
import com.it.jobfinder.entities.EmployerDetails;
import com.it.jobfinder.entities.User;
import com.it.jobfinder.entities.UserRole;
import com.it.jobfinder.exceptions.IncorrectCredentialsException;
import com.it.jobfinder.exceptions.UserDuplicateException;
import com.it.jobfinder.repositories.DetailsRepository;
import com.it.jobfinder.repositories.UserRepository;
import com.it.jobfinder.security.CustomUserDetailsService;
import com.it.jobfinder.services.EmployerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmployerServiceUnitTests {

    @Mock
    Principal principal;

    @Mock
    UserRepository userRepository;

    @Mock
    DetailsRepository detailsRepository;

    @Mock
    CustomUserDetailsService userDetailsService;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    EmployerService employerService;


    UUID employerId = UUID.randomUUID();
    EmployerDetails employerDetails = new EmployerDetails("Employer", "");
    User employerUser = new User(employerId, "employer", "password", "employer@mail.com", UserRole.EMPLOYER, employerDetails);

    UUID otherEmployerId = UUID.randomUUID();
    EmployerDetails otherEmployerDetails = new EmployerDetails("OtherEmployer", "");
    User otherEmployerUser = new User(otherEmployerId, "oemployer", "password", "oemployer@mail.com", UserRole.EMPLOYER, otherEmployerDetails);

    AdminDetails adminDetails = new AdminDetails("Ad", "Min");
    User adminUser = new User("admin", "admin", "admin@admin.com", UserRole.ADMIN, adminDetails);


    @BeforeEach
    void beforeEach(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addEmployerTest(){
        when(userRepository.findByUsername("employer")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("employer@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        EmployerRegistrationDTO dto = EmployerRegistrationDTO.builder()
                .name("Employer").username("employer").email("employer@mail.com").password("password").description("")
                .build();

        User createdUser = employerService.addEmployer(dto);

        Assertions.assertEquals(employerUser.getUsername(), createdUser.getUsername());
        Assertions.assertEquals(employerUser.getEmail(), createdUser.getEmail());
        Assertions.assertTrue(passwordEncoder.matches(employerUser.getPassword(), createdUser.getPassword()));
        Assertions.assertEquals(employerUser.getRole(), createdUser.getRole());
        Assertions.assertEquals(((EmployerDetails) employerUser.getDetails()).getName(), ((EmployerDetails) createdUser.getDetails()).getName());
        Assertions.assertEquals(((EmployerDetails) employerUser.getDetails()).getDescription(), ((EmployerDetails) createdUser.getDetails()).getDescription());
    }

    @Test
    void addEmployerUsernameDuplicateTest() {
        when(userRepository.findByUsername("employer")).thenReturn(Optional.of(employerUser));

        EmployerRegistrationDTO dto = EmployerRegistrationDTO.builder()
                .name("Employer").username("employer").email("employer@mail.com").password("password").description("")
                .build();

        Assertions.assertThrows(UserDuplicateException.class, () -> employerService.addEmployer(dto));
    }

    @Test
    void addEmployerEmailDuplicateTest() {
        when(userRepository.findByUsername("employer")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("employer@mail.com")).thenReturn(Optional.of(employerUser));

        EmployerRegistrationDTO dto = EmployerRegistrationDTO.builder()
                .name("Employer").username("employer").email("employer@mail.com").password("password").description("")
                .build();

        Assertions.assertThrows(UserDuplicateException.class, () -> employerService.addEmployer(dto));
    }


    @Test
    void deleteEmployer(){
        when(userRepository.findByUsername("employer")).thenReturn(Optional.of(employerUser));
        when(principal.getName()).thenReturn("employer");
        when(userDetailsService.loadUserByUsername("employer")).thenReturn(employerUser);
        when(passwordEncoder.matches("password", "password")).thenReturn(true);

        LoginDTO dto = LoginDTO.builder()
                .username("employer").password("password")
                .build();

        employerService.deleteEmployer(dto, principal);

        verify(userRepository).delete(employerUser);
    }

    @Test
    void deleteEmployerByAdminTest(){
        when(userRepository.findByUsername("employer")).thenReturn(Optional.of(employerUser));
        when(principal.getName()).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(adminUser);
        when(passwordEncoder.matches("password", "password")).thenReturn(true);

        LoginDTO dto = LoginDTO.builder()
                .username("employer").password("password")
                .build();

        employerService.deleteEmployer(dto, principal);

        verify(userRepository).delete(employerUser);
    }

    @Test
    void deleteEmployerUsernameNotFoundTest(){
        when(userRepository.findByUsername("employer")).thenReturn(Optional.empty());

        LoginDTO dto = LoginDTO.builder()
                .username("employer").password("password")
                .build();

        Assertions.assertThrows(UsernameNotFoundException.class, () -> employerService.deleteEmployer(dto, principal));
    }

    @Test
    void deleteEmployerBadCredentialsTest(){
        when(userRepository.findByUsername("employer")).thenReturn(Optional.of(employerUser));
        when(principal.getName()).thenReturn("employer");
        when(userDetailsService.loadUserByUsername("employer")).thenReturn(employerUser);
        when(passwordEncoder.matches("password", "password")).thenReturn(false);

        LoginDTO dto = LoginDTO.builder()
                .username("employer").password("password")
                .build();

        Assertions.assertThrows(BadCredentialsException.class, () -> employerService.deleteEmployer(dto, principal));
    }

    @Test
    void getAllTest(){
        when(userRepository.findByRole(UserRole.EMPLOYER)).thenReturn(List.of(employerUser, otherEmployerUser));

        Assertions.assertIterableEquals(List.of(employerUser, otherEmployerUser), employerService.getAll());
    }

    @Test
    void getTest(){
        when(userRepository.findByRoleAndUsername(UserRole.EMPLOYER, "employer")).thenReturn(Optional.of(employerUser));

        Assertions.assertEquals(employerUser, employerService.get("employer"));
    }

    @Test
    void getUsernameNotFoundTest(){
        when(userRepository.findByRoleAndUsername(UserRole.EMPLOYER, "employer")).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> employerService.get("employer"));
    }

    @Test
    void updateTest(){
        when(userRepository.getReferenceById(employerId)).thenReturn(employerUser);
        when(principal.getName()).thenReturn("employer");
        when(userDetailsService.loadUserByUsername("employer")).thenReturn(employerUser);
        when(passwordEncoder.encode("nPassword")).thenReturn("encodednPassword");
        when(passwordEncoder.matches("nPassword", "encodednPassword")).thenReturn(true);
        when(userRepository.save(employerUser)).thenReturn(employerUser);

        EmployerUpdateDTO dto = EmployerUpdateDTO.builder()
                .id(employerId).username("nemployer").email("nemployer@mail.com").password("nPassword").name("nEmployer").description("hello")
                .build();

        User updatedUser = employerService.update(dto, principal);

        verify(userRepository).save(employerUser);

        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(dto.getId(), updatedUser.getId());
        Assertions.assertEquals(dto.getUsername(), updatedUser.getUsername());
        Assertions.assertEquals(dto.getEmail(), updatedUser.getEmail());
        Assertions.assertTrue(passwordEncoder.matches(dto.getPassword(), updatedUser.getPassword()));
        Assertions.assertEquals(dto.getName(), ((EmployerDetails) updatedUser.getDetails()).getName());
        Assertions.assertEquals(dto.getDescription(), ((EmployerDetails) updatedUser.getDetails()).getDescription());
    }

    @Test
    void updateByAdminTest(){
        when(userRepository.getReferenceById(employerId)).thenReturn(employerUser);
        when(principal.getName()).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(adminUser);
        when(passwordEncoder.encode("nPassword")).thenReturn("encodednPassword");
        when(passwordEncoder.matches("nPassword", "encodednPassword")).thenReturn(true);
        when(userRepository.save(employerUser)).thenReturn(employerUser);

        EmployerUpdateDTO dto = EmployerUpdateDTO.builder()
                .id(employerId).username("nemployer").email("nemployer@mail.com").password("nPassword").name("nEmployer").description("hello")
                .build();

        User updatedUser = employerService.update(dto, principal);

        verify(userRepository).save(employerUser);

        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(dto.getId(), updatedUser.getId());
        Assertions.assertEquals(dto.getUsername(), updatedUser.getUsername());
        Assertions.assertEquals(dto.getEmail(), updatedUser.getEmail());
        Assertions.assertTrue(passwordEncoder.matches(dto.getPassword(), updatedUser.getPassword()));
        Assertions.assertEquals(dto.getName(), ((EmployerDetails) updatedUser.getDetails()).getName());
        Assertions.assertEquals(dto.getDescription(), ((EmployerDetails) updatedUser.getDetails()).getDescription());
    }

    @Test
    void updateNotAllValuesTest(){
        when(userRepository.getReferenceById(employerId)).thenReturn(employerUser);
        when(principal.getName()).thenReturn("employer");
        when(userDetailsService.loadUserByUsername("employer")).thenReturn(employerUser);
        when(passwordEncoder.matches("password", "password")).thenReturn(true);
        when(userRepository.save(employerUser)).thenReturn(employerUser);

        EmployerUpdateDTO dto = EmployerUpdateDTO.builder()
                .id(employerId).username("nemployer").email("nemployer@mail.com").name("nEmployer")
                .build();

        User updatedUser = employerService.update(dto, principal);

        verify(userRepository).save(employerUser);

        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(dto.getId(), updatedUser.getId());
        Assertions.assertEquals(dto.getUsername(), updatedUser.getUsername());
        Assertions.assertEquals(dto.getEmail(), updatedUser.getEmail());
        Assertions.assertTrue(passwordEncoder.matches("password", updatedUser.getPassword()));
        Assertions.assertEquals(dto.getName(), ((EmployerDetails) updatedUser.getDetails()).getName());
        Assertions.assertEquals("", ((EmployerDetails) updatedUser.getDetails()).getDescription());
    }

    @Test
    void updateIncorrectCredentialsTest(){
        when(userRepository.getReferenceById(employerId)).thenReturn(employerUser);
        when(principal.getName()).thenReturn("oemployer");
        when(userDetailsService.loadUserByUsername("oemployer")).thenReturn(otherEmployerUser);

        EmployerUpdateDTO dto = EmployerUpdateDTO.builder()
                .id(employerId).username("nemployer").email("nemployer@mail.com").password("nPassword").name("nEmployer").description("hello")
                .build();

        Assertions.assertThrows(IncorrectCredentialsException.class, () -> employerService.update(dto, principal));
    }
}
