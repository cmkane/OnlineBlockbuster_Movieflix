package edu.uci.ics.kanec1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderPlaceResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private Integer resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    private String redirectURL;
    private String token;

    @JsonCreator
    public OrderPlaceResponseModel(@JsonProperty(value = "resultCode", required = true) Integer resultCode,
                                   @JsonProperty(value = "message", required = true) String message,
                                   String redirectURL,
                                   String token) {
        this.resultCode = resultCode;
        this.message = message;
        this.redirectURL = redirectURL;
        this.token = token;
    }

    @JsonProperty
    public Integer getResultCode() {
        return resultCode;
    }

    @JsonProperty
    public String getMessage() {
        return message;
    }

    @JsonProperty
    public String getRedirectURL() {
        return redirectURL;
    }

    @JsonProperty
    public String getToken() {
        return token;
    }
}
