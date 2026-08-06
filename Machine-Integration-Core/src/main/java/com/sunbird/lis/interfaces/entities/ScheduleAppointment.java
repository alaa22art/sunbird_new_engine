package com.sunbird.lis.interfaces.entities;



public class ScheduleAppointment {
    private String AppointmentId;
    private String doctor;
    private String facility;
    private String recordedDate;
    private String appointmentSource;
    private String AppointmentDate;
    private String appointmentType;
    private String recordedBy;
    private String appointmentCode;
    private String notes;
    private String response;
    private String messageBody;
    

    public String getAppointmentCode() {
        return appointmentCode;
    }

    public void setAppointmentCode(String appointmentCode) {
        this.appointmentCode= appointmentCode;
    }

    public String getAppointmentId() {
        return AppointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        AppointmentId= appointmentId;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor= doctor;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility= facility;
    }

    public String getRecordedDate() {
        return recordedDate;
    }

    public void setRecordedDate(String recordedDate) {
        this.recordedDate= recordedDate;
    }

    public String getPatientClass() {
        return appointmentSource;
    }

    public void setPatientClass(String appointmentSource) {
        this.appointmentSource= appointmentSource;
    }

    public String getAppointmentDate() {
        return AppointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        AppointmentDate= appointmentDate;
    }

    public String getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType= appointmentType;
    }

    public String getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(String recordedBy) {
        this.recordedBy= recordedBy;
    }

	public String getAppointmentSource() {
		return appointmentSource;
	}

	public void setAppointmentSource(String appointmentSource) {
		this.appointmentSource = appointmentSource;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

}
