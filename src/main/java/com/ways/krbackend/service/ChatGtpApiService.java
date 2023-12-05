package com.ways.krbackend.service;

import com.ways.krbackend.DTO.ApplicationPointsII;
import com.ways.krbackend.model.Application;
import com.ways.krbackend.model.email;

import java.util.List;
import java.util.Optional;

public interface ChatGtpApiService {


    List<Application> validateApplicationsQuick(String inquiry, int noOfApplications);

    Optional<List<ApplicationPointsII>> validateApplicationsLong(String inquiry, int noOfApplications);

    Optional<Application> applicationFromEmail(email email);
}
