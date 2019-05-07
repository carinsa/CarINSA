package com.carinsa.model;

public class Place {
    private String spotid;
    private String name;
    private Double lat;
    private Double lng;
    private int nb;
    public Place(String spotid,String name,Double lat,Double lng,int nb){
        this.spotid=spotid;
        this.name=name;
        this.lat=lat;
        this.lng=lng;
        this.nb=nb;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setNb(int nb) {
        this.nb = nb;
    }

    public void setSpotid(String spotid) {
        this.spotid = spotid;
    }

    public String getName() {
        return name;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public int getNb() {
        return nb;
    }

    public String getSpotid() {
        return spotid;
    }
}
