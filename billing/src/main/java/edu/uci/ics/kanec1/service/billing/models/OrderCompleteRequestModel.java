package edu.uci.ics.kanec1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderCompleteRequestModel {
    @JsonProperty(value = "paymentId", required = true)
    private String paymentId;
    @JsonProperty(value = "token", required = true)
    private String token;
    @JsonProperty(value = "PayerID", required = true)
    private String PayerID;

    @JsonCreator
    public OrderCompleteRequestModel(@JsonProperty(value = "paymentId", required = true) String paymentId,
                                     @JsonProperty(value = "token", required = true) String token,
                                     @JsonProperty(value = "PayerID", required = true) String payerID) {
        this.paymentId = paymentId;
        this.token = token;
        PayerID = payerID;
    }

    @JsonProperty
    public String getPaymentId() {
        return paymentId;
    }

    @JsonProperty
    public String getToken() {
        return token;
    }

    @JsonProperty
    public String getPayerID() {
        return PayerID;
    }
}
