package edu.uci.ics.kanec1.service.movies.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.kanec1.service.movies.core.DBUses;
import edu.uci.ics.kanec1.service.movies.logger.ServiceLogger;
import edu.uci.ics.kanec1.service.movies.models.AddPageResponseModel;
import edu.uci.ics.kanec1.service.movies.models.GenericResponseModel;
import edu.uci.ics.kanec1.service.movies.models.RatingRequestModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("rating")
public class RatingsPage {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setRating(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to change movie rating.");
        ServiceLogger.LOGGER.info("json: " + jsonText);
        // Get the email and sessionID from the HTTP header
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info("EMAIL: " +  email);
        ServiceLogger.LOGGER.info("SESSIONID: " + sessionID);
        ServiceLogger.LOGGER.info("TRANSACTIONID: " + transactionID);

        ObjectMapper mapper = new ObjectMapper();
        int code;
        String message;

        RatingRequestModel requestModel = null;
        GenericResponseModel responseModel = null;

        try {
            requestModel = mapper.readValue(jsonText, RatingRequestModel.class);

            code = DBUses.updateRating(requestModel);
            message = getMessage(code);
            responseModel = new GenericResponseModel(code, message);

            return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionID).header("transactionID", transactionID).build();

        } catch(Exception e) {
            if(e instanceof JsonMappingException) {
                code = -2;
                message = getMessage(code);
            } else if(e instanceof JsonParseException) {
                code = -3;
                message = getMessage(code);
            } else {
                code = -1;
                message = getMessage(code);
                e.printStackTrace();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity(new GenericResponseModel(code, message)).header("sessionID", sessionID).header("transactionID", transactionID).build();
        }
    }

    private String getMessage(int code) {
        switch(code) {
            case -1:
                return "Internal Server Error";
            case -3:
                return "JSON parse exception.";
            case -2:
                return "JSON mapping exception.";
            case 211:
                return "No movies found with search parameters.";
            case 250:
                return "Rating successfully updated.";
            case 251:
                return "Could not update rating.";
            default:
                ServiceLogger.LOGGER.info("There is no message for this code");
                return "";
        }
    }
}
