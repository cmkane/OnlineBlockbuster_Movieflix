package edu.uci.ics.kanec1.service.movies.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties("dataValid")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenreModel {
    @JsonProperty(value = "id", required = true)
    private int id;
    @JsonProperty(value = "name", required = true)
    private String name;

    @JsonCreator
    public GenreModel(@JsonProperty(value = "id", required = true) int id,
                      @JsonProperty(value = "name", required = true) String name) {
        this.id = id;
        this.name = name;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }
}
