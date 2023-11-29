package com.ways.krbackend.service;

import com.ways.krbackend.DTO.ApplicationPoints;
import com.ways.krbackend.model.Application;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ChatGtpApiService {
    Optional<LinkedList<ApplicationPoints>> validateApplications(String inquiry);
}
