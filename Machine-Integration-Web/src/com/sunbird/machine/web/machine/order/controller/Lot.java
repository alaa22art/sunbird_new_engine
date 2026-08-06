package com.certacure.machine.web.machine.order.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/*public class Lot {
    private String acquisitionCost;
    private String jFDACostPrice;
    private String quantity;
    private String tenderFlag;
    private String lotNumber;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern= "yyyy-MM-dd'T'HH:mm:ss")
    private String expiry;

    private String jFDAPublicPrice;
    private String order;

    public String getAcquisitionCost() {
        return acquisitionCost;
    }

    public void setAcquisitionCost(String acquisitionCost) {
        this.acquisitionCost= acquisitionCost;
    }

    public String getjFDACostPrice() {
        return jFDACostPrice;
    }

    public void setjFDACostPrice(String jFDACostPrice) {
        this.jFDACostPrice= jFDACostPrice;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity= quantity;
    }

    public String getTenderFlag() {
        return tenderFlag;
    }

    public void setTenderFlag(String tenderFlag) {
        this.tenderFlag= tenderFlag;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber= lotNumber;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry= expiry;
    }

    public String getjFDAPublicPrice() {
        return jFDAPublicPrice;
    }

    public void setjFDAPublicPrice(String jFDAPublicPrice) {
        this.jFDAPublicPrice= jFDAPublicPrice;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order= order;
    }
}*/


public class Lot{


	
	
	@JsonProperty("jFDAPublicPrice")
	public String jFDAPublicPrice;
	
	@JsonProperty("jFDACostPrice")
	public String jFDACostPrice;
	
	@JsonProperty("acquisitionCost")
	public String acquisitionCost;
	
	@JsonProperty("quantity")
	public String quantity;
	
	@JsonProperty("tenderFlag")
	public String tenderFlag;
	
	@JsonProperty("lotNumber")
	public String lotNumber;
	
	@JsonProperty("expiry")
	public String expiry;
	
	@JsonProperty("order")
	public String order;
}
