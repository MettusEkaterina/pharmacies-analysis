package ru.ifmo.pharmacies.analysis.web.model;

import ru.ifmo.pharmacies.analysis.domain.Contact;
import ru.ifmo.pharmacies.analysis.domain.Coordinates;

import java.util.List;

public class PharmacyDTO {

    private String name;

    private String address;

    private List<Contact> contacts;

    private String workHours;

    private Coordinates coordinates;

    private List<ProductDTO> medicines;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public List<ProductDTO> getMedicines() {
        return medicines;
    }

    public void setMedicines(List<ProductDTO> medicines) {
        this.medicines = medicines;
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

    public String getWorkHours() {
        return workHours;
    }

    public void setWorkHours(String workHours) {
        this.workHours = workHours;
    }

    public PharmacyDTO() {}

    public PharmacyDTO(
            String name,
            String address,
            List<Contact> contacts,
            Coordinates coordinates,
            String workHours,
            List<ProductDTO> medicines) {
        this.name = name;
        this.address = address;
        this.contacts = contacts;
        this.coordinates = coordinates;
        this.workHours = workHours;
        this.medicines = medicines;
    }
}
