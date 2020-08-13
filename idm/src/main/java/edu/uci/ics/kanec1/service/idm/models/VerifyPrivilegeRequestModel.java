package edu.uci.ics.kanec1.service.idm.models;

public class VerifyPrivilegeRequestModel {
    private String email;
    private int plevel;

    public VerifyPrivilegeRequestModel() {
    }

    public VerifyPrivilegeRequestModel(String email, int plevel) {
        this.email = email;
        this.plevel = plevel;
    }

    public String getEmail() {
        return email;
    }

    public int getPlevel() {
        return plevel;
    }
}
