package edu.uci.ics.kanec1.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.kanec1.service.billing.core.DBUses;
import edu.uci.ics.kanec1.service.billing.core.Validator;
import edu.uci.ics.kanec1.service.billing.logger.ServiceLogger;
import edu.uci.ics.kanec1.service.billing.models.CustomerModel;
import edu.uci.ics.kanec1.service.billing.models.CustomerRetrieveRequestModel;
import edu.uci.ics.kanec1.service.billing.models.CustomerRetrieveResponseModel;
import edu.uci.ics.kanec1.service.billing.models.GenericResponseModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("customer")
public class customerPage {
    @Path("insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertCustomer(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to insert into customer");
        ServiceLogger.LOGGER.info("json: " + jsonText);

        // Get the email and sessionID from the HTTP header
        String emailHeader = headers.getHeaderString("email");
        String sessionIDHeader = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("HEADER EMAIL: " +  emailHeader);
        ServiceLogger.LOGGER.info("HEADER SESSIONID: " + sessionIDHeader);

        ObjectMapper mapper = new ObjectMapper();
        int code;
        String message;

        CustomerModel requestModel = null;
        GenericResponseModel responseModel = null;
        String email;
        String firstName;
        String lastName;
        String ccId;
        String address;

        try {
            requestModel = mapper.readValue(jsonText, CustomerModel.class);
            email = requestModel.getEmail();
            firstName = requestModel.getFirstName();
            lastName = requestModel.getLastName();
            ccId = requestModel.getCcId();
            address = requestModel.getAddress();

            code = Validator.emailValidator(email);
            if(code != 1) return buildResponseError(code);

            // Check if CC id is valid
            code = Validator.validateCC(ccId);
            if(code != 1) {
                responseModel = new GenericResponseModel(code, getMessage(code));
                return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionIDHeader).build();
            }

            code = DBUses.insertCustomer(email, firstName, lastName, ccId, address);

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
    public Response updateCustomer(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to update customer");
        ServiceLogger.LOGGER.info("json: " + jsonText);

        // Get the email and sessionID from the HTTP header
        String emailHeader = headers.getHeaderString("email");
        String sessionIDHeader = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("HEADER EMAIL: " +  emailHeader);
        ServiceLogger.LOGGER.info("HEADER SESSIONID: " + sessionIDHeader);

        ObjectMapper mapper = new ObjectMapper();
        int code;
        String message;

        CustomerModel requestModel = null;
        GenericResponseModel responseModel = null;
        String email;
        String firstName;
        String lastName;
        String ccId;
        String address;

        try {
            requestModel = mapper.readValue(jsonText, CustomerModel.class);
            email = requestModel.getEmail();
            firstName = requestModel.getFirstName();
            lastName = requestModel.getLastName();
            ccId = requestModel.getCcId();
            address = requestModel.getAddress();

            code = Validator.emailValidator(email);
            if(code != 1) return buildResponseError(code);

            // Check if CC id is valid
            code = Validator.validateCC(ccId);
            if(code != 1) {
                responseModel = new GenericResponseModel(code, getMessage(code));
                return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionIDHeader).build();
            }

            code = DBUses.updateCustomer(email, firstName, lastName, ccId, address);

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
    public Response retrieveCustomer(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to retrieve customer");
        ServiceLogger.LOGGER.info("json: " + jsonText);

        // Get the email and sessionID from the HTTP header
        String emailHeader = headers.getHeaderString("email");
        String sessionIDHeader = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("HEADER EMAIL: " +  emailHeader);
        ServiceLogger.LOGGER.info("HEADER SESSIONID: " + sessionIDHeader);

        ObjectMapper mapper = new ObjectMapper();
        int code;
        String message;

        CustomerRetrieveRequestModel requestModel = null;
        CustomerRetrieveResponseModel responseModel = null;
        String email;

        try {
            requestModel = mapper.readValue(jsonText, CustomerRetrieveRequestModel.class);
            email = requestModel.getEmail();

            code = Validator.emailValidator(email);
            if(code != 1) return buildResponseError(code);

            responseModel = DBUses.retrieveCustomer(email);

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
            case -11:
                return "Email address has invalid format.";
            case -10:
                return "Email address has invalid length.";
            case 321:
                return "Credit card ID has invalid length.";
            case 322:
                return "Credit card ID has invalid value.";
            case 331:
                return "Credit card ID not found.";
            case 332:
                return "Customer does not exist.";
            case 333:
                return "Duplicate insertion.";
            case 3310:
                return "Customer updated successfully.";
            case 3300:
                return "Customer inserted successfully.";
            default:
                ServiceLogger.LOGGER.info("There is no message for this code");
                return "";
        }
    }
}
