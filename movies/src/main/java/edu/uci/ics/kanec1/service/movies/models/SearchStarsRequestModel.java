package edu.uci.ics.kanec1.service.movies.models;

public class SearchStarsRequestModel {
    private String name;
    private Integer birthYear;
    private String movieTitle;
    private Integer offset;
    private Integer limit;
    private String orderby;
    private String direction;

    public SearchStarsRequestModel(String name, Integer birthYear, String movieTitle, Integer offset, Integer limit, String orderby, String direction) {
        this.name = name;
        this.birthYear = birthYear;
        this.movieTitle = movieTitle;
        this.offset = offset;
        this.limit = limit;
        this.orderby = orderby;
        this.direction = direction;
    }

    public String getName() {
        return name;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public Integer getOffset() {
        return offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public String getOrderby() {
        return orderby;
    }

    public String getDirection() {
        return direction;
    }
}
