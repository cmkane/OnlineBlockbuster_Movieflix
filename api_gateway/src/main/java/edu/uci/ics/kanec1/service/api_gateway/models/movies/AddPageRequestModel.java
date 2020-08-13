package edu.uci.ics.kanec1.service.api_gateway.models.movies;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.kanec1.service.api_gateway.models.RequestModel;

@JsonIgnoreProperties(value = "dataValid")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddPageRequestModel extends RequestModel {
    @JsonProperty(value = "title", required = true)
    private String title;
    @JsonProperty(value = "director", required = true)
    private String director;
    @JsonProperty(value = "year", required = true)
    private Integer year;
    @JsonProperty(value = "backdrop_path")
    private String backdrop_path;
    @JsonProperty(value = "budget")
    private Integer budget;
    @JsonProperty(value = "overview")
    private String overview;
    @JsonProperty(value = "poster_path")
    private String poster_path;
    @JsonProperty(value = "revenue")
    private Integer revenue;
    @JsonProperty(value = "genres", required = true)
    private GenreModel[] genres;

    @JsonCreator
    public AddPageRequestModel(@JsonProperty(value = "title", required = true) String title,
                               @JsonProperty(value = "director", required = true) String director,
                               @JsonProperty(value = "year", required = true) Integer year,
                               @JsonProperty(value = "backdrop_path") String backdrop_path,
                               @JsonProperty(value = "budget") Integer budget,
                               @JsonProperty(value = "overview") String overview,
                               @JsonProperty(value = "poster_path") String poster_path,
                               @JsonProperty(value = "revenue") Integer revenue,
                               @JsonProperty(value = "genres", required = true) GenreModel[] genres) {
        this.title = title;
        this.director = director;
        this.year = year;
        this.backdrop_path = backdrop_path;
        this.budget = budget;
        this.overview = overview;
        this.poster_path = poster_path;
        this.revenue = revenue;
        this.genres = genres;
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
    public GenreModel[] getGenres() {
        return genres;
    }
}
