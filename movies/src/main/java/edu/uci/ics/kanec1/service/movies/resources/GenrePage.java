package edu.uci.ics.kanec1.service.movies.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.kanec1.service.movies.core.DBUses;
import edu.uci.ics.kanec1.service.movies.core.UserRecords;
import edu.uci.ics.kanec1.service.movies.logger.ServiceLogger;
import edu.uci.ics.kanec1.service.movies.models.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("genre")
public class GenrePage {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGenres(@Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("Received request to get genres.");

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

        GenreListResponseModel responseModel = null;

        try {
            plevel = UserRecords.userSufficientPrivilege(email, 3);

            // Check if user has sufficient privilege
            if(!plevel) {
                code = 141;
                return Response.status(Response.Status.OK).entity(new GenericResponseModel(code, getMessage(code))).header("sessionID", sessionID).header("transactionID", transactionID).build();
            }

            responseModel = DBUses.fetchGenres();
            if(responseModel == null) return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionID).header("transactionID", transactionID).build();

        } catch (Exception e) {
            ServiceLogger.LOGGER.info("Unable to retrieve genres.");
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addGenre(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to get genres.");
        ServiceLogger.LOGGER.info("request: " + jsonText);

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

        AddGenreRequestModel requestModel = null;
        GenericResponseModel responseModel = null;
        try {
            requestModel = mapper.readValue(jsonText, AddGenreRequestModel.class);
            String name = requestModel.getName();

            plevel = UserRecords.userSufficientPrivilege(email, 3);

            // Check if user has sufficient privilege
            if(!plevel) {
                code = 141;
                return Response.status(Response.Status.OK).entity(new GenericResponseModel(code, getMessage(code))).header("sessionID", sessionID).header("transactionID", transactionID).build();
            }

            code = DBUses.addGenre(name);
            message = getMessage(code);
            responseModel = new GenericResponseModel(code, message);
            return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionID).header("transactionID", transactionID).build();

        } catch (Exception e) {
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

    @Path("{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGenresForMovie(@PathParam("movieid") String movieid,
                                      @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("Received request to get genres.");

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

        GenreListResponseModel responseModel = null;
        try {
            plevel = UserRecords.userSufficientPrivilege(email, 3);

            // Check if user has sufficient privilege
            if(!plevel) {
                code = 141;
                return Response.status(Response.Status.OK).entity(new GenericResponseModel(code, getMessage(code))).header("sessionID", sessionID).header("transactionID", transactionID).build();
            }

            ArrayList<GenreModel> modelList = DBUses.getGenres(movieid);
            if(modelList == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
            int len = modelList.size();
            GenreModel[] array;
            if(len == 0) {
                array = null;
                code = 211;
            } else {
                array = new GenreModel[len];
                for(int x = 0; x < len; x++) {
                    array[x] = modelList.get(x);
                }
                code = 219;
            }
            responseModel = new GenreListResponseModel(code, getMessage(code), array);
            return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionID).header("transactionID", transactionID).build();

        } catch (Exception e) {
            ServiceLogger.LOGGER.warning("Unable to get genres for movieid="+movieid);
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String getMessage(int code) {
        switch(code) {
            case -1:
                return "Internal Server Error";
            case -2:
                return "JSON mapping exception.";
            case -3:
                return "JSON parse exception.";
            case 141:
                return "User has insufficient privilege.";
            case 211:
                return "No movies found with search parameters.";
            case 217:
                return "Genre successfully added.";
            case 218:
                return "Genre could not be added.";
            case 219:
                return "Genres successfully retrieved.";
            default:
                ServiceLogger.LOGGER.info("There is no message for this code");
                return "";
        }
    }
}
