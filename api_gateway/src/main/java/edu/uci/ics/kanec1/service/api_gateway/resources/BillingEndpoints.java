package edu.uci.ics.kanec1.service.api_gateway.resources;

import edu.uci.ics.kanec1.service.api_gateway.GatewayService;
import edu.uci.ics.kanec1.service.api_gateway.core.UserRecords;
import edu.uci.ics.kanec1.service.api_gateway.exceptions.ModelValidationException;
import edu.uci.ics.kanec1.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.kanec1.service.api_gateway.models.billing.*;
import edu.uci.ics.kanec1.service.api_gateway.models.idm.VerifySessionResponseModel;
import edu.uci.ics.kanec1.service.api_gateway.threadpool.ClientRequest;
import edu.uci.ics.kanec1.service.api_gateway.utilities.HTTPStatusCodes;
import edu.uci.ics.kanec1.service.api_gateway.utilities.ModelValidator;
import edu.uci.ics.kanec1.service.api_gateway.utilities.TransactionIDGenerator;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("billing")
public class BillingEndpoints {
    @Path("cart/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertToCartRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to insert into cart.");
        CartInsertRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CartInsertRequestModel) ModelValidator.verifyModel(jsonText, CartInsertRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GenericResponseModel.class);
        }

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        if(sessionID == null) {
            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header", null);
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        VerifySessionResponseModel verifySession = UserRecords.verifySession(email, sessionID);

        if(verifySession == null || verifySession.getResultCode() == -1) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        if(verifySession.getResultCode() != 130) {
            return Response.status(HTTPStatusCodes.setHTTPStatus(verifySession.getResultCode())).entity(verifySession)
                    .header("email", email).header("sessionID", sessionID).build();
        } else {
            sessionID = verifySession.getSessionID();
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        /*
            Create ClientRequest wrapper for HTTP request. This will be potentially unique for each endpoint, because
            not every endpoint requires the same information. The register user request in particular does not contain
            any information in the HTTP Header (email, sessionID, transactionID) because the client making this request
            doesn't have any of that information yet, whereas for most other endpoints, this will be the case. So, for
            this endpoint, all we can set is the RequestModel, the URI of the microservice we're sending the request to,
            the endpoint we're sending this request to, and the transactionID for this request.
         */
        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartInsert());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        return Response.status(Status.NO_CONTENT)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "*")
                .header("Access-Control-Allow-Headers", "*")
                .header("Access-Control-Expose-Headers", "*")
                .header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay())
                .header("transactionID", transactionID)
                .header("email", email)
                .header("sessionID", sessionID).build();    }

    @Path("cart/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCartRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to update cart.");
        CartInsertRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CartInsertRequestModel) ModelValidator.verifyModel(jsonText, CartInsertRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GenericResponseModel.class);
        }

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        if(sessionID == null) {
            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header", null);
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        VerifySessionResponseModel verifySession = UserRecords.verifySession(email, sessionID);

        if(verifySession == null || verifySession.getResultCode() == -1) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        if(verifySession.getResultCode() != 130) {
            return Response.status(HTTPStatusCodes.setHTTPStatus(verifySession.getResultCode())).entity(verifySession)
                    .header("email", email).header("sessionID", sessionID).build();
        } else {
            sessionID = verifySession.getSessionID();
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        /*
            Create ClientRequest wrapper for HTTP request. This will be potentially unique for each endpoint, because
            not every endpoint requires the same information. The register user request in particular does not contain
            any information in the HTTP Header (email, sessionID, transactionID) because the client making this request
            doesn't have any of that information yet, whereas for most other endpoints, this will be the case. So, for
            this endpoint, all we can set is the RequestModel, the URI of the microservice we're sending the request to,
            the endpoint we're sending this request to, and the transactionID for this request.
         */
        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartUpdate());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        return Response.status(Status.NO_CONTENT)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "*")
                .header("Access-Control-Allow-Headers", "*")
                .header("Access-Control-Expose-Headers", "*")
                .header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay())
                .header("transactionID", transactionID)
                .header("email", email)
                .header("sessionID", sessionID).build();    }

    @Path("cart/delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCartRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to update cart.");
        CartDeleteRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CartDeleteRequestModel) ModelValidator.verifyModel(jsonText, CartDeleteRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GenericResponseModel.class);
        }

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        if(sessionID == null) {
            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header", null);
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        VerifySessionResponseModel verifySession = UserRecords.verifySession(email, sessionID);

        if(verifySession == null || verifySession.getResultCode() == -1) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        if(verifySession.getResultCode() != 130) {
            return Response.status(HTTPStatusCodes.setHTTPStatus(verifySession.getResultCode())).entity(verifySession)
                    .header("email", email).header("sessionID", sessionID).build();
        } else {
            sessionID = verifySession.getSessionID();
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        /*
            Create ClientRequest wrapper for HTTP request. This will be potentially unique for each endpoint, because
            not every endpoint requires the same information. The register user request in particular does not contain
            any information in the HTTP Header (email, sessionID, transactionID) because the client making this request
            doesn't have any of that information yet, whereas for most other endpoints, this will be the case. So, for
            this endpoint, all we can set is the RequestModel, the URI of the microservice we're sending the request to,
            the endpoint we're sending this request to, and the transactionID for this request.
         */
        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartDelete());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        return Response.status(Status.NO_CONTENT)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "*")
                .header("Access-Control-Allow-Headers", "*")
                .header("Access-Control-Expose-Headers", "*")
                .header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay())
                .header("transactionID", transactionID)
                .header("email", email)
                .header("sessionID", sessionID).build();    }

    @Path("cart/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCartRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to retrieve cart.");
        CartRetrieveRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CartRetrieveRequestModel) ModelValidator.verifyModel(jsonText, CartRetrieveRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, CartRetrieveResponseModel.class);
        }

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        if(sessionID == null) {
            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header", null);
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        VerifySessionResponseModel verifySession = UserRecords.verifySession(email, sessionID);

        if(verifySession == null || verifySession.getResultCode() == -1) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        if(verifySession.getResultCode() != 130) {
            return Response.status(HTTPStatusCodes.setHTTPStatus(verifySession.getResultCode())).entity(verifySession)
                    .header("email", email).header("sessionID", sessionID).build();
        } else {
            sessionID = verifySession.getSessionID();
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        /*
            Create ClientRequest wrapper for HTTP request. This will be potentially unique for each endpoint, because
            not every endpoint requires the same information. The register user request in particular does not contain
            any information in the HTTP Header (email, sessionID, transactionID) because the client making this request
            doesn't have any of that information yet, whereas for most other endpoints, this will be the case. So, for
            this endpoint, all we can set is the RequestModel, the URI of the microservice we're sending the request to,
            the endpoint we're sending this request to, and the transactionID for this request.
         */
        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartRetrieve());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        return Response.status(Status.NO_CONTENT)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "*")
                .header("Access-Control-Allow-Headers", "*")
                .header("Access-Control-Expose-Headers", "*")
                .header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay())
                .header("transactionID", transactionID)
                .header("email", email)
                .header("sessionID", sessionID).build();    }

    @Path("cart/clear")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearCartRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to clear cart.");
        CartRetrieveRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CartRetrieveRequestModel) ModelValidator.verifyModel(jsonText, CartRetrieveRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GenericResponseModel.class);
        }

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        if(sessionID == null) {
            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header", null);
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        VerifySessionResponseModel verifySession = UserRecords.verifySession(email, sessionID);

        if(verifySession == null || verifySession.getResultCode() == -1) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        if(verifySession.getResultCode() != 130) {
            return Response.status(HTTPStatusCodes.setHTTPStatus(verifySession.getResultCode())).entity(verifySession)
                    .header("email", email).header("sessionID", sessionID).build();
        } else {
            sessionID = verifySession.getSessionID();
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        /*
            Create ClientRequest wrapper for HTTP request. This will be potentially unique for each endpoint, because
            not every endpoint requires the same information. The register user request in particular does not contain
            any information in the HTTP Header (email, sessionID, transactionID) because the client making this request
            doesn't have any of that information yet, whereas for most other endpoints, this will be the case. So, for
            this endpoint, all we can set is the RequestModel, the URI of the microservice we're sending the request to,
            the endpoint we're sending this request to, and the transactionID for this request.
         */
        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCartClear());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        return Response.status(Status.NO_CONTENT)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "*")
                .header("Access-Control-Allow-Headers", "*")
                .header("Access-Control-Expose-Headers", "*")
                .header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay())
                .header("transactionID", transactionID)
                .header("email", email)
                .header("sessionID", sessionID).build();    }

    @Path("creditcard/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertCreditCardRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to insert credit card.");
        CreditCardModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CreditCardModel) ModelValidator.verifyModel(jsonText, CreditCardModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GenericResponseModel.class);
        }

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        if(sessionID == null) {
            ServiceLogger.LOGGER.info("Session ID was not there.");
            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header", null);
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        VerifySessionResponseModel verifySession = UserRecords.verifySession(email, sessionID);

        if(verifySession == null || verifySession.getResultCode() == -1) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        if(verifySession.getResultCode() != 130) {
            ServiceLogger.LOGGER.info("Session was not active.");
            return Response.status(HTTPStatusCodes.setHTTPStatus(verifySession.getResultCode())).entity(verifySession)
                    .header("email", email).header("sessionID", sessionID).build();
        } else {
            sessionID = verifySession.getSessionID();
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        /*
            Create ClientRequest wrapper for HTTP request. This will be potentially unique for each endpoint, because
            not every endpoint requires the same information. The register user request in particular does not contain
            any information in the HTTP Header (email, sessionID, transactionID) because the client making this request
            doesn't have any of that information yet, whereas for most other endpoints, this will be the case. So, for
            this endpoint, all we can set is the RequestModel, the URI of the microservice we're sending the request to,
            the endpoint we're sending this request to, and the transactionID for this request.
         */
        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcInsert());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        return Response.status(Status.NO_CONTENT).header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay())
                .header("transactionID", transactionID).header("email", email).header("sessionID", sessionID).build();
    }

    @Path("creditcard/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCreditCardRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to update credit card.");
        CreditCardModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CreditCardModel) ModelValidator.verifyModel(jsonText, CreditCardModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GenericResponseModel.class);
        }

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        if(sessionID == null) {
            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header", null);
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        VerifySessionResponseModel verifySession = UserRecords.verifySession(email, sessionID);

        if(verifySession == null || verifySession.getResultCode() == -1) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        if(verifySession.getResultCode() != 130) {
            return Response.status(HTTPStatusCodes.setHTTPStatus(verifySession.getResultCode())).entity(verifySession)
                    .header("email", email).header("sessionID", sessionID).build();
        } else {
            sessionID = verifySession.getSessionID();
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        /*
            Create ClientRequest wrapper for HTTP request. This will be potentially unique for each endpoint, because
            not every endpoint requires the same information. The register user request in particular does not contain
            any information in the HTTP Header (email, sessionID, transactionID) because the client making this request
            doesn't have any of that information yet, whereas for most other endpoints, this will be the case. So, for
            this endpoint, all we can set is the RequestModel, the URI of the microservice we're sending the request to,
            the endpoint we're sending this request to, and the transactionID for this request.
         */
        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcUpdate());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        return Response.status(Status.NO_CONTENT).header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay())
                .header("transactionID", transactionID).header("email", email).header("sessionID", sessionID).build();    }

    @Path("creditcard/delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCreditCardRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to delete credit card.");
        CreditCardIDRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CreditCardIDRequestModel) ModelValidator.verifyModel(jsonText, CreditCardIDRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GenericResponseModel.class);
        }

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        if(sessionID == null) {
            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header", null);
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        VerifySessionResponseModel verifySession = UserRecords.verifySession(email, sessionID);

        if(verifySession == null || verifySession.getResultCode() == -1) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        if(verifySession.getResultCode() != 130) {
            return Response.status(HTTPStatusCodes.setHTTPStatus(verifySession.getResultCode())).entity(verifySession)
                    .header("email", email).header("sessionID", sessionID).build();
        } else {
            sessionID = verifySession.getSessionID();
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        /*
            Create ClientRequest wrapper for HTTP request. This will be potentially unique for each endpoint, because
            not every endpoint requires the same information. The register user request in particular does not contain
            any information in the HTTP Header (email, sessionID, transactionID) because the client making this request
            doesn't have any of that information yet, whereas for most other endpoints, this will be the case. So, for
            this endpoint, all we can set is the RequestModel, the URI of the microservice we're sending the request to,
            the endpoint we're sending this request to, and the transactionID for this request.
         */
        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcDelete());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        return Response.status(Status.NO_CONTENT).header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay())
                .header("transactionID", transactionID).header("email", email).header("sessionID", sessionID).build();    }

    @Path("creditcard/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCreditCardRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to retrieve credit card.");
        CreditCardIDRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CreditCardIDRequestModel) ModelValidator.verifyModel(jsonText, CreditCardIDRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, CreditCardRetrieveResponseModel.class);
        }

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        if(sessionID == null) {
            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header", null);
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        VerifySessionResponseModel verifySession = UserRecords.verifySession(email, sessionID);

        if(verifySession == null || verifySession.getResultCode() == -1) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        if(verifySession.getResultCode() != 130) {
            return Response.status(HTTPStatusCodes.setHTTPStatus(verifySession.getResultCode())).entity(verifySession)
                    .header("email", email).header("sessionID", sessionID).build();
        } else {
            sessionID = verifySession.getSessionID();
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        /*
            Create ClientRequest wrapper for HTTP request. This will be potentially unique for each endpoint, because
            not every endpoint requires the same information. The register user request in particular does not contain
            any information in the HTTP Header (email, sessionID, transactionID) because the client making this request
            doesn't have any of that information yet, whereas for most other endpoints, this will be the case. So, for
            this endpoint, all we can set is the RequestModel, the URI of the microservice we're sending the request to,
            the endpoint we're sending this request to, and the transactionID for this request.
         */
        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCcRetrieve());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        return Response.status(Status.NO_CONTENT).header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay())
                .header("transactionID", transactionID).header("email", email).header("sessionID", sessionID).build();    }

    @Path("customer/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertCustomerRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to insert customer.");
        CustomerModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CustomerModel) ModelValidator.verifyModel(jsonText, CustomerModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GenericResponseModel.class);
        }

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        if(sessionID == null) {
            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header", null);
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        VerifySessionResponseModel verifySession = UserRecords.verifySession(email, sessionID);

        if(verifySession == null || verifySession.getResultCode() == -1) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        if(verifySession.getResultCode() != 130) {
            return Response.status(HTTPStatusCodes.setHTTPStatus(verifySession.getResultCode())).entity(verifySession)
                    .header("email", email).header("sessionID", sessionID).build();
        } else {
            sessionID = verifySession.getSessionID();
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        /*
            Create ClientRequest wrapper for HTTP request. This will be potentially unique for each endpoint, because
            not every endpoint requires the same information. The register user request in particular does not contain
            any information in the HTTP Header (email, sessionID, transactionID) because the client making this request
            doesn't have any of that information yet, whereas for most other endpoints, this will be the case. So, for
            this endpoint, all we can set is the RequestModel, the URI of the microservice we're sending the request to,
            the endpoint we're sending this request to, and the transactionID for this request.
         */
        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCustomerInsert());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        return Response.status(Status.NO_CONTENT)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "*")
                .header("Access-Control-Allow-Headers", "*")
                .header("Access-Control-Expose-Headers", "*")
                .header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay())
                .header("transactionID", transactionID)
                .header("email", email)
                .header("sessionID", sessionID).build();    }

    @Path("customer/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCustomerRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to update customer.");
        CustomerModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CustomerModel) ModelValidator.verifyModel(jsonText, CustomerModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, GenericResponseModel.class);
        }

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        if(sessionID == null) {
            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header", null);
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        VerifySessionResponseModel verifySession = UserRecords.verifySession(email, sessionID);

        if(verifySession == null || verifySession.getResultCode() == -1) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        if(verifySession.getResultCode() != 130) {
            return Response.status(HTTPStatusCodes.setHTTPStatus(verifySession.getResultCode())).entity(verifySession)
                    .header("email", email).header("sessionID", sessionID).build();
        } else {
            sessionID = verifySession.getSessionID();
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        /*
            Create ClientRequest wrapper for HTTP request. This will be potentially unique for each endpoint, because
            not every endpoint requires the same information. The register user request in particular does not contain
            any information in the HTTP Header (email, sessionID, transactionID) because the client making this request
            doesn't have any of that information yet, whereas for most other endpoints, this will be the case. So, for
            this endpoint, all we can set is the RequestModel, the URI of the microservice we're sending the request to,
            the endpoint we're sending this request to, and the transactionID for this request.
         */
        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCustomerUpdate());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        return Response.status(Status.NO_CONTENT).header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay())
                .header("transactionID", transactionID).header("email", email).header("sessionID", sessionID).build();    }

    @Path("customer/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveCustomerRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to retrieve customer.");
        CustomerRetrieveRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CustomerRetrieveRequestModel) ModelValidator.verifyModel(jsonText, CustomerRetrieveRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, CustomerRetrieveResponseModel.class);
        }

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        if(sessionID == null) {
            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header", null);
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        VerifySessionResponseModel verifySession = UserRecords.verifySession(email, sessionID);

        if(verifySession == null || verifySession.getResultCode() == -1) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        if(verifySession.getResultCode() != 130) {
            return Response.status(HTTPStatusCodes.setHTTPStatus(verifySession.getResultCode())).entity(verifySession)
                    .header("email", email).header("sessionID", sessionID).build();
        } else {
            sessionID = verifySession.getSessionID();
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        /*
            Create ClientRequest wrapper for HTTP request. This will be potentially unique for each endpoint, because
            not every endpoint requires the same information. The register user request in particular does not contain
            any information in the HTTP Header (email, sessionID, transactionID) because the client making this request
            doesn't have any of that information yet, whereas for most other endpoints, this will be the case. So, for
            this endpoint, all we can set is the RequestModel, the URI of the microservice we're sending the request to,
            the endpoint we're sending this request to, and the transactionID for this request.
         */
        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPCustomerRetrieve());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        return Response.status(Status.NO_CONTENT).header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay())
                .header("transactionID", transactionID).header("email", email).header("sessionID", sessionID).build();    }

    @Path("order/place")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response placeOrderRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to place an order.");
        CustomerRetrieveRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CustomerRetrieveRequestModel) ModelValidator.verifyModel(jsonText, CustomerRetrieveRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, OrderPlaceResponseModel.class);
        }

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        if(sessionID == null) {
            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header", null);
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        VerifySessionResponseModel verifySession = UserRecords.verifySession(email, sessionID);

        if(verifySession == null || verifySession.getResultCode() == -1) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        if(verifySession.getResultCode() != 130) {
            return Response.status(HTTPStatusCodes.setHTTPStatus(verifySession.getResultCode())).entity(verifySession)
                    .header("email", email).header("sessionID", sessionID).build();
        } else {
            sessionID = verifySession.getSessionID();
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        /*
            Create ClientRequest wrapper for HTTP request. This will be potentially unique for each endpoint, because
            not every endpoint requires the same information. The register user request in particular does not contain
            any information in the HTTP Header (email, sessionID, transactionID) because the client making this request
            doesn't have any of that information yet, whereas for most other endpoints, this will be the case. So, for
            this endpoint, all we can set is the RequestModel, the URI of the microservice we're sending the request to,
            the endpoint we're sending this request to, and the transactionID for this request.
         */
        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPOrderPlace());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        return Response.status(Status.NO_CONTENT)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "*")
                .header("Access-Control-Allow-Headers", "*")
                .header("Access-Control-Expose-Headers", "*")
                .header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay())
                .header("transactionID", transactionID)
                .header("email", email)
                .header("sessionID", sessionID).build();    }

    @Path("order/retrieve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveOrderRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to retrieve an order.");
        CustomerRetrieveRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (CustomerRetrieveRequestModel) ModelValidator.verifyModel(jsonText, CustomerRetrieveRequestModel.class);
        } catch (ModelValidationException e) {
            return ModelValidator.returnInvalidRequest(e, OrderRetrieveResponseModel.class);
        }

        String email = headers.getHeaderString("email");
        String sessionID = headers.getHeaderString("sessionID");
        if(sessionID == null) {
            VerifySessionResponseModel responseModel = new VerifySessionResponseModel(-17, "SessionID not provided in request header", null);
            return Response.status(Status.BAD_REQUEST).entity(responseModel).build();
        }

        VerifySessionResponseModel verifySession = UserRecords.verifySession(email, sessionID);

        if(verifySession == null || verifySession.getResultCode() == -1) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        if(verifySession.getResultCode() != 130) {
            return Response.status(HTTPStatusCodes.setHTTPStatus(verifySession.getResultCode())).entity(verifySession)
                    .header("email", email).header("sessionID", sessionID).build();
        } else {
            sessionID = verifySession.getSessionID();
        }

        // Generate transaction id.
        String transactionID = TransactionIDGenerator.generateTransactionID();

        /*
            Create ClientRequest wrapper for HTTP request. This will be potentially unique for each endpoint, because
            not every endpoint requires the same information. The register user request in particular does not contain
            any information in the HTTP Header (email, sessionID, transactionID) because the client making this request
            doesn't have any of that information yet, whereas for most other endpoints, this will be the case. So, for
            this endpoint, all we can set is the RequestModel, the URI of the microservice we're sending the request to,
            the endpoint we're sending this request to, and the transactionID for this request.
         */
        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getBillingConfigs().getBillingUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getBillingConfigs().getEPOrderRetrieve());
        // set the request model
        cr.setRequest(requestModel);
        // set the transactionID
        cr.setTransactionID(transactionID);
        // set email
        cr.setEmail(email);
        // set sessionID
        cr.setSessionID(sessionID);

        // Now that the ClientRequest has been built, we can add it to our queue of requests.
        GatewayService.getThreadPool().getQueue().enqueue(cr);

        return Response.status(Status.NO_CONTENT)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "*")
                .header("Access-Control-Allow-Headers", "*")
                .header("Access-Control-Expose-Headers", "*")
                .header("requestDelay", GatewayService.getGatewayConfigs().getRequestDelay())
                .header("transactionID", transactionID)
                .header("email", email)
                .header("sessionID", sessionID).build();
    }
}
