package com.certacure.lis.interfaces.middleware.flow_component.lab_http;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.client.methods.CloseableHttpResponse;

import com.certacure.lis.interfaces.entities.LkpMessageTransactionDirection;
import com.certacure.lis.interfaces.entities.MessageTransaction;
import com.certacure.lis.interfaces.entities.PostDetailFinancialTransaction;
import com.certacure.lis.interfaces.entities.ScheduleAppointment;
import com.certacure.lis.interfaces.middleware.enums.Enums;
import com.certacure.lis.interfaces.middleware.enums.Enums.ERROR_TYPE;
import com.certacure.lis.interfaces.middleware.enums.Enums.HTTP_REQUESTED_METHOD_TYPE;
import com.certacure.lis.interfaces.middleware.enums.Enums.REQUEST_RESULT_TYPE;
import com.certacure.lis.interfaces.middleware.enums.Enums.VALUDATION_RESULT_TYPE;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_ADT_Msg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_BASIC_HL7_MSG;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_HeaderRecord;

public class httpRequstTransaction {

    private String strJSON;
    private String strSecretKey;
    private String strRequest;
    private String strResponse;
    private String strRMC_InternalError;
    private String strMessageID;
    private String strResponseDateTime;
    private String strResponceCode;
    private String strEnviroment;
    private String strAPIPrefex;
    private String strAppointmentID;
    private String strAdmissionNumber;

    private String API_URL_SUFFIX;
    private String API_URL_PREFIX;
    private String API_AUTH_KEY;

    private String MSH4_SendingFacility;
    private String MSH6_ReceivingFacility;
    private String MSG_Type;
    private String MSA6_Error_Condition;

    private ERROR_TYPE errorType;
    private String errorDesc;
    private PostDetailFinancialTransaction postDetailFinancialTransaction;

    private VALUDATION_RESULT_TYPE valudation_result_type;
    private MessageTransaction messageTransaction;
    private LkpMessageTransactionDirection messageFlowDirection;
    private LIS2A2Msg lis2aHL7Msg;
    private HTTP_REQUESTED_METHOD_TYPE httpMethodType;

    private REQUEST_RESULT_TYPE request_result_type;
    
    private String strHL7MsgText;
    private CloseableHttpResponse HttpResponse ; 
    private ScheduleAppointment scheduleAppointment;

   

    public httpRequstTransaction() {
        this.setLis2aMsg(new LIS2A2_BASIC_HL7_MSG());
        // this.setResponseDateTime();
        // this.strJSON = "";
        this.strEnviroment= "D";

        this.messageTransaction= new MessageTransaction();
    }
    
    public httpRequstTransaction(String strInitialMsgText) {
        this.setLis2aMsg(new LIS2A2_BASIC_HL7_MSG());
        // this.setResponseDateTime();
        // this.strJSON = "";
        this.strEnviroment= "D";

        this.messageTransaction= new MessageTransaction();
        this.setHL7MsgText(strInitialMsgText);
    }

    public REQUEST_RESULT_TYPE getRequest_result_type() {
        return request_result_type;
    }
    
    

    public ScheduleAppointment getScheduleAppointment() {
		return scheduleAppointment;
	}

	public void setScheduleAppointment(ScheduleAppointment scheduleAppointment) {
		this.scheduleAppointment = scheduleAppointment;
	}

	public void setRequest_result_type(REQUEST_RESULT_TYPE request_result_type) {
        this.request_result_type= request_result_type;
    }
    
    public void setPostDetailFinancialTransaction(
        PostDetailFinancialTransaction postDetailFinancialTransaction) {
        this.postDetailFinancialTransaction= postDetailFinancialTransaction;
    }

    public void setHttpMethodType(HTTP_REQUESTED_METHOD_TYPE httpMethodType) {
        this.httpMethodType= httpMethodType;
    }

    public void setMessageFlowDirection(LkpMessageTransactionDirection messageFlowDirection) {
        this.messageFlowDirection= messageFlowDirection;
    }

    public void setMessageTransaction(MessageTransaction messageTransaction) {
        this.messageTransaction= messageTransaction;
    }

    public void setErrorType(ERROR_TYPE errorType) {
        this.errorType= errorType;
    }

    public void setResponceCode(String strResponceCode) {
        this.strResponceCode= strResponceCode;
    }

    public void setMessageID(String strmessageID) {
        this.strMessageID= strmessageID;
    }

    public void setSecretKey(String strSecretKey) {
        this.strSecretKey= strSecretKey;
    }

    public void setRequest(String strRequest) {
        this.strRequest= strRequest;
    }

    public void setResponse(String strResponse) {
        this.strResponse= strResponse;
    }

    public void setRMC_InternalError(String strRMC_InternalError) {
        this.strRMC_InternalError= strRMC_InternalError;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc= errorDesc;
    }

    public void setEnviroment(String strEnviroment) {
        this.strEnviroment= strEnviroment;
    }

    public void setValudation_result_type(VALUDATION_RESULT_TYPE valudation_result_type) {
        this.valudation_result_type= valudation_result_type;
    }

    public void setStrAPIPrefex(String strAPIPrefex) {
        this.strAPIPrefex= strAPIPrefex;
    }

    public String getAPI_URL_SUFFIX() {
        return API_URL_SUFFIX;
    }

    public void setAPI_URL_SUFFIX(String aPI_URL_SUFFIX) {
        API_URL_SUFFIX= aPI_URL_SUFFIX;
    }

    public void setAPI_URL_PREFIX(String aPI_URL_PREFIX) {
        API_URL_PREFIX= aPI_URL_PREFIX;
    }

    public String getAPI_URL_PREFIX() {
        return API_URL_PREFIX;
    }

    public void setResponseDateTime() {
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyyMMddHHmmssSSS");
        strResponseDateTime= dateFormat.format(new Date());
    }

    public void setSendingFacility(String MSH4_SendingFacility) {
        if (MSH4_SendingFacility == null || MSH4_SendingFacility.isEmpty())
            this.MSH4_SendingFacility= "";
        else
            this.MSH4_SendingFacility= MSH4_SendingFacility;
    }

    public void setReceivingFacility(String MSH6_ReceivingFacility) {
        if (MSH6_ReceivingFacility == null || MSH6_ReceivingFacility.isEmpty())
            this.MSH6_ReceivingFacility= "";
        else
            this.MSH6_ReceivingFacility= MSH6_ReceivingFacility;
    }

   
   

    public void setHttpResponse(CloseableHttpResponse httpResponse) {
        HttpResponse= httpResponse;
    }
    
    public PostDetailFinancialTransaction getPostDetailFinancialTransaction() {
        return postDetailFinancialTransaction;
                                        
    }


    public String getMSA6_Error_Condition() {
       
            return MSA6_Error_Condition;
    }


    public String getACKMSG_Type() {
        return MSG_Type;
    }

    public String getSendingFacility() {
        return MSH4_SendingFacility;
    }

    public String getReceivingFacility() {
        return MSH6_ReceivingFacility;
    }

    public String getJSON() {
        return strJSON;
    }
    
    public CloseableHttpResponse getHttpResponse() {
        return HttpResponse;
    }

    public LkpMessageTransactionDirection getMessageFlowDirection() {
        return messageFlowDirection;
    }

    public String getSecretKey() {
        return strSecretKey;
    }

    public String getRequest() {
        return strRequest;
    }

    public String getResponse() {
        return strResponse;
    }

    public String getRMC_InternalError() {
        return strRMC_InternalError;
    }

    public String getMessageID() {
        return strMessageID;
    }

    public MessageTransaction getMessageTransaction() {
        return messageTransaction;
    }

    public String getResponceCode() {
        return strResponceCode;
    }

    public String getEnviroment() {
        return strEnviroment;
    }

    public ERROR_TYPE getErrorType() {
        return errorType;
    }

    public String getErrorDesc() {
        return errorDesc;
    }
    
    public String getHL7MsgText() {
       return strHL7MsgText;
    }


    public HTTP_REQUESTED_METHOD_TYPE getHttpMethodType() {
        return httpMethodType;
    }

    public void setJSON(String strJSON) {
        this.strJSON= strJSON;
    }

    public VALUDATION_RESULT_TYPE getValudation_result_type() {
        return valudation_result_type;
    }

    public LIS2A2Msg getLis2aHL7Msg() {
        return lis2aHL7Msg;
    }

    public String getStrAPIPrefex() {
        return strAPIPrefex;
    }

    public String getResponseDateTime() {
        return strResponseDateTime;
    }

    public void setLis2aMsg(LIS2A2Msg lis2aHL7Msg) {
        this.lis2aHL7Msg= (LIS2A2Msg) lis2aHL7Msg;
    }

    public static MessageTransaction updateMessageTransLog() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setLis2aHL7Msg(Class<? extends LIS2A2Msg> class1) {
        // TODO Auto-generated method stub
        
    }

    public void setHL7MsgText(String strInitialMsgText) {
        this.strHL7MsgText = strInitialMsgText;
        
    }

    public String getAPI_AUTH_KEY() {
        return API_AUTH_KEY;
    }

    public void setAPI_AUTH_KEY(String aPI_AUTH_KEY) {
        API_AUTH_KEY = aPI_AUTH_KEY;
    }
    
    public String getMSG_Type() {
        return MSG_Type;
    }

    public void setMSG_Type(String mSG_Type) {
        MSG_Type= mSG_Type;
    }

	public void setAppointmentID(String pV1_5_PreadmitNumber) {
		strAppointmentID = pV1_5_PreadmitNumber;
		
	}
	
	public String getAppointmentID() {
		return strAppointmentID ;
		
	}

	public String getAdmissionNumber() {
		return strAdmissionNumber;
	}

	public void setAdmissionNumber(String strAdmissionNumber) {
		this.strAdmissionNumber = strAdmissionNumber;
	}
	
	


}
