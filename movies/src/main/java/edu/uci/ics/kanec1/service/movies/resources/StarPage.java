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

@Path("star")
public class StarPage {
    @Path("search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchStars(@Context HttpHeaders headers,
                                @DefaultValue("") @QueryParam("name") String name,
                                @QueryParam("birthYear") int birthYear,
                                @QueryParam("movieTitle") String movieTitle,
                                @DefaultValue("0") @QueryParam("offset") int offset,
                                @DefaultValue("10") @QueryParam("limit") int limit,
                                @QueryParam("orderby") String orderby,
                                @QueryParam("direction") String direction) {
        ServiceLogger.LOGGER.info("Received request to search stars.");
        // Get the email and sessionID from the HTTP header
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info("EMAIL: " +  email);
        ServiceLogger.LOGGER.info("SESSIONID: " + sessionID);

        SearchStarsRequestModel requestModel = new SearchStarsRequestModel(name, birthYear, movieTitle, offset, limit, orderby, direction);
        SearchStarsResponseModel responseModel = null;
        int code;
        String message;

        try {
            ArrayList<StarModel> modelList = DBUses.searchStars(requestModel);
            if(modelList == null) return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            int len = modelList.size();
            StarModel[] array;
            if(len == 0) {
                array = null;
                code = 213;
                message = getMessage(code);
            } else {
                array = new StarModel[len];
                for(int x = 0; x < len; x++) {
                    array[x] = modelList.get(x);
                }
                code = 212;
                message = getMessage(code);
            }
            responseModel = new SearchStarsResponseModel(code, message, array);
            return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionID).header("transactionID", transactionID).build();

        } catch (Exception e) {
            ServiceLogger.LOGGER.warning("Unable to search stars.");
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Path("{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStar(@PathParam("id") String id,
                            @Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("Received request to find a star.");
        // Get the email and sessionID from the HTTP header
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info("EMAIL: " +  email);
        ServiceLogger.LOGGER.info("SESSIONID: " + sessionID);

        GetStarByIDResponseModel responseModel = null;
        int code;
        String message;
        try {
            StarModel model = DBUses.retrieveStar(id);
            if(model == null) return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            if(model.getId() == null) {
                model = null;
                code = 213;
            } else {
                code = 212;
            }
            message = getMessage(code);
            responseModel = new GetStarByIDResponseModel(code, message, model);
            return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionID).header("transactionID", transactionID).build();
        } catch (Exception e) {
            ServiceLogger.LOGGER.warning("Unable to get star by id.");
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

    }

    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStar(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to add a star.");
        ServiceLogger.LOGGER.info("json: " + jsonText);
        // Get the email and sessionID from the HTTP header
        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        String transactionID = headers.getHeaderString("transactionID");
        ServiceLogger.LOGGER.info("EMAIL: " +  email);
        ServiceLogger.LOGGER.info("SESSIONID: " + sessionID);


        ObjectMapper mapper = new ObjectMapper();
        int code;
        String message;
        Boolean plevel;

        AddStarRequestModel requestModel = null;
        GenericResponseModel responseModel = null;

        try {
            plevel = UserRecords.userSufficientPrivilege(email, 3);

            // Check if user has sufficient privilege
            if(!plevel) {
                code = 141;
                responseModel = new GenericResponseModel(code, getMessage(code));
                return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionID).header("transactoinID", transactionID).build();
            }

            requestModel = mapper.readValue(jsonText, AddStarRequestModel.class);

            // Attempt to add star to DB
            code = DBUses.addStarToDB(requestModel);

            responseModel = new GenericResponseModel(code, getMessage(code));

            return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionID).header("transactoinID", transactionID).build();

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

    @Path("starsin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response starsIn(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to add a stars in movie.");
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
        Boolean plevel;

        StarsInRequestModel requestModel = null;
        GenericResponseModel responseModel = null;

        try {
            plevel = UserRecords.userSufficientPrivilege(email, 3);

            // Check if user has sufficient privilege
            if(!plevel) {
                code = 141;
                responseModel = new GenericResponseModel(code, getMessage(code));
                return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionID).header("transactoinID", transactionID).build();
            }

            requestModel = mapper.readValue(jsonText, StarsInRequestModel.class);

            // Attempt to add stars in to DB
            code = DBUses.addToStarsInDB(requestModel);

            responseModel = new GenericResponseModel(code, getMessage(code));

            return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionID).header("transactoinID", transactionID).build();

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

    private String getMessage(int code) {
        switch(code) {
            case -1:
                return "Internal Server Error";
            case -3:
                return "JSON parse exception.";
            case -2:
                return "JSON mapping exception.";
            case 141:
                return "User has insufficient privilege.";
            case 211:
                return "No movies found with search parameters.";
            case 212:
                return "Found stars with search parameters.";
            case 213:
                return "No stars found with search parameters.";
            case 220:
                return "Star successfully added.";
            case 221:
                return "Could not add star";
            case 222:
                return "Star already exists.";
            case 230:
                return "Star successfully added to movie.";
            case 231:
                return "Could not add star to movie.";
            case 232:
                return "Star already exists in movie.";
            default:
                ServiceLogger.LOGGER.info("There is no message for this code");
                return "";
        }
    }
}
