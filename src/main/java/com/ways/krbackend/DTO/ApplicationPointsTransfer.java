package com.ways.krbackend.DTO;

import com.ways.krbackend.model.Application;

public class ApplicationPointsTransfer {
    private int applicationId;
    private int points;
    private String reason;


    public ApplicationPointsTransfer(int applicationId, int points, String reason) {
        this.applicationId = applicationId;
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


}
