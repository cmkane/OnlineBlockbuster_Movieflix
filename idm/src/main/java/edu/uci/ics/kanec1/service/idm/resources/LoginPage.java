package edu.uci.ics.kanec1.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.kanec1.service.idm.core.DBUses;
import edu.uci.ics.kanec1.service.idm.core.Validator;
import edu.uci.ics.kanec1.service.idm.logger.BasicLogger;
import edu.uci.ics.kanec1.service.idm.models.LoginRequestModel;
import edu.uci.ics.kanec1.service.idm.models.LoginResponseModel;
import edu.uci.ics.kanec1.service.idm.models.RegisterRequestModel;
import edu.uci.ics.kanec1.service.idm.models.RegisterResponseModel;
import edu.uci.ics.kanec1.service.idm.security.Session;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("login")
public class LoginPage {
    private static final int RESULTCODE_SUCCESS = 120;
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(String jsonText) {
        BasicLogger.LOGGER.info("Received request to login. . .");
        BasicLogger.LOGGER.info("Request:\n"+jsonText);

        ObjectMapper mapper = new ObjectMapper();
        LoginRequestModel requestModel = null;
        LoginResponseModel responseModel = null;
        int code;
        String message;

        try {
            requestModel = mapper.readValue(jsonText, LoginRequestModel.class);
            String email = requestModel.getEmail();
            char[] pword = requestModel.getPassword();

            code = Validator.validateUserCredentials(email, pword);
            if(code != 1) {
                switch(code) {
                    case -12:
                        BasicLogger.LOGGER.info("Password has invalid length (cannot be empty/null)");
                        message = "Password has invalid length (cannot be empty/null)";
                        responseModel = new LoginResponseModel(code, message, null);
                        return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
                    case -11:
                        BasicLogger.LOGGER.info("Email address has invalid format.");
                        message = "Email address has invalid format.";
                        responseModel = new LoginResponseModel(code, message, null);
                        return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
                    case -10:
                        BasicLogger.LOGGER.info("Email address has invalid length.");
                        message = "Email address has invalid length.";
                        responseModel = new LoginResponseModel(code, message, null);
                        return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
                    default:
                        break;
                }
            }
            BasicLogger.LOGGER.info("Email and password pass basic requirements.");

            // Check if user exists and if password matches
            code = DBUses.checkLoginCredentials(email, pword);
            if(code != 1) {
                switch(code) {
                    case 14:
                        message = "User not found.";
                        BasicLogger.LOGGER.info(message);
                        responseModel = new LoginResponseModel(code, message, null);
                        return Response.status(Response.Status.OK).entity(responseModel).build();
                    case 11:
                        message = "Passwords do not match.";
                        BasicLogger.LOGGER.info(message);
                        responseModel = new LoginResponseModel(code, message, null);
                        return Response.status(Response.Status.OK).entity(responseModel).build();
                    case -1:
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                    default:
                        break;
                }
            }

            Session session = DBUses.createSession(email);
            if(session == null) {
                BasicLogger.LOGGER.warning("Unable to create session.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }

            message = "User logged in successfully.";
            BasicLogger.LOGGER.info("User logged in successfully with sessionID="+session.getSessionID());
            responseModel = new LoginResponseModel(RESULTCODE_SUCCESS, message, session.getSessionID().toString());


        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                BasicLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new LoginResponseModel(-2, "JSON Mapping Exception. Unable to map JSON to POJO", null);
            } else if (e instanceof JsonParseException) {
                BasicLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new LoginResponseModel(-3, "JSON Parse Exception. Unable to parse JSON.", null);
            } else {
                BasicLogger.LOGGER.warning("IOException.");
                responseModel = new LoginResponseModel(-2, "Invalid request format", null);
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        return Response.status(Response.Status.OK).entity(responseModel).build();
    }
}
