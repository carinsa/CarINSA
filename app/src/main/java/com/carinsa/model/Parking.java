package com.carinsa.model;

public class Parking {
    private boolean isSpot=false;
    private String pkgid;
    private String name;
    private double lat;
    private double lng;
    private int availableSpots;
    private Avis avis;
    public Parking(String pkgid,String name, double lat, double lng, int availableSpots){
        this.pkgid=pkgid;
        this.name=name;
        this.lat=lat;
        this.lng=lng;
        this.availableSpots=availableSpots;
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
}
