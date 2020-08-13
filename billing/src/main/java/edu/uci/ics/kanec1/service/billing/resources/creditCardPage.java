package edu.uci.ics.kanec1.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.kanec1.service.billing.core.DBUses;
import edu.uci.ics.kanec1.service.billing.core.Validator;
import edu.uci.ics.kanec1.service.billing.logger.ServiceLogger;
import edu.uci.ics.kanec1.service.billing.models.CreditCardIDRequestModel;
import edu.uci.ics.kanec1.service.billing.models.CreditCardModel;
import edu.uci.ics.kanec1.service.billing.models.CreditCardRetrieveResponseModel;
import edu.uci.ics.kanec1.service.billing.models.GenericResponseModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

@Path("creditcard")
public class creditCardPage {
    @Path("insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertCC(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to insert CC...");
        ServiceLogger.LOGGER.info("json: " + jsonText);

        // Get the email and sessionID from the HTTP header
        String emailHeader = headers.getHeaderString("email");
        String sessionIDHeader = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("HEADER EMAIL: " +  emailHeader);
        ServiceLogger.LOGGER.info("HEADER SESSIONID: " + sessionIDHeader);

        ObjectMapper mapper = new ObjectMapper();
        int code;
        String message;

        String id;
        String firstName;
        String lastName;
        Date expiration;

        CreditCardModel requestModel = null;
        GenericResponseModel responseModel = null;

        try {
            requestModel = mapper.readValue(jsonText, CreditCardModel.class);
            id = requestModel.getId();
            firstName = requestModel.getFirstName();
            lastName = requestModel.getLastName();
            expiration = requestModel.getExpiration();

            // Check if CC id is valid
            code = Validator.validateCC(id);
            if(code != 1) {
                responseModel = new GenericResponseModel(code, getMessage(code));
                return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionIDHeader).build();
            }

            // Check if expiration date is valid
            code = Validator.validateDate(expiration);
            if (code != 1) {
                responseModel = new GenericResponseModel(code, getMessage(code));
                return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionIDHeader).build();
            }

            code = DBUses.insertCC(id, firstName, lastName, expiration);
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
    public Response updateCC(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to update CC...");
        ServiceLogger.LOGGER.info("json: " + jsonText);

        // Get the email and sessionID from the HTTP header
        String emailHeader = headers.getHeaderString("email");
        String sessionIDHeader = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("HEADER EMAIL: " +  emailHeader);
        ServiceLogger.LOGGER.info("HEADER SESSIONID: " + sessionIDHeader);

        ObjectMapper mapper = new ObjectMapper();
        int code;
        String message;

        String id;
        String firstName;
        String lastName;
        Date expiration;

        CreditCardModel requestModel = null;
        GenericResponseModel responseModel = null;

        try {
            requestModel = mapper.readValue(jsonText, CreditCardModel.class);
            id = requestModel.getId();
            firstName = requestModel.getFirstName();
            lastName = requestModel.getLastName();
            expiration = requestModel.getExpiration();

            // Check if CC id is valid
            code = Validator.validateCC(id);
            if(code != 1) {
                responseModel = new GenericResponseModel(code, getMessage(code));
                return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionIDHeader).build();
            }

            // Check if expiration date is valid
            code = Validator.validateDate(expiration);
            if (code != 1) {
                responseModel = new GenericResponseModel(code, getMessage(code));
                return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionIDHeader).build();
            }

            code = DBUses.updateCC(id, firstName, lastName, expiration);
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

    @Path("delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCC(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to delete CC...");
        ServiceLogger.LOGGER.info("json: " + jsonText);

        // Get the email and sessionID from the HTTP header
        String emailHeader = headers.getHeaderString("email");
        String sessionIDHeader = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("HEADER EMAIL: " +  emailHeader);
        ServiceLogger.LOGGER.info("HEADER SESSIONID: " + sessionIDHeader);

        ObjectMapper mapper = new ObjectMapper();
        int code;
        String message;

        String id;

        CreditCardIDRequestModel requestModel = null;
        GenericResponseModel responseModel = null;

        try {
            requestModel = mapper.readValue(jsonText, CreditCardIDRequestModel.class);
            id = requestModel.getId();

            // Check if CC id is valid
            code = Validator.validateCC(id);
            if(code != 1) {
                responseModel = new GenericResponseModel(code, getMessage(code));
                return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionIDHeader).build();
            }

            code = DBUses.deleteCC(id);
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


    @Path("retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCC(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to retrieve CC...");
        ServiceLogger.LOGGER.info("json: " + jsonText);

        // Get the email and sessionID from the HTTP header
        String emailHeader = headers.getHeaderString("email");
        String sessionIDHeader = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("HEADER EMAIL: " +  emailHeader);
        ServiceLogger.LOGGER.info("HEADER SESSIONID: " + sessionIDHeader);

        ObjectMapper mapper = new ObjectMapper();
        int code;
        String message;

        String id;

        CreditCardIDRequestModel requestModel = null;
        CreditCardRetrieveResponseModel responseModel = null;

        try {
            requestModel = mapper.readValue(jsonText, CreditCardIDRequestModel.class);
            id = requestModel.getId();

            // Check if CC id is valid
            code = Validator.validateCC(id);
            if(code != 1) {
                responseModel = new CreditCardRetrieveResponseModel(code, getMessage(code), null);
                return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionIDHeader).build();
            }

            responseModel = DBUses.retrieveCC(id);
            if(responseModel == null) return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
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
            case 321:
                return "Credit card ID has invalid length.";
            case 322:
                return "Credit card ID has invalid value.";
            case 323:
                return "expiration has invalid value.";
            case 324:
                return "Credit card does not exist.";
            case 325:
                return "Duplicate insertion.";
            case 3200:
                return "Credit card inserted successfully.";
            case 3210:
                return "Credit card updated successfully.";
            case 3220:
                return "Credit card deleted successfully.";
            default:
                ServiceLogger.LOGGER.info("There is no message for this code");
                return "";
        }
    }
}
