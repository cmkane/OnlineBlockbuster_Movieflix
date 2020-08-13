package edu.uci.ics.kanec1.service.movies.models;

import com.fasterxml.jackson.annotation.*;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "name", "birthYear", "movies"})
public class StarModel {
    @JsonProperty(value = "id", required = true)
    private String id;
    @JsonProperty(value = "name", required = true)
    private String name;
    private Integer birthYear;

    @JsonCreator
    public StarModel(String id,
                     String name,
                     Integer birthYear) {
        this.id = id;
        this.name = name;
        this.birthYear = birthYear;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

}
