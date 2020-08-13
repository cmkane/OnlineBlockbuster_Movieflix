package edu.uci.ics.kanec1.service.api_gateway.models.idm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.kanec1.service.api_gateway.models.RequestModel;

@JsonIgnoreProperties(value = "dataValid")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerifyPrivilegeRequestModel extends RequestModel {
    @JsonProperty(value = "email", required = true)
    private String email;
    @JsonProperty(value = "plevel", required = true)
    private int plevel;

    @JsonCreator
    public VerifyPrivilegeRequestModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "plevel", required = true) int plevel) {
        this.email = email;
        this.plevel = plevel;
    }

    @Override
    public String toString() {
        return "Email: " + email + " Privilege Level: " + plevel;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("plevel")
    public int getPlevel() {
        return plevel;
    }
}
