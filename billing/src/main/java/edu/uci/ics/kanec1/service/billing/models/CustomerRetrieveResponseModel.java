package edu.uci.ics.kanec1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerRetrieveResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private Integer resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    private CustomerModel customer;

    public CustomerRetrieveResponseModel(@JsonProperty(value = "resultCode", required = true) Integer resultCode,
                                         @JsonProperty(value = "message", required = true) String message,
                                         CustomerModel customer) {
        this.resultCode = resultCode;
        this.message = message;
        this.customer = customer;
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
    public CustomerModel getCustomer() {
        return customer;
    }
}
