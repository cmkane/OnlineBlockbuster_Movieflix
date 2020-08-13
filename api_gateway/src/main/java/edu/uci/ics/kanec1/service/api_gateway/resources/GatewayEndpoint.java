package edu.uci.ics.kanec1.service.api_gateway.resources;

import edu.uci.ics.kanec1.service.api_gateway.GatewayService;
import edu.uci.ics.kanec1.service.api_gateway.core.DBUses;
import edu.uci.ics.kanec1.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.kanec1.service.api_gateway.models.ReportResponseModel;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("report")
public class GatewayEndpoint {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response gateway(@Context HttpHeaders headers) {
        // Get the email and sessionID from the HTTP header
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info("EMAIL: " +  email);
        ServiceLogger.LOGGER.info("SESSIONID: " + sessionID);
        ServiceLogger.LOGGER.info("TRANSACTIONID: " + transactionID);

        ReportResponseModel responseModel;
        try {
            responseModel = DBUses.getResponse(email, sessionID, transactionID);
            if(responseModel == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
            if(responseModel.getJson() == null) {
                return Response.status(Response.Status.NO_CONTENT).header("email", email).header("sessionid", sessionID).header("transactionid", transactionID)
                        .header("message", "Response not ready.").header("RequestDelay", GatewayService.getGatewayConfigs().getRequestDelay()).build();
            }
            return Response.status(responseModel.getHttpStatus()).entity(responseModel.getJson())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "*")
                    .header("Access-Control-Allow-Headers", "*")
                    .header("Access-Control-Expose-Headers", "*")
                    .header("email", email).header("sessionid", sessionID).build();
        } catch (Exception e) {
            ServiceLogger.LOGGER.info("Unable to get response.");
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
