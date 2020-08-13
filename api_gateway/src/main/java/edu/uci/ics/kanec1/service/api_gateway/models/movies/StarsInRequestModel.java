package edu.uci.ics.kanec1.service.api_gateway.models.movies;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.kanec1.service.api_gateway.models.RequestModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StarsInRequestModel extends RequestModel {
    @JsonProperty(value = "starid", required = true)
    private String starid;
    @JsonProperty(value = "movieid", required = true)
    private String movieid;

    @JsonCreator
    public StarsInRequestModel(@JsonProperty(value = "starid", required = true) String starid,
                               @JsonProperty(value = "movieid", required = true) String movieid) {
        this.starid = starid;
        this.movieid = movieid;
    }

    @JsonProperty
    public String getStarid() {
        return starid;
    }

    @JsonProperty
    public String getMovieid() {
        return movieid;
    }
}
