package edu.uci.ics.kanec1.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.kanec1.service.api_gateway.utilities.ResultCodes;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRetrieveResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private Integer resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    private TransactionModel[] transactions;

    @JsonCreator
    public OrderRetrieveResponseModel(@JsonProperty(value = "resultCode", required = true) Integer resultCode,
                                      @JsonProperty(value = "message", required = true) String message,
                                      TransactionModel[] transactions) {
        this.resultCode = resultCode;
        this.message = message;
        this.transactions = transactions;
    }

    public OrderRetrieveResponseModel(int resultCode) {
        this.resultCode = resultCode;
        this.message = ResultCodes.setMessage(resultCode);
        this.transactions = null;
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
    public TransactionModel[] getTransactions() {
        return transactions;
    }
}
