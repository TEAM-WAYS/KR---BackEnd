package com.ways.krbackend.service;

import com.ways.krbackend.DTO.ApplicationPointsTransfer;
import com.ways.krbackend.model.Application;
import com.ways.krbackend.model.email;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface ChatGtpApiService {


    List<Application> validateApplicationsQuick(String inquiry, int noOfApplications);

    Optional<String> validateApplicationsLong(String inquiry, int noOfApplications);

    Optional<Application> applicationFromEmail(email email);

    ResponseEntity<Object> turnEmailIntoApplication();
}
