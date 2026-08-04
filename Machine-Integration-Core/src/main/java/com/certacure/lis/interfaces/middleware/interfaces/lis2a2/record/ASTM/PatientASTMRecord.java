package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM;


import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;



public class PatientASTMRecord extends LIS2A2Record {

	public static PatientASTMRecord create(int sequenceNumber) {
		return (PatientASTMRecord) new PatientASTMRecord().setField(1, Type.P.name()).setField(2, String.valueOf(sequenceNumber));
	}

	private PatientASTMRecord() {
	}

	public PatientASTMRecord(DelimitedData<Field> fields) {
		super(fields);
	}

	public PatientASTMRecord setMessageControlId(String messageControlId) {
		return (PatientASTMRecord) setField(3, messageControlId);
	}

	public String getFirstName() {
		return getComponentValue(6, 2 , "^");
	}

	public PatientASTMRecord setFirstName(String firstName) {
		return (PatientASTMRecord) setComponent(6, 1, firstName);
	}
	
	
	public PatientASTMRecord setFirstName(String firstName , String delimiter) {
		return (PatientASTMRecord) setComponent(6, 1, firstName , delimiter);
	}
	
	public PatientASTMRecord setFirstName(String firstName , String delimiter , int FieldIndex , int ComponentIndex ) {
		return (PatientASTMRecord) setComponent(FieldIndex, ComponentIndex, firstName , delimiter);
	}
	
	public PatientASTMRecord setSecondName(String secondName , String delimiter , int FieldIndex , int ComponentIndex ) {
		return (PatientASTMRecord) setComponent(FieldIndex, ComponentIndex, secondName , delimiter);
	}
	
	public PatientASTMRecord setSecondName(String secondName , String delimiter) {
		return (PatientASTMRecord) setComponent(6, 3, secondName , delimiter);
	}

	public PatientASTMRecord setFirstNameAU(String firstName, int index) {
		return (PatientASTMRecord) setField(index, firstName);
	}

	public PatientASTMRecord setSurnameAU(String surname, int index) {
		return (PatientASTMRecord) setField(index, surname);
	}

	public String getSurname() {
		return getComponentValue(6, 1,"^");
	}

	public PatientASTMRecord setSurname(String surname) {
		return (PatientASTMRecord) setComponent(6, 2, surname);
	}
	
	public PatientASTMRecord setSurname(String surname , int FieldIndex , int ComponentIndex ) {
		return (PatientASTMRecord) setComponent(FieldIndex, ComponentIndex, surname);
	}
	
	
	public PatientASTMRecord setSurname(String surname , String delimiter) {
		return (PatientASTMRecord)  setComponent(6, 2, surname , delimiter);
	}

	public String getPatientId(int index) {
		return getFieldValue(index);
	}

	public String getGender(int index) {
		return getFieldValue(index);
	}

	public PatientASTMRecord setPracticePatientId(String patientId) {
		return (PatientASTMRecord) setField(3, patientId);
	}

	public PatientASTMRecord setPatientId(String patientId) {
		return (PatientASTMRecord) setField(4, patientId);
	}
	
	public PatientASTMRecord setPatientId(String patientId , int FieldIndex) {
		return (PatientASTMRecord) setField(FieldIndex, patientId);
	}

	public PatientASTMRecord setSpecialField1(String specialFieldValue) {
		return (PatientASTMRecord) setField(15, specialFieldValue);
	}

	public PatientASTMRecord setLocation(String location, int index) {
		return (PatientASTMRecord) setField(index, location);
	}

	public PatientASTMRecord setGender(String gender) {
		return (PatientASTMRecord) setField(9, gender);
	}

	public PatientASTMRecord setBirthDate(String dateOfBirth) {
		return (PatientASTMRecord) setField(8, dateOfBirth);
	}

	public PatientASTMRecord setAgeYear(String ageYear) {
		return (PatientASTMRecord) setComponent(8, 1, ageYear);
	}

	public PatientASTMRecord setAgeMonth(String ageMonth) {
		return (PatientASTMRecord) setComponent(8, 2, ageMonth);
	}

	public PatientASTMRecord setBirthDateAU(String dateOfBirth) {
		return (PatientASTMRecord) setComponent(8, 3, dateOfBirth);
	}

	public PatientASTMRecord setAttendingPhysician(String doctorName) {
		return (PatientASTMRecord) setField(14, doctorName);
	}

	public PatientASTMRecord setRegistrationDate(String registrationDate) {
		return (PatientASTMRecord) setField(33, registrationDate);
	}

	@Override
	protected PatientASTMRecord getNew(DelimitedData<Field> fields) {
		return new PatientASTMRecord(fields);
	}

	@Override
	public String toString() {
		return "P{" +
				"data=" + data +
				'}';
	}

}
