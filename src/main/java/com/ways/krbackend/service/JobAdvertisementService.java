package com.ways.krbackend.service;

import com.ways.krbackend.model.JobAdvertisement;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface JobAdvertisementService {


     default Optional<JobAdvertisement> createJobAdvertisement(JobAdvertisement jobAdvertisement) {

        return Optional.of(jobAdvertisement);
    }

    List<JobAdvertisement> getAllJobAdvertisements();
}
