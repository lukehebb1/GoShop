package com.example.goshop;

import com.google.android.gms.maps.model.LatLng;

public class Availability {

    private LatLng startPoint;
    private String date;
    private String timeSlot;
    private String carType;
    private String travelDist;

    public Availability() {
    }

    public Availability(LatLng startPoint, String date, String timeSlot, String carType, String travelDist) {
        this.startPoint = startPoint;
        this.date = date;
        this.timeSlot = timeSlot;
        this.carType = carType;
        this.travelDist = travelDist;
    }

    public LatLng getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(LatLng startPoint) {
        this.startPoint = startPoint;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getTravelDist() {
        return travelDist;
    }

    public void setTravelDist(String travelDist) {
        this.travelDist = travelDist;
    }
}
