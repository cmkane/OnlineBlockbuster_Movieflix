package edu.uci.ics.kanec1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RatingRequestModel {
    @JsonProperty(value = "id", required = true)
    private String id;
    @JsonProperty(value = "rating", required = true)
    private Float rating;

    @JsonCreator
    public RatingRequestModel(@JsonProperty(value = "id", required = true) String id,
                              @JsonProperty(value = "rating", required = true) Float rating) {
        this.id = id;
        this.rating = rating;
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    @JsonProperty
    public Float getRating() {
        return rating;
    }
}
