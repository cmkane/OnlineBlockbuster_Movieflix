package edu.uci.ics.kanec1.service.idm.resources;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.kanec1.service.idm.core.DBUses;
import edu.uci.ics.kanec1.service.idm.core.Validator;
import edu.uci.ics.kanec1.service.idm.logger.BasicLogger;
import edu.uci.ics.kanec1.service.idm.models.VerifyPrivilegeRequestModel;
import edu.uci.ics.kanec1.service.idm.models.VerifyPrivilegeResponseModel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("privilege")
public class VerifyPrivilegePage {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyPrivilege(String jsonText) {
        BasicLogger.LOGGER.info("Received request for privilege verification.");
        BasicLogger.LOGGER.info("Request:\n"+jsonText);

        ObjectMapper mapper = new ObjectMapper();
        VerifyPrivilegeRequestModel requestModel = null;
        VerifyPrivilegeResponseModel responseModel = null;
        int code;
        String message;
        String email;
        int plevel;

        try {
            requestModel = mapper.readValue(jsonText, VerifyPrivilegeRequestModel.class);
            email = requestModel.getEmail();
            plevel = requestModel.getPlevel();

            code = Validator.privilegeValidator(plevel);
            switch(code) {
                case -14:
                    message = "Privilege level out of valid range.";
                    BasicLogger.LOGGER.info(message);
                    responseModel = new VerifyPrivilegeResponseModel(code, message);
                    return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            // Check if email is valid
            code = Validator.emailValidator(email);
            if (code != 1) {
                switch (code) {
                    case -11:
                        responseModel = new VerifyPrivilegeResponseModel(code, "Email address has invalid format.");
                        BasicLogger.LOGGER.info("Email has invalid format.");
                        break;
                    case -10:
                        responseModel = new VerifyPrivilegeResponseModel(code, "Email address has invalid length.");
                        BasicLogger.LOGGER.info("Email has invalid length.");
                        break;
                }
                return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
            }

            code = DBUses.checkUserPrivilege(email, plevel);
            switch(code) {
                case 14:
                    message = "User not found";
                    BasicLogger.LOGGER.info(message);
                    responseModel = new VerifyPrivilegeResponseModel(code, message);
                    break;
                case 140:
                    message = "User has sufficient privilege level.";
                    BasicLogger.LOGGER.info(message);
                    responseModel = new VerifyPrivilegeResponseModel(code, message);
                    break;
                case 141:
                    message = "User has insufficient privilege level.";
                    BasicLogger.LOGGER.info(message);
                    responseModel = new VerifyPrivilegeResponseModel(code, message);
                    break;
                default:
                    message = "Internal server error.";
                    BasicLogger.LOGGER.info(message);
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }

            return Response.status(Response.Status.OK).entity(responseModel).build();

        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof JsonMappingException) {
                BasicLogger.LOGGER.warning("Unable to map JSON to POJO.");
                responseModel = new VerifyPrivilegeResponseModel(-2, "JSON Mapping Exception. Unable to map JSON to POJO");
            } else if (e instanceof JsonParseException) {
                BasicLogger.LOGGER.warning("Unable to parse JSON.");
                responseModel = new VerifyPrivilegeResponseModel(-3, "JSON Parse Exception. Unable to parse JSON.");
            } else {
                BasicLogger.LOGGER.warning("IOException.");
                responseModel = new VerifyPrivilegeResponseModel(-2, "Invalid request format");
            }
            return Response.status(Response.Status.BAD_REQUEST).entity(responseModel).build();
        }
    }
}
