package com.sunbird.machine.web.machine.order.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestWrapperClass {

    private ReturnRequest[] returns;

    @JsonProperty("return")
    public ReturnRequest[] getReturns() {
        return returns;
    }

    public void setReturns(ReturnRequest[] returns) {
        this.returns= returns;
    }

}



