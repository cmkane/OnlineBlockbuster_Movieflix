package edu.uci.ics.kanec1.service.idm.models;

public class LoginRequestModel {
    private String email;
    private char[] password;

    public LoginRequestModel() {
    }

    public LoginRequestModel(String email, char[] password) {
        this.email = email;
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public char[] getPassword() {
        return password;
    }
}
