package edu.uci.ics.kanec1.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.kanec1.service.api_gateway.models.RequestModel;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartInsertRequestModel extends RequestModel {
    @JsonProperty(value = "email", required = true)
    private String email;
    @JsonProperty(value = "movieId", required = true)
    private String movieId;
    @JsonProperty(value = "quantity", required = true)
    private Integer quantity;

    @JsonCreator
    public CartInsertRequestModel(@JsonProperty(value = "email", required = true) String email,
                                  @JsonProperty(value = "movieId", required = true) String movieId,
                                  @JsonProperty(value = "quantity", required = true) Integer quantity) {
        this.email = email;
        this.movieId = movieId;
        this.quantity = quantity;
    }

    @JsonProperty
    public String getEmail() {
        return email;
    }

    @JsonProperty
    public String getMovieId() {
        return movieId;
    }

    @JsonProperty
    public Integer getQuantity() {
        return quantity;
    }
}
