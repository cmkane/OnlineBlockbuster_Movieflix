package edu.uci.ics.kanec1.service.api_gateway.core;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.kanec1.service.api_gateway.GatewayService;
import edu.uci.ics.kanec1.service.api_gateway.configs.GatewayConfigs;
import edu.uci.ics.kanec1.service.api_gateway.configs.IDMConfigs;
import edu.uci.ics.kanec1.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.kanec1.service.api_gateway.models.idm.VerifyPrivilegeRequestModel;
import edu.uci.ics.kanec1.service.api_gateway.models.idm.VerifyPrivilegeResponseModel;
import edu.uci.ics.kanec1.service.api_gateway.models.idm.VerifySessionRequestModel;
import edu.uci.ics.kanec1.service.api_gateway.models.idm.VerifySessionResponseModel;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class UserRecords {
    public static boolean userSufficientPrivilege(String email, int plevel) {
        ServiceLogger.LOGGER.info("Verifying privilege level...");

        // Check that status code of the request
            try {
                if(email == null || email.length() <= 1) return false;
                IDMConfigs myConfigs = GatewayService.getIdmConfigs();

                Client client = ClientBuilder.newClient();
                client.register(JacksonFeature.class);

                // Create a new Client
                ServiceLogger.LOGGER.info("Building client...");

                // Get the URI for the IDM
                ServiceLogger.LOGGER.info("Building URI...");
                String IDM_URI = myConfigs.getIdmUri();

                ServiceLogger.LOGGER.info("Setting path to endpoint...");
                String IDM_ENPOINT_PATH = myConfigs.getEPUserPrivilegeVerify();

                // Create a WebTarget to send a request at
                ServiceLogger.LOGGER.info("Building WebTarget...");
                WebTarget webTarget = client.target(IDM_URI).path(IDM_ENPOINT_PATH);

                // Create an InvocationBuilder to create the HTTP request
                ServiceLogger.LOGGER.info("Starting invocation builder...");
                Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

                // Set the payload
                ServiceLogger.LOGGER.info("Setting payload of the request");
                VerifyPrivilegeRequestModel requestModel = new VerifyPrivilegeRequestModel(email, plevel);

                // Send the request and save it to a Response
                ServiceLogger.LOGGER.info("Sending request...");
                Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
                ServiceLogger.LOGGER.info("Sent!");

                if(response.getStatus() == 200) {
                    ServiceLogger.LOGGER.info("Received status 200!");
                    String jsonText = response.readEntity(String.class);
                    ServiceLogger.LOGGER.info("jsonText: "+jsonText);
                    ObjectMapper mapper = new ObjectMapper();
                    VerifyPrivilegeResponseModel responseModel = null;
                    responseModel = mapper.readValue(jsonText, VerifyPrivilegeResponseModel.class);
                    int resultCode = responseModel.getResultCode();
                    if(resultCode == 140) {
                        ServiceLogger.LOGGER.info("User has sufficient privilege level.");
                        return true;
                    } else {
                        ServiceLogger.LOGGER.info("User does not have sufficient privilege level.");
                    }

                } else {
                    ServiceLogger.LOGGER.info("Received status " + response.getStatus() + " :(");
                }
        } catch(Exception e) {
            if(e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("JsonMappingException occurred when parsing responseModel from IDM");
            } else if(e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("JsonParseException occurred when parsing responseModel from IDM");
            }
            e.printStackTrace();
        }

            return false;
    }

    public static VerifySessionResponseModel verifySession(String email, String sessionID) {
        ServiceLogger.LOGGER.info("Verifying session id...");

        // Check that status code of the request
        try {
            if(email == null || email.length() <= 1) return new VerifySessionResponseModel(-11, null, sessionID);
            IDMConfigs myConfigs = GatewayService.getIdmConfigs();

            Client client = ClientBuilder.newClient();
            client.register(JacksonFeature.class);

            // Get the URI for the IDM
            ServiceLogger.LOGGER.info("Building URI...");
            String IDM_URI = myConfigs.getIdmUri();

            ServiceLogger.LOGGER.info("Setting path to endpoint...");
            String IDM_ENPOINT_PATH = myConfigs.getEPSessionVerify();

            // Create a WebTarget to send a request at
            ServiceLogger.LOGGER.info("Building WebTarget...");
            WebTarget webTarget = client.target(IDM_URI).path(IDM_ENPOINT_PATH);

            // Create an InvocationBuilder to create the HTTP request
            ServiceLogger.LOGGER.info("Starting invocation builder...");
            Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

            // Set the payload
            ServiceLogger.LOGGER.info("Setting payload of the request");
            VerifySessionRequestModel requestModel = new VerifySessionRequestModel(email, sessionID);

            // Send the request and save it to a Response
            ServiceLogger.LOGGER.info("Sending request...");
            Response response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
            ServiceLogger.LOGGER.info("Sent!");

            ObjectMapper mapper = new ObjectMapper();
            if(response.getStatus() != -1) {
                String jsonText = response.readEntity(String.class);
                VerifySessionResponseModel responseModel = mapper.readValue(jsonText, VerifySessionResponseModel.class);
                ServiceLogger.LOGGER.info("resultCode = " + responseModel.getResultCode());
                return responseModel;
            } else {
                return new VerifySessionResponseModel(-1, null, null);
            }

        } catch(Exception e) {
            if(e instanceof JsonMappingException) {
                ServiceLogger.LOGGER.warning("JsonMappingException occurred when parsing responseModel from IDM");
            } else if(e instanceof JsonParseException) {
                ServiceLogger.LOGGER.warning("JsonParseException occurred when parsing responseModel from IDM");
            }
            e.printStackTrace();
        }

        return null;
    }
}
