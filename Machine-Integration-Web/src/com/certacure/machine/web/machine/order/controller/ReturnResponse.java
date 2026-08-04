package com.certacure.machine.web.machine.order.controller;

import java.io.Serializable;

// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/*public class Item{
    public String item;
    public String uom;
    public String quantity;
    public ArrayList<Lot> lots;
}*/

/*public class Lot{
    public String quantity;
    public String lotNumber;
}*/

public class ReturnResponse implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@JsonProperty("orderId")
    public String orderId;
	@JsonProperty("errorCode")
    public String errorCode;
	@JsonProperty("errorMessage")
    public String errorMessage;
	@JsonProperty("actionId")
    public String actionId;
	
    public ArrayList<Item> items;
    @JsonProperty("toCompany")
	public String toCompany;
    @JsonProperty("requestingLocation")
	public String requestingLocation;
    @JsonProperty("fromLocation")
	public String fromLocation;
    @JsonProperty("staFlag")
	public String staFlag;
    @JsonProperty("fromCompany")
	public String fromCompany;
    @JsonProperty("company")
	public String company;
    @JsonProperty("returnId")
	public String returnId;
    @JsonProperty("transactionNumber")
    public String transactionNumber;

	
}

/*public class Root{
    @JsonProperty("return") 
    public ArrayList<Return> myreturn;
}*/

