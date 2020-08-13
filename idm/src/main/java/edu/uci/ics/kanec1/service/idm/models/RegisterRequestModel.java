package edu.uci.ics.kanec1.service.idm.models;

public class RegisterRequestModel {
    private String email;
    private char[] password;

    public RegisterRequestModel() {
    }

    public RegisterRequestModel(String email, char[] password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public char[] getPassword() {
        return password;
    }
}
