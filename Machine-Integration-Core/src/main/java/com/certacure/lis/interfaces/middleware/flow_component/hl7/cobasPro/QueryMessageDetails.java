package com.certacure.lis.interfaces.middleware.flow_component.hl7.cobasPro;

//Query message details class
public class QueryMessageDetails {
 private String sampleNumber;
 private String position;
 private String rack;
 private String qualityControlId;
 private String queryTag;
 private String messageControlId;
 
 public QueryMessageDetails(String sampleNumber, String position, String rack, String qualityControlId, String queryTag, String messageControlId) {
     this.sampleNumber = sampleNumber;
     this.position = position;
     this.rack = rack;
     this.qualityControlId = qualityControlId;
     this.queryTag = queryTag;
     this.messageControlId = messageControlId;
 }
 
 // Getters
 public String getSampleNumber() {
     return sampleNumber;
 }
 
 public String getPosition() {
     return position;
 }
 
 public String getRack() {
     return rack;
 }
 
 public String getQualityControlId() {
     return qualityControlId;
 }
 
 public String getQueryTag() {
     return queryTag;
 }
 
 public String getMessageControlId() {
     return messageControlId;
 }
 
 // Setters
 public void setSampleNumber(String sampleNumber) {
     this.sampleNumber = sampleNumber;
 }
 
 public void setPosition(String position) {
     this.position = position;
 }
 
 public void setRack(String rack) {
     this.rack = rack;
 }
 
 public void setQualityControlId(String qualityControlId) {
     this.qualityControlId = qualityControlId;
 }
 
 public void setQueryTag(String queryTag) {
     this.queryTag = queryTag;
 }
 
 public void setMessageControlId(String messageControlId) {
     this.messageControlId = messageControlId;
 }
 
 @Override
 public String toString() {
     return "QueryMessageDetails{" +
             "sampleNumber='" + sampleNumber + '\'' +
             ", position='" + position + '\'' +
             ", rack='" + rack + '\'' +
             ", qualityControlId='" + qualityControlId + '\'' +
             ", queryTag='" + queryTag + '\'' +
             ", messageControlId='" + messageControlId + '\'' +
             '}';
 }
}