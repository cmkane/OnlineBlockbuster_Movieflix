package edu.uci.ics.kanec1.service.billing.core;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import edu.uci.ics.kanec1.service.billing.BillingService;
import edu.uci.ics.kanec1.service.billing.configs.Configs;
import edu.uci.ics.kanec1.service.billing.logger.ServiceLogger;
import edu.uci.ics.kanec1.service.billing.models.AmountModel;
import edu.uci.ics.kanec1.service.billing.models.ConfigsModel;
import edu.uci.ics.kanec1.service.billing.models.TransactionFeeModel;
import edu.uci.ics.kanec1.service.billing.models.TransactionModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayPalClient {
    private static final String clientID = "temp";
    private static final String clientSecret = "temp";

    public static Map<String, Object> makePayment(String sum) {
        Map<String, Object> response = new HashMap<String, Object>();
        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(sum);
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl("http://localhost:4200/cancel");
        Configs configs = BillingService.getConfigs();
        String url = configs.getScheme() + configs.getHostName() + ":" + configs.getPort() + configs.getPath() + "/order/complete";
        redirectUrls.setReturnUrl(url);
        payment.setRedirectUrls(redirectUrls);
        Payment createdPayment;
        try {
            String redirectUrl = "";
            APIContext context = new APIContext(clientID, clientSecret, "sandbox");
            createdPayment = payment.create(context);
            if(createdPayment!=null){
                List<Links> links = createdPayment.getLinks();
                for (Links link:links) {
                    if(link.getRel().equals("approval_url")){
                        redirectUrl = link.getHref();
                        break;
                    }
                }
                response.put("status", "success");
                response.put("redirect_url", redirectUrl);
            }
        } catch (PayPalRESTException e) {
            ServiceLogger.LOGGER.warning("Error happened during payment creation!");
            e.printStackTrace();
        }
        return response;
    }

    public static Map<String, Object> completePayment(String payerId, String token, String paymentId) {
        Map<String, Object> response = new HashMap();
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);
        try {
            APIContext context = new APIContext(clientID, clientSecret, "sandbox");
            Payment createdPayment = payment.execute(context, paymentExecution);
            if(createdPayment!=null){
                response.put("status", "success");
                response.put("payment", createdPayment);
            }
        } catch (PayPalRESTException e) {
            ServiceLogger.LOGGER.info("Unable to complete payment.");
            e.printStackTrace();
        }
        return response;
    }

    public static TransactionModel getTransactionData(String transactionId) {
        APIContext apiContext = new APIContext(clientID, clientSecret, "sandbox");
        TransactionModel model = null;
        AmountModel amountModel = null;
        TransactionFeeModel transactionFeeModel = null;
        try {
            Sale sale = Sale.get(apiContext, transactionId);
            String state = sale.getState();
            amountModel = new AmountModel(sale.getAmount().getTotal(), sale.getAmount().getCurrency());
            transactionFeeModel = new TransactionFeeModel(sale.getTransactionFee().getValue(), sale.getTransactionFee().getCurrency());
            String create_time = sale.getCreateTime();
            String update_time = sale.getUpdateTime();
            model = new TransactionModel(transactionId, state, amountModel, transactionFeeModel, create_time, update_time, null);
            return model;

        } catch(Exception e) {
            ServiceLogger.LOGGER.warning("Unable to get transaction data for tId="+transactionId);
            e.printStackTrace();
            return null;
        }
    }
}
