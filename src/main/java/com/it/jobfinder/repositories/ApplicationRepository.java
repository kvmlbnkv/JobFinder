package com.it.jobfinder.repositories;

import com.it.jobfinder.entities.Application;
import com.it.jobfinder.entities.Job;
import com.it.jobfinder.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    List<Application> findByUser(User user);
    List<Application> findByJob(Job job);
    Optional<Application> findByUserAndJob(User user, Job job);
}
