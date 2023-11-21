package com.ways.krbackend.service;

import com.ways.krbackend.model.JobAdvertisement;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JobAdvertisementService {


    public Optional<JobAdvertisement> createJobAdvertisement(JobAdvertisement jobAdvertisement) {

        return Optional.of(jobAdvertisement);
    }
}
