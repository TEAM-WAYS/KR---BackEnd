package com.ways.krbackend.DTO;

public class ApplicationPointsII {
    private int appId;
    private int points;

    public ApplicationPointsII(int appId, int points) {
        this.appId = appId;
        this.points = points;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
