package com.ways.krbackend.repository;

import com.ways.krbackend.model.email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface emailRepository extends JpaRepository<email, Long> {

    Optional<email> findBySubject(String subject);
}
