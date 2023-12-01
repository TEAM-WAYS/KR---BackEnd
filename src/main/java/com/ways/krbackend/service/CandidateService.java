package com.ways.krbackend.service;

import com.ways.krbackend.model.Candidate;
import com.ways.krbackend.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    public void saveCandidate(Candidate candidate) {
        candidateRepository.save(candidate);
    }
}


