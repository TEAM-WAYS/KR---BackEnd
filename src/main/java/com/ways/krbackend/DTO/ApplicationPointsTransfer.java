package com.ways.krbackend.DTO;

public class ApplicationPointsTransfer {
    private int id;
    private int points;
    private String reason;


    public ApplicationPointsTransfer(int id, int points, String reason) {
        this.id = id;
        this.points = points;
        this.reason = reason;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
