package com.ways.krbackend.conroller;

import com.ways.krbackend.model.JobAdvertisement;
import com.ways.krbackend.service.JobAdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/jobadvertisement")
public class JobAdvertisementController {

    @Autowired
    private JobAdvertisementService jobAdvertisementService;

    @PostMapping("/create")
    public ResponseEntity<?> createJobAdvertisement(@RequestBody JobAdvertisement jobAdvertisement) {
        Optional<JobAdvertisement> response = jobAdvertisementService.createJobAdvertisement(jobAdvertisement);
        if (response.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body("Job advertisement created successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create job advertisement");
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<JobAdvertisement>> getAllJobAdvertisements() {
        List<JobAdvertisement> jobAdvertisements = jobAdvertisementService.getAllJobAdvertisements();
        return ResponseEntity.ok(jobAdvertisements);
    }
}

