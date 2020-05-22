package ru.ifmo.pharmacies.analysis.domain;

public class Coordinates {

    private double lat;
    private double lon;

    public Coordinates() {};

    public Coordinates(double lat, double lon){
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat(){
        return this.lat;
    }

    public double getLon(){
        return this.lon;
    }

    public boolean isNull(){
        return (this.lat == 0.0 && this.lon == 0.0);
    }

    @Override
    public String toString(){
        return lat + "," + lon;
    }

}
