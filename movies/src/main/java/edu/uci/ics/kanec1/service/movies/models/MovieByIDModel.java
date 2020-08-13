package edu.uci.ics.kanec1.service.movies.models;

import com.fasterxml.jackson.annotation.*;
import edu.uci.ics.kanec1.service.movies.logger.ServiceLogger;

import java.util.ArrayList;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"movieId", "title", "director", "year", "backdrop_path", "overview", "poster_path", "hidden", "rating", "numVotes", "genres", "stars"})
public class MovieByIDModel {
    @JsonProperty(required = true)
    private String movieId;
    @JsonProperty(required = true)
    private String title;
    private String director;
    private Integer year;
    @JsonProperty(required = true)
    private Float rating;
    private Integer numVotes;
    @JsonProperty(required = true)
    private GenreModel[] genres;
    @JsonProperty(required = true)
    private StarModel[] stars;
    private String backdrop_path;
    private Integer budget;
    private String overview;
    private String poster_path;
    private Integer revenue;
    private Boolean hidden;

    @JsonCreator
    public MovieByIDModel(@JsonProperty(required = true) String movieId,
                          @JsonProperty(required = true) String title,
                          String director,
                          Integer year,
                          @JsonProperty(required = true) Float rating,
                          Integer numVotes,
                          @JsonProperty(required = true) GenreModel[] genres,
                          @JsonProperty(required = true) StarModel[] stars,
                          String backdrop_path, Integer budget, String overview,
                          String poster_path, Integer revenue, Boolean hidden) {
        this.movieId = movieId;
        this.title = title;
        this.director = director;
        this.year = year;
        this.rating = rating;
        this.numVotes = numVotes;
        this.genres = genres;
        this.stars = stars;
        this.backdrop_path = backdrop_path;
        this.budget = budget;
        this.overview = overview;
        this.poster_path = poster_path;
        this.revenue = revenue;
        this.hidden = hidden;
    }

    @JsonIgnore()
    public void buildStarsFromList(ArrayList<StarModel> list) {
        ServiceLogger.LOGGER.info("Creating model...");

        if(list == null || list.size() == 0) {
            ServiceLogger.LOGGER.info("No star list passed to model constructor.");
            return;
        }

        ServiceLogger.LOGGER.info("Star list is not empty...");

        int len = list.size();
        StarModel[] array = new StarModel[len];
        for(int x = 0; x < len; x++) {
            array[x] = list.get(x);
        }

        ServiceLogger.LOGGER.info("Finished building model. Setting star model array.");
        setStars(array);
    }

    @JsonIgnore()
    public void buildGenreFromList(ArrayList<GenreModel> list) {
        ServiceLogger.LOGGER.info("Creating model...");

        if(list == null || list.size() == 0) {
            ServiceLogger.LOGGER.info("No genre list passed to model constructor.");
            return;
        }

        ServiceLogger.LOGGER.info("Genre list is not empty...");

        int len = list.size();
        GenreModel[] array = new GenreModel[len];
        for(int x = 0; x < len; x++) {
            array[x] = list.get(x);
        }

        ServiceLogger.LOGGER.info("Finished building model.");
        setGenres(array);
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
    public Float getRating() {
        return rating;
    }

    @JsonProperty
    public Integer getNumVotes() {
        return numVotes;
    }

    @JsonProperty
    public GenreModel[] getGenres() {
        return genres;
    }

    @JsonIgnore
    public void setGenres(GenreModel[] genres) {
        this.genres = genres;
    }

    @JsonProperty
    public StarModel[] getStars() {
        return stars;
    }

    @JsonIgnore
    public void setStars(StarModel[] stars) {
        this.stars = stars;
    }

    @JsonProperty
    public String getBackdrop_path() {
        return backdrop_path;
    }

    @JsonProperty
    public Integer getBudget() {
        return budget;
    }

    @JsonProperty
    public String getOverview() {
        return overview;
    }

    @JsonProperty
    public String getPoster_path() {
        return poster_path;
    }

    @JsonProperty
    public Integer getRevenue() {
        return revenue;
    }

    @JsonProperty
    public Boolean getHidden() {
        return hidden;
    }

    @JsonIgnore
    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    @JsonIgnore
    public void setRating(Float rating) {
        this.rating = rating;
    }

    @JsonIgnore
    public void setNumVotes(Integer numVotes) {
        this.numVotes = numVotes;
    }
}
