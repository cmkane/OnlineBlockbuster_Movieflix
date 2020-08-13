package edu.uci.ics.kanec1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private Integer resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;

    @JsonCreator
    public GenericResponseModel(@JsonProperty(value = "resultCode", required = true) Integer resultCode,
                                @JsonProperty(value = "message", required = true) String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    @JsonProperty
    public Integer getResultCode() {
        return resultCode;
    }

    @JsonProperty
    public String getMessage() {
        return message;
    }
}
