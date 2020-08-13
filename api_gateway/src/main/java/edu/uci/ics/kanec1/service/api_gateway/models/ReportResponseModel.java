package edu.uci.ics.kanec1.service.api_gateway.models;

public class ReportResponseModel {
    private String json;
    private int httpStatus;

    public ReportResponseModel(String json, int httpStatus) {
        this.json = json;
        this.httpStatus = httpStatus;
    }

    public String getJson() {
        return json;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
