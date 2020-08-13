package edu.uci.ics.kanec1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartRetrieveResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    private ItemModel[] items;

    @JsonCreator
    public CartRetrieveResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                                     @JsonProperty(value = "message", required = true) String message,
                                     ItemModel[] items) {
        this.resultCode = resultCode;
        this.message = message;
        this.items = items;
    }

    @JsonProperty
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty
    public String getMessage() {
        return message;
    }

    @JsonProperty
    public ItemModel[] getItems() {
        return items;
    }
}
