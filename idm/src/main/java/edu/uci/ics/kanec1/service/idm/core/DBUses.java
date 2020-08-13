package edu.uci.ics.kanec1.service.idm.core;

import edu.uci.ics.kanec1.service.idm.Idm;
import edu.uci.ics.kanec1.service.idm.logger.BasicLogger;
import edu.uci.ics.kanec1.service.idm.security.Crypto;
import edu.uci.ics.kanec1.service.idm.security.Session;
import edu.uci.ics.kanec1.service.idm.security.Token;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DBUses {
    private static final int STATUS_ACTIVE = 1;

    public static int registerUser(String email, int plevel, int status,  String salt, String pword) {
        BasicLogger.LOGGER.info("Starting to registerUser . . .");
        ResultSet rs = null;
        String SQL;

        try {
            BasicLogger.LOGGER.info("Checking if the user with email="+email+" already exists. . .");
            SQL = "SELECT COUNT(*) FROM users WHERE email=?;";
            PreparedStatement pstmt = Idm.getCon().prepareStatement(SQL);
            pstmt.setString(1, email);
            BasicLogger.LOGGER.info("Trying query "+pstmt.toString());
            rs = pstmt.executeQuery();
            BasicLogger.LOGGER.info("Query succeeded.");

            int result = 1;
            while(rs.next()) {
                result = rs.getInt("COUNT(*)");
            }
            if(result >= 1) {
                BasicLogger.LOGGER.info("Email is already in use.");
                return 16;
            }
            BasicLogger.LOGGER.info("No user with this email exists.");

            SQL = "INSERT INTO users (email, plevel, status, salt, pword) VALUES (?, ?, ?, ?, ?);";
            pstmt = Idm.getCon().prepareStatement(SQL);
            pstmt.setString(1, email);
            pstmt.setInt(2, plevel);
            pstmt.setInt(3, status);
            pstmt.setString(4, salt);
            pstmt.setString(5, pword);
            BasicLogger.LOGGER.info("Trying execution "+pstmt.toString());
            boolean success = pstmt.execute();
            BasicLogger.LOGGER.info("Execution succeeded.");

            return 110;

        } catch (SQLException e) {
            BasicLogger.LOGGER.warning("Unable to add user to database.");
            e.printStackTrace();
            return -1;
        }
    }

    public static int checkLoginCredentials(String email, char[] password) {
        BasicLogger.LOGGER.info("Checking user credentials. . .");
        ResultSet rs = null;
        String SQL;
        int count = 0;
        String accountEmail;
        String accountSalt = null;
        String accountPassword = null;
        int accountStatus;

        try {
            SQL = "SELECT * FROM users WHERE email=?;";

            PreparedStatement pstmt = Idm.getCon().prepareStatement(SQL);
            pstmt.setString(1, email);

            BasicLogger.LOGGER.info("Trying query "+pstmt.toString());
            rs = pstmt.executeQuery();
            BasicLogger.LOGGER.info("Execution succeeded.");

            while(rs.next()) {
                count++;
                accountEmail = rs.getString("email");
                accountSalt = rs.getString("salt");
                accountPassword = rs.getString("pword");
                accountStatus = rs.getInt("status");
            }

            if(count == 0) {
                BasicLogger.LOGGER.info("No user exists with the given email: "+email);
                return 14;
            }

            byte[] salt = Token.convert(accountSalt);
            byte[] hashedPassword = Crypto.hashPassword(password, salt, Crypto.ITERATIONS, Crypto.KEY_LENGTH);
            String hashPass = Crypto.getHashedPass(hashedPassword);

            if(!hashPass.equals(accountPassword)) {
                BasicLogger.LOGGER.info("User exists, but passwords do not match.");
                return 11;
            }
            BasicLogger.LOGGER.info("Password matches. User credentials verified.");

            return 1;

        } catch(SQLException e) {
            BasicLogger.LOGGER.info("Unable to verify user email and password.");
            e.printStackTrace();
            return -1;
        }
    }

    public static Session createSession(String email) {
        BasicLogger.LOGGER.info("Creating session for email: "+email);

        PreparedStatement pstmt;
        ResultSet rs = null;
        String SQL = null;
        int count;

        try {
            SQL = "UPDATE sessions SET status = 4 WHERE email=?;";
            pstmt = Idm.getCon().prepareStatement(SQL);
            pstmt.setString(1, email);

            BasicLogger.LOGGER.info("Trying update "+pstmt.toString());
            count = pstmt.executeUpdate();
            BasicLogger.LOGGER.info("Update succeeded. Changed "+count+" session(s) to revoked status.");


            Session session = Session.createSession(email);
            SQL = "INSERT INTO sessions (sessionID, email, status, timeCreated, lastUsed, exprTime) VALUES (?, ?, ?, ?, ?, ?);";
            pstmt = Idm.getCon().prepareStatement(SQL);
            pstmt.setString(1, session.getSessionID().toString());
            pstmt.setString(2, session.getEmail());
            pstmt.setInt(3, STATUS_ACTIVE);
            pstmt.setTimestamp(4, session.getTimeCreated());
            pstmt.setTimestamp(5, session.getLastUsed());
            pstmt.setTimestamp(6, session.getExprTime());

            BasicLogger.LOGGER.info("Trying insert "+pstmt.toString());
            pstmt.execute();
            BasicLogger.LOGGER.info("Insert succeeded.");

            BasicLogger.LOGGER.info("Successfully created a session for user with email:"+session.getEmail());
            return session;

        } catch(SQLException e) {
            BasicLogger.LOGGER.info("Unable to create a new session.");
            e.printStackTrace();
            return null;
        }
    }

    public static int verifyUserExists(String email) {
        BasicLogger.LOGGER.info("Checking if user with email "+email+" exists...");
        ResultSet rs = null;
        String SQL;
        int result = 0;

        try {
            SQL = "SELECT COUNT(*) FROM users WHERE email=?";
            PreparedStatement pstmt = Idm.getCon().prepareStatement(SQL);
            pstmt.setString(1, email);
            BasicLogger.LOGGER.info("Trying query "+pstmt.toString());
            rs = pstmt.executeQuery();
            BasicLogger.LOGGER.info("Execution succeeded.");
            while(rs.next()) {
                result = rs.getInt("COUNT(*)");
            }
            if(result != 1) {
                return 0;
            }
            else {
                return 1;
            }

        } catch (SQLException e) {
            BasicLogger.LOGGER.info("Unable to verify if user exists.");
            e.printStackTrace();
            return 0;
        }
    }

    public static int checkSessionStatus(String sessionID) {
        BasicLogger.LOGGER.info("Getting session status with sessionID: "+sessionID);

        ResultSet rs;
        Session mySession = null;
        String SQL;

        int currStatus = -1;
        int count = 0;

        try {
            SQL = "SELECT * FROM sessions WHERE sessionID=?";
            PreparedStatement pstmt = Idm.getCon().prepareStatement(SQL);
            pstmt.setString(1, sessionID);

            BasicLogger.LOGGER.info("Trying Query "+pstmt.toString());
            rs = pstmt.executeQuery();
            BasicLogger.LOGGER.info("Query successful");

            while(rs.next()) {
                count++;
                currStatus = rs.getInt("status");
            }
            if(count == 0) {
                BasicLogger.LOGGER.info("Session not found.");
                return -1;
            }

            return currStatus;


        } catch (SQLException e) {
            BasicLogger.LOGGER.info("Unable to retrieve session status");
            e.printStackTrace();
            return -1;
        }
    }

    public static Session getSession(String sessionID, String email) {
        BasicLogger.LOGGER.info("Getting session with sessionID: "+sessionID);

        ResultSet rs;
        Session mySession = null;
        String SQL;

        int currStatus;
        Timestamp timeCreated = null;
        Timestamp lastUsed = null;
        Timestamp exprTime = null;
        int count = 0;

        try {
            SQL = "SELECT * FROM sessions WHERE sessionID=? AND email=?";
            PreparedStatement pstmt = Idm.getCon().prepareStatement(SQL);
            pstmt.setString(1, sessionID);
            pstmt.setString(2, email);

            BasicLogger.LOGGER.info("Trying Query "+pstmt.toString());
            rs = pstmt.executeQuery();
            BasicLogger.LOGGER.info("Query successful");

            while(rs.next()) {
                count++;
                currStatus = rs.getInt("status");
                timeCreated = rs.getTimestamp("timeCreated");
                lastUsed = rs.getTimestamp("lastUsed");
                exprTime = rs.getTimestamp("exprTime");
            }
            if(count == 0) {
                BasicLogger.LOGGER.info("Session not found.");
                return null;
            }

            BasicLogger.LOGGER.info("Rebuilding session.");
            mySession = Session.rebuildSession(email, Token.rebuildToken(sessionID), timeCreated, lastUsed, exprTime);

            return mySession;


        } catch (SQLException e) {
            BasicLogger.LOGGER.info("Unable to retrieve session");
            e.printStackTrace();
            return null;
        }
    }

    public static void updateSessionStatus(String sessionID, int newStatus) {
        BasicLogger.LOGGER.info("Updating session with sessionID: "+sessionID+", status: "+newStatus);
        try {
            String SQL = "UPDATE sessions SET status = ? WHERE sessionID = ?;";
            PreparedStatement pstmt = Idm.getCon().prepareStatement(SQL);
            pstmt.setString(2, sessionID);
            pstmt.setInt(1, newStatus);
            BasicLogger.LOGGER.info("Trying update "+pstmt.toString());
            int numUpdated = pstmt.executeUpdate();
            BasicLogger.LOGGER.info("Update succeeded. Updated "+numUpdated+" session(s).");
        } catch (SQLException e) {
            BasicLogger.LOGGER.info("Unable to update session");
            e.printStackTrace();
        }
    }

    public static void updateSession(Session session) {
        // Update lastUsed of session to current time
        session.update();
        BasicLogger.LOGGER.info("Updating session...");
        try {
            String SQL = "UPDATE sessions SET lastUsed = ? WHERE sessionID = ? AND email = ?;";
            PreparedStatement pstmt = Idm.getCon().prepareStatement(SQL);
            pstmt.setTimestamp(1, session.getLastUsed());
            pstmt.setString(2, session.getSessionID().toString());
            pstmt.setString(3, session.getEmail());
            BasicLogger.LOGGER.info("Trying update");
            int numUpdated = pstmt.executeUpdate();
            BasicLogger.LOGGER.info("Update succeeded. Updated "+numUpdated+" session(s).");
        } catch (SQLException e) {
            BasicLogger.LOGGER.info("Unable to update session");
            e.printStackTrace();
        }
    }

    public static int checkUserPrivilege(String email, int plevel) {
        BasicLogger.LOGGER.info("Checking privilege of user with email "+email+" ...");
        BasicLogger.LOGGER.info("Given plevel: "+plevel);
        ResultSet rs = null;
        String SQL;
        int privilege = 0;
        int count = 0;

        try {
            SQL = "SELECT * FROM users WHERE email=?";
            PreparedStatement pstmt = Idm.getCon().prepareStatement(SQL);
            pstmt.setString(1, email);
            BasicLogger.LOGGER.info("Trying query "+pstmt.toString());
            rs = pstmt.executeQuery();
            BasicLogger.LOGGER.info("Execution succeeded.");
            while(rs.next()) {
                count++;
                privilege = rs.getInt("plevel");
            }
            if(count == 0) return 14;
            BasicLogger.LOGGER.info("User's plevel: "+privilege);
            if(privilege <= plevel) {
                return 140;
            } else {
                return 141;
            }


        } catch (SQLException e) {
            BasicLogger.LOGGER.info("Unable to get user privilege.");
            e.printStackTrace();
            return -1;
        }
    }

}
