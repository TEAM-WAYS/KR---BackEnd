package com.ways.krbackend.service;

import com.ways.krbackend.model.Application;
import com.ways.krbackend.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ApplicationServiceImpl implements ApplicasionService{
    @Autowired
    ApplicationRepository applicationRepository;

    @Override
    public Optional<Application> postApplication(Application application) {
        return Optional.of(applicationRepository.save(application));
    }
}
