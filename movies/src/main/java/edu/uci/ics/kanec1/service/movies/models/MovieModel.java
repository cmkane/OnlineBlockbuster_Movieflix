package edu.uci.ics.kanec1.service.movies.models;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"movieId", "title", "director", "year", "rating", "numVotes", "hidden"})
public class MovieModel {
    @JsonProperty(value = "movieId", required = true)
    private String movieId;
    @JsonProperty(value = "title", required = true)
    private String title;
    @JsonProperty(value = "director", required = true)
    private String director;
    @JsonProperty(value = "year", required = true)
    private Integer year;
    @JsonProperty(value = "rating", required = true)
    private float rating;
    @JsonProperty(value = "numVotes", required = true)
    private Integer numVotes;
    private Boolean hidden;

    @JsonCreator
    public MovieModel(@JsonProperty(value = "movieId", required = true) String movieId,
                      @JsonProperty(value = "title", required = true) String title,
                      @JsonProperty(value = "director", required = true) String director,
                      @JsonProperty(value = "year", required = true) Integer year,
                      @JsonProperty(value = "rating", required = true) float rating,
                      @JsonProperty(value = "numVotes", required = true) Integer numVotes,
                      Boolean hidden) {
        this.movieId = movieId;
        this.title = title;
        this.director = director;
        this.year = year;
        this.rating = rating;
        this.numVotes = numVotes;
        this.hidden = hidden;
    }

    @JsonProperty
    public String getMovieId() {
        return movieId;
    }

    @JsonProperty
    public String getTitle() {
        return title;
    }

    @JsonProperty
    public String getDirector() {
        return director;
    }

    @JsonProperty
    public Integer getYear() {
        return year;
    }

    @JsonProperty
    public float getRating() {
        return rating;
    }

    @JsonProperty
    public Integer getNumVotes() {
        return numVotes;
    }

    @JsonProperty
    public Boolean getHidden() {
        return hidden;
    }

    @JsonIgnore
    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }
}
