package com.sunbird.machine.web.machine.order.controller;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RootReturn{
    @JsonProperty("return") 
    public ArrayList<ReturnResponse> arrReturnArray;
}