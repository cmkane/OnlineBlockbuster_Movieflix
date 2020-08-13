package edu.uci.ics.kanec1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchStarsResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "stars", required = true)
    private StarModel[] stars;

    @JsonCreator
    public SearchStarsResponseModel(@JsonProperty(value = "resultCode", required = true) int resultCode,
                                    @JsonProperty(value = "message", required = true) String message,
                                    @JsonProperty(value = "stars", required = true) StarModel[] stars) {
        this.resultCode = resultCode;
        this.message = message;
        this.stars = stars;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getMessage() {
        return message;
    }

    public StarModel[] getStars() {
        return stars;
    }
}
