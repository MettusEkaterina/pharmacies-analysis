package ru.ifmo.pharmacies.analysis.domain;

import com.google.gson.Gson;

import java.util.Date;
import java.util.List;

public class Pharmacy {

    private transient String id;

    private String name;

    private String address;

    private List<Contact> contacts;

    private String district;

    private String subway;

    private String workHours;

    private Date lastUpdate;

    public Pharmacy() {}

    public Pharmacy(
            String id,
            String name,
            String address,
            List<Contact> contacts,
            String district,
            String subway,
            String workHours,
            Date lastUpdate) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.contacts = contacts;
        this.district = district;
        this.subway = subway;
        this.workHours = workHours;
        this.lastUpdate = lastUpdate;
    }

    public String asJSON(){
        return new Gson().toJson(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getSubway() {
        return subway;
    }

    public void setSubway(String subway) {
        this.subway = subway;
    }

    public String getWorkHours() {
        return workHours;
    }

    public void setWorkHours(String workHours) {
        this.workHours = workHours;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Pharmacy{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}