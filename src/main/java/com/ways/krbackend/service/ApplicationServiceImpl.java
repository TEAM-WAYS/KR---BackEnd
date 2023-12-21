package com.ways.krbackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ways.krbackend.DTO.Choice;
import com.ways.krbackend.model.Application;
import com.ways.krbackend.model.Email;
import com.ways.krbackend.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private EmailService emailService;





    @Override
    public Optional<Application> postApplication(Application application) {
        return Optional.of(applicationRepository.save(application));
    }

    @Override
    public Optional<Application> getApplicationById(int appId) {
        return Optional.of(applicationRepository.getApplicationById(appId));
    }

    @Override
    public List<Application> getApplications() {
        return applicationRepository.findAll();
    }




}
