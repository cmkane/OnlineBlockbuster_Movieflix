package edu.uci.ics.kanec1.service.movies.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.kanec1.service.movies.core.DBUses;
import edu.uci.ics.kanec1.service.movies.core.UserRecords;
import edu.uci.ics.kanec1.service.movies.logger.ServiceLogger;
import edu.uci.ics.kanec1.service.movies.models.AddPageRequestModel;
import edu.uci.ics.kanec1.service.movies.models.AddPageResponseModel;
import edu.uci.ics.kanec1.service.movies.models.GenreModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("add")
public class AddPage {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMovie(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to add movie...");

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
        Boolean plevel;

        AddPageRequestModel requestModel = null;
        AddPageResponseModel responseModel = null;

        try {

            plevel = UserRecords.userSufficientPrivilege(email, 3);

            // Check if user has sufficient privilege
            if(!plevel) {
                code = 141;
                responseModel = new AddPageResponseModel(code, getMessage(code), null, null);
                return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionID).build();
            }

            requestModel = mapper.readValue(jsonText, AddPageRequestModel.class);

            // Attempt to add movie to DB
            responseModel = DBUses.addMovieToDB(requestModel);

            return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionID).build();



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
            return Response.status(Response.Status.BAD_REQUEST).entity(new AddPageResponseModel(code, message, null, null)).header("sessionID", sessionID).build();

        }

    }
    private Response buildResponseError(int code) {
        AddPageResponseModel responseModel = new AddPageResponseModel(code, getMessage(code), null, null);
        return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
    }

    private String getMessage(int code) {
        switch(code) {
            case -1:
                return "Internal Server Error";
            case -3:
                return "JSON parse exception.";
            case -2:
                return "JSON mapping exception.";
            case -11:
                return "Email has invalid format.";
            case -10:
                return "Email has invalid length.";
            case -16:
                return "Email not provided in request header.";
            case -17:
                return "SessionID not provided in request header.";
            case 141:
                return "User has insufficient privilege.";
            case 214:
                return "Movie successfully added.";
            case 215:
                return "Could not add movie.";
            default:
                ServiceLogger.LOGGER.info("There is no message for this code");
                return "";
        }
    }
}
