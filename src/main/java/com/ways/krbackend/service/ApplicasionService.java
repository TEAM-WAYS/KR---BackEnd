package com.ways.krbackend.service;

import com.ways.krbackend.model.Application;

import java.util.Optional;

public interface ApplicasionService {
Optional<Application> postApplication(Application application);
}
