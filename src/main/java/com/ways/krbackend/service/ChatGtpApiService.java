package com.ways.krbackend.service;

import com.ways.krbackend.DTO.ApplicationPoints;

import java.util.LinkedList;
import java.util.Optional;

public interface ChatGtpApiService {
    Optional<LinkedList<ApplicationPoints>> validateApplicationsQuiq(String inquiry);

    Optional<LinkedList<ApplicationPoints>> validateApplicationsLong(String inquiry);
}
