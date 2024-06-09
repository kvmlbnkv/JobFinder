package com.it.jobfinder.repositories;

import com.it.jobfinder.entities.User;
import com.it.jobfinder.entities.UserRole;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
    Optional<User> findByRoleAndUsername(UserRole role, String username);
}
