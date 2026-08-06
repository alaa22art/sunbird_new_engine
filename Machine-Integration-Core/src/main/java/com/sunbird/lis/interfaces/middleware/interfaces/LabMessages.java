package com.certacure.lis.interfaces.middleware.interfaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.certacure.lis.interfaces.entities.Machine;

public class LabMessages {

	public static class LabResultMsg implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public final Order order;

		public LabResultMsg(Order order) {
			this.order = order;
		}

		public LabResultMsg() {
			this(null); //for gson
		}

		@Override
		public String toString() {
			return "MyLabResultMsg{" +
					"order=" + order +
					'}';
		}
	}

	public static class ConnectionStatus implements Serializable {

		private static final long serialVersionUID = 1L;
		public String status;
		public Integer port;
		public Machine machine;

		public ConnectionStatus(String status, Integer port, Machine machine) {
			this.status = status;
			this.port = port;
			this.machine = machine;
		}

		public ConnectionStatus() {
			this(null, null, null); //for gson
		}

		@Override
		public String toString() {
			return "ConnectionStatus{" +
					"status=" + status +
					"port=" + port +
					"machine=" + machine +
					'}';
		}
	}

	public static class LabOrderMsg implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public final Order order;

		public LabOrderMsg(Order order) {
			this.order = order;
		}

		public LabOrderMsg() {
			this(null); // for gson
		}

		@Override
		public String toString() {
			return "MyLabOrderMsg{" +
					"order=" + order +
					'}';
		}
	}

	public static class LabQueryMsg implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public final String specimenIds;
		public final List<String> analysisCodes;
		public String machineName;
		public String specimenPosition;

		public LabQueryMsg(String specimenIds, List<String> analysisCodes, String machineName, String specimenPosition) {
			this.specimenIds = specimenIds;
			this.analysisCodes = Collections.unmodifiableList(analysisCodes);
			this.machineName = machineName;
			this.specimenPosition = specimenPosition;
		}

		public LabQueryMsg(String specimenIds, String machineName, String specimenPosition) {
			this.specimenIds = specimenIds;
			this.analysisCodes = null;
			this.machineName = machineName;
			this.specimenPosition = specimenPosition;
		}

		public LabQueryMsg() {
			this(new String(), new ArrayList<>(), new String(), new String());
		}

		@Override
		public String toString() {
			return "MyLabQueryMsg{" +
					"specimenIds=" + specimenIds +
					", analysisCodes=" + analysisCodes +
					", machineName=" + machineName +
					", specimenPosition=" + specimenPosition +
					'}';
		}
	}

	public static class LabMachineMsg implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public String machineName;
		public String machineActorPath;
		public String saveActorPathUrl;

		public LabMachineMsg(String machineName, String machineActorPath, String saveActorPathUrl) {
			this.machineName = machineName;
			this.machineActorPath = machineActorPath;
			this.saveActorPathUrl = saveActorPathUrl;
		}

		public LabMachineMsg() {
			this(new String(), new String(), new String());
		}

		@Override
		public String toString() {
			return "MyLabMachineMsg{" +
					"machineName=" + machineName +
					", machineActorPath=" + machineActorPath +
					", saveActorPathUrl=" + saveActorPathUrl +
					'}';
		}
	}

	public static class Order {

		public final Patient patient;
		public final List<Container> containers;
		public final Container container;
		public final String password;
		public final String machineName;
		public final String senderName;
		public final long messageTransactionID;

		public Order(Patient patient, List<Container> containers, String password, String machineName, String senderName,
				long messageTransaction) {
			this.patient = patient;
			this.containers = containers;
			this.password = password;
			this.machineName = machineName;
			this.senderName = senderName;
			this.container = null;
			this.messageTransactionID = messageTransaction;
		}

		public Order(Patient patient, Container container, String password, String machineName, String senderName,
				long messageTransaction) {
			this.patient = patient;
			this.container = container;
			this.password = password;
			this.machineName = machineName;
			this.senderName = senderName;
			this.containers = null;
			this.messageTransactionID = messageTransaction;
		}

		public long getMessageTransactionID() {
			return messageTransactionID;
		}

		@Override
		public String toString() {
			return "Order{" +
					"patient=" + patient +
					", containers=" + containers +
					", container=" + container +
					", password=" + password +
					", machineName=" + machineName +
					", messageTransactionID =" + messageTransactionID +
					'}';
		}
	}

	public static class Container {

		public final String specimenId;
		public final List<Analysis> analyses;
		public final String specimenPosition;
		public final String lotNumber;
		public final String qcName;
		public final String qcNumber;
		public final String priority;
		public final String collectionDate;
		public final String specimenDescriptor;
		public final String actionCode;
		public final String reportType;

		public Container(String specimenId, String specimenPosition, String collectionDate, String actionCode, String reportType) {

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

		public Container(String specimenId, List<Analysis> analyses, String specimenPosition, String priority, String collectionDate,
				String specimenDescriptor, String actionCode, String reportType) {
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

		public Container(String specimenId, List<Analysis> analyses, String specimenPosition, String priority, String collectionDate,
				String specimenDescriptor, String actionCode, String qcName, String lotNumber, String qcNumber, String reportType) {
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
			this.reportType = reportType;
		}

		public Container() {
			this(null, null, null, null, null, null, null, null);
		}

		@Override
		public String toString() {
			return "Container{" +
					"specimenId='" + specimenId + '\'' +
					", analyses=" + analyses +
					", priority=" + priority +
					", collectionDate=" + collectionDate +
					", specimenDescriptor=" + specimenDescriptor +
					", actionCode=" + actionCode +
					'}';
		}
	}

	public static class Patient {

		public final String firstName;
		public String secondName;
		public String lastName;
		public final String surname;
		public final String patientId;
		public final Date dateOfBirth;
		public final String gender;
		public final String doctorName;

		public Patient(String firstName, String secondName, String patientId, Date dateOfBirth, String gender, String doctorName) {
			this.firstName = firstName;
			this.secondName = secondName;
			this.surname = secondName;
			this.patientId = patientId;
			this.dateOfBirth = dateOfBirth;
			this.gender = gender;
			this.doctorName = doctorName;
		}

		public Patient(String firstName, String secondName, String lastName, String surname, String patientId, Date dateOfBirth,
				String gender, String doctorName) {
			this.firstName = firstName.replace(" ", ".").replace("'", "");
			this.surname = surname.replace(" ", ".").replace("'", "");
			this.secondName = secondName.replace(" ", ".").replace("'", "");
			this.lastName = lastName.replace(" ", ".").replace("'", "");
			this.patientId = patientId;
			this.dateOfBirth = dateOfBirth;
			this.gender = gender;
			this.doctorName = doctorName;
		}

		public Patient(String firstName, String secondName, String surname, String patientId, Date dateOfBirth, String gender,
				String doctorName) {
			this.firstName = firstName;
			this.secondName = secondName;
			this.surname = surname;
			this.patientId = patientId;
			this.dateOfBirth = dateOfBirth;
			this.gender = gender;
			this.doctorName = doctorName;
		}

		public Patient() {
			this(null, null, null, null, null, null);
		}

		@Override
		public String toString() {
			return "Patient{" +
					"firstName='" + firstName + '\'' +
					"secondName='" + secondName + '\'' +
					"lastName='" + lastName + '\'' +
					", surname='" + surname + '\'' +
					", patientId='" + patientId + '\'' +
					", dateOfBirth='" + dateOfBirth + '\'' +
					", gender='" + gender + '\'' +
					", doctorName='" + doctorName + '\'' +
					'}';
		}
	}

	public static class Analysis {

		public String code;
		public String panelCode;
		public final String resultCode;
		public final String name;
		public final Result result;

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

		public Analysis() {
			this(null, null, null, null, null);
		}

		@Override
		public String toString() {
			return "Analysis{" +
					"panelCode='" + panelCode + '\'' +
					",code='" + code + '\'' +
					",resultCode='" + resultCode + '\'' +
					", name='" + name + '\'' +
					", result=" + result +
					'}';
		}
	}

	public static class Result {

		public final String value;
		public final String unit;
		public final String abnormalFlag;
		public final String status;
		public final String referanceRanges;

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

		public Result() {
			this(null, null, null, null, null);
		}

		@Override
		public String toString() {
			return "Result{" +
					"value='" + value + '\'' +
					", unit='" + unit + '\'' +
					", abnormalFlag='" + abnormalFlag + '\'' +
					", status='" + status + '\'' +
					'}';
		}
	}
}