package edu.uci.ics.kanec1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenreListResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private Integer resultCode;
    @JsonProperty(value = "message", required = true)
    private String message;
    @JsonProperty(value = "genres", required = true)
    private GenreModel[] genres;

    @JsonCreator
    public GenreListResponseModel(@JsonProperty(value = "resultCode", required = true) Integer resultCode,
                                  @JsonProperty(value = "message", required = true) String message,
                                  @JsonProperty(value = "genres", required = true) GenreModel[] genres) {
        this.resultCode = resultCode;
        this.message = message;
        this.genres = genres;
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
    public GenreModel[] getGenres() {
        return genres;
    }
}
