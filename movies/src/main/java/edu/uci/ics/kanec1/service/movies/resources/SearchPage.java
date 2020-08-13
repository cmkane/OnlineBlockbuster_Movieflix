package edu.uci.ics.kanec1.service.movies.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.kanec1.service.movies.core.DBUses;
import edu.uci.ics.kanec1.service.movies.core.UserRecords;
import edu.uci.ics.kanec1.service.movies.logger.ServiceLogger;
import edu.uci.ics.kanec1.service.movies.models.MovieModel;
import edu.uci.ics.kanec1.service.movies.models.SearchRequestModel;
import edu.uci.ics.kanec1.service.movies.models.SearchResponseModel;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("search")
public class SearchPage {
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchMovies(@Context HttpHeaders headers,
                                 @QueryParam("title") String title,
                                 @QueryParam("genre") String genre,
                                 @QueryParam("year") Integer year,
                                 @QueryParam("director") String director,
                                 @QueryParam("hidden") Boolean hidden,
                                 @DefaultValue("0") @QueryParam("offset") Integer offset,
                                 @DefaultValue("10") @QueryParam("limit") Integer limit,
                                 @QueryParam("orderby") String orderby,
                                 @QueryParam("direction") String direction
    ) {
        ServiceLogger.LOGGER.info("Received request for search...");

        // Get the email and sessionID from the HTTP header
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info("EMAIL: " +  email);
        ServiceLogger.LOGGER.info("SESSIONID: " + sessionID);

        ObjectMapper mapper = new ObjectMapper();
        int code;
        String message;
        boolean privilege = false;

        SearchRequestModel requestModel = null;
        SearchResponseModel responseModel = null;

        ServiceLogger.LOGGER.info("orderby = " + orderby);
        ServiceLogger.LOGGER.info("direction = "+direction);

        try {
            requestModel = new SearchRequestModel(title, genre, year, director, hidden, offset, limit, orderby, direction);
            ServiceLogger.LOGGER.info("in search request: orderby = "+requestModel.getOrderby());
            ServiceLogger.LOGGER.info("in search request: direction = " + requestModel.getDirection());

            privilege = UserRecords.userSufficientPrivilege(email, 4);

            if(privilege) ServiceLogger.LOGGER.info("User has sufficient plevel for viewing hidden movies.");
            else ServiceLogger.LOGGER.info("User does not have sufficient plevel for viewing hidden movies.");

            //Pass to DBUses to handle search
            ArrayList<MovieModel> modelList = DBUses.getMovies(requestModel, privilege);

            if(modelList.size() == 0) {
                code = 211;
                responseModel = new SearchResponseModel(code, getMessage(code), null);
                return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionID).header("transactionID", transactionID).build();
            }

            ServiceLogger.LOGGER.info("Movie result list is not empty.");

            int len = modelList.size();
            MovieModel[] array = new MovieModel[len];
            for(int x = 0; x < len; x++) {
                array[x] = modelList.get(x);
            }
            responseModel = new SearchResponseModel(210, getMessage(210), array);

            return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionID).header("transactionID", transactionID).build();


        } catch (Exception e) {
            ServiceLogger.LOGGER.info("Unable to get movies.");
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String getMessage(int code) {
        switch(code) {
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
