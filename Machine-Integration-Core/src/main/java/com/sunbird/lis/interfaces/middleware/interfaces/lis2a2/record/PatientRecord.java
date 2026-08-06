package com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record;


import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class PatientRecord extends LIS2A2Record {

	public static PatientRecord create(int sequenceNumber) {
		return (PatientRecord) new PatientRecord()
				.setField(1, Type.PID.name())
				.setField(2, String.valueOf(sequenceNumber));
	}

	private PatientRecord() {
	}

	PatientRecord(DelimitedData<Field> fields) {
		super(fields);
	}
	
	
	public PatientRecord setPatientID(String strPatientID) {
		return (PatientRecord) setField(2, strPatientID);
	}
	
	public PatientRecord setAlternatePatientID(String strAlternatePatientID) {
		return (PatientRecord) setField(4, strAlternatePatientID);
	}
	
	public PatientRecord setPatientName(String strPatientName) {
		return (PatientRecord) setField(5, strPatientName);
	}
	
	public PatientRecord setFirstName(String firstName) {
		return (PatientRecord) setComponent(5, 2, firstName);
	}
	
	public PatientRecord setMiddleName(String strMiddleName) {
		return (PatientRecord) setComponent(5, 3, strMiddleName);
	}
	
	public PatientRecord setFamilyName(String strFamilyName) {
		return (PatientRecord) setComponent(5, 1, strFamilyName);
	}
	
	public PatientRecord setMotherMaidenName(String strMotherMaidenName) {
		return (PatientRecord) setField(6, strMotherMaidenName);
	}
	
	public PatientRecord setDateTimeOfBirth(String strDateTimeOfBirth) {
		return (PatientRecord) setField(7, strDateTimeOfBirth);
	}
	
	public PatientRecord setAdministrativeSex(String strAdministrativeSex) {
		return (PatientRecord) setField(8, strAdministrativeSex);
	}
	
	public PatientRecord setPatientAlias(String strPatientAlias) {
		return (PatientRecord) setField(9, strPatientAlias);
	}
	
	public PatientRecord setRace(String strRace) {
		return (PatientRecord) setField(10, strRace);
	}
	
	public PatientRecord setPatientAddress(String strPatientAddress) {
		return (PatientRecord) setField(11, strPatientAddress);
	}
	
	public PatientRecord setCountyCode(String strCountyCode) {
		return (PatientRecord) setField(12, strCountyCode);
	}
	
	public PatientRecord setPhoneNumberHome(String strPhoneNumberHome) {
		return (PatientRecord) setField(13, strPhoneNumberHome);
	}
	
	public PatientRecord setPhoneNumberBusiness(String strPhoneNumberBusiness) {
		return (PatientRecord) setField(14, strPhoneNumberBusiness);
	}
	
	public PatientRecord setPrimaryLanguage(String strPrimaryLanguage) {
		return (PatientRecord) setField(15, strPrimaryLanguage);
	}
	
	public PatientRecord setMaritalStatus(String strMaritalStatus) {
		return (PatientRecord) setField(16, strMaritalStatus);
	}
	
	public PatientRecord setReligion(String strReligion) {
		return (PatientRecord) setField(17, strReligion);
	}
	
	public PatientRecord setPatientAccountNumber(String strPatientAccountNumber) {
		return (PatientRecord) setField(18, strPatientAccountNumber);
	}
	
	public PatientRecord setSSNNumberPatient(String strSSNNumberPatient) {
		return (PatientRecord) setField(19, strSSNNumberPatient);
	}
	
	public PatientRecord setDriverLicenseNumberPatient(String strDriverLicenseNumberPatient) {
		return (PatientRecord) setField(20, strDriverLicenseNumberPatient);
	}
	
	public PatientRecord setMotherIdentifier(String strMotherIdentifier) {
		return (PatientRecord) setField(21, strMotherIdentifier);
	}
	
	public PatientRecord setEthnicGroup(String strEthnicGroup) {
		return (PatientRecord) setField(22, strEthnicGroup);
	}
	
	public PatientRecord setBirthPlace(String strBirthPlace) {
		return (PatientRecord) setField(23, strBirthPlace);
	}
	
	public PatientRecord setMultipleBirth(String strMultipleBirth) {
		return (PatientRecord) setField(24, strMultipleBirth);
	}
	
	public PatientRecord setBirthOrder(String strBirthOrder) {
		return (PatientRecord) setField(25, strBirthOrder);
	}
	
	public PatientRecord setCitizenship(String strCitizenship) {
		return (PatientRecord) setField(26, strCitizenship);
	}
	
	public PatientRecord setVeteransMilitaryStatus(String strVeteransMilitaryStatus ){
		return (PatientRecord) setField(27, strVeteransMilitaryStatus);
	}
	
	public PatientRecord setNationality(String strNationality ){
		return (PatientRecord) setField(28, strNationality);
	}
	
	public PatientRecord setPatientDeathDateAndTime(String strPatientDeathDateAndTime ){
		return (PatientRecord) setField(29, strPatientDeathDateAndTime);
	}
	
	public PatientRecord setPatientDeathIndicator(String strPatientDeathIndicator ){
		return (PatientRecord) setField(30, strPatientDeathIndicator);
	}
	
	public PatientRecord setIdentityUnknownIndicator(String strIdentityUnknownIndicator){
		return (PatientRecord) setField(31, strIdentityUnknownIndicator);
	}
	
	public PatientRecord setIdentityReliabilityCode(String strIdentityReliabilityCode){
		return (PatientRecord) setField(32, strIdentityReliabilityCode);
	}
	
	public PatientRecord setLastUpdateDateTime(String strLastUpdateDateTime){
		return (PatientRecord) setField(33, strLastUpdateDateTime);
	}
	
	public PatientRecord setLastUpdateFacility(String strLastUpdateFacility){
		return (PatientRecord) setField(34, strLastUpdateFacility);
	}
	
	public PatientRecord setSpeciesCode(String strSpeciesCode){
		return (PatientRecord) setField(35, strSpeciesCode);
	}
	
	public PatientRecord setBreedCode(String strBreedCode){
		return (PatientRecord) setField(36, strBreedCode);
	}
	
	public PatientRecord setStrain(String strStrain){
		return (PatientRecord) setField(37, strStrain);
	}
	
	public PatientRecord setProductionClassCode(String strProductionClassCode){
		return (PatientRecord) setField(38, strProductionClassCode);
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
		return this. getComponentValue(5, 2);
	}
	
	public String getMiddleName() {
		return this. getComponentValue(5, 3);
	}
	
	public String getFamilyName() {
		return this. getComponentValue(5, 1);
	}
	
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
	
	public String getPhoneNumberHome(String strPhoneNumberHome) {
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
		return this. getFieldValue(19);
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
	
	public String getPatientId(int index) {
		return getFieldValue(index);
	}

	public String getGender(int index) {
		return getFieldValue(index);
	}

	

	@Override
	protected PatientRecord getNew(DelimitedData<Field> fields) {
		return new PatientRecord(fields);
	}

	@Override
	public String toString() {
		return "PID{" +
				"data=" + data +
				'}';
	}

}
