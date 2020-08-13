package edu.uci.ics.kanec1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditCardIDRequestModel {
    @JsonProperty(value = "id", required = true)
    private String id;

    @JsonCreator
    public CreditCardIDRequestModel(@JsonProperty(value = "id", required = true) String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
