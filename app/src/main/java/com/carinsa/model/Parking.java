package com.carinsa.model;

import android.location.Location;

public class Parking {
    private boolean isSpot=false;
    private String pkgid;
    private String name;
    private double lat;
    private double lng;
    private int availableSpots;

    private int type;
    private boolean free;
    private int capacity;
    private int nbContributor;

    private Avis avis;



    public Parking(String pkgid,String name, double lat, double lng, int availableSpots){
        this.pkgid=pkgid;
        this.name=name;
        this.lat=lat;
        this.lng=lng;
        this.availableSpots=availableSpots;
    }

    public Parking(String spotid,String name, double lat, double lng, int type, boolean free, int capacity, int nbContributor){
        this.pkgid=spotid;
        this.name=name;
        this.lat=lat;
        this.lng=lng;
        this.type=type;
        this.free=free;
        this.capacity=capacity;
        this.isSpot=true;
        this.nbContributor=nbContributor;

    }

    public boolean isSpot() {
        return isSpot;
    }
    public void setSpot(boolean spot) {
        isSpot = spot;
    }


    public Avis getAvis() {
        return avis;
    }

    public String getPkgid() { return pkgid; }
    public String getName() {
        return name;
    }
    public double getLat() {
        return lat;
    }
    public double getLng() {
        return lng;
    }
    public int getAvailableSpots() {
        return availableSpots;
    }

    public boolean isFree() {
        return free;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getType() {
        return type;
    }

    public int getNbContributor() {
        return nbContributor;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setNbContributor(int nbContributor) {
        this.nbContributor = nbContributor;
    }

    public void setAvis(Avis avis) {
        this.avis = avis;
    }

    public void setPkgid(String pkgid) { this.pkgid = pkgid; }
    public void setName(String name) {
        this.name = name;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }
    public void setLng(double lng) {
        this.lng = lng;
    }
    public void setAvailableSpots(int availableSpots) {
        this.availableSpots = availableSpots;
    }

    public String toString() {
        String ret=name+" "+lat+" "+lng+" "+availableSpots;
        return ret;
    }

    public boolean isFarFrom(double latitude, double longitude, float distance) {
        Location parkingLoc = new Location("");
        parkingLoc.setLatitude(this.lat);
        parkingLoc.setLongitude(this.lng);

        Location loc = new Location("");
        loc.setLatitude(latitude);
        loc.setLongitude(longitude);

        float distanceInMeters = parkingLoc.distanceTo(loc);
        if(distanceInMeters > distance)
        {
            return true;
        }
        return false;
    }
}
