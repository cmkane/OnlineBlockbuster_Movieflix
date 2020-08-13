package edu.uci.ics.kanec1.service.api_gateway.connectionpool;

import edu.uci.ics.kanec1.service.api_gateway.logger.ServiceLogger;
import org.glassfish.jersey.internal.util.ExceptionUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

public class ConnectionPool {
    LinkedList<Connection> connections;
    String driver;
    String url;
    String username;
    String password;

    public ConnectionPool(int numCons, String driver, String url, String username, String password) {
        connections = new LinkedList<Connection>();
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
        for(int x = 0; x < numCons; x++) {
            Connection con = createConnection();
            if(con != null) connections.add(con);
        }
        ServiceLogger.LOGGER.info(numCons + " connection(s) created successfully.");
    }

    public Connection requestCon() {
        if(connections.isEmpty()) {
            // make a new connection and return it
            Connection con = createConnection();
            return con;
        } else {
            // Pops the connection at the head of the list
            return connections.pop();
        }
    }

    public void releaseCon(Connection con) {
        // Adds the released connection to the end of the linked list
        connections.add(con);
    }

    private Connection createConnection() {
        ServiceLogger.LOGGER.info("Creating new connection...");
        Connection con;
        try {
            Class.forName(driver);
            ServiceLogger.LOGGER.config("Database URL: " + url);
            con = DriverManager.getConnection(url, username, password);
            ServiceLogger.LOGGER.config("Connected to database: " + url);
            return con;
        } catch (ClassNotFoundException | SQLException | NullPointerException e) {
            ServiceLogger.LOGGER.severe("Unable to connect to database.\n" + ExceptionUtils.exceptionStackTraceAsString(e));
            return null;
        }
    }
}
