package com.ways.krbackend.service;

import com.ways.krbackend.model.Candidate;
import com.ways.krbackend.repository.CandidateRepository;
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
        public Optional<Candidate> findById(Long id) {
        return Optional.empty();
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
        public void moveCandidateToEmployee(Long candidateId) {
        Optional<Candidate> optionalCandidate = candidateRepository.findById(candidateId);

        optionalCandidate.ifPresent(candidate -> {
            candidate.setIsEmployee(true);
            candidate.setHiredDate(LocalDate.now()); // We assume the hired date is same as the date the user is moving the candidate to employee list.
            candidateRepository.save(candidate);
        });
    }
}



