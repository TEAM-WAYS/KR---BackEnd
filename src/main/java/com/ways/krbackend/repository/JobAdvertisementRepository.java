package com.ways.krbackend.repository;

import com.ways.krbackend.model.JobAdvertisement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobAdvertisementRepository extends JpaRepository<JobAdvertisement, Long> {
}