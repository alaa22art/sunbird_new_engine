package com.certacure.lis.interfaces.middleware.flow_component.hl7_23_vidas;

public class VidasMedicalRecord {
	
	 private String messageType;
	    private String patientId;
	    private String patientName;
	    private String birthDate;
	    private String patientSex;
	    private String sampleOrigin;
	    private String sampleId;
	    private String clinicalInfo;
	    private String resultType;
	    private String resultName;
	    private String testTime;
	    private String testDate;
	    private String qualitative;
	    private String quantitativeValue;
	    private String unit;
	    private String unitY;
	    private String qualitativeData;
	    private String normalCriteria;
	    private String instrumentId;
	    private String serialNumber;
	    private String method;
	    
	    // Getters and Setters
	    public String getMessageType() { return messageType; }
	    public void setMessageType(String messageType) { this.messageType = messageType; }
	    
	    public String getPatientId() { return patientId; }
	    public void setPatientId(String patientId) { this.patientId = patientId; }
	    
	    public String getPatientName() { return patientName; }
	    public void setPatientName(String patientName) { this.patientName = patientName; }
	    
	    public String getBirthDate() { return birthDate; }
	    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
	    
	    public String getPatientSex() { return patientSex; }
	    public void setPatientSex(String patientSex) { this.patientSex = patientSex; }
	    
	    public String getSampleOrigin() { return sampleOrigin; }
	    public void setSampleOrigin(String sampleOrigin) { this.sampleOrigin = sampleOrigin; }
	    
	    public String getSampleId() { return sampleId; }
	    public void setSampleId(String sampleId) { this.sampleId = sampleId; }
	    
	    public String getClinicalInfo() { return clinicalInfo; }
	    public void setClinicalInfo(String clinicalInfo) { this.clinicalInfo = clinicalInfo; }
	    
	    public String getResultType() { return resultType; }
	    public void setResultType(String resultType) { this.resultType = resultType; }
	    
	    public String getResultName() { return resultName; }
	    public void setResultName(String resultName) { this.resultName = resultName; }
	    
	    public String getTestTime() { return testTime; }
	    public void setTestTime(String testTime) { this.testTime = testTime; }
	    
	    public String getTestDate() { return testDate; }
	    public void setTestDate(String testDate) { this.testDate = testDate; }
	    
	    public String getQualitative() { return qualitative; }
	    public void setQualitative(String qualitative) { this.qualitative = qualitative; }
	    
	    public String getQuantitativeValue() { return quantitativeValue; }
	    public void setQuantitativeValue(String quantitativeValue) { this.quantitativeValue = quantitativeValue; }
	    
	    public String getUnit() { return unit; }
	    public void setUnit(String unit) { this.unit = unit; }
	    
	    public String getUnitY() { return unitY; }
	    public void setUnitY(String unitY) { this.unitY = unitY; }
	    
	    public String getQualitativeData() { return qualitativeData; }
	    public void setQualitativeData(String qualitativeData) { this.qualitativeData = qualitativeData; }
	    
	    public String getNormalCriteria() { return normalCriteria; }
	    public void setNormalCriteria(String normalCriteria) { this.normalCriteria = normalCriteria; }
	    
	    public String getInstrumentId() { return instrumentId; }
	    public void setInstrumentId(String instrumentId) { this.instrumentId = instrumentId; }
	    
	    public String getSerialNumber() { return serialNumber; }
	    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
	    
	    public String getMethod() { return method; }
	    public void setMethod(String method) { this.method = method; }
	    
	    @Override
	    public String toString() {
	        StringBuilder sb = new StringBuilder();
	        sb.append("MedicalRecord {\n");
	        sb.append("  Message Type: ").append(messageType).append("\n");
	        sb.append("  Patient ID: ").append(patientId).append("\n");
	        sb.append("  Patient Name: ").append(patientName).append("\n");
	        sb.append("  Birth Date: ").append(birthDate).append("\n");
	        sb.append("  Patient Sex: ").append(patientSex).append("\n");
	        sb.append("  Sample Origin: ").append(sampleOrigin).append("\n");
	        sb.append("  Sample ID: ").append(sampleId).append("\n");
	        sb.append("  Clinical Info: ").append(clinicalInfo).append("\n");
	        sb.append("  Result Type: ").append(resultType).append("\n");
	        sb.append("  Result Name: ").append(resultName).append("\n");
	        sb.append("  Test Time: ").append(testTime).append("\n");
	        sb.append("  Test Date: ").append(testDate).append("\n");
	        sb.append("  Qualitative: ").append(qualitative).append("\n");
	        sb.append("  Quantitative Value: ").append(quantitativeValue).append("\n");
	        sb.append("  Unit: ").append(unit).append("\n");
	        sb.append("  Unit Y: ").append(unitY).append("\n");
	        sb.append("  Qualitative Data: ").append(qualitativeData).append("\n");
	        sb.append("  Normal Criteria: ").append(normalCriteria).append("\n");
	        sb.append("  Instrument ID: ").append(instrumentId).append("\n");
	        sb.append("  Serial Number: ").append(serialNumber).append("\n");
	        sb.append("  Method: ").append(method).append("\n");
	        sb.append("}");
	        return sb.toString();
	    }

}
