package edu.uci.ics.kanec1.service.api_gateway.resources;

import edu.uci.ics.kanec1.service.api_gateway.GatewayService;
import edu.uci.ics.kanec1.service.api_gateway.core.UserRecords;
import edu.uci.ics.kanec1.service.api_gateway.exceptions.ModelValidationException;
import edu.uci.ics.kanec1.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.kanec1.service.api_gateway.models.billing.GenericResponseModel;
import edu.uci.ics.kanec1.service.api_gateway.models.idm.VerifySessionResponseModel;
import edu.uci.ics.kanec1.service.api_gateway.models.movies.*;
import edu.uci.ics.kanec1.service.api_gateway.threadpool.ClientRequest;
import edu.uci.ics.kanec1.service.api_gateway.utilities.HTTPStatusCodes;
import edu.uci.ics.kanec1.service.api_gateway.utilities.ModelValidator;
import edu.uci.ics.kanec1.service.api_gateway.utilities.TransactionIDGenerator;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.HashMap;
import java.util.Map;

@Path("movies")
public class MovieEndpoints {
    @Path("search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchMovieRequest(@Context HttpHeaders headers,
                                       @QueryParam("title") String title,
                                       @QueryParam("genre") String genre,
                                       @QueryParam("year") Integer year,
                                       @QueryParam("director") String director,
                                       @QueryParam("hidden") Boolean hidden,
                                       @DefaultValue("0") @QueryParam("offset") Integer offset,
                                       @DefaultValue("10") @QueryParam("limit") Integer limit,
                                       @QueryParam("orderby") String orderby,
                                       @QueryParam("direction") String direction) {
        ServiceLogger.LOGGER.info("Received request to search for movies.");

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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());

        // get the register endpoint path from IDM configs
        String ENDPOINT = GatewayService.getMovieConfigs().getEPMovieSearch();
        Map<String, String> map = new HashMap<String, String>();
        if(title != null) map.put("title", title);
        if(genre != null) map.put("genre", genre);
        if(year != null) map.put("year", year.toString());
        if(director != null) map.put("director", director);
        if(hidden != null) map.put("hidden", hidden.toString());
        if(offset != null) map.put("offset", offset.toString());
        if(limit != null) map.put("limit", limit.toString());
        if(orderby != null) map.put("orderby", orderby);
        if(direction != null) map.put("direction", direction);

        cr.setQueryParams(map);

        cr.setEndpoint(ENDPOINT);
        // set the request model
        cr.setRequest(null);
        // set type of request
        cr.setType("GET");
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

    @Path("get/{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovieRequest(@Context HttpHeaders headers, @PathParam("movieid") String movieid) {
        ServiceLogger.LOGGER.info("Received request to get movie by id.");

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

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPMovieGet() + "/" + movieid);
        // set the request to null for get
        cr.setRequest(null);
        // set type of request
        cr.setType("GET");
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

    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addMovieRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to add a movie.");
        AddPageRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (AddPageRequestModel) ModelValidator.verifyModel(jsonText, AddPageRequestModel.class);
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

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPMovieAdd());
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

    @Path("delete/{movieid}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteMovieRequest(@Context HttpHeaders headers, @PathParam("movieid") String movieid) {
        ServiceLogger.LOGGER.info("Received request to delete movie.");

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

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPMovieDelete() + "/" + movieid);
        // set the request to null for get
        cr.setRequest(null);
        // set type of request
        cr.setType("DELETE");
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

    @Path("genre")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGenresRequest(@Context HttpHeaders headers) {
        ServiceLogger.LOGGER.info("Received request to get genre.");

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

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPGenreGet());
        // set the request to null for get
        cr.setRequest(null);
        // set type of request
        cr.setType("GET");
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

    @Path("genre/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addGenreRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to add a genre.");
        AddGenreRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (AddGenreRequestModel) ModelValidator.verifyModel(jsonText, AddGenreRequestModel.class);
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

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPGenreAdd());
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

    @Path("genre/{movieid}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGenresForMovieRequest(@Context HttpHeaders headers, @PathParam("movieid") String movieid) {
        ServiceLogger.LOGGER.info("Received request to get genre of a given movie.");

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

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPGenreMovie() + "/" + movieid);
        // set the request to null for get
        cr.setRequest(null);
        // set type of request
        cr.setType("GET");
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

    @Path("star/search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response starSearchRequest(@Context HttpHeaders headers,
                                      @QueryParam("name") String name,
                                      @QueryParam("birthYear") Integer birthYear,
                                      @QueryParam("movieTitle") String movieTitle,
                                      @DefaultValue("0") @QueryParam("offset") Integer offset,
                                      @DefaultValue("10") @QueryParam("limit") Integer limit,
                                      @QueryParam("orderby") String orderby,
                                      @QueryParam("direction") String direction) {
        ServiceLogger.LOGGER.info("Received request to search stars.");

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
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());

        // get the register endpoint path from IDM configs
        String ENDPOINT = GatewayService.getMovieConfigs().getEPStarSearch();
        Map<String, String> map = new HashMap<String, String>();
        if(name != null) map.put("name", name);
        if(birthYear != null) map.put("birthYear", birthYear.toString());
        if(movieTitle != null) map.put("movieTitle", movieTitle);
        if(offset != null) map.put("offset", offset.toString());
        if(limit != null) map.put("limit", limit.toString());
        if(orderby != null) map.put("orderby", orderby);
        if(direction != null) map.put("direction", direction);

        cr.setQueryParams(map);

        cr.setEndpoint(ENDPOINT);
        // set the request model
        cr.setRequest(null);
        // set type of request
        cr.setType("GET");
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

    @Path("star/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStarRequest(@Context HttpHeaders headers, @PathParam("id") String id) {
        ServiceLogger.LOGGER.info("Received request to get star by id.");

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

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPStarGet() + "/" + id);
        // set the request to null for get
        cr.setRequest(null);
        // set type of request
        cr.setType("GET");
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

    @Path("star/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStarRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to add a star.");
        AddStarRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (AddStarRequestModel) ModelValidator.verifyModel(jsonText, AddStarRequestModel.class);
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

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPStarAdd());
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

    @Path("star/starsin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStarToMovieRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to add a starsin.");
        StarsInRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (StarsInRequestModel) ModelValidator.verifyModel(jsonText, StarsInRequestModel.class);
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

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPStarIn());
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

    @Path("rating")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRatingRequest(@Context HttpHeaders headers, String jsonText) {
        ServiceLogger.LOGGER.info("Received request to add a score to rating.");
        RatingRequestModel requestModel;

        // Map jsonText to RequestModel
        try {
            requestModel = (RatingRequestModel) ModelValidator.verifyModel(jsonText, RatingRequestModel.class);
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

        ClientRequest cr = new ClientRequest();
        // get the IDM URI from IDM configs
        cr.setURI(GatewayService.getMovieConfigs().getMoviesUri());
        // get the register endpoint path from IDM configs
        cr.setEndpoint(GatewayService.getMovieConfigs().getEPRating());
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
