package edu.uci.ics.kanec1.service.movies.models;

public class SearchRequestModel {
    private String title;
    private String genre;
    private Integer year;
    private String director;
    private Boolean hidden;
    private Integer offset;
    private Integer limit;
    private String orderby;
    private String direction;

    public SearchRequestModel(String title,
                              String genre,
                              Integer year,
                              String director,
                              Boolean hidden,
                              Integer offset,
                              Integer limit,
                              String orderby,
                              String direction) {
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.director = director;
        this.hidden = hidden;
        this.offset = offset;
        this.limit = limit;
        this.orderby = orderby;
        this.direction = direction;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public Integer getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public Boolean isHidden() {
        return hidden;
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
