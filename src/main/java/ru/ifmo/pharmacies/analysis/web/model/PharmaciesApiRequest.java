package ru.ifmo.pharmacies.analysis.web.model;

import lombok.Data;

import java.util.List;

@Data
public class PharmaciesApiRequest {

    public List<String> getMedicines() {
        return medicines;
    }

    public void setMedicines(List<String> medicines) {
        this.medicines = medicines;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    double lat;
    double lon;
    int distance;
    List<String> medicines;

    public PharmaciesApiRequest(double lat, double lon, int distance, List<String> medicines) {
        this.medicines = medicines;
        this.lat = lat;
        this.lon = lon;
        this.distance = distance;
    }

    public PharmaciesApiRequest() {}
}
