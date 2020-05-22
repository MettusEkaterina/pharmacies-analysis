package ru.ifmo.pharmacies.analysis.web.model;

import lombok.Data;

import java.util.List;

@Data
public class MedicineApiResponse {

    private List<String> forms;

    public MedicineApiResponse(List<String> forms) {
        this.forms = forms;
    }

    public MedicineApiResponse() {}
}
