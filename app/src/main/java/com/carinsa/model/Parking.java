package com.carinsa.model;

import android.location.Location;

public class Parking {
    private int pkgid;
    private String name;
    private double lat;
    private double lng;
    private int availableSpots;
    private Avis avis;
    public Parking(int pkgid,String name, double lat, double lng, int availableSpots){
        this.pkgid=pkgid;
        this.name=name;
        this.lat=lat;
        this.lng=lng;
        this.availableSpots=availableSpots;
    }

    public Avis getAvis() {
        return avis;
    }

    public int getPkgid() { return pkgid; }
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

    public void setAvis(Avis avis) {
        this.avis = avis;
    }

    public void setPkgid(int pkgid) { this.pkgid = pkgid; }
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
