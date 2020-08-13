package edu.uci.ics.kanec1.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.kanec1.service.api_gateway.models.RequestModel;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditCardModel extends RequestModel {
    @JsonProperty(value = "id", required = true)
    private String id;
    @JsonProperty(value = "firstName", required = true)
    private String firstName;
    @JsonProperty(value = "lastName", required = true)
    private String lastName;
    @JsonProperty(value = "expiration", required = true)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern ="yyyy-MM-dd", timezone = "PST")
    private Date expiration;

    @JsonCreator
    public CreditCardModel(@JsonProperty(value = "id", required = true) String id,
                           @JsonProperty(value = "firstName", required = true) String firstName,
                           @JsonProperty(value = "lastName", required = true) String lastName,
                           @JsonProperty(value = "expiration", required = true) Date expiration) {
            this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.expiration = expiration;
    }

    @JsonProperty
    public String getId() {
        return id;
    }

    @JsonProperty
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty
    public String getLastName() {
        return lastName;
    }

    @JsonProperty
    public Date getExpiration() {
        return expiration;
    }
}
