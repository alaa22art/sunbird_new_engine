package com.certacure.lis.interfaces.middleware.interfaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.QueryRecord;

public class LabMessages {

	public static class Analysis {

		public String code;
		public final String name;
		public String panelCode;
		public final Result result;
		public final String resultCode;

		public Analysis() {
			this(null, null, null, null, null);
		}

		public Analysis(String code, String resultCode, String name, Result result) {
			this.code = code;
			this.resultCode = resultCode;
			this.name = name;
			this.result = result;
		}

		public Analysis(String panelCode, String code, String resultCode, String name, Result result) {
			this.panelCode = panelCode;
			this.code = code;
			this.resultCode = resultCode;
			this.name = name;
			this.result = result;
		}

		@Override
		public String toString() {
			return "Analysis{" + "panelCode='" + panelCode + '\'' + ",code='" + code + '\'' + ",resultCode='"
					+ resultCode + '\'' + ", name='" + name + '\'' + ", result=" + result + '}';
		}
	}

	public static class ConnectionStatus implements Serializable {

		private static final long serialVersionUID = 1L;
		public Machine machine;
		public Integer port;
		public String status;

		public ConnectionStatus() {
			this(null, null, null); // for gson
		}

		public ConnectionStatus(String status, Integer port, Machine machine) {
			this.status = status;
			this.port = port;
			this.machine = machine;
		}

		@Override
		public String toString() {
			return "ConnectionStatus{" + "status=" + status + "port=" + port + "machine=" + machine + '}';
		}
	}

	public static class Container {

		public final String actionCode;
		public final List<Analysis> analyses;
		public final String collectionDate;
		public final String lotNumber;
		public final String priority;
		public final String qcName;
		public final String qcNumber;
		public final String reportType;
		public final String specimenDescriptor;
		public final String specimenId;
		public final String specimenPosition;
		public  String specimenRackID;
		public  String queryControlID;
		public  String queryTag;
		public  String barcodeMode;

		public Container() {
			this(null, null, null, null, null, null, null, null);
		}

		public Container(String specimenId, List<Analysis> analyses, String specimenPosition, String priority,
				String collectionDate, String specimenDescriptor, String actionCode, String reportType) {
			this.specimenId = specimenId;
			this.analyses = analyses;
			this.specimenPosition = specimenPosition;
			this.priority = priority;
			this.collectionDate = collectionDate;
			this.specimenDescriptor = specimenDescriptor;
			this.actionCode = actionCode;
			this.lotNumber = null;
			this.qcName = null;
			this.qcNumber = null;
			this.reportType = reportType;
		}
		
		
		public Container(String specimenId,
				List<Analysis> analyses,
				String specimenRackID ,
				String specimenPosition,
				String priority,
				String collectionDate,
				String specimenDescriptor,
				String actionCode, 
				String reportType) {
			this.specimenId = specimenId;
			this.analyses = analyses;
			this.specimenPosition = specimenPosition;
			this.specimenRackID = specimenRackID;
			this.priority = priority;
			this.collectionDate = collectionDate;
			this.specimenDescriptor = specimenDescriptor;
			this.actionCode = actionCode;
			this.lotNumber = null;
			this.qcName = null;
			this.qcNumber = null;
			this.reportType = reportType;
		}
		
		
		public Container(String specimenId,
				List<Analysis> analyses,
				String specimenRackID ,
				String specimenPosition,
				String priority,
				String collectionDate,
				String specimenDescriptor,
				String actionCode,
				String reportType ,
				String queryTag ,
				String QueryControlID ,
				String strBarcodeMode) {
			this.specimenId = specimenId;
			this.analyses = analyses;
			this.specimenPosition = specimenPosition;
			this.specimenRackID = specimenRackID;
			this.priority = priority;
			this.collectionDate = collectionDate;
			this.specimenDescriptor = specimenDescriptor;
			this.actionCode = actionCode;
			this.lotNumber = null;
			this.qcName = null;
			this.qcNumber = null;
			this.reportType = reportType;
			this.queryControlID = QueryControlID;
			this.queryTag = queryTag;
			this.barcodeMode = strBarcodeMode;
		}

		public Container(String specimenId, List<Analysis> analyses, String specimenPosition, String priority,
				String collectionDate, String specimenDescriptor, String actionCode, String qcName, String lotNumber,
				String qcNumber, String reportType , String queryTag , String QueryControlID) {
			this.specimenId = specimenId;
			this.analyses = analyses;
			this.specimenPosition = specimenPosition;
			this.priority = priority;
			this.collectionDate = collectionDate;
			this.specimenDescriptor = specimenDescriptor;
			this.actionCode = actionCode;
			this.lotNumber = lotNumber;
			this.qcName = qcName;
			this.qcNumber = qcNumber;
			this.queryTag = queryTag;
			this.reportType = reportType;
		}

		public Container(String specimenId, String specimenPosition, String collectionDate, String actionCode,
				String reportType) {

			this.specimenId = specimenId;
			this.analyses = null;
			this.specimenPosition = specimenPosition;
			this.priority = null;
			this.collectionDate = collectionDate;
			this.specimenDescriptor = "";
			this.lotNumber = null;
			this.qcName = null;
			this.qcNumber = null;
			this.actionCode = actionCode;
			this.reportType = reportType;
		}

		@Override
		public String toString() {
			return "Container{" + "specimenId='" + specimenId + '\'' + ", analyses=" + analyses + ", priority="
					+ priority + ", collectionDate=" + collectionDate + ", specimenDescriptor=" + specimenDescriptor
					+ ", actionCode=" + actionCode + '}';
		}
	}

	public static class LabMachineMsg implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public String machineActorPath;
		public String machineName;
		public String saveActorPathUrl;

		public LabMachineMsg() {
			this(new String(), new String(), new String());
		}

		public LabMachineMsg(String machineName, String machineActorPath, String saveActorPathUrl) {
			this.machineName = machineName;
			this.machineActorPath = machineActorPath;
			this.saveActorPathUrl = saveActorPathUrl;
		}

		@Override
		public String toString() {
			return "MyLabMachineMsg{" + "machineName=" + machineName + ", machineActorPath=" + machineActorPath
					+ ", saveActorPathUrl=" + saveActorPathUrl + '}';
		}
	}

	public static class LabOrderMsg implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public final Order order;

		public LabOrderMsg() {
			this(null); // for gson
		}

		public LabOrderMsg(Order order) {
			this.order = order;
		}

		public Order getOrder() {
			return this.order;
		}

		@Override
		public String toString() {
			return "MyLabOrderMsg{" + "order=" + order + '}';
		}
	}

	public static class LabQueryMsg implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public final List<String> analysisCodes;
		public String machineName;
		public String queryMessageControlID;
		public String queryTag;
		public final String specimenIds;
		public String specimenPosition;
		public String strContainerRackNumber;
		public String strConatinerRackPositionNumber;
		public String strBarcodeMode;

		public LabQueryMsg() {
			this(new String(), new ArrayList<>(), new String(), new String());
		}

		public LabQueryMsg(String specimenIds, List<String> analysisCodes, String machineName,
				String specimenPosition) {
			this.specimenIds = specimenIds;
			this.analysisCodes = Collections.unmodifiableList(analysisCodes);
			this.machineName = machineName;
			this.specimenPosition = specimenPosition;
		}

		
		
		public LabQueryMsg(String specimenIds,
				List<String> analysisCodes,
				String machineName,
				String specimenPosition,
				String queryMessageControlID,
				String queryTag , 
				String strContainerRackNumber , 
				String strConatinerRackPositionNumber ) {
			this.specimenIds = specimenIds;
			this.analysisCodes = Collections.unmodifiableList(analysisCodes);
			this.machineName = machineName;
			this.specimenPosition = specimenPosition;
			this.queryMessageControlID = queryMessageControlID;
			this.queryTag = queryTag;
			this.strConatinerRackPositionNumber = strConatinerRackPositionNumber;
			this.strContainerRackNumber = strContainerRackNumber;
		}
		
		
		public LabQueryMsg(String specimenIds, 
				String machineName,
				String specimenPosition,
				String queryMessageControlID,
				String queryTag ,
				String strContainerRackNumber, 
				String barcodeMode) {
			this.specimenIds = specimenIds;
			this.analysisCodes = null;
			this.machineName = machineName;
			this.specimenPosition = specimenPosition;
			this.queryMessageControlID = queryMessageControlID;
			this.queryTag = queryTag;
			this.strContainerRackNumber = strContainerRackNumber;
			this.strBarcodeMode = barcodeMode;
		}

		public LabQueryMsg(String specimenIds, String machineName, String specimenPositionInfo,
				String queryMessageControlID) {
			this.specimenIds = specimenIds;
			this.analysisCodes = null;
			this.machineName = machineName;
			this.specimenPosition = specimenPositionInfo;
			this.queryMessageControlID = queryMessageControlID;
		}

		public LabQueryMsg(String specimenIds, String machineName, String specimenPosition,
				String queryMessageControlID, String queryTag) {
			this.specimenIds = specimenIds;
			this.analysisCodes = null;
			this.machineName = machineName;
			this.specimenPosition = specimenPosition;
			this.queryMessageControlID = queryMessageControlID;
			this.queryTag = queryTag;
		}
		
		public LabQueryMsg(String specimenIds, String machineName, String specimenPosition) {
			this.specimenIds = specimenIds;
			this.analysisCodes = null;
			this.machineName = machineName;
			this.specimenPosition = specimenPosition;
			
		}


		@Override
		public String toString() {
			return "MyLabQueryMsg{" + "specimenIds=" + specimenIds + ", analysisCodes=" + analysisCodes
					+ ", machineName=" + machineName + ", specimenPosition=" + specimenPosition
					+ ", queryMessageControlID = " + queryMessageControlID + ", queryTag = " + queryTag 
					+ ", barcodeMode = " + strBarcodeMode + '}';
		}
	}

	public static class LabResultMsg implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public final Order order;

		public LabResultMsg() {
			this(null); // for gson
		}

		public LabResultMsg(Order order) {
			this.order = order;
		}

		@Override
		public String toString() {
			return "MyLabResultMsg{" + "order=" + order + '}';
		}
	}

	public static class Order {

		public final Container container;
		public final List<Container> containers;
		public final String machineName;
		public final long messageTransactionID;
		public final String password;
		public final Patient patient;
		public final String queryMessageContrlID;
		public final String queryTag;
		public final String senderName;
		public String rackNumber;
		public String rackPosition;

		public Order(Patient patient, Container container, String password, String machineName, String senderName,
				long messageTransaction, String queryMessageContrlID, String queryTag) {
			this.patient = patient;
			this.container = container;
			this.password = (password == null ? "" : password);
			this.machineName = machineName;
			this.senderName = (senderName == null ? "" : senderName);
			this.containers = new ArrayList<Container>();
			this.messageTransactionID = messageTransaction;
			this.queryMessageContrlID = queryMessageContrlID;
			this.queryTag = queryTag;
		}

		public Order(Patient patient, List<Container> containers, String password, String machineName,
				String senderName, long messageTransaction, String queryMessageContrlID, String queryTag) {

			this.patient = patient;
			this.containers = containers;
			this.password = password;
			this.machineName = machineName;
			this.senderName = senderName;
			this.container = null;
			this.messageTransactionID = messageTransaction;
			this.queryMessageContrlID = queryMessageContrlID;
			this.queryTag = queryTag;
		}

		public long getMessageTransactionID() {
			return messageTransactionID;
		}

		@Override
		public String toString() {
			return "Order{" + "patient=" + patient + ", containers=" + containers + ", container=" + container
					+ ", password=" + password + ", machineName=" + machineName + ", messageTransactionID ="
					+ messageTransactionID + ", queryMessageContrlID =" + queryMessageContrlID + '}';
		}
	}

	public static class Patient {

		public final Date dateOfBirth;
		public final String doctorName;
		public final String firstName;
		public final String gender;
		public String lastName;
		public final String patientId;
		public String secondName;
		public final String surname;

		public Patient() {
			this(null, null, null, null, null, null);
		}

		public Patient(String firstName, String secondName, String patientId, Date dateOfBirth, String gender,
				String doctorName) {
			this.firstName = firstName;
			this.secondName = secondName;
			this.surname = secondName;
			this.patientId = patientId;
			this.dateOfBirth = dateOfBirth;
			this.gender = gender;
			this.doctorName = doctorName;
		}

		public Patient(String firstName, String secondName, String surname, String patientId, Date dateOfBirth,
				String gender, String doctorName) {
			this.firstName = firstName;
			this.secondName = secondName;
			this.surname = surname;
			this.patientId = patientId;
			this.dateOfBirth = dateOfBirth;
			this.gender = gender;
			this.doctorName = doctorName;
		}

		public Patient(String firstName, String secondName, String lastName, String surname, String patientId,
				Date dateOfBirth, String gender, String doctorName) {
			this.firstName = firstName.replace(" ", ".").replace("'", "");
			this.surname = surname.replace(" ", ".").replace("'", "");
			this.secondName = secondName.replace(" ", ".").replace("'", "");
			this.lastName = lastName.replace(" ", ".").replace("'", "");
			this.patientId = patientId;
			this.dateOfBirth = dateOfBirth;
			this.gender = gender;
			this.doctorName = doctorName;
		}

		@Override
		public String toString() {
			return "Patient{" + "firstName='" + firstName + '\'' + "secondName='" + secondName + '\'' + "lastName='"
					+ lastName + '\'' + ", surname='" + surname + '\'' + ", patientId='" + patientId + '\''
					+ ", dateOfBirth='" + dateOfBirth + '\'' + ", gender='" + gender + '\'' + ", doctorName='"
					+ doctorName + '\'' + '}';
		}
	}

	public static class Result {

		public final String abnormalFlag;
		public final String referanceRanges;
		public final String status;
		public final String unit;
		public final String value;

		public Result() {
			this(null, null, null, null, null);
		}

		public Result(String value, String unit) {
			this.value = value;
			this.unit = unit;
			this.abnormalFlag = "";
			this.status = "";
			this.referanceRanges = "";

		}

		public Result(String value, String unit, String abnormalFlag) {
			this.value = value;
			this.unit = unit;
			this.abnormalFlag = abnormalFlag;
			this.status = "";
			this.referanceRanges = "";

		}

		public Result(String value, String unit, String abnormalFlag, String status) {
			this.value = value;
			this.unit = unit;
			this.abnormalFlag = abnormalFlag;
			this.status = status;
			this.referanceRanges = "";
		}

		public Result(String value, String unit, String abnormalFlag, String status, String referanceRanges) {
			this.value = value;
			this.unit = unit;
			this.abnormalFlag = abnormalFlag;
			this.status = status;
			this.referanceRanges = referanceRanges;
		}

		@Override
		public String toString() {
			return "Result{" + "value='" + value + '\'' + ", unit='" + unit + '\'' + ", abnormalFlag='" + abnormalFlag
					+ '\'' + ", status='" + status + '\'' + '}';
		}
	}
}