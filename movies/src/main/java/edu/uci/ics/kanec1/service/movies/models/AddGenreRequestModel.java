package edu.uci.ics.kanec1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddGenreRequestModel {
    @JsonProperty(value = "name", required = true)
    private String name;

    @JsonCreator
    public AddGenreRequestModel(@JsonProperty(value = "name", required = true) String name) {
        this.name = name;
    }

    @JsonProperty
    public String getName() {
        return name;
    }
}
