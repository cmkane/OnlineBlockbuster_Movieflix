package edu.uci.ics.kanec1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetMovieByIDResponseModel {
    @JsonProperty(value="resultCode", required = true)
    private int resultCode;
    @JsonProperty(value="message", required = true)
    private String message;
    private MovieByIDModel movie;

    @JsonCreator
    public GetMovieByIDResponseModel(@JsonProperty(value="resultCode", required = true) int resultCode,
                                     @JsonProperty(value="message", required = true) String message,
                                     MovieByIDModel movie) {
        this.resultCode = resultCode;
        this.message = message;
        this.movie = movie;
    }


    @JsonProperty("resultCode")
    public int getResultCode() {
        return resultCode;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty
    public MovieByIDModel getMovie() {
        return movie;
    }
}
