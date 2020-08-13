package edu.uci.ics.kanec1.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.kanec1.service.api_gateway.utilities.ResultCodes;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditCardRetrieveResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private Integer resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    private CreditCardModel creditcard;

    @JsonCreator
    public CreditCardRetrieveResponseModel(@JsonProperty(value = "resultCode", required = true) Integer resultCode,
                                           @JsonProperty(value = "message", required = true) String message,
                                           CreditCardModel creditcard) {
        this.resultCode = resultCode;
        this.message = message;
        this.creditcard = creditcard;
    }

    public CreditCardRetrieveResponseModel(int resultCode) {
        this.resultCode = resultCode;
        this.message = ResultCodes.setMessage(resultCode);
        this.creditcard = null;
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
    public CreditCardModel getCreditcard() {
        return creditcard;
    }
}
