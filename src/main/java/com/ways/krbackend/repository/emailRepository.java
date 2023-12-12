package com.ways.krbackend.repository;

import com.ways.krbackend.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface emailRepository extends JpaRepository<Email, Long> {

    Optional<Email> findBySubject(String subject);

    String getContentById(Long id);
}
