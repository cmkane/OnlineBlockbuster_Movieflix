package edu.uci.ics.kanec1.service.movies.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.kanec1.service.movies.core.DBUses;
import edu.uci.ics.kanec1.service.movies.core.UserRecords;
import edu.uci.ics.kanec1.service.movies.logger.ServiceLogger;
import edu.uci.ics.kanec1.service.movies.models.GenericResponseModel;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("delete/{movieid}")
public class DeletePage {
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeMovie(@PathParam("movieid") String movieid,
                                @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("Received request to remove movie...");

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

        GenericResponseModel responseModel = null;

        try {

            plevel = UserRecords.userSufficientPrivilege(email, 3);

            // Check if user has sufficient privilege
            if(!plevel) {
                code = 141;
                responseModel = new GenericResponseModel(code, getMessage(code));
                return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionID).build();
            }

            responseModel = DBUses.removeMovie(movieid);
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
            return Response.status(Response.Status.BAD_REQUEST).entity(new GenericResponseModel(code, message)).header("sessionID", sessionID).build();
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
            case 217:
                return "Movie successfully removed.";
            case 218:
                return "Could not remove movie.";
            default:
                ServiceLogger.LOGGER.info("There is no message for this code");
                return "";
        }
    }
}
