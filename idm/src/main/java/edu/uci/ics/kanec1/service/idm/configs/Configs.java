package edu.uci.ics.kanec1.service.idm.configs;

import edu.uci.ics.kanec1.service.idm.logger.BasicLogger;
import edu.uci.ics.kanec1.service.idm.models.ConfigsModel;

public class Configs {
    // Default service configs
    private final String DEFAULT_SCHEME = "http://";
    private final String DEFAULT_HOSTNAME = "0.0.0.0";
    private final int    DEFAULT_PORT = 1234;
    private final String DEFAULT_PATH = "/api/basicService";
    // Default logger configs
    private final String DEFAULT_OUTPUTDIR = "./logs/";
    private final String DEFAULT_OUTPUTFILE = "basicService.log";
    // Default  database configs
    private final String DEFAULT_DBUSERNAME = "kanec1";
    private final String DEFAULT_DBPASSWORD = "cs122b";
    private final String DEFAULT_DBHOSTNAME = "localhost";
    private final String DEFAULT_DBDRIVER = "mysql";
    private final int    DEFAULT_DBPORT = 3306;
    private final String DEFAULT_DBNAME = "basicdb";
    private final String DEFAULT_DBSETTINGS = "?autoReconnect=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=PST";
    // Default session configs
    private final int DEFAULT_TIMEOUT = 600000;
    private final int DEFAULT_EXPIRATION = 1800000;

    // Service configs
    private String scheme;
    private String hostName;
    private int    port;
    private String path;

    // BasicLogger configs
    private String outputDir;
    private String outputFile;

    // Database configs
    private String dbUsername;
    private String dbPassword;
    private String dbHostname;
    private String dbDriver;
    private int    dbPort;
    private String dbName;
    private String dbSettings;

    // Session configs
    private int timeout;
    private int expiration;

    public Configs() {
        scheme = DEFAULT_SCHEME;
        hostName = DEFAULT_HOSTNAME;
        port = DEFAULT_PORT;
        path = DEFAULT_PATH;
        outputDir = DEFAULT_OUTPUTDIR;
        outputFile = DEFAULT_OUTPUTFILE;
        dbUsername = DEFAULT_DBUSERNAME;
        dbPassword = DEFAULT_DBPASSWORD;
        dbHostname = DEFAULT_DBHOSTNAME;
        dbDriver = DEFAULT_DBDRIVER;
        dbPort = DEFAULT_DBPORT;
        dbName = DEFAULT_DBNAME;
        dbSettings = DEFAULT_DBSETTINGS;
        timeout = DEFAULT_TIMEOUT;
        expiration = DEFAULT_EXPIRATION;
    }

    public Configs(ConfigsModel cm) throws NullPointerException {
        if (cm == null) {
            throw new NullPointerException("Unable to create Configs from ConfigsModel.");
        } else {
            // Set service configs
            scheme = cm.getServiceConfig().get("scheme");
            if (scheme == null) {
                scheme = DEFAULT_SCHEME;
                BasicLogger.LOGGER.info("Scheme not found in configuration file. Using default.");
            } else {
                BasicLogger.LOGGER.info("Scheme: " + scheme);
            }

            hostName = cm.getServiceConfig().get("hostName");
            if (hostName == null) {
                hostName = DEFAULT_HOSTNAME;
                BasicLogger.LOGGER.info("Hostname not found in configuration file. Using default.");
            } else {
                BasicLogger.LOGGER.info("Hostname: " + hostName);
            }

            port = Integer.parseInt(cm.getServiceConfig().get("port"));
            if (port == 0) {
                port = DEFAULT_PORT;
                BasicLogger.LOGGER.info("Port not found in configuration file. Using default.");
            } else if (port < 1024 || port > 65536) {
                port = DEFAULT_PORT;
                BasicLogger.LOGGER.info("Port is not within valid range. Using default.");
            } else {
                BasicLogger.LOGGER.info("Port: " + port);
            }

            path = cm.getServiceConfig().get("path");
            if (path == null) {
                path = DEFAULT_PATH;
                BasicLogger.LOGGER.info("Path not found in configuration file. Using default.");
            } else {
                BasicLogger.LOGGER.info("Path: " + path);
            }

            // Set logger configs
            outputDir = cm.getLoggerConfig().get("outputDir");
            if (outputDir == null) {
                outputDir = DEFAULT_OUTPUTDIR;
                BasicLogger.LOGGER.info("Logging output directory not found in configuration file. Using default.");
            } else {
                BasicLogger.LOGGER.info("Logging output directory: " + outputDir);
            }

            outputFile = cm.getLoggerConfig().get("outputFile");
            if (outputFile == null) {
                outputFile = DEFAULT_OUTPUTFILE;
                BasicLogger.LOGGER.info("Logging output file not found in configuration file. Using default.");
            } else {
                BasicLogger.LOGGER.info("Logging output file: " + outputFile);
            }

            dbUsername = cm.getDatabaseConfig().get("dbUsername");
            if(dbUsername == null) {
                dbUsername = DEFAULT_DBUSERNAME;
                BasicLogger.LOGGER.info("DB username not found in configuration file. Using default.");
            } else {
                BasicLogger.LOGGER.info("Logging DB username: " + dbUsername);
            }

            dbPassword = cm.getDatabaseConfig().get("dbPassword");
            if(dbPassword == null) {
                dbPassword = DEFAULT_DBPASSWORD;
                BasicLogger.LOGGER.info("DB password not found in configuration file. Using default.");
            } else {
                BasicLogger.LOGGER.info("Logging DB password: " + dbPassword);
            }

            dbHostname = cm.getDatabaseConfig().get("dbHostname");
            if(dbHostname == null) {
                dbHostname = DEFAULT_DBHOSTNAME;
                BasicLogger.LOGGER.info("DB hostname not found in configuration file. Using default.");
            } else {
                BasicLogger.LOGGER.info("Logging DB hostname: " + dbHostname);
            }

            dbPort = Integer.parseInt(cm.getDatabaseConfig().get("dbPort"));
            if (dbPort == 0) {
                dbPort = DEFAULT_DBPORT;
                BasicLogger.LOGGER.info("DB port not found in configuration file. Using default.");
            } else if (dbPort < 1024 || dbPort > 65536) {
                dbPort = DEFAULT_DBPORT;
                BasicLogger.LOGGER.info("DB port is not within valid range. Using default.");
            } else {
                BasicLogger.LOGGER.info("DB port: " + dbPort);
            }

            dbDriver = cm.getDatabaseConfig().get("dbDriver");
            if(dbDriver == null) {
                dbDriver = DEFAULT_DBDRIVER;
                BasicLogger.LOGGER.info("DB driver not found in configuration file. Using default.");
            } else {
                BasicLogger.LOGGER.info("Logging DB driver: " + dbDriver);
            }

            dbName = cm.getDatabaseConfig().get("dbName");
            if(dbName == null) {
                dbName = DEFAULT_DBNAME;
                BasicLogger.LOGGER.info("DB name not found in configuration file. Using default.");
            } else {
                BasicLogger.LOGGER.info("Logging DB name: " + dbName);
            }

            dbSettings = cm.getDatabaseConfig().get("dbSettings");
            if(dbSettings == null) {
                dbSettings = DEFAULT_DBSETTINGS;
                BasicLogger.LOGGER.info("DB settings not found in configuration file. Using default.");
            } else {
                BasicLogger.LOGGER.info("Logging DB settings: " + dbSettings);
            }

            timeout = Integer.parseInt(cm.getSessionConfig().get("timeout"));
            if (timeout == 0) {
                timeout = DEFAULT_TIMEOUT;
                BasicLogger.LOGGER.info("Session timeout not found in configuration file. Using default.");
            } else if (timeout < 0) {
                timeout = DEFAULT_TIMEOUT;
                BasicLogger.LOGGER.info("Timeout is not within valid range. Using default.");
            } else {
                BasicLogger.LOGGER.info("Timeout: " + timeout + " ms");
            }

            expiration = Integer.parseInt(cm.getSessionConfig().get("expiration"));
            if (expiration == 0) {
                expiration = DEFAULT_EXPIRATION;
                BasicLogger.LOGGER.info("Session expiration time not found in configuration file. Using default.");
            } else if (expiration < 0) {
                expiration = DEFAULT_EXPIRATION;
                BasicLogger.LOGGER.info("Session expiration time is not within valid range. Using default.");
            } else {
                BasicLogger.LOGGER.info("Expiration: " + expiration + " ms");
            }

        }
    }

    public void currentConfigs() {
        BasicLogger.LOGGER.config("Scheme: " + scheme);
        BasicLogger.LOGGER.config("Hostname: " + hostName);
        BasicLogger.LOGGER.config("Port: " + port);
        BasicLogger.LOGGER.config("Path: " + path);
        BasicLogger.LOGGER.config("BasicLogger output directory: " + outputDir);
        BasicLogger.LOGGER.config("BasicLogger output file: " + outputFile);
        BasicLogger.LOGGER.config("DB username: " + dbUsername);
        BasicLogger.LOGGER.config("DB password: found in the configuration file.");
        BasicLogger.LOGGER.config("DB hostname: " + dbHostname);
        BasicLogger.LOGGER.config("DB port: " + dbPort);
        BasicLogger.LOGGER.config("DB driver" + dbDriver);
        BasicLogger.LOGGER.config("DB name: " + dbName);
        BasicLogger.LOGGER.config("DB settings: " + dbSettings);
        BasicLogger.LOGGER.config("Session timeout: " + timeout);
        BasicLogger.LOGGER.config("Session expiration: " + expiration);
    }

    public String getScheme() {
        return scheme;
    }

    public String getHostName() {
        return hostName;
    }

    public int getPort() {
        return port;
    }

    public String getPath() {
        return path;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getDbHostname() {
        return dbHostname;
    }

    public String getDbDriver() {
        return dbDriver;
    }

    public int getDbPort() {
        return dbPort;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbSettings() {
        return dbSettings;
    }

    public int getTimeout() {
        return timeout;
    }

    public int getExpiration() {
        return expiration;
    }
}
