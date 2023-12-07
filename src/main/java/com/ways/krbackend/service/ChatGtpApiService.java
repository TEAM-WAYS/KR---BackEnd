package com.ways.krbackend.service;

import com.ways.krbackend.DTO.ApplicationPointsII;
import com.ways.krbackend.DTO.Choice;
import com.ways.krbackend.model.Application;
import com.ways.krbackend.model.Email;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface ChatGtpApiService {
    public List<Choice> chatWithGPT(String message);
/*
    List<Application> validateApplicationsQuick(String inquiry, int noOfApplications);

    Optional<List<ApplicationPointsII>> validateApplicationsLong(String inquiry, int noOfApplications);

    Optional<Application> applicationFromEmail(Email email);

    ResponseEntity<Object> turnEmailIntoApplication();

 */
}
