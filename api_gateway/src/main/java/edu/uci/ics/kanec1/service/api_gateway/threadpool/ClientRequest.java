package edu.uci.ics.kanec1.service.api_gateway.threadpool;

import edu.uci.ics.kanec1.service.api_gateway.models.RequestModel;

import java.util.HashMap;
import java.util.Map;

public class ClientRequest {
    private String email;
    private String sessionID;
    private String transactionID;
    private RequestModel request;
    private String URI;
    private String endpoint;
    private Map<String, String> queryParams;
    private String type;

    public ClientRequest() {

    }

    public ClientRequest(String email, String sessionID, String transactionID, RequestModel request, String URI, String endpoint) {
        this.email = email;
        this.sessionID = sessionID;
        this.transactionID = transactionID;
        this.request = request;
        this.URI = URI;
        this.endpoint = endpoint;
        this.queryParams = new HashMap<String, String>();
        this.type = "";
    }

    public String getEmail() {
        return email;
    }

    public String getSessionID() {
        return sessionID;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public RequestModel getRequest() {
        return request;
    }

    public String getURI() {
        return URI;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getType() {
        return type;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public void setRequest(RequestModel request) {
        this.request = request;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public void setType(String type) {
        this.type = type;
    }
}
