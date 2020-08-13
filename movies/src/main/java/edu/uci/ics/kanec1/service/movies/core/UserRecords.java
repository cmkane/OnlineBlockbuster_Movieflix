package edu.uci.ics.kanec1.service.movies.core;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.kanec1.service.movies.MovieService;
import edu.uci.ics.kanec1.service.movies.configs.MovieConfigs;
import edu.uci.ics.kanec1.service.movies.logger.ServiceLogger;
import edu.uci.ics.kanec1.service.movies.models.VerifyPrivilegeRequestModel;
import edu.uci.ics.kanec1.service.movies.models.VerifyPrivilegeResponseModel;
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
                MovieConfigs myConfigs = MovieService.getMovieConfigs();

                Client client = ClientBuilder.newClient();
                client.register(JacksonFeature.class);

                // Create a new Client
                ServiceLogger.LOGGER.info("Building client...");

                // Get the URI for the IDM
                ServiceLogger.LOGGER.info("Building URI...");
                String IDM_URI = myConfigs.getIdmScheme() + myConfigs.getIdmHostName() + ":" + myConfigs.getIdmPort() + myConfigs.getIdmPath();

                ServiceLogger.LOGGER.info("Setting path to endpoint...");
                String IDM_ENPOINT_PATH = myConfigs.getIdmPrivilege();

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
}
