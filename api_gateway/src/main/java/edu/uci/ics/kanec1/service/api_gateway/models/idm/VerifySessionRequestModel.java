package edu.uci.ics.kanec1.service.api_gateway.models.idm;

import edu.uci.ics.kanec1.service.api_gateway.models.RequestModel;

public class VerifySessionRequestModel extends RequestModel {
    private String email;
    private String sessionID;

    public VerifySessionRequestModel() {
    }

    public VerifySessionRequestModel(String email, String sessionID) {
        this.email = email;
        this.sessionID = sessionID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
}
