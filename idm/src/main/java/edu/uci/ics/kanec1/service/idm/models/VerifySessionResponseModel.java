package edu.uci.ics.kanec1.service.idm.models;

import com.fasterxml.jackson.annotation.JsonInclude;

public class VerifySessionResponseModel {
    private int resultCode;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sessionID;

    public VerifySessionResponseModel(int resultCode, String message, String sessionID) {
        this.resultCode = resultCode;
        this.message = message;
        this.sessionID = sessionID;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
}
