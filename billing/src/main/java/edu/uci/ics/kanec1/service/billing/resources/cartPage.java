package edu.uci.ics.kanec1.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.kanec1.service.billing.core.DBUses;
import edu.uci.ics.kanec1.service.billing.core.Validator;
import edu.uci.ics.kanec1.service.billing.logger.ServiceLogger;
import edu.uci.ics.kanec1.service.billing.models.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("cart")
public class cartPage {

    @Path("insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertCart(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to insert into cart");
        ServiceLogger.LOGGER.info("json: " + jsonText);

        // Get the email and sessionID from the HTTP header
        String emailHeader = headers.getHeaderString("email");
        String sessionIDHeader = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("HEADER EMAIL: " +  emailHeader);
        ServiceLogger.LOGGER.info("HEADER SESSIONID: " + sessionIDHeader);

        ObjectMapper mapper = new ObjectMapper();
        int code;
        String message;

        CartInsertRequestModel requestModel = null;
        GenericResponseModel responseModel = null;
        String email;
        String movieId;
        int quantity;

        try {
            requestModel = mapper.readValue(jsonText, CartInsertRequestModel.class);
            email = requestModel.getEmail();
            movieId = requestModel.getMovieId();
            quantity = requestModel.getQuantity();

            code = Validator.emailValidator(email);
            if(code != 1) return buildResponseError(code);

            if(quantity <= 0) {
                code = 33;
                message = getMessage(code);
                responseModel = new GenericResponseModel(code, message);
                return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionIDHeader).build();
            }

            code = DBUses.insertIntoCart(email, movieId, quantity);

            if(code == -1) return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

            responseModel = new GenericResponseModel(code, getMessage(code));
            return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionIDHeader).build();

        } catch(Exception e) {
            if(e instanceof JsonMappingException) {
                code = -2;
                message = getMessage(code);
            } else if(e instanceof JsonParseException) {
                code = -3;
                message = getMessage(code);
            } else {
                e.printStackTrace();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity(new GenericResponseModel(code, message)).build();
        }
    }

    @Path("update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCart(@Context HttpHeaders headers, String jsonText){
        ServiceLogger.LOGGER.info("Received request to update cart.");
        ServiceLogger.LOGGER.info("json: " + jsonText);

        // Get the email and sessionID from the HTTP header
        String emailHeader = headers.getHeaderString("email");
        String sessionIDHeader = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("HEADER EMAIL: " +  emailHeader);
        ServiceLogger.LOGGER.info("HEADER SESSIONID: " + sessionIDHeader);

        ObjectMapper mapper = new ObjectMapper();
        int code;
        String message;

        String email;
        String movieId;
        int quantity;

        CartInsertRequestModel requestModel = null;
        GenericResponseModel responseModel = null;

        try {
            requestModel = mapper.readValue(jsonText, CartInsertRequestModel.class);
            email = requestModel.getEmail();
            movieId = requestModel.getMovieId();
            quantity = requestModel.getQuantity();

            code = Validator.emailValidator(email);
            if(code != 1) return buildResponseError(code);

            if(quantity <= 0) {
                code = 33;
                message = getMessage(code);
                responseModel = new GenericResponseModel(code, message);
                return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionIDHeader).build();
            }

            code = DBUses.updateCart(email, movieId, quantity);
            message = getMessage(code);
            responseModel = new GenericResponseModel(code, message);
            return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionIDHeader).build();

        } catch (Exception e) {
            if(e instanceof JsonMappingException) {
                code = -2;
                message = getMessage(code);
            } else if(e instanceof JsonParseException) {
                code = -3;
                message = getMessage(code);
            } else {
                e.printStackTrace();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity(new GenericResponseModel(code, message)).build();
        }

    }

    @Path("delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteItemCart(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to delete item in cart.");
        ServiceLogger.LOGGER.info("json: " + jsonText);

        // Get the email and sessionID from the HTTP header
        String emailHeader = headers.getHeaderString("email");
        String sessionIDHeader = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("HEADER EMAIL: " +  emailHeader);
        ServiceLogger.LOGGER.info("HEADER SESSIONID: " + sessionIDHeader);

        ObjectMapper mapper = new ObjectMapper();
        int code;
        String message;

        String email;
        String movieId;

        CartDeleteRequestModel requestModel = null;
        GenericResponseModel responseModel = null;

        try {
            requestModel = mapper.readValue(jsonText, CartDeleteRequestModel.class);
            email = requestModel.getEmail();
            movieId = requestModel.getMovieId();

            code = Validator.emailValidator(email);
            if(code != 1) return buildResponseError(code);

            code = DBUses.deleteItemCart(email, movieId);
            if(code == -1)
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            message = getMessage(code);
            responseModel = new GenericResponseModel(code, message);
            return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionIDHeader).build();

        } catch (Exception e) {
            if(e instanceof JsonMappingException) {
                code = -2;
                message = getMessage(code);
            } else if(e instanceof JsonParseException) {
                code = -3;
                message = getMessage(code);
            } else {
                e.printStackTrace();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity(new GenericResponseModel(code, message)).build();
        }
    }

    @Path("retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCart(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to retrieve cart details.");
        ServiceLogger.LOGGER.info("json: " + jsonText);

        // Get the email and sessionID from the HTTP header
        String emailHeader = headers.getHeaderString("email");
        String sessionIDHeader = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("HEADER EMAIL: " +  emailHeader);
        ServiceLogger.LOGGER.info("HEADER SESSIONID: " + sessionIDHeader);

        ObjectMapper mapper = new ObjectMapper();
        int code;
        String message;

        String email;

        CartRetrieveRequestModel requestModel = null;
        CartRetrieveResponseModel responseModel = null;

        try {
            requestModel = mapper.readValue(jsonText, CartRetrieveRequestModel.class);
            email = requestModel.getEmail();

            code = Validator.emailValidator(email);
            if(code != 1){
                return Response.status(Response.Status.BAD_REQUEST).entity(new CartRetrieveResponseModel(code, getMessage(code), null)).header("sessionID", sessionIDHeader).build();
            }

            responseModel = DBUses.retrieveCart(email);
            if(responseModel == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
            return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionIDHeader).build();

        } catch(Exception e) {
            if(e instanceof JsonMappingException) {
                code = -2;
                message = getMessage(code);
            } else if(e instanceof JsonParseException) {
                code = -3;
                message = getMessage(code);
            } else {
                e.printStackTrace();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity(new CartRetrieveResponseModel(code, message, null)).build();
        }
    }

    @Path("clear")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearCart(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to clear cart.");
        ServiceLogger.LOGGER.info("json: " + jsonText);

        // Get the email and sessionID from the HTTP header
        String emailHeader = headers.getHeaderString("email");
        String sessionIDHeader = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("HEADER EMAIL: " +  emailHeader);
        ServiceLogger.LOGGER.info("HEADER SESSIONID: " + sessionIDHeader);

        ObjectMapper mapper = new ObjectMapper();
        int code;
        String message;

        String email;

        CartRetrieveRequestModel requestModel = null;
        GenericResponseModel responseModel = null;

        try {
            requestModel = mapper.readValue(jsonText, CartRetrieveRequestModel.class);
            email = requestModel.getEmail();

            code = Validator.emailValidator(email);
            if(code != 1) return buildResponseError(code);

            code = DBUses.clearCart(email);
            if(code == -1) return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            responseModel = new GenericResponseModel(code, getMessage(code));
            return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionIDHeader).build();

        } catch(Exception e) {
            if(e instanceof JsonMappingException) {
                code = -2;
                message = getMessage(code);
            } else if(e instanceof JsonParseException) {
                code = -3;
                message = getMessage(code);
            } else {
                e.printStackTrace();
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity(new GenericResponseModel(code, message)).build();
        }
    }


    private Response buildResponseError(int code) {
        GenericResponseModel responseModel = new GenericResponseModel(code, getMessage(code));
        return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
    }

    private String getMessage(int code) {
        switch(code) {
            case -3:
                return "JSON Parse Exception.";
            case -2:
                return "JSON Mapping Exception.";
            case -11:
                return "Email address has invalid format.";
            case -10:
                return "Email address has invalid length.";
            case 33:
                return "Quantity has invalid value.";
            case 311:
                return "Duplicate insertion.";
            case 312:
                return "Shopping item does not exist.";
            case 3100:
                return "Shopping cart item inserted successfully.";
            case 3110:
                return "Shopping cart item updated successfully.";
            case 3120:
                return "Shopping cart item deleted successfully.";
            case 3130:
                return "Shopping cart retrieved successfully.";
            case 3140:
                return "Shopping cart cleared successfully.";
            default:
                ServiceLogger.LOGGER.info("There is no message for this code");
                return "";
        }
    }
}
