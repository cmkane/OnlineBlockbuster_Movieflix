package edu.uci.ics.kanec1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartRetrieveRequestModel {
    @JsonProperty(value = "email", required = true)
    private String email;

    @JsonCreator
    public CartRetrieveRequestModel(@JsonProperty(value = "email", required = true) String email) {
        this.email = email;
    }

    @JsonProperty
    public String getEmail() {
        return email;
    }
}
