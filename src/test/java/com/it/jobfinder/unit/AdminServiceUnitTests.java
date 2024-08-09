package com.it.jobfinder.unit;

import com.it.jobfinder.dtos.AdminRegistrationDTO;
import com.it.jobfinder.dtos.AdminUpdateDTO;
import com.it.jobfinder.dtos.LoginDTO;
import com.it.jobfinder.entities.AdminDetails;
import com.it.jobfinder.entities.User;
import com.it.jobfinder.entities.UserRole;
import com.it.jobfinder.exceptions.UserDuplicateException;
import com.it.jobfinder.repositories.DetailsRepository;
import com.it.jobfinder.repositories.UserRepository;
import com.it.jobfinder.services.AdminService;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminServiceUnitTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private DetailsRepository detailsRepository;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void beforeEach(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addAdminTest(){
        AdminDetails adminDetails = new AdminDetails("Ad", "Min");
        User adminUser = new User("admin", "admin", "admin@admin.com", UserRole.ADMIN, adminDetails);

        when(userRepository.save(adminUser)).thenReturn(adminUser);
        when(passwordEncoder.encode("admin")).thenReturn("encodedAdmin");
        when(passwordEncoder.matches("admin", "encodedAdmin")).thenReturn(true);

        AdminRegistrationDTO dto = AdminRegistrationDTO.builder()
                .username("admin").password("admin").email("admin@admin.com").name("Ad").surname("Min")
                .build();

        User createdUser = adminService.addAdmin(dto);

        Assertions.assertNotNull(createdUser);
        Assertions.assertEquals(adminUser.getUsername(), createdUser.getUsername());
        Assertions.assertTrue(passwordEncoder.matches(adminUser.getPassword(), createdUser.getPassword()));
        Assertions.assertEquals(adminUser.getEmail(), createdUser.getEmail());
        Assertions.assertEquals(adminUser.getRole().name(), createdUser.getRole().name());
        Assertions.assertEquals(adminDetails.getName(), ((AdminDetails) createdUser.getDetails()).getName());
        Assertions.assertEquals(adminDetails.getSurname(), ((AdminDetails) createdUser.getDetails()).getSurname());
    }

    @Test
    void addAdminUsernameTakenTest(){
        AdminDetails adminDetails = new AdminDetails("Ad", "Min");
        User adminUser = new User("admin", "admin", "admin@admin.com", UserRole.ADMIN, adminDetails);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

        AdminRegistrationDTO dto = AdminRegistrationDTO.builder()
                .username("admin").password("admin").email("admin@admin.com").name("Ad").surname("Min")
                .build();

        Assertions.assertThrows(UserDuplicateException.class, () -> adminService.addAdmin(dto));
    }

    @Test
    void addAdminEmailTakenTest(){
        AdminDetails adminDetails = new AdminDetails("Ad", "Min");
        User adminUser = new User("admin", "admin", "admin@admin.com", UserRole.ADMIN, adminDetails);

        when(userRepository.findByEmail("admin@admin.com")).thenReturn(Optional.of(adminUser));

        AdminRegistrationDTO dto = AdminRegistrationDTO.builder()
                .username("admin1").password("admin").email("admin@admin.com").name("Ad").surname("Min")
                .build();

        Assertions.assertThrows(UserDuplicateException.class, () -> adminService.addAdmin(dto));
    }

    @Test
    void deleteAdminTest(){
        AdminDetails adminDetails = new AdminDetails("Ad", "Min");
        User adminUser = new User("admin", "admin", "admin@admin.com", UserRole.ADMIN, adminDetails);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches("admin", "admin")).thenReturn(true);

        LoginDTO dto = LoginDTO.builder()
                .username("admin").password("admin")
                .build();

        adminService.deleteAdmin(dto);

        verify(userRepository).delete(adminUser);
    }

    @Test
    void deleteAdminUsernameNotFoundTest(){
        when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());

        LoginDTO dto = LoginDTO.builder()
                .username("admin").password("admin")
                .build();

        Assertions.assertThrows(UsernameNotFoundException.class, () -> adminService.deleteAdmin(dto));
    }

    @Test
    void deleteAdminBadCredentialsTest(){
        AdminDetails adminDetails = new AdminDetails("Ad", "Min");
        User adminUser = new User("admin", "admin", "admin@admin.com", UserRole.ADMIN, adminDetails);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches("admin", "admin")).thenReturn(false);

        LoginDTO dto = LoginDTO.builder()
                .username("admin").password("admin")
                .build();

        Assertions.assertThrows(BadCredentialsException.class, () -> adminService.deleteAdmin(dto));
    }

    @Test
    void getAllTest(){
        AdminDetails adminDetails1 = new AdminDetails("Ad", "Min");
        User adminUser1 = new User("admin1", "admin", "admin1@admin.com", UserRole.ADMIN, adminDetails1);

        AdminDetails adminDetails2 = new AdminDetails("Ad", "Min");
        User adminUser2 = new User("admin2", "admin", "admin2@admin.com", UserRole.ADMIN, adminDetails2);

        when(userRepository.findByRole(UserRole.ADMIN)).thenReturn(List.of(adminUser1, adminUser2));

        List<User> admins = adminService.getAll();

        Assertions.assertNotEquals(0, admins.size());

        Assertions.assertEquals(adminUser1.getUsername(), admins.get(0).getUsername());
        Assertions.assertEquals(adminUser1.getPassword(), admins.get(0).getPassword());
        Assertions.assertEquals(adminUser1.getEmail(), admins.get(0).getEmail());
        Assertions.assertEquals(adminUser1.getRole().name(), admins.get(0).getRole().name());
        Assertions.assertEquals(adminDetails1.getName(), ((AdminDetails) admins.get(0).getDetails()).getName());
        Assertions.assertEquals(adminDetails1.getSurname(), ((AdminDetails) admins.get(0).getDetails()).getSurname());

        Assertions.assertEquals(adminUser2.getUsername(), admins.get(1).getUsername());
        Assertions.assertEquals(adminUser2.getPassword(), admins.get(1).getPassword());
        Assertions.assertEquals(adminUser2.getEmail(), admins.get(1).getEmail());
        Assertions.assertEquals(adminUser2.getRole().name(), admins.get(1).getRole().name());
        Assertions.assertEquals(adminDetails2.getName(), ((AdminDetails) admins.get(1).getDetails()).getName());
        Assertions.assertEquals(adminDetails2.getSurname(), ((AdminDetails) admins.get(1).getDetails()).getSurname());
    }

    @Test
    void getTest(){
        AdminDetails adminDetails = new AdminDetails("Ad", "Min");
        User adminUser = new User("admin", "admin", "admin@admin.com", UserRole.ADMIN, adminDetails);

        when(userRepository.findByRoleAndUsername(UserRole.ADMIN, "admin")).thenReturn(Optional.of(adminUser));

        User receivedAdmin = adminService.get("admin");

        Assertions.assertEquals(adminUser.getUsername(), receivedAdmin.getUsername());
        Assertions.assertEquals(adminUser.getPassword(), receivedAdmin.getPassword());
        Assertions.assertEquals(adminUser.getEmail(), receivedAdmin.getEmail());
        Assertions.assertEquals(adminUser.getRole().name(), receivedAdmin.getRole().name());
        Assertions.assertEquals(adminDetails.getName(), ((AdminDetails) receivedAdmin.getDetails()).getName());
        Assertions.assertEquals(adminDetails.getSurname(), ((AdminDetails) receivedAdmin.getDetails()).getSurname());
    }

    @Test
    void getNotFoundTest(){
        when(userRepository.findByRoleAndUsername(UserRole.ADMIN, "admin")).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> adminService.get("admin"));
    }

    @Test
    void updateTest() {
        AdminDetails adminDetails = new AdminDetails("Ad", "Min");
        User adminUser = new User("admin", "admin", "admin@admin.com", UserRole.ADMIN, adminDetails);

        UUID uuid = UUID.randomUUID();

        when(userRepository.getReferenceById(uuid)).thenReturn(adminUser);
        when(passwordEncoder.encode("admin1")).thenReturn("encodedAdmin1");
        when(passwordEncoder.matches("admin1", "encodedAdmin1")).thenReturn(true);
        when(userRepository.save(adminUser)).thenReturn(adminUser);

        AdminUpdateDTO dto = AdminUpdateDTO.builder()
                .id(uuid).username("admin1").password("admin1").email("admin1@admin.com").name("Add").surname("Minn")
                .build();

        User updatedAdmin = adminService.update(dto);

        Assertions.assertEquals(dto.getUsername(), updatedAdmin.getUsername());
        Assertions.assertTrue(passwordEncoder.matches(dto.getPassword(), updatedAdmin.getPassword()));
        Assertions.assertEquals(dto.getEmail(), updatedAdmin.getEmail());
        Assertions.assertEquals(adminUser.getRole().name(), updatedAdmin.getRole().name());
        Assertions.assertEquals(dto.getName(), ((AdminDetails) updatedAdmin.getDetails()).getName());
        Assertions.assertEquals(dto.getSurname(), ((AdminDetails) updatedAdmin.getDetails()).getSurname());
    }

    @Test
    void updateNotAllValuesTest(){
        AdminDetails adminDetails = new AdminDetails("Ad", "Min");
        User adminUser = new User("admin", "admin", "admin@admin.com", UserRole.ADMIN, adminDetails);

        UUID uuid = UUID.randomUUID();

        when(userRepository.getReferenceById(uuid)).thenReturn(adminUser);
        when(userRepository.save(adminUser)).thenReturn(adminUser);

        AdminUpdateDTO dto = AdminUpdateDTO.builder()
                .id(uuid).username("admin1").email("admin1@admin.com").name("Add")
                .build();

        User updatedAdmin = adminService.update(dto);

        Assertions.assertEquals(dto.getUsername(), updatedAdmin.getUsername());
        Assertions.assertNotNull(updatedAdmin.getPassword());
        Assertions.assertEquals("admin", updatedAdmin.getPassword());
        Assertions.assertEquals(dto.getEmail(), updatedAdmin.getEmail());
        Assertions.assertEquals(adminUser.getRole().name(), updatedAdmin.getRole().name());
        Assertions.assertEquals(dto.getName(), ((AdminDetails) updatedAdmin.getDetails()).getName());
        Assertions.assertNotNull(((AdminDetails) updatedAdmin.getDetails()).getSurname());
        Assertions.assertEquals("Min", ((AdminDetails) updatedAdmin.getDetails()).getSurname());
    }

}
