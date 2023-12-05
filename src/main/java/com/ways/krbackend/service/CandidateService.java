package com.ways.krbackend.service;

import com.ways.krbackend.model.Candidate;
import com.ways.krbackend.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface CandidateService {

    List<Candidate> getAllCandidates();

    Optional<Candidate> getCandidateById(Long candidateId);

    List<Candidate> getFavoriteCandidates();

    void addToFavorites(Long candidateId);

    Optional<Candidate> findById(Long id);

    void save(Candidate candidate);

    List<Candidate> getEmployeeCandidates();
    List<Candidate> getEmployeesWithHiredDate();

    void moveCandidateToEmployee(Long candidateId);
}