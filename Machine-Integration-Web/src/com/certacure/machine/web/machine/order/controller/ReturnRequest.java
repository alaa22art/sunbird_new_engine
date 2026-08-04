package com.certacure.machine.web.machine.order.controller;

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

public class ReturnRequest{

    public ArrayList<Item> items;
    @JsonProperty("company")
	public String company;
    @JsonProperty("fromCompany")
	public String fromCompany;
    @JsonProperty("toCompany")
	public String toCompany;
    @JsonProperty("fromLocation")
	public String fromLocation;
    @JsonProperty("requestingLocation")
	public String requestingLocation;
    @JsonProperty("staFlag")
	public String staFlag;
    @JsonProperty("orderId")
  	public String orderId;
    @JsonProperty("actionId")
  	public String actionId;
    @JsonProperty("returnId")
	public String returnId;
    
    
    
}

/*public class Root{
    @JsonProperty("return") 
    public ArrayList<Return> myreturn;
}*/

