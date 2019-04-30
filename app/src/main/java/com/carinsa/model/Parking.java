package com.carinsa.model;

public class Parking {
    private String name;
    private double lat;
    private double lng;
    private int state;
    private int availableSpots;
    public Parking(String name, double lat, double lng, int state, int availableSpots){
        this.name=name;
        this.lat=lat;
        this.lng=lng;
        this.state=state;
        this.availableSpots=availableSpots;
    }

    public String getName() {
        return name;
    }
    public double getLat() {
        return lat;
    }
    public double getLng() {
        return lng;
    }
    public int getState() {
        return state;
    }
    public int getAvailableSpots() {
        return availableSpots;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }
    public void setState(int state) {
        this.state = state;
    }
    public void setAvailableSpots(int availableSpots) {
        this.availableSpots = availableSpots;
    }

    public String toString() {
        String ret=name+" "+lat+" "+lng+" "+state+" "+availableSpots;
        return ret;
    }
}
