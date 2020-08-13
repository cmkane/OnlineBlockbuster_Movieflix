package edu.uci.ics.kanec1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddStarRequestModel {
    @JsonProperty(value = "name", required = true)
    private String name;
    @JsonProperty(value = "birthYear", required = false)
    private Integer birthYear;

    @JsonCreator
    public AddStarRequestModel(@JsonProperty(value = "name", required = true) String name,
                               @JsonProperty(value = "birthYear", required = false) Integer birthYear) {
        this.name = name;
        this.birthYear = birthYear;
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public Integer getBirthYear() {
        return birthYear;
    }
}
