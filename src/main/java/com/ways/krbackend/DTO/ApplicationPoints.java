package com.ways.krbackend.DTO;

import com.ways.krbackend.model.Application;

public class ApplicationPoints {
    private Application application;
    private Integer points;

    public ApplicationPoints(Application application, Integer points) {
        this.application = application;
        this.points = points;
    }

    public Application getApplication() {
        return application;
    }

    public Integer getPoints() {
        return points;
    }
}
