package com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24;

import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record.Type;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;

public class HL7_V24_Visit1Record extends LIS2A2Record {
	/*
	 * private String SetID_PV1; private String strPatientClass; private String
	 * strAssignedPatientLocation; private String strAdmissionType ; private String
	 * strPreadmitNumber ; private String strPriorPatientLocation ; private String
	 * strAttendingDoctor; private String strReferringDoctor; private String
	 * strConsultingDoctor; private String strHospitalService; private String
	 * strTemporaryLocation; private String strPreadmitTestIndicator; private String
	 * strRe_admissionIndicator; private String strAdmitSource; private String
	 * strAmbulatoryStatus; private String strVIPIndicator; private String
	 * strAdmittingDoctor; private String strPatientType; private String
	 * strVisitNumber; private String strFinancialClass; private String
	 * strChargePriceIndicator; private String strCourtesyCode; private String
	 * strCreditRating; private String strContractCode; private String
	 * strContractEffectiveDate; private String strContractAmount; private String
	 * strContractPeriod; private String strInterestCode; private String
	 * strTransfertoBadDebtCode; private String strTransfertoBadDebtDate; private
	 * String strBadDebtAgencyCode; private String strBadDebtTransferAmount; private
	 * String strBadDebtRecoveryAmount; private String strDeleteAccountIndicator;
	 * private String strDeleteAccountDate; private String strDischargeDisposition;
	 * private String strDischargedtoLocation; private String strDietType ; private
	 * String strServicingFacility; private String strBedStatus; private String
	 * strAccountStatus; private String strPendingLocation; private String
	 * strPriorTemporaryLocation; private String strAdmitDateTime; private String
	 * strDischargeDateTime; private String strCurrentPatientBalance; private String
	 * strTotalCharges ; private String strTotalAdjustments ; private String
	 * strTotalPayments; private String strAlternateVisitID; private String
	 * strVisitIndicator; private String strOtherHealthcareProvider;
	 * 
	 */

	public static HL7_V24_Visit1Record create(int sequenceNumber) {
		return (HL7_V24_Visit1Record) new HL7_V24_Visit1Record().setField(1, Type.PV1.name()).setField(2,
				String.valueOf(sequenceNumber));
	}

	public HL7_V24_Visit1Record() {
	}

	public HL7_V24_Visit1Record(DelimitedData<Field> fields) {
		super(fields);
	}

	public HL7_V24_Visit1Record setPatientClass(String strPatientClass) {
		return (HL7_V24_Visit1Record) setField(2, strPatientClass);
	}

	public HL7_V24_Visit1Record setAssignedPatientLocation(String strPointOfCare, String strRoom, String strBed) {
		return (HL7_V24_Visit1Record) setComponent(3, 1, strPointOfCare).setComponent(3, 2, strRoom).setComponent(3, 3,
				strBed);

	}

	public HL7_V24_Visit1Record setSetID_PV1(String setID_PV1) {
		return (HL7_V24_Visit1Record) this.setField(1, setID_PV1);
	}

	public HL7_V24_Visit1Record setPatientClassPV1(String strPatientClass) {
		return (HL7_V24_Visit1Record) this.setField(2, strPatientClass);
	}

	public HL7_V24_Visit1Record setAssignedPatientLocation(String assignedPatientLocation) {
		return (HL7_V24_Visit1Record) this.setField(3, assignedPatientLocation);
	}

	public HL7_V24_Visit1Record setAdmissionType(String admissionType) {
		return (HL7_V24_Visit1Record) this.setField(4, admissionType);
	}

	public HL7_V24_Visit1Record setPreadmitNumber(String preadmitNumber) {
		return (HL7_V24_Visit1Record) this.setField(5, preadmitNumber);
	}

	public HL7_V24_Visit1Record setPriorPatientLocation(String priorPatientLocation) {
		return (HL7_V24_Visit1Record) this.setField(6, priorPatientLocation);
	}

	public HL7_V24_Visit1Record setAttendingDoctor(String attendingDoctor) {
		return (HL7_V24_Visit1Record) this.setField(7, attendingDoctor);
	}

	public HL7_V24_Visit1Record setReferringDoctor(String referringDoctor) {
		return (HL7_V24_Visit1Record) this.setField(8, referringDoctor);
	}

	public HL7_V24_Visit1Record setConsultingDoctor(String consultingDoctor) {
		return (HL7_V24_Visit1Record) this.setField(9, consultingDoctor);
	}

	public HL7_V24_Visit1Record setHospitalService(String hospitalService) {
		return (HL7_V24_Visit1Record) this.setField(10, hospitalService);
	}

	public HL7_V24_Visit1Record setTemporaryLocation(String temporaryLocation) {
		return (HL7_V24_Visit1Record) this.setField(11, temporaryLocation);
	}

	public HL7_V24_Visit1Record setPreadmitTestIndicator(String preadmitTestIndicator) {
		return (HL7_V24_Visit1Record) this.setField(12, preadmitTestIndicator);
	}

	public HL7_V24_Visit1Record setRe_admissionIndicator(String re_admissionIndicator) {
		return (HL7_V24_Visit1Record) this.setField(13, re_admissionIndicator);
	}

	public HL7_V24_Visit1Record setAdmitSource(String admitSource) {
		return (HL7_V24_Visit1Record) this.setField(14, admitSource);
	}

	public HL7_V24_Visit1Record setAmbulatoryStatus(String ambulatoryStatus) {
		return (HL7_V24_Visit1Record) this.setField(15, ambulatoryStatus);
	}

	public HL7_V24_Visit1Record setVIPIndicator(String vIPIndicator) {
		return (HL7_V24_Visit1Record) this.setField(16, vIPIndicator);
	}

	public HL7_V24_Visit1Record setAdmittingDoctor(String admittingDoctor) {
		return (HL7_V24_Visit1Record) this.setField(17, admittingDoctor);
	}

	public HL7_V24_Visit1Record setPatientType(String patientType) {
		return (HL7_V24_Visit1Record) this.setField(18, patientType);
	}

	public HL7_V24_Visit1Record setVisitNumber(String visitNumber) {
		return (HL7_V24_Visit1Record) this.setField(19, visitNumber);
	}

	public HL7_V24_Visit1Record setFinancialClass(String financialClass) {
		return (HL7_V24_Visit1Record) this.setField(20, financialClass);
	}

	public HL7_V24_Visit1Record setChargePriceIndicator(String chargePriceIndicator) {
		return (HL7_V24_Visit1Record) this.setField(21, chargePriceIndicator);
	}

	public HL7_V24_Visit1Record setCourtesyCode(String courtesyCode) {
		return (HL7_V24_Visit1Record) this.setField(22, courtesyCode);
	}

	public HL7_V24_Visit1Record setCreditRating(String creditRating) {
		return (HL7_V24_Visit1Record) this.setField(23, creditRating);
	}

	public HL7_V24_Visit1Record setContractCode(String contractCode) {
		return (HL7_V24_Visit1Record) this.setField(24, contractCode);
	}

	public HL7_V24_Visit1Record setContractEffectiveDate(String contractEffectiveDate) {
		return (HL7_V24_Visit1Record) this.setField(25, contractEffectiveDate);
	}

	public HL7_V24_Visit1Record setContractAmount(String contractAmount) {
		return (HL7_V24_Visit1Record) this.setField(26, contractAmount);
	}

	public HL7_V24_Visit1Record setContractPeriod(String contractPeriod) {
		return (HL7_V24_Visit1Record) this.setField(27, contractPeriod);
	}

	public HL7_V24_Visit1Record setInterestCode(String interestCode) {
		return (HL7_V24_Visit1Record) this.setField(28, interestCode);
	}

	public HL7_V24_Visit1Record setTransfertoBadDebtCode(String transfertoBadDebtCode) {
		return (HL7_V24_Visit1Record) this.setField(29, transfertoBadDebtCode);
	}

	public HL7_V24_Visit1Record setTransfertoBadDebtDate(String transfertoBadDebtDate) {
		return (HL7_V24_Visit1Record) this.setField(30, transfertoBadDebtDate);
	}

	public HL7_V24_Visit1Record setBadDebtAgencyCode(String badDebtAgencyCode) {
		return (HL7_V24_Visit1Record) this.setField(31, badDebtAgencyCode);
	}

	public HL7_V24_Visit1Record setBadDebtTransferAmount(String badDebtTransferAmount) {
		return (HL7_V24_Visit1Record) this.setField(32, badDebtTransferAmount);
	}

	public HL7_V24_Visit1Record setBadDebtRecoveryAmount(String badDebtRecoveryAmount) {
		return (HL7_V24_Visit1Record) this.setField(33, badDebtRecoveryAmount);
	}

	public HL7_V24_Visit1Record setDeleteAccountIndicator(String deleteAccountIndicator) {
		return (HL7_V24_Visit1Record) this.setField(34, deleteAccountIndicator);
	}

	public HL7_V24_Visit1Record setDeleteAccountDate(String deleteAccountDate) {
		return (HL7_V24_Visit1Record) this.setField(35, deleteAccountDate);
	}

	public HL7_V24_Visit1Record setDischargeDisposition(String dischargeDisposition) {
		return (HL7_V24_Visit1Record) this.setField(36, dischargeDisposition);
	}

	public HL7_V24_Visit1Record setDischargedtoLocation(String dischargedtoLocation) {
		return (HL7_V24_Visit1Record) this.setField(37, dischargedtoLocation);
	}

	public HL7_V24_Visit1Record setDietType(String dietType) {
		return (HL7_V24_Visit1Record) this.setField(38, dietType);
	}

	public HL7_V24_Visit1Record setServicingFacility(String servicingFacility) {
		return (HL7_V24_Visit1Record) this.setField(39, servicingFacility);
	}

	public HL7_V24_Visit1Record setBedStatus(String bedStatus) {
		return (HL7_V24_Visit1Record) this.setField(40, bedStatus);
	}

	public HL7_V24_Visit1Record setAccountStatus(String accountStatus) {
		return (HL7_V24_Visit1Record) this.setField(41, accountStatus);
	}

	public HL7_V24_Visit1Record setPendingLocation(String pendingLocation) {
		return (HL7_V24_Visit1Record) this.setField(42, pendingLocation);
	}

	public HL7_V24_Visit1Record setPriorTemporaryLocation(String priorTemporaryLocation) {
		return (HL7_V24_Visit1Record) this.setField(43, priorTemporaryLocation);
	}

	public HL7_V24_Visit1Record setAdmitDateTime(String admitDateTime) {
		return (HL7_V24_Visit1Record) this.setField(44, admitDateTime);
	}

	public HL7_V24_Visit1Record setDischargeDateTime(String dischargeDateTime) {
		return (HL7_V24_Visit1Record) this.setField(45, dischargeDateTime);
	}

	public HL7_V24_Visit1Record setCurrentPatientBalance(String currentPatientBalance) {
		return (HL7_V24_Visit1Record) this.setField(46, currentPatientBalance);
	}

	public HL7_V24_Visit1Record setTotalCharges(String totalCharges) {
		return (HL7_V24_Visit1Record) this.setField(47, totalCharges);
	}

	public HL7_V24_Visit1Record setTotalAdjustments(String totalAdjustments) {
		return (HL7_V24_Visit1Record) this.setField(48, totalAdjustments);
	}

	public HL7_V24_Visit1Record setTotalPayments(String totalPayments) {
		return (HL7_V24_Visit1Record) this.setField(49, totalPayments);
	}

	public HL7_V24_Visit1Record setAlternateVisitID(String alternateVisitID) {
		return (HL7_V24_Visit1Record) this.setField(50, alternateVisitID);
	}

	public HL7_V24_Visit1Record setVisitIndicator(String visitIndicator) {
		return (HL7_V24_Visit1Record) this.setField(51, visitIndicator);
	}

	public HL7_V24_Visit1Record setOtherHealthcareProvider(String otherHealthcareProvider) {
		return (HL7_V24_Visit1Record) this.setField(52, otherHealthcareProvider);
	}

	public HL7_V24_Visit1Record setAssignedPatient_PointOfCare(String AssignedPatient_PointOfCare) {
		return (HL7_V24_Visit1Record) this.setComponent(2, 1, AssignedPatient_PointOfCare);
	}

	public HL7_V24_Visit1Record setAssignedPatient_Room(String AssignedPatient_Room) {
		return (HL7_V24_Visit1Record) this.setComponent(2, 2, AssignedPatient_Room);
	}

	public HL7_V24_Visit1Record setAssignedPatient_Bed(String AssignedPatient_Bed) {
		return (HL7_V24_Visit1Record) this.setComponent(2, 3, AssignedPatient_Bed);
	}
	
	///// Getters
	
	public String getAssignedPatient_PointOfCare() {
		return this.getComponentValue(3,1);
	}
	
	public String getAssignedPatient_Room() {
		return this.getComponentValue(3,2);
	}
	
	public String getAssignedPatient_Bed() {
		return this.getComponentValue(3,3);
	}
	
	
	public String getSetID_PV1() {
		return this.getFieldValue(1);
	}

	public String getPatientClass() {
		return this.getFieldValue(2);
	}

	public String getAssignedPatientLocation() {
		return this.getFieldValue(3);
	}

	public String getAdmissionType() {
		return this.getFieldValue(4);
	}

	public String getPreadmitNumber() {
		return this.getFieldValue(5);
	}

	public String getPriorPatientLocation() {
		return this.getFieldValue(6);
	}

	public String getAttendingDoctor() {
		return this.getFieldValue(7);
	}

	public String getReferringDoctor() {
		return this.getFieldValue(8);
	}

	public String getConsultingDoctor() {
		return this.getFieldValue(9);
	}

	public String getHospitalService() {
		return this.getFieldValue(10);
	}

	public String getTemporaryLocation() {
		return this.getFieldValue(11);
	}

	public String getPreadmitTestIndicator() {
		return this.getFieldValue(12);
	}

	public String getRe_admissionIndicator() {
		return this.getFieldValue(13);
	}

	public String getAdmitSource() {
		return this.getFieldValue(14);
	}

	public String getAmbulatoryStatus() {
		return this.getFieldValue(15);
	}

	public String getVIPIndicator() {
		return this.getFieldValue(16);
	}

	public String getAdmittingDoctor() {
		return this.getFieldValue(17);
	}

	public String getPatientType() {
		return this.getFieldValue(18);
	}

	public String getVisitNumber() {
		return this.getFieldValue(19);
	}

	public String getFinancialClass() {
		return this.getFieldValue(20);
	}

	public String getChargePriceIndicator() {
		return this.getFieldValue(21);
	}

	public String getCourtesyCode() {
		return this.getFieldValue(22);
	}

	public String getCreditRating() {
		return this.getFieldValue(23);
	}

	public String getContractCode() {
		return this.getFieldValue(24);
	}

	public String getContractEffectiveDate() {
		return this.getFieldValue(25);
	}

	public String getContractAmount() {
		return this.getFieldValue(26);
	}

	public String getContractPeriod() {
		return this.getFieldValue(27);
	}

	public String getInterestCode() {
		return this.getFieldValue(28);
	}

	public String getTransfertoBadDebtCode() {
		return this.getFieldValue(29);
	}

	public String getTransfertoBadDebtDate() {
		return this.getFieldValue(30);
	}

	public String getBadDebtAgencyCode() {
		return this.getFieldValue(31);
	}

	public String getBadDebtTransferAmount() {
		return this.getFieldValue(32);
	}

	public String getBadDebtRecoveryAmount() {
		return this.getFieldValue(33);
	}

	public String getDeleteAccountIndicator() {
		return this.getFieldValue(34);
	}

	public String getDeleteAccountDate() {
		return this.getFieldValue(35);
	}

	public String getDischargeDisposition() {
		return this.getFieldValue(36);
	}

	public String getDischargedtoLocation() {
		return this.getFieldValue(37);
	}

	public String getDietType() {
		return this.getFieldValue(38);
	}

	public String getServicingFacility() {
		return this.getFieldValue(39);
	}

	public String getBedStatus() {
		return this.getFieldValue(40);
	}

	public String getAccountStatus() {
		return this.getFieldValue(41);
	}

	public String getPendingLocation() {
		return this.getFieldValue(42);
	}

	public String getPriorTemporaryLocation() {
		return this.getFieldValue(43);
	}

	public String getAdmitDateTime() {
		return this.getFieldValue(44);
	}

	public String getDischargeDateTime() {
		return this.getFieldValue(45);
	}

	public String getCurrentPatientBalance() {
		return this.getFieldValue(46);
	}

	public String getTotalCharges() {
		return this.getFieldValue(47);
	}

	public String getTotalAdjustments() {
		return this.getFieldValue(48);
	}

	public String getTotalPayments() {
		return this.getFieldValue(49);
	}

	public String getAlternateVisitID() {
		return this.getFieldValue(50);
	}

	public String getVisitIndicator() {
		return this.getFieldValue(51);
	}

	public String getOtherHealthcareProvider() {
		return this.getFieldValue(52);
	}

	@Override
	public String toString() {
		return "PV1{" + "data=" + data + '}';
	}

	@Override
	protected LIS2A2Record getNew(DelimitedData<Field> data) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAttendingDoctorCode() {
		return this.getComponentValue(7,1);
	}
	
	public String getAttendingDoctorFamilyName() {
		return this.getComponentValue(7,2);
	}
	public String getAttendingDoctorSecondAndFatherName() {
		return this.getComponentValue(7,3);
	}
	public String getAttendingDoctorSuffix() {
		return this.getComponentValue(7,4);
	}

    public String getAssignedPatient_Bed_Name() {
        return this.getComponentValue(3,3);
    }
    
    public String getAssignedPatient_Bed_Code() {
        return this.getComponentValue(3,4);
    }

    public String getAdmittingDoctorCode() {
        return this.getComponentValue(17,1);
    }
    
    public String getAdmittingDoctor_2() {
        return this.getComponentValue(17,1);
    }
    
    public String getAdmittingDoctor_3() {
        return this.getComponentValue(17,1);
    }

	public String getConsultingDoctorCode() {
		return this.getComponentValue(9,1);
	}
	public String getReferringDoctorCode() {

		return this.getComponentValue(8,1);
	}

}
