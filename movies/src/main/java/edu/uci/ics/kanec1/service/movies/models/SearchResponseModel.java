package edu.uci.ics.kanec1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = "dataValid")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResponseModel {
    @JsonProperty(value="resultCode", required = true)
    private int resultCode;
    @JsonProperty(value="message", required = true)
    private String message;
    @JsonProperty(value="movies", required = true)
    private MovieModel[] movies;

    @JsonCreator
    public SearchResponseModel(@JsonProperty(value="resultCode", required = true) int resultCode,
                               @JsonProperty(value="message", required = true) String message,
                               @JsonProperty(value="movies", required = true) MovieModel[] movies) {
        this.resultCode = resultCode;
        this.message = message;
        this.movies = movies;
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
    public MovieModel[] getMovies() {
        return movies;
    }
}
