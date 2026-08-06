package com.certacure.machine.web.machine.order.controller;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseWrapperClass implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ReturnResponse[] returns;

    @JsonProperty("return")
    public ReturnResponse[] getReturns() {
        return returns;
    }

    public void setReturns(ReturnResponse[] returns) {
        this.returns= returns;
    }

}
