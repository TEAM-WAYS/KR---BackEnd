package com.ways.krbackend.service;

import com.ways.krbackend.model.Application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ApplicasionService {
Optional<Application> postApplication(Application application);

    List<Application> getApplications();
}

