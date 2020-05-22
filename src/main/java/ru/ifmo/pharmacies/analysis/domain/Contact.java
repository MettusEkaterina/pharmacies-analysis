package ru.ifmo.pharmacies.analysis.domain;

public class Contact {

    private String telephone;

    private String extra;

    public Contact() {}

    public Contact(String telephone, String extra) {
        this.telephone = telephone;
        this.extra = extra;
    }

    public Contact(String telephone) {
        this.telephone = telephone;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString(){
        return telephone + (extra != null ? " " + extra : "");
    }
}
