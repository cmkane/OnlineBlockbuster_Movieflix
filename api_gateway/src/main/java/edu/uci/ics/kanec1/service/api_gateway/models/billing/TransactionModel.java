package edu.uci.ics.kanec1.service.api_gateway.models.billing;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionModel {
    @JsonProperty(value = "transactionId", required =true)
    private String transactionId;
    @JsonProperty(value = "state", required = true)
    private String state;
    @JsonProperty(value = "amount", required = true)
    private AmountModel amount;
    @JsonProperty(value = "transaction_fee", required = true)
    private TransactionFeeModel transaction_fee;
    @JsonProperty(value = "create_time", required = true)
    private String create_time;
    @JsonProperty(value = "update_time", required = true)
    private String update_time;
    private ItemModelWithPrices[] items;

    @JsonCreator
    public TransactionModel(@JsonProperty(value = "transactionId", required =true) String transactionId,
                            @JsonProperty(value = "state", required = true) String state,
                            @JsonProperty(value = "amount", required = true) AmountModel amount,
                            @JsonProperty(value = "transaction_fee", required = true) TransactionFeeModel transaction_fee,
                            @JsonProperty(value = "create_time", required = true) String create_time,
                            @JsonProperty(value = "update_time", required = true) String update_time,
                            ItemModelWithPrices[] items) {
        this.transactionId = transactionId;
        this.state = state;
        this.amount = amount;
        this.transaction_fee = transaction_fee;
        this.create_time = create_time;
        this.update_time = update_time;
        this.items = items;
    }

    @JsonProperty
    public String getTransactionId() {
        return transactionId;
    }

    @JsonProperty
    public String getState() {
        return state;
    }

    @JsonProperty
    public AmountModel getAmount() {
        return amount;
    }

    @JsonProperty
    public TransactionFeeModel getTransaction_fee() {
        return transaction_fee;
    }

    @JsonProperty
    public String getCreate_time() {
        return create_time;
    }

    @JsonProperty
    public String getUpdate_time() {
        return update_time;
    }

    @JsonProperty
    public ItemModelWithPrices[] getItems() {
        return items;
    }

    @JsonIgnore
    public void setItems(ItemModelWithPrices[] items) {
        this.items = items;
    }
}
