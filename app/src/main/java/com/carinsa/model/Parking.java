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
    private boolean byUser;

    private Avis avis;



    public Parking(String pkgid,String name, double lat, double lng, int availableSpots){
        this.pkgid=pkgid;
        this.name=name;
        this.lat=lat;
        this.lng=lng;
        this.availableSpots=availableSpots;
    }

    public Parking(String spotid,String name, double lat, double lng, int type, boolean free, int capacity, int nbContributor, boolean byUser){
        this.pkgid=spotid;
        this.name=name;
        this.lat=lat;
        this.lng=lng;
        this.type=type;
        this.free=free;
        this.capacity=capacity;
        this.isSpot=true;
        this.nbContributor=nbContributor;
        this.byUser=byUser;

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

    public String isFree() {
        if(free){
            return "Gratuit";
        }else {
            return "Payant";
        }
    }

    public String getCapacity() {
        if(capacity == 0){
            return "1-20 Places";
        }else if(capacity == 1){
            return "21-80 Places";
        }else if(capacity == 2){
            return "80+ Places";
        }else if(capacity == 3){
            return "300+ Places";
        }else{
            return "";
        }
    }

    public String getType() {
        if(type == 0){
            return "Parking Public";
        }else if(type == 1){
            return "Parking Privé";
        }else if(type == 2){
            return "Stationnement";
        }else{
            return "";
        }
    }

    public int getNbContributor() {
        return nbContributor;
    }

    public boolean isByUser() {
        return byUser;
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

    public void setByUser(boolean byUser) {
        this.byUser = byUser;
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
