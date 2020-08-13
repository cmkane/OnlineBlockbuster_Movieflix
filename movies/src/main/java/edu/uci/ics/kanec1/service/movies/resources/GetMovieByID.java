package edu.uci.ics.kanec1.service.movies.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import edu.uci.ics.kanec1.service.movies.core.DBUses;
import edu.uci.ics.kanec1.service.movies.core.UserRecords;
import edu.uci.ics.kanec1.service.movies.logger.ServiceLogger;
import edu.uci.ics.kanec1.service.movies.models.GetMovieByIDResponseModel;
import edu.uci.ics.kanec1.service.movies.models.MovieByIDModel;
import edu.uci.ics.kanec1.service.movies.models.SearchResponseModel;
import edu.uci.ics.kanec1.service.movies.models.VerifyPrivilegeResponseModel;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("get")
public class GetMovieByID {
    @Path("{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovieById(@PathParam("movieid") String id,
                                 @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("Received request to find movie with id="+id);

        // Get the email and sessionID from the HTTP header
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info("EMAIL: " +  email);
        ServiceLogger.LOGGER.info("SESSIONID: " + sessionID);
        ServiceLogger.LOGGER.info("TRANSACTIONID: " + transactionID);

        int code;
        String message;
        Boolean plevel;

        GetMovieByIDResponseModel responseModelSearch = null;
        VerifyPrivilegeResponseModel responseModelPriv = null;

        try {

            plevel = UserRecords.userSufficientPrivilege(email, 4);

            //Retreive Movie Model Array List
            ArrayList<MovieByIDModel> list = DBUses.retreiveMovie(id);

            // Check if list is null or empty
            if(list == null) {
                ServiceLogger.LOGGER.info("Internal Server Error: Error when retrieving movie.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            } else if(list.size() == 0) {
                ServiceLogger.LOGGER.info("No movie found with movieId = "+id);
                code = 211;
                responseModelSearch = new GetMovieByIDResponseModel(code, getMessage(code), null);
                return Response.status(Response.Status.OK).entity(responseModelSearch).header("sessionID", sessionID).header("transactionID", transactionID).build();
            }

            // Check if user has sufficient privilege level to see movie
            if(!plevel && list.get(0).getHidden()) {
                ServiceLogger.LOGGER.info("User doesn't have sufficient plevel to view movie.");
                code = 141;
                responseModelPriv = new VerifyPrivilegeResponseModel(code, getMessage(code));
                return Response.status(Response.Status.OK).entity(responseModelPriv).header("sessionID", sessionID).header("transactionID", transactionID).build();
            }

            // If plevel sufficient, respond with movie
            code = 210;
            if(!plevel) list.get(0).setHidden(null);
            responseModelSearch = new GetMovieByIDResponseModel(code, getMessage(code), list.get(0));

            return Response.status(Response.Status.OK).entity(responseModelSearch).header("sessionID", sessionID).header("transactionID", transactionID).build();


        } catch (Exception e) {
            ServiceLogger.LOGGER.warning("Unable to get movie with given id.");
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        }

    }

    private Response buildResponseError(int code) {
        SearchResponseModel responseModel = new SearchResponseModel(code, getMessage(code), null);
        return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
    }

    private String getMessage(int code) {
        switch(code) {
            case -1:
                return "Internal Server Error";
            case -11:
                return "Email has invalid format.";
            case -10:
                return "Email has invalid length.";
            case -3:
                return "JSON parse exception.";
            case -2:
                return "JSON mapping exception.";
            case -16:
                return "Email not provided in request header.";
            case -17:
                return "SessionID not provided in request header.";
            case 141:
                return "User has insufficient privilege.";
            case 210:
                return "Found movies with search parameters.";
            case 211:
                return "No movies found with search parameters.";
            default:
                ServiceLogger.LOGGER.info("There is no message for this code");
                return "";
        }
    }
}
