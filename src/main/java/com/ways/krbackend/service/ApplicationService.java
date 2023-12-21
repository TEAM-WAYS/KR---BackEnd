package com.ways.krbackend.service;

import com.ways.krbackend.model.Application;

import java.util.List;
import java.util.Optional;

public interface ApplicationService {
    public List<Application> getApplications();
    public Optional<Application> postApplication(Application application);

    public Optional<Application> getApplicationById(int appId);

}
