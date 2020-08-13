package edu.uci.ics.kanec1.service.billing.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.api.payments.Payment;
import edu.uci.ics.kanec1.service.billing.core.DBUses;
import edu.uci.ics.kanec1.service.billing.core.PayPalClient;
import edu.uci.ics.kanec1.service.billing.core.Validator;
import edu.uci.ics.kanec1.service.billing.logger.ServiceLogger;
import edu.uci.ics.kanec1.service.billing.models.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("order")
public class orderPage {
    @Path("place")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response placeOrder(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received a request to place an order...");
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
        OrderPlaceResponseModel responseModel = null;

        try {
            requestModel = mapper.readValue(jsonText, CustomerRetrieveRequestModel.class);
            String email = requestModel.getEmail();

            code = Validator.emailValidator(email);
            if(code != 1) return buildResponseError(code);

            responseModel = DBUses.placeOrder(email);
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
            return Response.status(Response.Status.BAD_REQUEST).entity(new GenericResponseModel(code, message)).build();
        }

    }

    @Path("complete")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response completeOrder(@Context HttpHeaders headers,
                                  @QueryParam("paymentId") String paymentId,
                                  @QueryParam("token") String token,
                                  @QueryParam("PayerID") String PayerID) {
        ServiceLogger.LOGGER.info("Received a request to complete order...");

        // Get the email and sessionID from the HTTP header
        String emailHeader = headers.getHeaderString("email");
        String sessionIDHeader = headers.getHeaderString("sessionID");
        ServiceLogger.LOGGER.info("HEADER EMAIL: " +  emailHeader);
        ServiceLogger.LOGGER.info("HEADER SESSIONID: " + sessionIDHeader);

        OrderCompleteRequestModel requestModel = null;
        GenericResponseModel responseModel = null;

        int code;
        String message;

        try {

            code = DBUses.tokenExists(token);
            if(code != 1) {
                switch(code) {
                    case -1:
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                    case 0:
                        code = 3421;
                        responseModel = new GenericResponseModel(code, getMessage(code));
                        return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionIDHeader).build();
                }
            }

            Map<String, Object> result = PayPalClient.completePayment(PayerID, token, paymentId);
            String success = (String) result.get("status");
            if(success == null) {
                ServiceLogger.LOGGER.info("Unable to complete payment.");
                code = 3422;
                responseModel = new GenericResponseModel(code, getMessage(code));
                return Response.status(Response.Status.OK).entity(responseModel).header("sessionID", sessionIDHeader).build();
            }

            Payment createdPayment = (Payment) result.get("payment");
            String transactionId = createdPayment.getTransactions().get(0).getRelatedResources().get(0).getSale().getId();

            code = DBUses.updateTransactions(token, transactionId);
            if(code != 1) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }

            code = 3420;
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
    public Response retrieveOrder(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received a request to retrieve order...");
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
        OrderRetrieveResponseModel responseModel = null;

        try {
            requestModel = mapper.readValue(jsonText, CustomerRetrieveRequestModel.class);
            String email = requestModel.getEmail();

            responseModel = DBUses.retrieveOrder(email);
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
            case 332:
                return "Customer does not exist.";
            case 342:
                return "Create payment failed.";
            case 341:
                return "Shopping cart for this customer not found.";
            case 3400:
                return "Order placed successfully.";
            case 3421:
                return "Token not found.";
            case 3422:
                return "Payment can not be completed.";
            case 3420:
                return "Payment is completed successfully.";
            default:
                ServiceLogger.LOGGER.info("There is no message for this code");
                return "";
        }
    }
}
