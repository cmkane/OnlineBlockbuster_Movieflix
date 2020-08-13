package edu.uci.ics.kanec1.service.api_gateway.models.idm;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.uci.ics.kanec1.service.api_gateway.utilities.ResultCodes;

public class LoginResponseModel {
    private int resultCode;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sessionID;

    public LoginResponseModel(int resultCode, String message, String sessionID) {
        this.resultCode = resultCode;
        this.message = message;
        this.sessionID = sessionID;
    }

    public LoginResponseModel(int resultCode) {
        this.resultCode = resultCode;
        this.message = ResultCodes.setMessage(resultCode);
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
