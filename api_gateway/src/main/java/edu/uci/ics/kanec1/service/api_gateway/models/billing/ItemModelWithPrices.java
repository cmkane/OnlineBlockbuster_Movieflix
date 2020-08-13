package edu.uci.ics.kanec1.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemModelWithPrices {
    @JsonProperty(value = "email", required = true)
    private String email;
    @JsonProperty(value = "movieId", required = true)
    private String movieId;
    @JsonProperty(value = "quantity", required = true)
    private Integer quantity;
    @JsonProperty(value = "unit_price", required = true)
    private Float unit_price;
    @JsonProperty(value = "discount", required = true)
    private Float discount;
    @JsonProperty(value = "saleDate", required = true)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd", timezone = "PST")
    private Date saleDate;

    @JsonCreator
    public ItemModelWithPrices(@JsonProperty(value = "email", required = true) String email,
                               @JsonProperty(value = "movieId", required = true) String movieId,
                               @JsonProperty(value = "quantity", required = true) Integer quantity,
                               @JsonProperty(value = "unit_price", required = true) Float unit_price,
                               @JsonProperty(value = "discount", required = true) Float discount,
                               @JsonProperty(value = "saleDate", required = true) Date saleDate) {
        this.email = email;
        this.movieId = movieId;
        this.quantity = quantity;
        this.unit_price = unit_price;
        this.discount = discount;
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
    public Float getUnit_price() {
        return unit_price;
    }

    @JsonProperty
    public Float getDiscount() {
        return discount;
    }

    @JsonProperty
    public Date getSaleDate() {
        return saleDate;
    }
}
