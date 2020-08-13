package edu.uci.ics.kanec1.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.kanec1.service.idm.core.DBUses;
import edu.uci.ics.kanec1.service.idm.core.Validator;
import edu.uci.ics.kanec1.service.idm.logger.BasicLogger;
import edu.uci.ics.kanec1.service.idm.models.RegisterRequestModel;
import edu.uci.ics.kanec1.service.idm.models.RegisterResponseModel;
import edu.uci.ics.kanec1.service.idm.security.Crypto;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;

@Path("register")
public class RegisterPage {
    public static final int DEFAULT_PLEVEL = 5;
    public static final int DEFAULT_STATUS = 1;
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(String jsonText) {
        BasicLogger.LOGGER.info("Received request to register a new user.");
        BasicLogger.LOGGER.info("Request:\n"+jsonText);

        ObjectMapper mapper = new ObjectMapper();
        RegisterRequestModel requestModel = null;
        RegisterResponseModel responseModel = null;
        String message;
        int code;

        try {
            requestModel = mapper.readValue(jsonText, RegisterRequestModel.class);
            Response.Status stat = null;
            BasicLogger.LOGGER.info("Registering user with email: "+requestModel.getEmail());

            String email = requestModel.getEmail();
            char[] pass = requestModel.getPassword();

            int emailCode = Validator.emailValidator(email);
            int passCode = Validator.passwordValidator(pass);
            if (emailCode != 1) {
                switch (emailCode) {
                    case -11:
                        responseModel = new RegisterResponseModel(emailCode, "Email address has invalid format.");
                        stat = Response.Status.BAD_REQUEST;
                        BasicLogger.LOGGER.info("Email has invalid format.");
                        break;
                    case -10:
                        responseModel = new RegisterResponseModel(emailCode, "Email address has invalid length.");
                        stat = Response.Status.BAD_REQUEST;
                        BasicLogger.LOGGER.info("Email has invalid length.");
                        break;
                    default:
                        BasicLogger.LOGGER.info("Invalid email.");
                        stat = Response.Status.BAD_REQUEST;
                        break;
                }
                return Response.status(stat).entity(responseModel).build();
            }
            if (passCode != 1) {
                switch (passCode) {
                    case -12:
                        responseModel = new RegisterResponseModel(passCode, "Password has invalid length. (Cannot be empty/null)");
                        stat = Response.Status.BAD_REQUEST;
                        BasicLogger.LOGGER.info("Password has invalid length.");
                        break;
                    case 12:
                        responseModel = new RegisterResponseModel(passCode, "Password does not meet length requirements.");
                        stat = Response.Status.OK;
                        BasicLogger.LOGGER.info("Password does not meet length requirements.");
                        break;
                    case 13:
                        responseModel = new RegisterResponseModel(passCode, "Password does not meet character requirements.");
                        stat = Response.Status.OK;
                        BasicLogger.LOGGER.info("Password does not meet character requirements.");
                        break;
                    default:
                        BasicLogger.LOGGER.info("Invalid password");
                        stat = Response.Status.BAD_REQUEST;
                        break;
                }
                return Response.status(stat).entity(responseModel).build();
            }

            BasicLogger.LOGGER.info("Email and password meet basic requirements.");

            byte[] salt = Crypto.genSalt();
            byte[] hashedPassword = Crypto.hashPassword(pass, salt, Crypto.ITERATIONS, Crypto.KEY_LENGTH);
            String hashPass = Crypto.getHashedPass(hashedPassword);

            code = DBUses.registerUser(email, DEFAULT_PLEVEL, DEFAULT_STATUS, Crypto.getHashedPass(salt), hashPass);
            switch(code) {
                case -1:
                    BasicLogger.LOGGER.warning("SQLException occurred when accessing database.");
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                case 16:
                    BasicLogger.LOGGER.info("Email already in use.");
                    message = "Email already in use.";
                    break;
                case 110:
                    BasicLogger.LOGGER.info("User registered successfully.");
                    message = "User registered successfully.";
                    break;
                default:
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }

            responseModel = new RegisterResponseModel(code, message);

        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                BasicLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new RegisterResponseModel(-2, "JSON Mapping Exception. Unable to map JSON to POJO");
            } else if (e instanceof JsonParseException) {
                BasicLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new RegisterResponseModel(-3, "JSON Parse Exception. Unable to parse JSON.");
            } else {
                BasicLogger.LOGGER.warning("IOException.");
                responseModel = new RegisterResponseModel(-2, "Invalid request format");
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }

        return Response.status(Response.Status.OK).entity(responseModel).build();
    }


}
