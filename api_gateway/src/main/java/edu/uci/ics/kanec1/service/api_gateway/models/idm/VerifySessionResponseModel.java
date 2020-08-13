package edu.uci.ics.kanec1.service.api_gateway.models.idm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.kanec1.service.api_gateway.utilities.ResultCodes;

public class VerifySessionResponseModel {
    private int resultCode;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sessionID;

    @JsonCreator
    public VerifySessionResponseModel(@JsonProperty(value = "resultCode") int resultCode,
                                      @JsonProperty(value = "message") String message,
                                      @JsonProperty(value = "sessionID") String sessionID) {
        this.resultCode = resultCode;
        this.message = message;
        this.sessionID = sessionID;
    }

    public VerifySessionResponseModel(int resultCode) {
        this.resultCode = resultCode;
        this.message = ResultCodes.setMessage(resultCode);
        this.sessionID = null;
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
