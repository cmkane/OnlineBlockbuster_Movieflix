package edu.uci.ics.kanec1.service.billing.core;

import edu.uci.ics.kanec1.service.billing.BillingService;
import edu.uci.ics.kanec1.service.billing.logger.ServiceLogger;
import edu.uci.ics.kanec1.service.billing.models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class DBUses {
    public static int insertIntoCart(String email, String movieId, int quantity) {
        ServiceLogger.LOGGER.info("Entered Cart Insertion...");
        int code;
        try {
            String SQL = "INSERT INTO carts (email, movieId, quantity) VALUES (?,?,?);";

            PreparedStatement ps = BillingService.getCon().prepareStatement(SQL);
            ps.setString(1, email);
            ps.setString(2, movieId);
            ps.setInt(3, quantity);

            ServiceLogger.LOGGER.info("Attempting Insert: "+ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Insert successful.");

            code = 3100;
            return code;


        } catch(SQLException e) {
            if(e instanceof SQLIntegrityConstraintViolationException) {
                ServiceLogger.LOGGER.info("Constraint Violation occurred.");
                ServiceLogger.LOGGER.info("Duplicate Insertion.");
                e.printStackTrace();
                code = 311;
                return code;
            } else {
                ServiceLogger.LOGGER.warning("Unable to insert into carts. email = "+email+", movieId = "+movieId+", quantity = "+quantity);
                e.printStackTrace();
                code = -1;
                return code;
            }
        }
    }

    public static int updateCart(String email, String movieId, int quantity) {
        ServiceLogger.LOGGER.info("Entered Cart Update...");
        int code;
        try {
            String SQL = "UPDATE carts SET quantity = ? WHERE email = ? AND movieId = ?;";

            PreparedStatement ps = BillingService.getCon().prepareStatement(SQL);
            ps.setInt(1, quantity);
            ps.setString(2, email);
            ps.setString(3, movieId);


            int result = 0;
            ServiceLogger.LOGGER.info("Attempting Update: "+ps.toString());
            result = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Update successful. Updated " + result + "cart entry(s).");

            if(result == 0) return 312;

            code = 3110;
            return code;


        } catch(SQLException e) {
            if(e instanceof SQLIntegrityConstraintViolationException) {
                ServiceLogger.LOGGER.info("Constraint Violation occurred.");
                ServiceLogger.LOGGER.info("Duplicate Insertion.");
                e.printStackTrace();
                code = 311;
                return code;
            } else {
                ServiceLogger.LOGGER.warning("Unable to insert into carts. email = "+email+", movieId = "+movieId+", quantity = "+quantity);
                e.printStackTrace();
                code = -1;
                return code;
            }
        }
    }

    public static int deleteItemCart(String email, String movieId) {
        ServiceLogger.LOGGER.info("Entered Cart Update...");
        int code;
        try {
            String SQL = "DELETE FROM carts WHERE email = ? AND movieId = ?;";

            PreparedStatement ps = BillingService.getCon().prepareStatement(SQL);
            ps.setString(1, email);
            ps.setString(2, movieId);


            int result = 0;
            ServiceLogger.LOGGER.info("Attempting Delete: " + ps.toString());
            result = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Delete successful. Deleted " + result + " cart entry(s).");

            if(result == 0) return 312;

            code = 3120;
            return code;

        } catch(SQLException e) {
                ServiceLogger.LOGGER.warning("Unable to delete from carts. email = "+email+", movieId = "+movieId);
                e.printStackTrace();
                code = -1;
                return code;
        }
    }

    public static CartRetrieveResponseModel retrieveCart(String email) {
        ServiceLogger.LOGGER.info("Entered DBUses retrieveCart.");
        CartRetrieveResponseModel responseModel = null;

        String SQL = "SELECT * FROM carts WHERE email = ?;";

        try {
            PreparedStatement ps = BillingService.getCon().prepareStatement(SQL);
            ps.setString(1, email);
            ServiceLogger.LOGGER.info("Attempting query: "+ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query Successful.");
            ArrayList<ItemModel> list = new ArrayList<ItemModel>();
            String currEmail;
            String currMovieId;
            int currQuantity;
            while(rs.next()) {
                currEmail = rs.getString("email");
                currMovieId = rs.getString("movieId");
                currQuantity = rs.getInt("quantity");
                list.add(new ItemModel(currEmail, currMovieId, currQuantity));
            }
            int len = list.size();
            if(len == 0) {
                responseModel = new CartRetrieveResponseModel(312, "Shopping item does not exist.", null);
                return responseModel;
            }

            ItemModel[] array = new ItemModel[len];
            for(int x = 0; x < len; x++) {
                array[x] = list.get(x);
            }

            responseModel = new CartRetrieveResponseModel(3130, "Shopping cart retrieved successfully.", array);
            return responseModel;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to retrieve carts for email = "+email);
            e.printStackTrace();
            return null;
        }
    }

    public static int clearCart(String email) {
        ServiceLogger.LOGGER.info("Entered Cart Clear...");
        int code;
        try {
            String SQL = "DELETE FROM carts WHERE email = ?;";

            PreparedStatement ps = BillingService.getCon().prepareStatement(SQL);
            ps.setString(1, email);


            int result = 0;
            ServiceLogger.LOGGER.info("Attempting Delete: " + ps.toString());
            result = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Delete successful. Deleted " + result + " cart entry(s).");

            code = 3140;
            return code;

        } catch(SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to delete from carts. email = "+email);
            e.printStackTrace();
            code = -1;
            return code;
        }
    }

    public static int insertCC(String id, String firstName, String lastName, Date expiration) {
        ServiceLogger.LOGGER.info("Entered CC Insertion...");
        int code;
        try {
            String SQL = "INSERT INTO creditcards (id, firstName, lastName, expiration) VALUES (?,?,?,?);";

            PreparedStatement ps = BillingService.getCon().prepareStatement(SQL);
            ps.setString(1, id);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setDate(4, new java.sql.Date(expiration.getTime()));

            ServiceLogger.LOGGER.info("Attempting Insert: "+ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Insert successful.");

            code = 3200;
            return code;


        } catch(SQLException e) {
            if(e instanceof SQLIntegrityConstraintViolationException) {
                ServiceLogger.LOGGER.info("Constraint Violation occurred.");
                ServiceLogger.LOGGER.info("Duplicate Insertion.");
                e.printStackTrace();
                code = 325;
                return code;
            } else {
                ServiceLogger.LOGGER.warning("Unable to insert into credit cards.");
                e.printStackTrace();
                code = -1;
                return code;
            }
        }
    }

    public static int updateCC(String id, String firstName, String lastName, Date expiration) {
        ServiceLogger.LOGGER.info("Entered CC Update...");
        int code;
        try {
            String SQL = "UPDATE creditcards SET firstName = ?, lastName = ?, expiration = ? WHERE id = ?;";

            PreparedStatement ps = BillingService.getCon().prepareStatement(SQL);
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setDate(3, new java.sql.Date(expiration.getTime()));
            ps.setString(4, id);

            int result = 0 ;
            ServiceLogger.LOGGER.info("Attempting Update: "+ps.toString());
            result = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Update successful.");

            if(result == 0) return 324;

            code = 3210;
            return code;


        } catch(SQLException e) {
                ServiceLogger.LOGGER.warning("Unable to update credit card.");
                e.printStackTrace();
                code = -1;
                return code;
        }
    }

    public static int deleteCC(String id) {
        ServiceLogger.LOGGER.info("Entered CC Delete...");
        int code;
        try {
            String SQL = "DELETE FROM creditcards WHERE id = ?;";

            PreparedStatement ps = BillingService.getCon().prepareStatement(SQL);
            ps.setString(1, id);

            int result = 0 ;
            ServiceLogger.LOGGER.info("Attempting Delete: "+ps.toString());
            result = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Delete successful.");

            if(result == 0) return 324;

            code = 3220;
            return code;


        } catch(SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to delete credit card.");
            e.printStackTrace();
            code = -1;
            return code;
        }
    }

    public static CreditCardRetrieveResponseModel retrieveCC(String id) {
        ServiceLogger.LOGGER.info("Entered CC Retrieve...");
        int code;
        try {
            String SQL = "SELECT * FROM creditcards WHERE id = ?;";

            PreparedStatement ps = BillingService.getCon().prepareStatement(SQL);
            ps.setString(1, id);

            ServiceLogger.LOGGER.info("Attempting Delete: "+ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Delete successful.");

            CreditCardModel model = null;
            while(rs.next()) {
                Date date = new Date(rs.getDate("expiration").getTime());
                model = new CreditCardModel(rs.getString("id"), rs.getString("firstName"), rs.getString("lastName"), date);
            }

            if(model == null) {
                return new CreditCardRetrieveResponseModel(324, "Credit card does not exist.", null);
            }
            return new CreditCardRetrieveResponseModel(3230, "Credit card retrieved successfully.", model);


        } catch(SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to delete credit card.");
            e.printStackTrace();
            code = -1;
            return null;
        }
    }

    public static int insertCustomer(String email, String firstName, String lastName, String ccId, String address) {
        ServiceLogger.LOGGER.info("Entered Customer Insertion...");
        int code;
        try {
            String SQL = "INSERT INTO customers (email, firstName, lastName, ccId, address) VALUES (?,?,?,?,?);";

            PreparedStatement ps = BillingService.getCon().prepareStatement(SQL);
            ps.setString(1, email);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, ccId);
            ps.setString(5, address);

            ServiceLogger.LOGGER.info("Attempting Insert: "+ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Insert successful.");

            code = 3300;
            return code;


        } catch(SQLException e) {
            if(e.getErrorCode() == 1062) {
                ServiceLogger.LOGGER.info("Constraint Violation occurred.");
                ServiceLogger.LOGGER.info("Duplicate Insertion.");
                e.printStackTrace();
                code = 333;
                return code;
            } else if(e.getErrorCode() == 1216 || e.getErrorCode() == 1452) {
                ServiceLogger.LOGGER.info("Credit Card not found.");
                e.printStackTrace();
                code = 331;
                return code;
            }
            else {
                ServiceLogger.LOGGER.warning("Unable to insert into customers. + "+e.getErrorCode());
                e.printStackTrace();
                code = -1;
                return code;
            }
        }
    }

    public static int updateCustomer(String email, String firstName, String lastName, String ccId, String address) {
        ServiceLogger.LOGGER.info("Entered Customer Update...");
        int code;
        try {
            String SQL = "UPDATE customers SET firstName = ?, lastName = ?, ccId = ?, address = ? WHERE email = ?;";

            PreparedStatement ps = BillingService.getCon().prepareStatement(SQL);
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, ccId);
            ps.setString(4, address);
            ps.setString(5, email);

            int result = 0;
            ServiceLogger.LOGGER.info("Attempting Update: "+ps.toString());
            result = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Insert successful.");

            if(result == 0) {
                return 332;
            }

            code = 3310;
            return code;


        } catch(SQLException e) {
            if(e.getErrorCode() == 1216 || e.getErrorCode() == 1452) {
                ServiceLogger.LOGGER.info("Credit Card not found.");
                e.printStackTrace();
                code = 331;
                return code;
            }
            else {
                ServiceLogger.LOGGER.warning("Unable to update customer. + ["+e.getErrorCode()+"]");
                e.printStackTrace();
                code = -1;
                return code;
            }
        }
    }

    public static CustomerRetrieveResponseModel retrieveCustomer(String email) {
        ServiceLogger.LOGGER.info("Entered Customer Update...");
        int code;
        try {
            String SQL = "SELECT * FROM customers WHERE email = ?;";

            PreparedStatement ps = BillingService.getCon().prepareStatement(SQL);
            ps.setString(1, email);

            ServiceLogger.LOGGER.info("Attempting Query: "+ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            CustomerModel model = null;
            while(rs.next()) {
                String email2 = rs.getString("email");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String ccId = rs.getString("ccId");
                String address = rs.getString("address");
                model = new CustomerModel(email2, firstName, lastName, ccId, address);
            }

            CustomerRetrieveResponseModel responseModel = null;
            if(model == null) {
                code = 332;
                responseModel = new CustomerRetrieveResponseModel(code, "Customer does not exist.", null);
                return responseModel;
            }

            responseModel = new CustomerRetrieveResponseModel(3320, "Customer retrieved successfully.", model);
            return responseModel;


        } catch(SQLException e) {
                ServiceLogger.LOGGER.warning("Unable to update customer. + ["+e.getErrorCode()+"]");
                e.printStackTrace();
                code = -1;
                return null;
        }
    }

    /*public static int placeOrder(String email) {
        ServiceLogger.LOGGER.info("Entering place order...");

        try {
            String SQL = "SELECT customers.email, movieId, quantity " +
                    "FROM customers LEFT JOIN carts ON customers.email = carts.email " +
                    "WHERE customers.email = ?;";

            PreparedStatement ps = BillingService.getCon().prepareStatement(SQL);
            ps.setString(1, email);
            ServiceLogger.LOGGER.info("Attempting query: "+ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");
            int count = 0;
            String movieId;
            Integer quantity;
            ArrayList<ItemModel> items = new ArrayList<ItemModel>();
            while(rs.next()) {
                count++;
                movieId = rs.getString("movieId");
                quantity = rs.getInt("quantity");
                if(movieId == null || quantity == 0) {
                    ServiceLogger.LOGGER.info("Shopping cart for this customer not found.");
                    return 341;
                }
                items.add(new ItemModel(email, movieId, quantity));
            }
            if(count == 0) {
                ServiceLogger.LOGGER.info("Customer does not exist.");
                return 332;
            }
            ServiceLogger.LOGGER.info("Customer does exist.");


            // Add the items in cart into the sales table
            SQL = "INSERT INTO sales (email, movieId, quantity, saleDate) VALUES ";
            Date current = new Date();
            int len = items.size();
            for(int x = 0; x < len; x++) {
                SQL += "(\'" + items.get(x).getEmail() + "\', \'" + items.get(x).getMovieId() + "\', "  + items.get(x).getQuantity() + ", ?)";
                if(x == len - 1) SQL += ";";
                else SQL += ", ";
            }
            ps = BillingService.getCon().prepareStatement(SQL);
            for(int x = 0; x < len; x++) {
                ps.setDate(x+1, new java.sql.Date(current.getTime()));
            }
            ServiceLogger.LOGGER.info("Attempting insert: " + ps.toString());
            ps.execute();
            ServiceLogger.LOGGER.info("Insert success.");

            //Clear the shopping cart for the given user
            ServiceLogger.LOGGER.info("Clearing shopping cart for user w/email: "+email);
            clearCart(email);

            return 3400;

        } catch (SQLException e) {
            ServiceLogger.LOGGER.info("Unable to complete order placing process. error code = "+e.getErrorCode());
            e.printStackTrace();
            return -1;
        }
    }*/

    public static OrderPlaceResponseModel placeOrder(String email) {
        ServiceLogger.LOGGER.info("Entering place order...");

        try {
            String SQL = "SELECT meow.email, meow.movieId, meow.quantity, unit_price, discount " +
                    "FROM (SELECT customers.email, movieId, quantity FROM customers LEFT JOIN carts ON customers.email = carts.email) AS meow " +
                    "LEFT JOIN movie_prices ON meow.movieId = movie_prices.movieId " +
                    "WHERE meow.email = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(SQL);
            ps.setString(1, email);
            ServiceLogger.LOGGER.info("Attempting query: "+ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");
            int count = 0;
            String movieId;
            Integer quantity;
            float sum = (float) 0.0;
            while(rs.next()) {
                count++;
                movieId = rs.getString("movieId");
                quantity = rs.getInt("quantity");
                if(movieId == null || quantity == 0) {
                    ServiceLogger.LOGGER.info("Shopping cart for this customer not found.");
                    return new OrderPlaceResponseModel(341, "Shopping cart for this customer not found.", null, null);
                }
                sum += (float) quantity * rs.getFloat("unit_price") * rs.getFloat("discount");
            }
            if(count == 0) {
                ServiceLogger.LOGGER.info("Customer does not exist.");
                return new OrderPlaceResponseModel(332, "Customer does not exist.", null, null);
            }
            ServiceLogger.LOGGER.info("Customer does exist.");
            ServiceLogger.LOGGER.info("The customer owes $" + sum + ".");

            // Make payment using PayPal's API
            Map<String, Object> result = PayPalClient.makePayment(String.format("%.2f", sum));
            String status = (String) result.get("status");
            if(status == null || !status.equals("success")) {
                ServiceLogger.LOGGER.info("Unable to make payment.");
                return new OrderPlaceResponseModel(342, "Create payment failed.", null, null);
            }
            String redirectUrl = (String) result.get("redirect_url");
            String[] token = redirectUrl.split("token=", 2);
            CallableStatement cst = BillingService.getCon().prepareCall("{call insert_sales_transactions(?,?)}");
            cst.setString(1, email);
            cst.setString(2, token[1]);
            ServiceLogger.LOGGER.info("Attempting callable statement: "+cst.toString());
            cst.execute();
            ServiceLogger.LOGGER.info("Execution successful.");
            ServiceLogger.LOGGER.info("Order placed successfully.");
            return new OrderPlaceResponseModel(3400, "Order placed successfully.", redirectUrl, token[1]);

        } catch(SQLException e) {
            ServiceLogger.LOGGER.info("Unable to complete order placing process. error code = "+e.getErrorCode());
            e.printStackTrace();
            return null;
        }
    }

    /*public static OrderRetrieveResponseModel retrieveOrder(String email) {
        ServiceLogger.LOGGER.info("Entered Order Retrieval...");
        int code;
        try {
            String SQL = "SELECT * FROM customers WHERE email = ?;";

            PreparedStatement ps = BillingService.getCon().prepareStatement(SQL);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while(rs.next()) {
                count++;
            }
            if(count == 0) return new OrderRetrieveResponseModel(332, "Customer does not exist.", null);
            ServiceLogger.LOGGER.info("Customer exists.");

            SQL = "SELECT * FROM sales WHERE email = ?;";

            ps = BillingService.getCon().prepareStatement(SQL);
            ps.setString(1, email);

            ServiceLogger.LOGGER.info("Attempting Query: "+ps.toString());
            rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            ArrayList<OrderItemModel> items = new ArrayList<OrderItemModel>();
            while(rs.next()) {
                String email2 = rs.getString("email");
                String movieId = rs.getString("movieId");
                Integer quantity = rs.getInt("quantity");
                Date saleDate = new Date(rs.getDate("saleDate").getTime());
                items.add(new OrderItemModel(email2, movieId, quantity, saleDate));
            }

            int len = items.size();
            OrderItemModel[] array = new OrderItemModel[len];
            for(int x = 0; x < len; x++) {
                array[x] = items.get(x);
            }

            OrderRetrieveResponseModel responseModel = new OrderRetrieveResponseModel(3410, "Orders retrieved successfully.", array);
            return responseModel;


        } catch(SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to update customer. + ["+e.getErrorCode()+"]");
            e.printStackTrace();
            code = -1;
            return null;
        }
    }*/

    public static int tokenExists(String token) {
        ServiceLogger.LOGGER.info("Entered Token Exists...");
        int code;
        try {
            String SQL = "SELECT * FROM transactions WHERE token = ?;";

            PreparedStatement ps = BillingService.getCon().prepareStatement(SQL);
            ps.setString(1, token);

            ServiceLogger.LOGGER.info("Attempting Query: "+ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            int count = 0;
            while(rs.next()) {
                count++;
            }

            if(count == 0) {
                ServiceLogger.LOGGER.info("Token does not exist.");
                return 0;
            }
            ServiceLogger.LOGGER.info("Token exists.");
            return 1;


        } catch(SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to verify if token exists. + ["+e.getErrorCode()+"]");
            e.printStackTrace();
            return -1;
        }
    }

    public static int updateTransactions(String token, String transactionId) {
        ServiceLogger.LOGGER.info("Entered Transaction Update...");
        try {
            String SQL = "UPDATE transactions SET transactionId = ? WHERE token = ?;";

            PreparedStatement ps = BillingService.getCon().prepareStatement(SQL);
            ps.setString(1, transactionId);
            ps.setString(2, token);

            int result = 0;
            ServiceLogger.LOGGER.info("Attempting Update: "+ps.toString());
            result = ps.executeUpdate();
            ServiceLogger.LOGGER.info("Update successful. Updated "+result+" transaction(s).");

            if(result == 0) {
                ServiceLogger.LOGGER.info("Transactions with that token don't not exist.");
                return 0;
            }
            ServiceLogger.LOGGER.info("Token exists.");
            return 1;


        } catch(SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to update transactions with transactionId. + ["+e.getErrorCode()+"]");
            e.printStackTrace();
            return -1;
        }
    }

    public static OrderRetrieveResponseModel retrieveOrder(String email) {
        ServiceLogger.LOGGER.info("Entered Order Retrieval...");
        try {
            /*String SQL = "SELECT * " +
                    "FROM (transactions LEFT JOIN sales s on transactions.sId = s.id) " +
                    "LEFT JOIN movie_prices ON s.movieId = movie_prices.movieId " +
                    "WHERE email = ?;";*/
            String SQL = "SELECT customers.email, token, transactionId, s.movieId, quantity, saleDate, unit_price, discount " +
                    "FROM (customers LEFT JOIN (transactions LEFT JOIN sales s on transactions.sId = s.id) ON customers.email = s.email) " +
                    "LEFT JOIN movie_prices ON s.movieId = movie_prices.movieId " +
                    "WHERE customers.email = ?;";
            PreparedStatement ps = BillingService.getCon().prepareStatement(SQL);
            ps.setString(1, email);
            ServiceLogger.LOGGER.info("Attempting query : "+ps.toString());
            ResultSet rs = ps.executeQuery();
            ServiceLogger.LOGGER.info("Query successful.");

            String currTransacId, prevTransacId;
            prevTransacId = "";
            ArrayList<ItemModelWithPrices> itemList = new ArrayList<ItemModelWithPrices>();
            String movieId;
            Integer quantity;
            Float unit_price;
            Float discount;
            Date saledate;

            ArrayList<TransactionModel> modelList = new ArrayList<TransactionModel>();
            TransactionModel tModel = null;
            int index = 0;
            int count = 0;

            while(rs.next()) {
                currTransacId = rs.getString("transactionId");
                if(rs.getString("movieId") == null) {
                    ServiceLogger.LOGGER.info("The user has no transactions.");
                    return new OrderRetrieveResponseModel(3410, "Orders retrieved successfully.", null);
                }
                if(currTransacId == null) continue;
                if(count == 0) {
                    tModel = PayPalClient.getTransactionData(currTransacId);
                }
                else if(!currTransacId.equals(prevTransacId) && count != 0) {
                    // Add the items to the previous transaction
                    int len = itemList.size();
                    ItemModelWithPrices[] itemArray = buildItemArray(itemList);
                    tModel.setItems(itemArray);
                    itemList = new ArrayList<ItemModelWithPrices>();
                    // Add the previous transaction to the transaction model list
                    modelList.add(tModel);
                    // Get data for the new transaction model
                    tModel = PayPalClient.getTransactionData(currTransacId);
                    index++;
                }
                movieId = rs.getString("movieId");
                quantity = rs.getInt("quantity");
                unit_price = rs.getFloat("unit_price");
                discount = rs.getFloat("discount");
                saledate = new Date(rs.getDate("saleDate").getTime());

                itemList.add(new ItemModelWithPrices(email, movieId, quantity, unit_price, discount, saledate));

                count++;
                prevTransacId = currTransacId;
            }
            if(count == 0) {
                ServiceLogger.LOGGER.info("Customer does not exist");
                return new OrderRetrieveResponseModel(332, "Customer does not exist.", null);
            }

            if(count > 0) {
                ItemModelWithPrices[] array = buildItemArray(itemList);
                tModel.setItems(array);
                modelList.add(tModel);
            }

            TransactionModel[] transactionsArray;
            if(count == 0) transactionsArray = null;
            else {
                int len = modelList.size();
                transactionsArray = new TransactionModel[len];
                for(int x = 0; x < len; x++) {
                    transactionsArray[x] = modelList.get(x);
                }
            }
            OrderRetrieveResponseModel responseModel = new OrderRetrieveResponseModel(3410, "Orders retrieved successfully.", transactionsArray);
            return responseModel;


        } catch(SQLException e) {
            ServiceLogger.LOGGER.warning("Unable to retrieve order.");
            e.printStackTrace();
            return null;
        }
    }

    public static ItemModelWithPrices[] buildItemArray(ArrayList<ItemModelWithPrices> list) {
        int len = list.size();
        ItemModelWithPrices[] array = new ItemModelWithPrices[len];
        for(int x = 0; x < len; x++) {
            array[x] = list.get(x);
        }
        return array;
    }
}
