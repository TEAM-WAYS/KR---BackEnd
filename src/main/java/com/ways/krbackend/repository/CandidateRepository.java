package com.ways.krbackend.repository;

import com.ways.krbackend.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    Optional<Candidate> findById(Long id);

    List<Candidate> findByIsFavoriteTrue();
}
