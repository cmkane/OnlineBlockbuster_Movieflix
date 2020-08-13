package edu.uci.ics.kanec1.service.api_gateway.threadpool;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.uci.ics.kanec1.service.api_gateway.GatewayService;
import edu.uci.ics.kanec1.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.kanec1.service.api_gateway.models.RequestModel;
import org.glassfish.jersey.internal.util.ExceptionUtils;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

public class Worker extends Thread {
    int id;
    ThreadPool threadPool;

    private Worker(int id, ThreadPool threadPool) {
        this.id = id;
        this.threadPool = threadPool;
    }

    public static Worker CreateWorker(int id, ThreadPool threadPool) {
        return new Worker(id, threadPool);
    }

    public void process(ClientRequest request) {
        // TODO Process the ClientRequest
        try {
            Client client = ClientBuilder.newClient();
            client.register(JacksonFeature.class);

            Map<String, String> queryParams = request.getQueryParams();

            String URI = request.getURI();

            //ServiceLogger.LOGGER.info("Setting path to endpoint...");
            String ENPOINT_PATH = request.getEndpoint();

            // Create a WebTarget to send a request at
            //ServiceLogger.LOGGER.info("Building WebTarget...");
            WebTarget webTarget = client.target(URI).path(ENPOINT_PATH);
            if(queryParams != null) {
                for(Map.Entry<String,String> entry : queryParams.entrySet()) {
                    webTarget = webTarget.queryParam(entry.getKey(), entry.getValue());
                }
            }
            ServiceLogger.LOGGER.info("Thread [" + id + "] " + webTarget.toString());

            // Create an InvocationBuilder to create the HTTP request
            //ServiceLogger.LOGGER.info("Starting invocation builder...");
            Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON).header("email", request.getEmail())
                    .header("sessionID", request.getSessionID()).header("transactionID", request.getTransactionID());

            // Set the payload
            //ServiceLogger.LOGGER.info("Setting payload of the request");
            RequestModel requestModel = request.getRequest();

            // Send the request and save it to a Response
            ServiceLogger.LOGGER.info("Thread [" + id + "] Sending request...");
            Response response;
            if(requestModel == null) {
                if(request.getType().equals("GET")){
                    ServiceLogger.LOGGER.info("Attempting GET Request.");
                    response = invocationBuilder.get();
                } else{
                    ServiceLogger.LOGGER.info("Attempting DELETE Request.");
                    response = invocationBuilder.delete();
                }
            }
            else {
                ServiceLogger.LOGGER.info("Attempting POST Request.");
                ServiceLogger.LOGGER.info("requestModel = " + requestModel.toString());
                response = invocationBuilder.post(Entity.entity(requestModel, MediaType.APPLICATION_JSON));
            }
            ServiceLogger.LOGGER.info("Thread [" + id + "] Sent!");

            Integer status = response.getStatus();
            ServiceLogger.LOGGER.info("Thread [" + id + "] Received status " + status);
            String jsonText = response.readEntity(String.class);
            ServiceLogger.LOGGER.info("Thread [" + id + "] jsonText: " + jsonText);

            String transactionId = request.getTransactionID();
            String email = request.getEmail();
            String sessionId = request.getSessionID();

            Connection connection = GatewayService.getConPool().requestCon();
            String SQL = "INSERT INTO responses (transactionid, email, sessionid, response, httpstatus)" +
                    " VALUES (?,?,?,?,?);";
            PreparedStatement ps = connection.prepareStatement(SQL);
            if(transactionId != null) ps.setString(1, transactionId);
            if(email != null) ps.setString(2, email);
            else ps.setNull(2, Types.VARCHAR);
            if(sessionId != null) ps.setString(3, sessionId);
            else ps.setNull(3, Types.VARCHAR);
            if(jsonText != null) ps.setString(4, jsonText);
            ps.setInt(5, status);
            ServiceLogger.LOGGER.info("Thread [" + id + "] Attempting Insert: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Thread [" + id + "] Insert Successful.");
            GatewayService.getConPool().releaseCon(connection);

        } catch(SQLException e) {
            ServiceLogger.LOGGER.warning("Thread [" + id + "] Unable to enter response into DB.");
            e.printStackTrace();
        } catch(Exception e) {
            ServiceLogger.LOGGER.warning("Thread [" + id + "] Unable to enter response into DB.");
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            if(threadPool.getQueue().isEmpty()) continue;
            ServiceLogger.LOGGER.info("Thread [" + id + "] Trying to dequeue and process.");
            ClientRequest request = threadPool.getQueue().dequeue();
            if(request == null) {
                ServiceLogger.LOGGER.info("Thread [" + id + "] request is null");
                continue;
            }
            ServiceLogger.LOGGER.info("Thread [" + id + "] request was not null");
            process(request);
            ServiceLogger.LOGGER.info("Thread [" + id + "] Thread still alive.");
        }
    }
}
