package edu.uci.ics.kanec1.service.api_gateway.core;

import edu.uci.ics.kanec1.service.api_gateway.GatewayService;
import edu.uci.ics.kanec1.service.api_gateway.logger.ServiceLogger;
import edu.uci.ics.kanec1.service.api_gateway.models.ReportResponseModel;

import java.sql.*;

public class DBUses {
    public static ReportResponseModel getResponse(String email, String sessionid, String transactionid) {
        ServiceLogger.LOGGER.info("Entering get response....");
        ReportResponseModel responseModel = null;

        String SQL = "SELECT * FROM responses WHERE ";
        try {
            //if(email == null) SQL += "email IS ? AND ";
            //else SQL += "email = ? AND ";
            //if(sessionid == null) SQL += "sessionid IS ? AND ";
            //else SQL += "sessionid = ? AND ";
            SQL += "transactionid = ?;";
            Connection con = GatewayService.getConPool().requestCon();
            PreparedStatement ps = con.prepareStatement(SQL);
            //if(email != null) ps.setString(1, email);
            //else ps.setNull(1, Types.VARCHAR);
            //ps.setString(2, sessionid);
            ps.setString(1, transactionid);
            ServiceLogger.LOGGER.info("Attempting Query: " + ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query Successful");
            while(rs.next()) {
                int httpStatus = rs.getInt("httpstatus");
                String response = rs.getString("response");
                responseModel = new ReportResponseModel(response, httpStatus);
            }
            if(responseModel == null) responseModel = new ReportResponseModel(null, 0);
            GatewayService.getConPool().releaseCon(con);
            return responseModel;
        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Unable to retrieve response.");
            e.printStackTrace();
            return null;
        }
    }
}
