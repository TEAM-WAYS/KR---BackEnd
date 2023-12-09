package com.ways.krbackend.DTO;

import com.ways.krbackend.model.Application;

public class ApplicationPointsII {
    private int applicationId;
    private int points;
    private String reason;
    private Application application;

    public ApplicationPointsII(int appId, int points) {
        this.applicationId = appId;
        this.points = points;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }
}
