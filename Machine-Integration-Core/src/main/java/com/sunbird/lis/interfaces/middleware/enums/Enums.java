package com.sunbird.lis.interfaces.middleware.enums;

public class Enums {
	
	
	public enum DISCHARGE_REASON {
        AGAINSTMEDICALADVICE("againstmedicaladvice"),
        CANCELADMISSION("canceladmission"),
        DEATH("death"),
        DOCTORPERMISSION("doctorpermission"),
        IRREGULAR("irregular"),
        REGULAR("regular"),
        TRANSFEROUT("transferout"),
        UNSPECIFIED("unspecified");

        private DISCHARGE_REASON(String value) {
            this.value= value;
        }

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value= value;
        }

    }
	

	 public enum HL7_v24_DFT_P03_TYPE {
	        // : A01 , A28, A31 , A04, ,A03,A01,A11,A08,A02, A13, A54
	        PSO("PSO"),
	        PSJ("PSJ"),
	        LA("LA"),
	        RA("RA");

	        private HL7_v24_DFT_P03_TYPE(String value) {
	            this.value= value;
	        }

	        private String value;

	        public String getValue() {
	            return value;
	        }

	        public void setValue(String value) {
	            this.value= value;
	        }

	    };


	public enum Type {
        H,
        P,
        O,
        OBR,
        NET,
        OBX,
        MSH,
        EVN,
        R,
        Q,
        L,
        C, PID, ORM, PV1, DG1, SCH, PV2
    }

    public enum HL7_v24_TRIGGER_EVENT {
        // : A01 , A28, A31 , A04, ,A03,A01,A11,A08,A02, A13, A54
        A01("A01"),
        A02("A02"),
        A03("A03"),
        A05("A05"),
        A04("A04"),
        A08("A08"),
        A11("A11"),
        A13("A13"),
        A28("A28"),
        A31("A31"),
        S12("S12"),
        S15("S15"),
        S13("S13"),
        P03("P03");

        private HL7_v24_TRIGGER_EVENT(String value) {
            this.value= value;
        }

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value= value;
        }

    };

    public enum MSG_TYPE {
        // : A01 , A28, A31 , A04, ,A03,A01,A11,A08,A02, A13, A54
        ADT("ADT"),
        SIU("SIU"),
        DFT("DFT"),
    	QBP("QBP");

        private MSG_TYPE(String value) {
            this.value= value;
        }

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value= value;
        }

    };

    public static enum REQUEST_RESULT_TYPE {
        SUCCUSS, FAILED
    }

    public static enum VALUDATION_RESULT_TYPE {
        SUCCUSS, FAILED , UNKNOWNTYPE
    }

    public static enum ERROR_TYPE {
        PARSING, VALUDATION, INTERNAL_SERVER_ERROR, UNKNOW_MESSAGE_TYPE
    }

    public static enum RUN_MODE {
        RELEASE, DEBUG
    }

    public static enum RESULT_TYPE {
        QUERY_RESULT, PATIENT_RESULT
    }

    public enum PATIENT_CLASS {
        IN_PATIENT("I"),
        OUT_PATIENT("O");

        private PATIENT_CLASS(String value) {
            this.value= value;
        }

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value= value;
        }

    }

    public static enum API_URL_PREFEX {
        PATIENT_CREATE("api/patients"),
        PATIENT_PREADMISSION("api/Preadmissions"),
        PATIENT_ADDMISIONS("api/Admissions"),
        DISCHARGE_ADDMISIONS("/api/Admissions/%s/Discharge"),
        PATIENT_UPDATE("api/patients/%s"),
        PATIENT_VISIT_CREATE("api/Visits"),
        PATIENT_VISIT_CREATE_BY_APPOINTMENT("api/Visits/ActivateByAppointment/%s"), 
        PATIENT_VISIT_CLOSE("api/Visits/%s/Close"),
        PATIENT_CANCEL_ADDMISIONS("api/Admissions/%s/Cancel"),
        PATIENT_TRANSFER_ADDMISIONS("/api/Admissions/%s/Transfer"),
        APPOINTMENTS_CREATE("api/Appointments"),
        APPOINTMENT_VISIT_ACTIVATE("/api/Visits/CreateByAppointment/%s"),
        APPOINTMENTS_CANCEL("api/Appointments/%s/Cancel"),
        APPOINTMENTS_UPDATE("api/Appointments/"),
        APPOINTMENTS_INTERNAL_UPDATE("api/Appointments/%s/UpdateIdentifier"),
        VISIT_INTERNAL_UPDATE("api/Visits/UpdateByAppointment/%s"),
        ORDER_INTERNAL_UPDATE("api/Orders/%s"),
        UPDATE_ADDMISIONS("api/Admissions/"),
        DETAIL_FINANCIAL_TRANSACTION_CREATE("api/Orders/"),
        DETAIL_FINANCIAL_TRANSACTION_CANCEL("api/OrderItems/%s/Cancel"),
        DETAIL_FINANCIAL_TRANSACTION_COMPLETE("api/OrderItems/%s/Complete"), 
        DETAIL_FINANCIAL_TRANSACTION_PHARMACY_CREATE("/api/PharmacyOrders"),
    	DETAIL_FINANCIAL_TRANSACTION_PHARMACY_RETURN_DISPENCE("api/OrderItems/%s/returnDispensed"),
    	DETAIL_FINANCIAL_TRANSACTION_PHARMACY_ACTIVATE_ORDER("api/PharmacyOrderItems/%s/ActivatePending"),
    	DETAIL_FINANCIAL_TRANSACTION_LAB_RAD_ACTIVATE_ORDER("api/OrderItems/%s/Activate");
    
    
        
        

        private API_URL_PREFEX(String value) {
            this.value= value;
        }

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value= value;
        }
    }

    public static enum HTTP_REQUESTED_METHOD_TYPE {
        POST("POST"), 
        PATCH("PATCH");

        private HTTP_REQUESTED_METHOD_TYPE(String value) {
            this.value= value;
        }

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value= value;
        }

    }

}
