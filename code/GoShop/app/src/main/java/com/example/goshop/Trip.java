package com.example.goshop;

import com.google.android.gms.maps.model.LatLng;

public class Trip {

    private LatLng pickupPoint;
    private LatLng destination;
    private String timeSlot;
    private String date;
    private String carType;

    public Trip() {
    }

    public Trip(LatLng pickupPoint, LatLng destination, String timeSlot, String date, String carType) {
        this.pickupPoint = pickupPoint;
        this.destination = destination;
        this.timeSlot = timeSlot;
        this.date = date;
        this.carType = carType;
    }

    public LatLng getPickupPoint() {
        return pickupPoint;
    }

    public void setPickupPoint(LatLng pickupPoint) {
        this.pickupPoint = pickupPoint;
    }

    public LatLng getDestination() {
        return destination;
    }

    public void setDestination(LatLng destination) {
        this.destination = destination;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }
}
