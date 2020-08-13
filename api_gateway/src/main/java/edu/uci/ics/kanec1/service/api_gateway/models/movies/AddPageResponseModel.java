package edu.uci.ics.kanec1.service.api_gateway.models.movies;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.kanec1.service.api_gateway.models.RequestModel;
import edu.uci.ics.kanec1.service.api_gateway.utilities.ResultCodes;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddPageResponseModel  {
    @JsonProperty(required = true)
    private int resultCode;
    @JsonProperty(required = true)
    private String message;
    @JsonProperty(required = true)
    private String movieid;
    @JsonProperty(required = true)
    private int[] genreid;

    @JsonCreator
    public AddPageResponseModel(int resultCode, String message, String movieid, int[] genreid) {
        this.resultCode = resultCode;
        this.message = message;
        this.movieid = movieid;
        this.genreid = genreid;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getMessage() {
        return message;
    }

    public String getMovieid() {
        return movieid;
    }

    public int[] getGenreid() {
        return genreid;
    }
}
