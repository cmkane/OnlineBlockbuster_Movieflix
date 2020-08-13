package edu.uci.ics.kanec1.service.movies.models;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TDMMovieModel {
    @JsonProperty(required = true)
    private boolean adult;
    @JsonProperty(required = true)
    private int id;
    @JsonProperty(required = true)
    private String original_title;
    @JsonProperty(required = true)
    private float popularity;
    @JsonProperty(required = true)
    private boolean video;
    @JsonCreator
    public TDMMovieModel(
            @JsonProperty(value = "adult",required = true)boolean adult,
            @JsonProperty(value = "id",required = true)int id,
            @JsonProperty(value = "original_title",required = true)String original_title,
            @JsonProperty(value = "popularity",required = true)float popularity,
            @JsonProperty(value = "video",required = true)boolean video
    ){
        this.adult = adult;
        this.id = id;
        this.original_title = original_title;
        this.popularity = popularity;
        this.video = video;
    }

    public boolean isAdult() {
        return adult;
    }

    public int getId() {
        return id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    @Override
    public String toString() {
        return "TDMMovieModel{" +
                "adult=" + adult +
                ", id=" + id +
                ", original_title='" + original_title + '\'' +
                ", popularity=" + popularity +
                ", video=" + video +
                '}';
    }
}
