package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24;


import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record.Type;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class HL7_V24_PatientRecord extends LIS2A2Record {

	public static HL7_V24_PatientRecord create(int sequenceNumber) {
		return (HL7_V24_PatientRecord) new HL7_V24_PatientRecord()
				.setField(1, Type.PID.name())
				.setField(2, String.valueOf(sequenceNumber));
	}

	private HL7_V24_PatientRecord() {
	}

	public HL7_V24_PatientRecord(DelimitedData<Field> fields) {
		super(fields);
	}
	
	
	public HL7_V24_PatientRecord setPatientID(String strPatientID) {
		return (HL7_V24_PatientRecord) setField(2, strPatientID);
	}
	
	public HL7_V24_PatientRecord setAlternatePatientID(String strAlternatePatientID) {
		return (HL7_V24_PatientRecord) setField(4, strAlternatePatientID);
	}
	
	public HL7_V24_PatientRecord setPatientName(String strPatientName) {
		return (HL7_V24_PatientRecord) setField(5, strPatientName);
	}
	
	public HL7_V24_PatientRecord setFirstName(String firstName) {
		return (HL7_V24_PatientRecord) setComponent(5, 2, firstName);
	}
	
	public HL7_V24_PatientRecord setMiddleName(String strMiddleName) {
		return (HL7_V24_PatientRecord) setComponent(5, 3, strMiddleName);
	}
	
	public HL7_V24_PatientRecord setFamilyName(String strFamilyName) {
		return (HL7_V24_PatientRecord) setComponent(5, 1, strFamilyName);
	}
	
	public HL7_V24_PatientRecord setMotherMaidenName(String strMotherMaidenName) {
		return (HL7_V24_PatientRecord) setField(6, strMotherMaidenName);
	}
	
	public HL7_V24_PatientRecord setDateTimeOfBirth(String strDateTimeOfBirth) {
		return (HL7_V24_PatientRecord) setField(7, strDateTimeOfBirth);
	}
	
	public HL7_V24_PatientRecord setAdministrativeSex(String strAdministrativeSex) {
		return (HL7_V24_PatientRecord) setField(8, strAdministrativeSex);
	}
	
	public HL7_V24_PatientRecord setPatientAlias(String strPatientAlias) {
		return (HL7_V24_PatientRecord) setField(9, strPatientAlias);
	}
	
	public HL7_V24_PatientRecord setRace(String strRace) {
		return (HL7_V24_PatientRecord) setField(10, strRace);
	}
	
	public HL7_V24_PatientRecord setPatientAddress(String strPatientAddress) {
		return (HL7_V24_PatientRecord) setField(11, strPatientAddress);
	}
	
	public HL7_V24_PatientRecord setCountyCode(String strCountyCode) {
		return (HL7_V24_PatientRecord) setField(12, strCountyCode);
	}
	
	public HL7_V24_PatientRecord setPhoneNumberHome(String strPhoneNumberHome) {
		return (HL7_V24_PatientRecord) setField(13, strPhoneNumberHome);
	}
	
	public HL7_V24_PatientRecord setPhoneNumberBusiness(String strPhoneNumberBusiness) {
		return (HL7_V24_PatientRecord) setField(14, strPhoneNumberBusiness);
	}
	
	public HL7_V24_PatientRecord setPrimaryLanguage(String strPrimaryLanguage) {
		return (HL7_V24_PatientRecord) setField(15, strPrimaryLanguage);
	}
	
	public HL7_V24_PatientRecord setMaritalStatus(String strMaritalStatus) {
		return (HL7_V24_PatientRecord) setField(16, strMaritalStatus);
	}
	
	public HL7_V24_PatientRecord setReligion(String strReligion) {
		return (HL7_V24_PatientRecord) setField(17, strReligion);
	}
	
	public HL7_V24_PatientRecord setPatientAccountNumber(String strPatientAccountNumber) {
		return (HL7_V24_PatientRecord) setField(18, strPatientAccountNumber);
	}
	
	 public HL7_V24_PatientRecord setSSNNumberPatient(String strSSNNumberPatient) {
	        return (HL7_V24_PatientRecord)setComponent(19, 1, strSSNNumberPatient);
	    }
	
	public HL7_V24_PatientRecord setDriverLicenseNumberPatient(String strDriverLicenseNumberPatient) {
		return (HL7_V24_PatientRecord) setField(20, strDriverLicenseNumberPatient);
	}
	
	public HL7_V24_PatientRecord setMotherIdentifier(String strMotherIdentifier) {
		return (HL7_V24_PatientRecord) setField(21, strMotherIdentifier);
	}
	
	public HL7_V24_PatientRecord setEthnicGroup(String strEthnicGroup) {
		return (HL7_V24_PatientRecord) setField(22, strEthnicGroup);
	}
	
	public HL7_V24_PatientRecord setBirthPlace(String strBirthPlace) {
		return (HL7_V24_PatientRecord) setField(23, strBirthPlace);
	}
	
	public HL7_V24_PatientRecord setMultipleBirth(String strMultipleBirth) {
		return (HL7_V24_PatientRecord) setField(24, strMultipleBirth);
	}
	
	public HL7_V24_PatientRecord setBirthOrder(String strBirthOrder) {
		return (HL7_V24_PatientRecord) setField(25, strBirthOrder);
	}
	
	public HL7_V24_PatientRecord setCitizenship(String strCitizenship) {
		return (HL7_V24_PatientRecord) setField(26, strCitizenship);
	}
	
	public HL7_V24_PatientRecord setVeteransMilitaryStatus(String strVeteransMilitaryStatus ){
		return (HL7_V24_PatientRecord) setField(27, strVeteransMilitaryStatus);
	}
	
	public HL7_V24_PatientRecord setNationality(String strNationality ){
		return (HL7_V24_PatientRecord) setField(28, strNationality);
	}
	
	public HL7_V24_PatientRecord setPatientDeathDateAndTime(String strPatientDeathDateAndTime ){
		return (HL7_V24_PatientRecord) setField(29, strPatientDeathDateAndTime);
	}
	
	public HL7_V24_PatientRecord setPatientDeathIndicator(String strPatientDeathIndicator ){
		return (HL7_V24_PatientRecord) setField(30, strPatientDeathIndicator);
	}
	
	public HL7_V24_PatientRecord setIdentityUnknownIndicator(String strIdentityUnknownIndicator){
		return (HL7_V24_PatientRecord) setField(31, strIdentityUnknownIndicator);
	}
	
	public HL7_V24_PatientRecord setIdentityReliabilityCode(String strIdentityReliabilityCode){
		return (HL7_V24_PatientRecord) setField(32, strIdentityReliabilityCode);
	}
	
	public HL7_V24_PatientRecord setLastUpdateDateTime(String strLastUpdateDateTime){
		return (HL7_V24_PatientRecord) setField(33, strLastUpdateDateTime);
	}
	
	public HL7_V24_PatientRecord setLastUpdateFacility(String strLastUpdateFacility){
		return (HL7_V24_PatientRecord) setField(34, strLastUpdateFacility);
	}
	
	public HL7_V24_PatientRecord setSpeciesCode(String strSpeciesCode){
		return (HL7_V24_PatientRecord) setField(35, strSpeciesCode);
	}
	
	public HL7_V24_PatientRecord setBreedCode(String strBreedCode){
		return (HL7_V24_PatientRecord) setField(36, strBreedCode);
	}
	
	public HL7_V24_PatientRecord setStrain(String strStrain){
		return (HL7_V24_PatientRecord) setField(37, strStrain);
	}
	
	public HL7_V24_PatientRecord setProductionClassCode(String strProductionClassCode){
		return (HL7_V24_PatientRecord) setField(38, strProductionClassCode);
	}
	
	 public HL7_V24_PatientRecord setAlternativeAppointmentID(String strProductionClassCode) {
         // TODO Auto-generated method stub
	     return (HL7_V24_PatientRecord) setField(50, strProductionClassCode);
     }
		
	public String getPatientID( ) {
		return this. getFieldValue(2);
	}
	
	public String getAlternatePatientID() {
		return this. getFieldValue(4);
	}
	
	public String getPatientName() {
		return this. getFieldValue(5);
	}
	
	public String getFirstName() {
	    
	    if(this. getComponentValue2(5,2,"^")!="")
        {
    
        if(getComponentValue2(5,2,"^") != null && getComponentValue2(5,2,"^").contains("~"))
        {
                return this. getComponentValue2(5,2,"^").split("~")[1].trim();
        }
        }
            return "NoName";
	}
	
	public String getSecondName() {
	    if(this. getComponentValue2(5,2,"^")!="")
        {
    
        if(getComponentValue2(5,2,"^") != null && getComponentValue2(5,2,"^").contains("~"))
        {
                return this. getComponentValue2(5,2,"^").split("~")[2].trim();
        }
        }
            return "NoName";
    }
	
	public String getGivenName() {
	   
	    	return this.getComponentValue(5,2);
    }
	
	//old before Zaina merge Tuesday 06/02/2024 
	/*public String getThiredName() {
	   
	    if(this. getComponentValue2(5,2,"^")!="")
        {
    
        if(getComponentValue2(5,2,"^") != null && getComponentValue2(5,2,"^").contains("~"))
        {
                return this. getComponentValue2(5,2,"^").split("~")[3].trim();
        }
        }
            return "NoName";
	}*/
	
	
	public String getThiredName() {

		System.out.println(this.getFieldValue(5).length());

		if (this.getComponentValue2(5, 2, "^") != "") {
			System.out.println(this.getComponentValue2(5, 2, "^"));
			System.out.println(this.getComponentValue2(5, 2, "^").split("~")[2].trim());
			if (getComponentValue2(5, 2, "^") != null && getComponentValue2(5, 2, "^").contains("~")) {
				try {
					return this.getComponentValue2(5, 2, "^").split("~")[3].trim();
				} catch (Exception e) {
					// TODO: handle exception
					return this.getComponentValue2(5, 2, "^").split("~")[2].trim();
				}

			}

		}
		return "NoName";

	}

	
	//old before Zaina merge Tuesday 06/02/2024 
	/*public String getFamilyName()
	{
	    
	    if(this. getComponentValue2(5,2,"^")!="")
        {
    
        if(getComponentValue2(5,2,"^") != null && getComponentValue2(5,2,"^").contains("~"))
        {
                return this. getComponentValue2(5,2,"^").split("~")[0].trim();
        }
        }
            return "NoName";
	}*/
	
	 public String getFamilyName() {

	        if (this.getComponentValue2(5, 2, "^") != "") {

	            System.out.println(this.getComponentValue2(5, 2, "^"));

	            if (getComponentValue2(5, 2, "^") != null &&
	                getComponentValue2(5, 2, "^").contains("~")) {
	                System.out.println(this.getComponentValue2(5, 2, "^").split("~")[0].trim());
	                return this.getComponentValue2(5, 2, "^").split("~")[0].trim();

	            }

	        }
	        return "NoName";
	    }
	
	 
	 
	//old before Zaina merge Tuesday 06/02/2024 
	/*public String getARFirstName() {
	    if(this. getComponentValue2(5,1,"^")!="")
        {
	
	    if(getComponentValue2(5,1,"^") != null && getComponentValue2(5,1,"^").contains("~"))
	    {
	            return this. getComponentValue2(5,1,"^").split("~")[1].trim();
	    }
        }
            return "NoName";
        
    }*/
	 
	 
	 public String getARFirstName() {
		 
		 

	        if (this.getComponentValue2(5, 1, "^") != "") {
					 
																				   

	            if (getComponentValue2(5, 1, "^") != null &&
	                getComponentValue2(5, 1, "^").contains("~")) {
	                return this.getComponentValue2(5, 1, "^").split("~")[1].trim();
	            }

	        }

	        return "NoName";

	    }
	 
	 
	 
	/*public String getArSecondName() {
	    if(this. getComponentValue2(5,1,"^")!="")
        {
	        if(getComponentValue2(5,1,"^") != null && getComponentValue2(5,1,"^").contains("~"))
	        {
	                return this. getComponentValue2(5,1,"^").split("~")[2].trim();
	        }
	        }
	            return "NoName";
	    
	 }*/
	 
	 
	
	
	
	 public String getArSecondName() {
	        if (this.getComponentValue2(5, 1, "^") != "") {
	            if (getComponentValue2(5, 1, "^") != null &&
	                getComponentValue2(5, 1, "^").contains("~")) {
	                return this.getComponentValue2(5, 1, "^").split("~")[2].trim();
	            }
	        }
	        return "NoName";

	    }
	 
	 /*public String getArThirdName() {
	    
	     if(this. getComponentValue2(5,1,"^")!="")
	        {
	            if(getComponentValue2(5,1,"^") != null && getComponentValue2(5,1,"^").contains("~"))
	            {
	                    return this. getComponentValue2(5,1,"^").split("~")[3].trim();
	            }
	            }
	                return "NoName";
     }*/
	 
	 
	 public String getArThirdName() {

	        if (this.getComponentValue2(5, 1, "^") != "") {
	            if (getComponentValue2(5, 1, "^") != null &&

	                getComponentValue2(5, 1, "^").contains("~")) {
	                try {
	                    return this.getComponentValue2(5, 1, "^").split("~")[3].trim();
	                } catch (Exception e) {
	                    // TODO: handle exception
	                    return this.getComponentValue2(5, 1, "^").split("~")[2].trim();
	                }

	            }
	        }
	        return "NoName";
	    }
	 
	 
	 
	 public String getArFamilyName() {
	        if (this.getComponentValue2(5, 1, "^") != "") {
	            if (getComponentValue2(5, 1, "^") != null &&
	                getComponentValue2(5, 1, "^").contains("~")) {
	                return this.getComponentValue2(5, 1, "^").split("~")[0].trim();
	            }
	        }
	        return "NoName";
	    }

	 
	/* public String getArFamilyName() {
	     if(this. getComponentValue2(5,1,"^")!="")
         {
             if(getComponentValue2(5,1,"^") != null && getComponentValue2(5,1,"^").contains("~"))
             {
                     return this. getComponentValue2(5,1,"^").split("~")[0].trim();
             }
             }
                 return "NoName";
	 }*/
	    
	public String getMotherMaidenName() {
		return this. getFieldValue(6);
	}
	
	public String getDateTimeOfBirth() {
		return this. getFieldValue(7);
	}
	
	public String getAdministrativeSex() {
		return this. getFieldValue(8);
	}
	
	public String getPatientAlias() {
		return this. getFieldValue(9);
	}
	
	public String getRace() {
		return this. getFieldValue(10);
	}
	
	public String getPatientAddress() {
		return this. getFieldValue(11);
	}
	
	public String getCountyCode() {
		return this. getFieldValue(12);
	}
	
	public String getPhoneNumberHome() {
		return this. getFieldValue(13);
	}
	
	public String getPhoneNumberBusiness() {
		return this. getFieldValue(14);
	}
	
	public String getPrimaryLanguage() {
		return this. getFieldValue(15);
	}
	
	public String getMaritalStatus() {
		return this. getFieldValue(16);
	}
	
	public String getReligion() {
		return this. getFieldValue(17);
	}
	
	public String getPatientAccountNumber() {
		return this. getFieldValue(18);
	}
	
	 public String getSSNNumberPatient() {
	        return this.getComponentValue(19, 1);
	    
	 }
	
	public String getDriverLicenseNumberPatient() {
		return this. getFieldValue(20);
	}
	
	public String getMotherIdentifier() {
		return this. getFieldValue(21);
	}
	
	public String getEthnicGroup() {
		return this. getFieldValue(22);
	}
	
	public String getBirthPlace() {
		return this. getFieldValue(23);
	}
	
	public String getMultipleBirth() {
		return this. getFieldValue(24);
	}
	
	public String getBirthOrder() {
		return this. getFieldValue(25);
	}
	
	public String getCitizenship() {
		return this. getFieldValue(26);
	}
	
	public String getVeteransMilitaryStatus( ){
		return this. getFieldValue(27);
	}
	
	public String getNationality( ){
		return this. getFieldValue(28);
	}
	
	public String getPatientDeathDateAndTime( ){
		return this. getFieldValue(29);
	}
	
	public String getPatientDeathIndicator( ){
		return this. getFieldValue(30);
	}
	
	public String getIdentityUnknownIndicator(){
		return this. getFieldValue(31);
	}
	
	public String getIdentityReliabilityCode(){
		return this. getFieldValue(32);
	}
	
	public String getLastUpdateDateTime(){
		return this. getFieldValue(33);
	}
	
	public String getLastUpdateFacility(){
		return this. getFieldValue(34);
	}
	
	public String getSpeciesCode(){
		return this. getFieldValue(35);
	}
	
	public String getBreedCode(){
		return this. getFieldValue(36);
	}
	
	public String getStrain(){
		return this. getFieldValue(37);
	}
	
	public String getProductionClassCode(){
		return this. getFieldValue(37);
	}
	
	 public String getAlternativeAppointmentID() {
	        // TODO Auto-generated method stub
	     return this. getFieldValue(50);
	    }
	
	public String getPatientId(int index) {
		return getFieldValue(index);
	}

	public String getGender(int index) {
		return getFieldValue(index);
	}

	

	@Override
	protected HL7_V24_PatientRecord getNew(DelimitedData<Field> fields) {
		return new HL7_V24_PatientRecord(fields);
	}

	@Override
	public String toString() {
		return "PID{" +
				"data=" + data +
				'}';
	}

   

   

   

    
}
