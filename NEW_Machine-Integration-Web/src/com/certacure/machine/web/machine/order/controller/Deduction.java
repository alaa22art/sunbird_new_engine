package com.certacure.machine.web.machine.order.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

//import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
//import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */

/*
"acquisitionCost": "0.075",
"jFDACostPrice": "0.075",
"quantity": "2.0",
"tenderFlag": "N",
"lotNumber": "EK0103",
"expiry": "2025-04-30 00:00:00",
"jFDAPublicPrice": "0.098",
"order": "1"*/
	
public class Deduction{
	@JsonProperty("orderId")
	 public String orderId;
	@JsonProperty("toCompany")
	    public String toCompany;
	@JsonProperty("requestingLocation")
	    public String requestingLocation;
	@JsonProperty("errorMessage")
	    public String errorMessage;
	@JsonProperty("actionId")
	    public String actionId;
	@JsonProperty("staFlag")
	    public String staFlag;
	@JsonProperty("errorCode")
	    public String errorCode;
	@JsonProperty("company")
	    public String company;
	@JsonProperty("fromLocation")
	    public String fromLocation;
	@JsonProperty("fromCompany")
	    public String fromCompany;
	@JsonProperty("items")
	    public ArrayList<Item> items;
	@JsonProperty("transactionNumber")
		public String transactionNumber;
	
	
	
	
	
	
}

/*public class Deduction {

    private String orderId;
    private String toCompany;
    private String requestingLocation;
    private String transactionNumber;
    private String errorMessage;
    private String actionId;
    private String staFlag;
    private String errorCode;
    private String company;
    private String fromLocation;
    private String fromCompany;
    private List<Item> items;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId= orderId;
    }

    public String getToCompany() {
        return toCompany;
    }

    public void setToCompany(String toCompany) {
        this.toCompany= toCompany;
    }

    public String getRequestingLocation() {
        return requestingLocation;
    }

    public void setRequestingLocation(String requestingLocation) {
        this.requestingLocation= requestingLocation;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber= transactionNumber;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage= errorMessage;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId= actionId;
    }

    public String getStaFlag() {
        return staFlag;
    }

    public void setStaFlag(String staFlag) {
        this.staFlag= staFlag;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode= errorCode;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company= company;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation= fromLocation;
    }

    public String getFromCompany() {
        return fromCompany;
    }

    public void setFromCompany(String fromCompany) {
        this.fromCompany= fromCompany;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items= items;
    }

}*/
