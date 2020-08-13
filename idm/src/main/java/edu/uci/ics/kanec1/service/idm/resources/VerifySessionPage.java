package edu.uci.ics.kanec1.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.kanec1.service.idm.core.DBUses;
import edu.uci.ics.kanec1.service.idm.core.Validator;
import edu.uci.ics.kanec1.service.idm.logger.BasicLogger;
import edu.uci.ics.kanec1.service.idm.models.RegisterResponseModel;
import edu.uci.ics.kanec1.service.idm.models.VerifySessionRequestModel;
import edu.uci.ics.kanec1.service.idm.models.VerifySessionResponseModel;
import edu.uci.ics.kanec1.service.idm.security.Session;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("session")
public class VerifySessionPage {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifySession(String jsonText) {
        BasicLogger.LOGGER.info("Received request for session verification.");
        BasicLogger.LOGGER.info("Request:\n"+jsonText);

        ObjectMapper mapper = new ObjectMapper();
        VerifySessionRequestModel requestModel = null;
        VerifySessionResponseModel responseModel = null;

        String message;
        int code;
        String email;
        String sessionID;

        try {
            requestModel = mapper.readValue(jsonText, VerifySessionRequestModel.class);
            email = requestModel.getEmail();
            sessionID = requestModel.getSessionID();

            // Check if session ID is valid
            code = Validator.sessionIDValidator(sessionID);
            if(code == -13) {
                message = "Token has invalid length.";
                BasicLogger.LOGGER.info(message);
                responseModel = new VerifySessionResponseModel(code, message, null);
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            // Check if email is valid
            code = Validator.emailValidator(email);
            if (code != 1) {
                switch (code) {
                    case -11:
                        responseModel = new VerifySessionResponseModel(code, "Email address has invalid format.", null);
                        BasicLogger.LOGGER.info("Email has invalid format.");
                        break;
                    case -10:
                        responseModel = new VerifySessionResponseModel(code, "Email address has invalid length.", null);
                        BasicLogger.LOGGER.info("Email has invalid length.");
                        break;
                }
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            // Verify user exists
            code = DBUses.verifyUserExists(email);
            if(code == 0) {
                message = "User not found.";
                BasicLogger.LOGGER.info(message);
                code = 14;
                responseModel = new VerifySessionResponseModel(code, message, null);
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }

            // Get session from database
            Session session = DBUses.getSession(sessionID, email);
            if(session == null) {
                message = "Session not found.";
                BasicLogger.LOGGER.info(message);
                code = 134;
                responseModel = new VerifySessionResponseModel(code, message, null);
                return Response.status(Response.Status.OK).entity(responseModel).build();
            }

            // Check the session status in DB, if expired, revoked, or closed return as so
            //int stat = DBUses.checkSessionStatus(session.getSessionID().toString());
            int stat = DBUses.checkSessionStatus(sessionID);
            switch(stat) {
                case 2:
                    message = "Session is closed.";
                    BasicLogger.LOGGER.info(message + " as stated in DB");
                    responseModel = new VerifySessionResponseModel(132, message, null);
                    return Response.status(Response.Status.OK).entity(responseModel).build();
                case 3:
                    message = "Session is expired.";
                    BasicLogger.LOGGER.info(message + " as stated in DB");
                    responseModel = new VerifySessionResponseModel(131, message, null);
                    return Response.status(Response.Status.OK).entity(responseModel).build();
                case 4:
                    message = "Session is revoked.";
                    BasicLogger.LOGGER.info(message + " as stated in DB");
                    responseModel = new VerifySessionResponseModel(133, message, null);
                    return Response.status(Response.Status.OK).entity(responseModel).build();
                case -1:
                    message = "Internal server error";
                    BasicLogger.LOGGER.info(message);
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }

            // If the session status in DB is valid, double check validity
            code = session.isDataValid();
            switch(code) {
                case 131:
                    //Update session to be expired
                    DBUses.updateSessionStatus(session.getSessionID().toString(), 3);
                    //Send response
                    message = "Session is expired.";
                    BasicLogger.LOGGER.info(message + " Determined after looking at timings.");
                    responseModel = new VerifySessionResponseModel(code, message, null);
                    break;
                case 133:
                    // Update session to be revoked
                    DBUses.updateSessionStatus(session.getSessionID().toString(), 4);
                    //Send response
                    message = "Session is revoked.";
                    BasicLogger.LOGGER.info(message + " Determined after looking at timings.");
                    responseModel = new VerifySessionResponseModel(code, message, null);
                    break;
                case 129:
                    // Create a new session and set all previous to revoked
                    Session newSession = DBUses.createSession(email);
                    // Send Response
                    message = "Session is active.";
                    BasicLogger.LOGGER.info(message + " Determined after looking at timings.");
                    int newCode = code + 1; //to indicate the newly created session is active
                    responseModel = new VerifySessionResponseModel(newCode, message, newSession.getSessionID().toString());
                    break;
                case 130:
                    // Update the session's lastUsed value in DB
                    DBUses.updateSession(session);
                    message = "Session is active.";
                    BasicLogger.LOGGER.info(message + " Determined after looking at timings.");
                    responseModel = new VerifySessionResponseModel(code, message, session.getSessionID().toString());
                    break;
            }
            return Response.status(Response.Status.OK).entity(responseModel).build();

        } catch(Exception e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                BasicLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new VerifySessionResponseModel(-2, "JSON Mapping Exception. Unable to map JSON to POJO", null);
            } else if (e instanceof JsonParseException) {
                BasicLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new VerifySessionResponseModel(-3, "JSON Parse Exception. Unable to parse JSON.", null);
            } else {
                BasicLogger.LOGGER.warning("IOException.");
                responseModel = new VerifySessionResponseModel(-2, "Invalid request format", null);
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }
    }
}
