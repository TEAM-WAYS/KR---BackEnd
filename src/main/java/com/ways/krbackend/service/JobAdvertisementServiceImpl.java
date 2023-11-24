package com.ways.krbackend.service;

import com.ways.krbackend.model.JobAdvertisement;
import com.ways.krbackend.repository.JobAdvertisementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JobAdvertisementServiceImpl implements JobAdvertisementService {

    @Autowired
    private JobAdvertisementRepository jobAdvertisementRepository;

    @Override
    public Optional<JobAdvertisement> createJobAdvertisement(JobAdvertisement jobAdvertisement) {
        return Optional.of(jobAdvertisementRepository.save(jobAdvertisement));
    }

    @Override
    public List<JobAdvertisement> getAllJobAdvertisements() {
        return null;
    }
}
