package com.ways.krbackend.service;

import com.ways.krbackend.DTO.ApplicationPoints;
import com.ways.krbackend.DTO.ApplicationPointsII;
import com.ways.krbackend.model.Application;
import com.ways.krbackend.model.email;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public interface ChatGtpApiService {
    Optional<LinkedList<ApplicationPoints>> validateApplicationsQuick(String inquiry);

    Optional<List<ApplicationPointsII>> validateApplicationsLong(String inquiry);

    Optional<Application> applicationFromEmail(email email);
}
