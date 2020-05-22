package ru.ifmo.pharmacies.analysis.domain;

import java.util.List;

public class Medicine {

    private transient String id;

    private String name;

    private List<String> forms;

    public Medicine() {}

    public Medicine (
            String id,
            String name,
            List<String> forms) {
        this.id = id;
        this.name = name;
        this.forms = forms;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getForms() {
        return forms;
    }

    public void setForms(List<String> forms) {
        this.forms = forms;
    }
}
