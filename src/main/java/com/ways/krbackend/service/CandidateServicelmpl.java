package com.ways.krbackend.service;

import com.ways.krbackend.model.Candidate;
import com.ways.krbackend.repository.CandidateRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CandidateServicelmpl implements CandidateService {

        @Autowired
        private CandidateRepository candidateRepository;
        @Override
        public List<Candidate> getAllCandidates() {
            return candidateRepository.findAll();
        }
        @Override
        public Optional<Candidate> getCandidateById(Long candidateId) {
            return candidateRepository.findById(candidateId);
        }
        @Override
        public List<Candidate> getFavoriteCandidates() {
            return candidateRepository.findByIsFavoriteTrue();
        }
        @Override
        public void addToFavorites(Long candidateId) {
            Optional<Candidate> optionalCandidate = candidateRepository.findById(candidateId);

            optionalCandidate.ifPresent(candidate -> {
                candidate.setFavorite(true);
                candidateRepository.save(candidate);
            });
        }

        @Override
        public void save(Candidate candidate) {

    }
        @Override
        public List<Candidate> getEmployeeCandidates() {
        return candidateRepository.findByIsEmployeeTrue();
    }

        @Override
        public List<Candidate> getEmployeesWithHiredDate() {
        return candidateRepository.findByHiredDateNotNull();
    }
    @Override
    public void deleteCandidate(Long candidateId) {
        candidateRepository.deleteById(candidateId);
    }
    @Override
    @Transactional  // Ensure both operations (move and delete) are part of the same transaction
    public void moveCandidateToEmployee(Long candidateId) {
        Optional<Candidate> optionalCandidate = candidateRepository.findById(candidateId);

        optionalCandidate.ifPresent(candidate -> {
            candidate.setIsEmployee(true);
            candidate.setHiredDate(LocalDate.now());
            candidateRepository.save(candidate);

            // Delete the candidate from the "Candidate list"
            candidateRepository.deleteById(candidateId);
        });
    }

}



