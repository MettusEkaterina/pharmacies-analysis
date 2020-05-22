package ru.ifmo.pharmacies.analysis.web.model;

import lombok.Data;

import java.util.List;

@Data
public class PharmaciesApiResponse {

    private List<PharmacyDTO> pharmacies;

    private List<String> notFound;

    public List<PharmacyDTO> getPharmacies() {
        return pharmacies;
    }

    public void setPharmacies(List<PharmacyDTO> pharmacies) {
        this.pharmacies = pharmacies;
    }

    public List<String> getNotFound() {
        return notFound;
    }

    public void setNotFound(List<String> notFound) {
        this.notFound = notFound;
    }

    public PharmaciesApiResponse(List<PharmacyDTO> pharmacies, List<String> notFound) {
        this.pharmacies = pharmacies;
        this.notFound = notFound;
    }

    public PharmaciesApiResponse() {}
}
