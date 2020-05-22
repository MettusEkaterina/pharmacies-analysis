package ru.ifmo.pharmacies.analysis.domain;

import java.util.Date;

public class Product {

    private transient String id;

    private String name;

    private double price;

    private String pharmacyId;

    private Coordinates coordinates;

    private Date lastupdate;

    public Product() {
    }

    public Product(
            String id,
            String name,
            double price,
            String pharmacyId,
            Coordinates coordinates,
            Date lastupdate) {
        this.id = id;
        this.name = name;
        this.pharmacyId = pharmacyId;
        this.coordinates = coordinates;
        this.price = price;
        this.lastupdate = lastupdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(Date lastupdate) {
        this.lastupdate = lastupdate;
    }

    public String getPharmacyId() {
        return pharmacyId;
    }

    public void setPharmacyId(String pharmacyId) {
        this.pharmacyId = pharmacyId;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", pharmacyId='" + pharmacyId + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}