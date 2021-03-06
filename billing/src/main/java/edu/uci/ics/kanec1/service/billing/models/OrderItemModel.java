package edu.uci.ics.kanec1.service.billing.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemModel {
    @JsonProperty(value = "email", required = true)
    private String email;
    @JsonProperty(value = "movieId", required = true)
    private String movieId;
    @JsonProperty(value = "quantity", required = true)
    private Integer quantity;
    @JsonProperty(value = "saleDate", required = true)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd", timezone = "PST")
    private Date saleDate;

    @JsonCreator
    public OrderItemModel(@JsonProperty(value = "email", required = true) String email,
                          @JsonProperty(value = "movieId", required = true) String movieId,
                          @JsonProperty(value = "quantity", required = true) Integer quantity,
                          @JsonProperty(value = "saleDate", required = true) Date saleDate) {
        this.email = email;
        this.movieId = movieId;
        this.quantity = quantity;
        this.saleDate = saleDate;
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

    @JsonProperty
    public Date getSaleDate() {
        return saleDate;
    }
}
