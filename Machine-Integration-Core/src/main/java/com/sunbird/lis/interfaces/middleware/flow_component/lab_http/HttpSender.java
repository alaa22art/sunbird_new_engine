package com.sunbird.lis.interfaces.middleware.flow_component.lab_http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.print.attribute.standard.MediaSize.Other;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sunbird.core.common.util.CollectionUtil;
import com.sunbird.core.common.util.DateUtil;
import com.sunbird.core.common.util.JSONUtil;
import com.sunbird.core.common.util.SpringUtil;
import com.sunbird.lis.interfaces.annotation.InterceptorFree;
import com.sunbird.lis.interfaces.entities.CoreEventLog;
import com.sunbird.lis.interfaces.entities.DataOutboundHL7Message;
import com.sunbird.lis.interfaces.entities.DataResultOutboundHL7Message;
import com.sunbird.lis.interfaces.entities.LabBranch;
import com.sunbird.lis.interfaces.entities.LkpMessageTransactionType;
import com.sunbird.lis.interfaces.entities.LkpOrderActionCode;
import com.sunbird.lis.interfaces.entities.Machine;
import com.sunbird.lis.interfaces.entities.MachineOrder;
import com.sunbird.lis.interfaces.entities.MachineOrderQueryResponse;
import com.sunbird.lis.interfaces.entities.MachineQuery;
import com.sunbird.lis.interfaces.entities.MachineResult;
import com.sunbird.lis.interfaces.entities.MachineTest;
import com.sunbird.lis.interfaces.entities.MachineType;
import com.sunbird.lis.interfaces.entities.MachineTypePanel;
import com.sunbird.lis.interfaces.entities.MessageTransaction;
import com.sunbird.lis.interfaces.entities.TestCatalog;
import com.sunbird.lis.interfaces.middleware.core.FlowComponent;
import com.sunbird.lis.interfaces.middleware.enums.Enums.ERROR_TYPE;
import com.sunbird.lis.interfaces.middleware.enums.Enums.REQUEST_RESULT_TYPE;
import com.sunbird.lis.interfaces.middleware.enums.Enums.RESULT_TYPE;
import com.sunbird.lis.interfaces.middleware.enums.Enums.RUN_MODE;
import com.sunbird.lis.interfaces.middleware.enums.Enums.VALUDATION_RESULT_TYPE;
import com.sunbird.lis.interfaces.middleware.flow_component.json.parsingHL7v24Error;
import com.sunbird.lis.interfaces.middleware.interfaces.LabMessages;
import com.sunbird.lis.interfaces.middleware.interfaces.LabMessages.Analysis;
import com.sunbird.lis.interfaces.middleware.interfaces.LabMessages.Container;
import com.sunbird.lis.interfaces.middleware.interfaces.LabMessages.LabMachineMsg;
import com.sunbird.lis.interfaces.middleware.interfaces.LabMessages.LabOrderMsg;
import com.sunbird.lis.interfaces.middleware.interfaces.LabMessages.LabQueryMsg;
import com.sunbird.lis.interfaces.middleware.interfaces.LabMessages.LabResultMsg;
import com.sunbird.lis.interfaces.middleware.interfaces.LabMessages.Order;
import com.sunbird.lis.interfaces.middleware.interfaces.LabMessages.Patient;
import com.sunbird.lis.interfaces.middleware.interfaces.LabMessages.Result;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2Msg;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2_ADT_Msg;
import com.sunbird.lis.interfaces.middleware.parser.hl7.HL7Parser;
import com.sunbird.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageDirection;
import com.sunbird.lis.interfaces.middleware.parser.hl7.HL7Parser.MessageSourceType;
import com.sunbird.lis.interfaces.middleware.util.CodesEnumAll;
import com.sunbird.lis.interfaces.middleware.util.MachineTypeEnum;
import com.sunbird.lis.interfaces.service.CoreEventLogService;
import com.sunbird.lis.interfaces.service.DataOutboundHL7MessageService;
import com.sunbird.lis.interfaces.service.DataResultOutboundHL7MessageService;
import com.sunbird.lis.interfaces.service.LabBranchService;
import com.sunbird.lis.interfaces.service.MachineOrderQueryResponceService;
import com.sunbird.lis.interfaces.service.MachineOrderService;
import com.sunbird.lis.interfaces.service.MachineQueryService;
import com.sunbird.lis.interfaces.service.MachineResultService;
import com.sunbird.lis.interfaces.service.MachineService;
import com.sunbird.lis.interfaces.service.MachineTestsService;
import com.sunbird.lis.interfaces.service.MachineTypePanelService;
import com.sunbird.lis.interfaces.service.MachineTypeService;
import com.sunbird.lis.interfaces.service.MessageTransactionService;
import com.sunbird.lis.interfaces.service.PostDetailFinancialTransactionService;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import akka.japi.pf.ReceiveBuilder;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.segment.MSH;
import ca.uhn.hl7v2.util.Terser;
import net.minidev.json.JSONObject;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class HttpSender extends FlowComponent<LabHttpClientConf> {

    public final RUN_MODE runMode= RUN_MODE.RELEASE;
    public final int SUCCUSS= 200;
    public final int INTERNAL_SERVER_ERROR= 500;
    public final int AUTHORITY_ERROR= 401;
    public final int BAD_REQUEST= 400;
    public final int METHOD_NOT_ALLOWED= 405;

    /*
     * @Override protected void init() {
     * 
     * initiateObjects(); }
     */

    @Override
    protected PartialFunction<Object, BoxedUnit> getBehaviour() {
        return ReceiveBuilder.match(LabMachineMsg.class, conf -> {
            context().become(getBehaviour());
        }).match(LabResultMsg.class, result -> {
            this.sendResult(result);
        }).match(LabQueryMsg.class, query -> {
            this.sendQuery(query);
        }).match(httpRequstTransaction.class, httpRequstTrans -> {
            this.sendToRCMMiddleWare(httpRequstTrans);
        })
            /*
             * .match(httpRequstTransaction.class, httpRequstTrans -> {
             * this.sendError(httpRequstTrans); })
             */
            .build();
    }

    private MachineService machineService;
    private MachineTestsService machineTestService;
    private Machine machine;
    private MachineQueryService mQueryService;
    private List<MachineResult> machineResultList;
    private List<Map<String, String>> resultMapList;
    private Map<String, String> resultMap;
    private MachineResult resultInfo;
    private MachineOrderService machineOrderService;
    private MachineOrder machineOrder;
    private MachineResultService machineResultService;
    private LabBranchService branchService;
    private LabBranch branch;
    private ObjectMapper objectMapper;
    private CoreEventLogService coreEventLogService;
    //////////////////// Query Member////////////////////////////
    private List<String> requesterTestList;
    private List<MachineOrder> ordersList;
    private MachineQuery machineQuery;
    private MachineOrderQueryResponceService machineOrderQueryResponceService;
    private MachineOrderQueryResponse machineOrderQueryResponse;

    private String patientFirstName;
    private String patientSecondName;
    private String patientLastName;
    private String patientId;
    private String gender;
    private Date dateOfBirth;
    private String attendingPhysician;
    private LkpOrderActionCode normalActionCode;
    private LkpOrderActionCode qualityActionCode;
    private MessageTransactionService messageTransactionService;
    private MachineTypeService machineTypeService;
    private PostDetailFinancialTransactionService postDetailFinancialTransactionService;
    private MachineTypePanelService machineTypePanelService;
    private MessageTransaction messageTransaction;
    private httpRequstTransaction httpReqeustTransaction;
    private static final Gson gson = new Gson();

    public void initiateObjects() {

        /////////////////////////////////////////// set service
        /////////////////////////////////////////// Beans/////////////////////////////////////////////////
        machineTestService= (MachineTestsService) SpringUtil.getBean("MachineTestsService");
        machineService= (MachineService) SpringUtil.getBean("MachineService");
        machine= machineService.getMachineByActorPath(getContext().parent().toString());
        mQueryService= (MachineQueryService) SpringUtil.getBean("MachineQueryService");
        machineOrderService= (MachineOrderService) SpringUtil.getBean("MachineOrderService");
        machineResultService= (MachineResultService) SpringUtil.getBean("MachineResultService");
        branchService= (LabBranchService) SpringUtil.getBean("LabBranchService");
        coreEventLogService= (CoreEventLogService) SpringUtil.getBean("CoreEventLogService");
        machineOrderQueryResponceService= (MachineOrderQueryResponceService) SpringUtil
            .getBean("MachineOrderQueryResponceService");
        messageTransactionService= (MessageTransactionService) SpringUtil
            .getBean("MessageTransactionService");
        machineTypeService= (MachineTypeService) SpringUtil.getBean("MachineTypeService");
        machineTypePanelService= (MachineTypePanelService) SpringUtil
            .getBean("MachineTypePanelService");
        
        postDetailFinancialTransactionService= (PostDetailFinancialTransactionService) SpringUtil
            .getBean("PostDetailFinancialTransactionService");
 
        /////////////////////////////////////////// set members data
        ///////////////////////////////////////////         
        /////////////////////////////////////////////////////////////////////
        machineQuery= new MachineQuery();
        resultMap= new HashMap<String, String>();
        objectMapper= new ObjectMapper();
        machineOrder= new MachineOrder();
        resultInfo= new MachineResult();
        machineResultList= new ArrayList<MachineResult>();
        resultMapList= new ArrayList<Map<String, String>>();
        requesterTestList= new ArrayList<String>();
        ordersList= new ArrayList<MachineOrder>();
    }

    public MachineQuery getQueryInfo(LabResultMsg resultMsg) {
        return mQueryService.getQueryBySampleAndMachine(
            resultMsg.order.container.specimenId.toString().trim(),
            machine);
    }
    
   
    
    
    
    

    private void sendToRCMMiddleWare(httpRequstTransaction httpRequestTransObj) throws Exception {
        

        initiateObjects();
        httpRequstTransaction httpRequest= httpRequestTransObj;
        String apiResponse= "";
        HttpUriRequest request= null;
        
        
        //check if StringJson isValid
        
        
        try {

            if (httpRequest.getValudation_result_type() == VALUDATION_RESULT_TYPE.FAILED) {
                
               
                httpRequestTransObj.getMessageTransaction().setIsValidated(false);
                
                // do not send to RCM
                httpRequest.setRequest_result_type(REQUEST_RESULT_TYPE.FAILED);
                httpRequest.setErrorDesc("Error: " +
                    httpRequest.getErrorDesc());

                throw new RuntimeException(httpRequest.getErrorDesc());
                // conf.recipient.tell(httpRequest, self());

            } else {

                try (CloseableHttpClient httpclient= HttpClients.createDefault()) 
                {                    httpRequestTransObj.getMessageTransaction().setIsValidated(true);
                    
                    switch (httpRequest.getHttpMethodType()) {
                    case POST:
                       request= RequestBuilder.post()
                            .setUri(
                                httpRequest.getAPI_URL_SUFFIX() + httpRequest.getAPI_URL_PREFIX())
                            .setHeader(HttpHeaders.ACCEPT, "application/json")
                            .setHeader("API-KEY", httpRequestTransObj.getAPI_AUTH_KEY())
                            .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            // add request body
                            .setEntity(new StringEntity(httpRequestTransObj.getJSON()))

                            .build();
                        break;
                    case PATCH:
                        request= RequestBuilder.patch()
                            .setUri(
                                httpRequest.getAPI_URL_SUFFIX() + httpRequest.getAPI_URL_PREFIX())
                            .setHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                            .setHeader(HttpHeaders.ACCEPT, "application/json")
                            .setHeader("API-KEY", httpRequestTransObj.getAPI_AUTH_KEY())
                            // add request body
                            .setEntity(new StringEntity(httpRequestTransObj.getJSON()))

                            .build();
                        break;

                    }

                    System.out.println(
                        "/////////////////////////////////////////////////////////////////////////");
                    System.out.println("Executing POST request... ");
                    
                    
                    
                    
                    CloseableHttpResponse response= httpclient.execute(request);
                    httpRequest.setHttpResponse(response);
                    httpRequest.getMessageTransaction().setNotes(response.toString());
                    HttpEntity entity= response.getEntity();
                    //EntityUtils.consume(entity);

                    response.setEntity(entity);
                    String responseString= EntityUtils.toString(entity, "UTF-8");
                    
                    System.out.println("ReasonPhrase: " + 
                        response.getStatusLine().getReasonPhrase().toString());
                    System.out.println("Error Responce : " + responseString);
                    System.out.println("Status code: " + response.getStatusLine().getStatusCode());
                    
                    System.out.println(
                        "/////////////////////////////////////////////////////////////////////////");

                    // HttpResponse<T> responseString = new
                    // BasicResponseHandler().handleResponse(response);
                    System.out.println(
                        "/////////////////////////////////////////////////////////////////////////");
                    System.out.println(
                        "/////////////////////////////////////////////////////////////////////////");
                    httpRequest.getMessageTransaction().setIsSent(true);
                    httpRequest.getMessageTransaction().setIsValidated(true);
                    httpRequest.setErrorDesc(responseString);
                    
                    switch (response.getStatusLine().getStatusCode()) {
                    case SUCCUSS:
                        
                        if (httpRequestTransObj.getMSG_Type().equals("DFT_P03")) {
                            ObjectMapper objectMapper= new ObjectMapper();
                            JsonNode jsonNode= objectMapper.readTree(responseString);
                            JsonNode identifierNode= jsonNode.path("payload").path("identifier")
                                .path("value");
                            JsonNode orderItemsArray= jsonNode.path("payload").path("orderItems");
                            JsonNode encounterNode= jsonNode.path("payload").path("encounter")
                                .path("value");
                            JsonNode actionIDNode= null;
                            for (JsonNode orderItem : orderItemsArray) {
                                actionIDNode= orderItem.path("identifier").path("value");
                                int identifierValue= identifierNode.asInt();
                                System.out.println("Identifier Value: " + identifierValue);
                            }
                            String action_id= actionIDNode.asText();
                            String certa_id= identifierNode.asText();
                            String encounter= encounterNode.asText();
                            System.out.println(certa_id + "," + action_id);
                            httpRequest.getPostDetailFinancialTransaction().setRCMOrderID(certa_id);
                            httpRequest.getPostDetailFinancialTransaction()
                                .setRCMOrderActionID(action_id);
                            httpRequest.getPostDetailFinancialTransaction().setEncounter(encounter);
                            postDetailFinancialTransactionService
                                .add(httpRequest.getPostDetailFinancialTransaction());
                        }
                        
                        
                        
                        httpRequest.setRequest_result_type(REQUEST_RESULT_TYPE.SUCCUSS);
                        httpRequest.setResponseDateTime();
                        httpRequest.getMessageTransaction().setIsSuccuss(true);
                        httpRequest.setErrorDesc("Error: " + responseString);
                        
                        break;
                    //case BAD_REQUEST:
                    //case METHOD_NOT_ALLOWED:
                    //case INTERNAL_SERVER_ERROR:
                    //case AUTHORITY_ERROR:
                    default:
                        httpRequest.setRequest_result_type(REQUEST_RESULT_TYPE.FAILED);
                        httpRequest.getMessageTransaction().setIsValidated(true);
                        httpRequest.getMessageTransaction().setIsSuccuss(false);
                        httpRequest.getMessageTransaction().setIsSent(true);
                        throw new RuntimeException(httpRequest.getErrorDesc());
                        
                      
                       // String[] strError= responseString.split(",");

                        /*if (strError.length > 2) {
                            httpRequest
                                .setErrorDesc("Error:" + response.getStatusLine().getStatusCode() +
                                    ": " + responseString);*/
                            
                       // } else {
                           
                      //  }

                        //httpRequest.getMessageTransaction().setIsValidated(true);
                        //httpRequest.getMessageTransaction().setIsSuccuss(false);
                       // httpRequest.getMessageTransaction()
                         //   .setMessageType(new LkpMessageTransactionType("HL7"));
                       // httpRequest.getMessageTransaction().setNotes(response.toString())
                        //httpRequest.setResponseDateTime();
                        //httpRequest.getMessageTransaction().setIsSuccuss(false);
                        //httpRequest.getMessageTransaction().setNotes(httpRequest.getErrorDesc());
                        
                        

                    /*  messageTransactionService.addMessageTransaction(
                            httpRequest.getMessageTransaction().getMachine(),
                            httpRequest.getMessageTransaction().getMessageBody(),
                            httpRequest.getMessageTransaction().getMessageDirection(),
                            httpRequest.getMessageTransaction().getMessageType(),
                            httpRequest.getMessageTransaction().getIsSuccuss(),
                            httpRequest.getMessageTransaction().getIsValidated(),
                            httpRequest.getMessageTransaction().getIsSent(),
                            httpRequest.getMessageTransaction().getNotes(),
                            httpRequest.getMessageTransaction().getMessageControlID());*/

                       
                   

                    }

                } catch (Exception ex) {

                    httpRequest.setRequest_result_type(REQUEST_RESULT_TYPE.FAILED);
                    httpRequest.setErrorDesc("Error:" + "Internal Engine Error: " +
                        ex.getMessage().toString());
                    httpRequest.getMessageTransaction().setNotes(httpRequest.getErrorDesc());

                    httpRequest.setValudation_result_type(VALUDATION_RESULT_TYPE.FAILED);
                    ERROR_TYPE error= ERROR_TYPE.PARSING;
                   // httpRequest.setJSON("NO JSON CREATED");
                    httpRequest.setMessageID(httpRequest.getMessageID());
                    httpRequest.setEnviroment("D");
                    httpRequest.setErrorType(error);
                    httpRequest.setErrorDesc(ex.getMessage());
                    httpRequest.getMessageTransaction().getIsValidated();
                    httpRequest.getMessageTransaction().getIsSuccuss();
                    httpRequest.getMessageTransaction().getMessageType();
                    httpRequest.getMessageTransaction().setNotes(httpRequest.getErrorDesc());
                    

                }
            }
        } catch (Exception ex) 
        {

            httpRequest.getMessageTransaction().setNotes("ERROR VALUDATION : " + ex.getMessage());
            httpRequest.setJSON("NO JSON REQUEST(CALL) CREATED");

        } finally {

            messageTransactionService.addMessageTransaction(
                httpRequest.getMessageTransaction().getMachine(),
                httpRequest.getJSON(),
                httpRequest.getMessageTransaction().getMessageDirection(),
                httpRequest.getMessageTransaction().getMessageType(),
                httpRequest.getMessageTransaction().getIsSuccuss(),
                httpRequest.getMessageTransaction().getIsValidated(),
                httpRequest.getMessageTransaction().getIsSent(),
                httpRequest.getMessageTransaction().getNotes(),
                "",
                httpRequest.getMessageTransaction().getMessageControlID());

            conf.recipient.tell(httpRequest, self());

        }

    }

    private void sendResult(LabResultMsg resultMsg) throws IOException {
        initiateObjects();
        machineQuery= getQueryInfo(resultMsg);
        Order order= resultMsg.order;
        Container container= order.container;
        branch= getMachineBranch();

        // Loop on the all tests received from the machine
        for (Analysis analysis : container.analyses) {
            // result map for the tests received from the machine to be prepared for sending
            // to the LIS
            resultMap= new HashMap<String, String>();
            // object from MachineResult entity to be prepared for saving the result in the
            // MW DB.
            resultInfo= new MachineResult();
            Result result= analysis.result;
            setResultValue(container, result);
            List<MachineTest> mappedMachineTestsList= getMappedMachineTestByCode(analysis);
            // Update MachineOrder entity by setting result received based on
            // RequesterTestCode and SampleNo
            if (!CollectionUtil.isCollectionEmpty(mappedMachineTestsList)) {

                for (MachineTest machineTest : mappedMachineTestsList) {
                    machineOrder= getAssignedMachineOrder(resultMsg, machineTest.getTestCatalog());

                    if (machineOrder == null) {
                        resultInfo.setNote("not matched order for Sample no : " +
                            resultMsg.order.container.specimenId + "and " + "host code : " +
                            machineTest.getHostCode());
                    }

                    if (isMatchedResultCode(analysis, machineTest) &&
                        getResultType(container) == RESULT_TYPE.PATIENT_RESULT) {

                        resultInfo= createMachineResultObj(normalActionCode, machineTest, container,
                            analysis, result);
                        MessageTransaction mt= messageTransactionService
                            .getByID(resultMsg.order.getMessageTransactionID());
                        resultInfo.setMessageTransaction(mt);
                        machineResultList.add(resultInfo);

                        // QCOperationResult operationResult =
                        // qcEventsDetailsService.qcNewPatientResult(resultInfo, machineTest);

                    } else if (machineOrder == null &&
                        getResultType(container) == RESULT_TYPE.QUERY_RESULT) {

                            // setQCSampleReferanceDetails(container);

                            resultInfo= createMachineResultObj(normalActionCode, machineTest,
                                container, analysis, result);
                            MessageTransaction mt= messageTransactionService
                                .getByID(resultInfo.getMessageTransaction().getRid());
                            resultInfo.setMessageTransaction(mt);
                            machineResultList.add(resultInfo);

                            // qcEventsDetailsService.qcNewQCResult(resultInfo, machineTest,
                            // machine);

                        } else if (machineOrder != null &&
                            getResultType(container) == RESULT_TYPE.QUERY_RESULT) {

                                // setQCSampleReferanceDetails(container);

                                resultInfo= createMachineResultObj(normalActionCode, machineTest,
                                    container, analysis, result);
                                MessageTransaction mt= new MessageTransaction(
                                    resultInfo.getMessageTransaction().getRid());
                                resultInfo.setMessageTransaction(mt);

                                // resultInfo = getMachineResultDetailsObj(qualityActionCode,
                                // machineTest,
                                // container, analysis);
                                addToMapList(container, result);
                                machineResultList.add(resultInfo);

                                // qcEventsDetailsService.qcNewQCResult(resultInfo, machineTest,
                                // machine);

                            } else {
                                continue;
                            }

                }

            } else {
                String strError= "\n------------------------------------------------------------------------------------------------------------------------------------------------\n" +
                    "------------------------------------------------------------------------------------------------------------------------------------------------\n" +
                    " [Error No Order Found] : " + " Machine : " + machine.getName() + "\n" +
                    " Receive a Result with Sample ID : " + "\n" +
                    resultMsg.order.container.specimenId + "\n" + " And Test Code: " + "\n" +
                    analysis.code + "\n" + " no referance order Exist or test not mapped " + "\n" +
                    "\n----------------------------------------------------------------------------------------------------------------------------------------------------\n" +
                    "----------------------------------------------------------------------------------------------------------------------------------------------------\n";

                if (runMode == RUN_MODE.DEBUG) {
                    System.out.println(strError);
                }

                CoreEventLog eventLog= getEventLogObject(strError);
                coreEventLogService.addCoreEventLog(eventLog);

                continue;
            }
        }

        if (!CollectionUtil.isCollectionEmpty(machineResultList)) {
            String barcode= machineResultList.get(0).getSampleNo();
            machineResultService.addListResult(machineResultList);
            sendRecivedResultListToLIS(barcode);
        }

    }

    private MachineResult createMachineResultObj(LkpOrderActionCode actionCode,
        MachineTest machineTest,
        Container container,

        Analysis analysis, Result result) {

        resultInfo.setTestCode(machineTest.getTestCatalog().getRequesterTestCode());
        resultInfo.setMachineTest(machineTest);
        resultInfo.setActionCode(actionCode);
        resultInfo.setAbnormalFlag(analysis.result.abnormalFlag);
        resultInfo.setReferanceRanges(analysis.result.referanceRanges);
        resultInfo.setTestCompletedDateTime(new Date());
        resultInfo.setResultCode(analysis.resultCode);
        resultInfo.setResultStatus(analysis.result.status);
        resultInfo.setMachineName(machine.getName());
        resultInfo.setSampleNo(container.specimenId.replaceAll("^\\s+", ""));
        resultInfo.setMachineQueryId(machineQuery);
        resultInfo.setMachine(machine);
        resultInfo.setIsSentToLIS(true);
        resultInfo.setTestCompletedDateTime(new Date());
        resultInfo.setMachineOrder(machineOrder);
        resultInfo.setDataOrMeasurementValue(checkResultValue(result.value).trim());
        resultInfo.setUnit(result.unit);
        resultInfo.setSampleNo(container.specimenId);
        resultInfo.setIsSentToLIS(false);
        // add test details result map
        resultMap.put("testCode", machineTest.getTestCatalog().getRequesterTestCode());
        resultMap.put("barcode", container.specimenId.trim());
        resultMap.put("result", resultInfo.getDataOrMeasurementValue());
        resultMapList.add(resultMap);

        return resultInfo;
    }

    private String checkResultValue(String replace) {

        String valudatedValue= "";

        // if(replace.contains("!"))
        // {
        valudatedValue= "";

        for (char str : replace.toCharArray()) {
            if (str != '!' && Character.isDigit(str) || str == '.') {
                valudatedValue+= str;
            }
            // }

        }

        return valudatedValue.trim();
    }

    private boolean isMatchedResultCode(Analysis analysis, MachineTest machineTest) {
        return machineTest.getResultCode().equals(analysis.resultCode);
    }

    private void addToMapList(Container container, Result result) {

        resultMap.put("barcode", container.specimenId.trim());
        resultMap.put("result", result.value);
        resultMapList.add(resultMap);

    }

    private LabBranch getMachineBranch() {
        return branchService.findById(machine.getBranchId());

    }

    private RESULT_TYPE getResultType(Container container) {
        if (container.actionCode.toUpperCase().equals("Q")) {

            return RESULT_TYPE.QUERY_RESULT;
        } else {

            return RESULT_TYPE.PATIENT_RESULT;
        }
    }

    private DataResultOutboundHL7Message initiateResultOutboundObj(MachineResult resultInfo,
        String messageHL7) {

        DataResultOutboundHL7Message outResult= new DataResultOutboundHL7Message();
        DataOutboundHL7Message dataOutboundHL7Message= new DataOutboundHL7Message();
        outResult.setMachineResult(resultInfo);

        dataOutboundHL7Message.setMessageBody(messageHL7);
        dataOutboundHL7Message.setSource("HL7");
        dataOutboundHL7Message.setPriority("R");

        outResult.setDataOutboundHL7Message(dataOutboundHL7Message);

        return outResult;
    }

    private void saveOutboundMessage(DataOutboundHL7Message dataOutboundHl7Message) {
        DataOutboundHL7MessageService dataOutboundHL7MessageService= (DataOutboundHL7MessageService) SpringUtil
            .getBean("DataOutboundHL7MessageService");
        dataOutboundHL7MessageService.addInbound(dataOutboundHl7Message);
    }

    private Long getNextOutboundControlID() {
        DataOutboundHL7MessageService dataOutboundHL7MessageService= (DataOutboundHL7MessageService) SpringUtil
            .getBean("DataOutboundHL7MessageService");
        return dataOutboundHL7MessageService.getDataOutboundHL7MessageControlID();

    }

    private void saveResultOutboundMessage(DataResultOutboundHL7Message outResultInfo) {
        DataResultOutboundHL7MessageService dataResultOutboundHL7MessageService= (DataResultOutboundHL7MessageService) SpringUtil
            .getBean("DataResultOutboundHL7MessageService");
        dataResultOutboundHL7MessageService.addResult(outResultInfo);

    }

    private String executeHL7Post(String requestBody, String targetUrl) throws IOException {
        HttpURLConnection connection= null;
        // String authorizationToken = token;
        try {
            URL url= new URL(targetUrl);
            connection= (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "text/plain");
            // connection.setRequestProperty("Authorization", authorizationToken);
            DataOutputStream outStream= new DataOutputStream(connection.getOutputStream());
            outStream.writeBytes(requestBody);
            outStream.flush();
            outStream.close();

            InputStream inStream= connection.getInputStream();
            BufferedReader br= new BufferedReader(new InputStreamReader(inStream));
            System.out.print("##########SENT TO eHope##########################");
            return br.lines().collect(Collectors.joining());
        } catch (Exception ex) {
            return null;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    private void sendRecivedResultListToLIS(String barcode)
        throws IOException, JsonParseException, JsonMappingException {

        Long machineBranchID= machine.getBranchId();
        branch= getMachineBranch(machineBranchID);
        String resultAsJason= JSONUtil.convertObjectToJSON(resultMapList);

        if (branch != null && resultAsJason != "") {
            String apiResponse= executePOST(resultAsJason, branch.getIntegrationUrl(),
                branch.getIntegrationToken());
            objectMapper= new ObjectMapper();
            List<Map<String, String>> responseMap= objectMapper.readValue(apiResponse,
                new TypeReference<List<Map<String, String>>>() {});

            for (Map<String, String> mapResponse : responseMap) {
                boolean isAccepted= Boolean.parseBoolean(mapResponse.get("isAccepted"));
                if (isAccepted == false) {
                    String output= MapUtil.mapToString(mapResponse);
                    CoreEventLog coreEventLog= getEventLogObject(output);
                    coreEventLogService.addCoreEventLog(coreEventLog);
                }
            }
        } else {
            String strError= "\n------------------------------------------------------------------------------------------------------------\n" +
                "------------------------------------------------------------------------------------------------------------\n"

                +
                " [Error On Sending Result ] : Sending error happed when sending data from LIS to MIW for " +
                "\n" + " Machine : " + machine.getName() + "\n" + " For Order with Sample ID : " +
                "\n" + barcode + "\n" + " For The Follwing Reason : " + "\n" +
                "check dependencies reference if null " + "\n"

                +
                "\n----------------------------------------------------------------------------------------------------------------------\n" +
                "------------------------------------------------------------------------------------------------------------\n";

            if (runMode == RUN_MODE.DEBUG) {
                System.out.println(strError);
            }

            CoreEventLog eventLog= getEventLogObject(strError);
            coreEventLogService.addCoreEventLog(eventLog);
        }
    }

    private CoreEventLog getEventLogObject(String output) {
        CoreEventLog coreEventLog= new CoreEventLog();
        coreEventLog.setBranchId(machine.getBranchId());
        coreEventLog.setTenantId(machine.getTenantId());
        coreEventLog.setSource(getContext().self().toString());
        coreEventLog.setText(output);
        coreEventLog.setMachine(machine);
        coreEventLog.setStatusId(4);
        Date date= new Date();
        coreEventLog.setSentDate(date);
        return coreEventLog;
    }

    private LabBranch getMachineBranch(Long BranchID) {
        return branchService.findById(BranchID);
    }

    private void setMachineResultDetails(Container container, Analysis analysis) {
        resultInfo.setResultCode(analysis.resultCode);
        resultInfo.setMachineName(machine.getName());
        resultInfo.setSampleNo(container.specimenId.trim());
        resultInfo.setMachineQueryId(machineQuery);
        resultInfo.setMachine(machine);
        resultInfo.setIsSentToLIS(true);
    }

    private MachineOrder getAssignedMachineOrder(LabResultMsg resultMsg,
        TestCatalog selectedTestCatalog) {
        MachineOrder machineOrder= getMachineOrder(resultMsg, selectedTestCatalog);
        if (machineOrder != null) {

            machineOrder.setResultReceived(true);
            machineOrder.setUpdatedBy(-1L);
            machineOrder.setTenantId(machine.getTenantId());
            machineOrder.setBranchId(machine.getBranchId());
            machineOrder.setOrderId(machineOrder.getRid());
            machineOrder= machineOrderService.updateOrder(machineOrder);
        } else {

            String strError= "\n------------------------------------------------------------------------------								    							-------------------------------\n" +
                "--------------------------------------------------------------------------------					         							-----------------------------\n"

                + " [Error On Mapping Test] : " + " Machine : " + machine.getName() + "\n" +
                " Recive Result For Order barcode : " + resultInfo.getSampleNo() + "\n" +
                " 							with test code: " + "\n" +
                selectedTestCatalog.getRequesterTestCode() + "\n" +
                " is not found or there is a problem (s) with mapping machine instance with 							branch or tenant id" +
                "\n------------------------------------------------------------------------------		  							-------------------------------\n" +
                "--------------------------------------------------------------------------------		 							-----------------------------\n";

            if (runMode == RUN_MODE.DEBUG) {
                System.out.println(strError);
            }

            CoreEventLog eventLog= getEventLogObject(strError);
            coreEventLogService.addCoreEventLog(eventLog);

        }

        return machineOrder;

    }

    private MachineOrder getMachineOrder(LabResultMsg resultMsg, TestCatalog selectedTestCatalog) {
        return machineOrderService.getBySampleAndTestCode(
            resultMsg.order.container.specimenId.trim(),
            selectedTestCatalog.getRequesterTestCode(), machine.getTenantId(),
            machine.getBranchId());
    }

    private List<MachineTest> getMappedMachineTestByCode(Analysis analysis) {

        String TestHostCode= analysis.code.trim();

        /*
         * if(analysis.code.contains("%")) { TestHostCode= analysis.code.replace('%',
         * '\0').trim(); } else { TestHostCode= analysis.code.trim(); }
         */

        return machineTestService.getMachineTestsByCodeAndMachine(TestHostCode, machine);
    }

    private void setResultValue(Container container, Result result) {
        resultInfo.setDataOrMeasurementValue(result.value);
        resultInfo.setUnit(result.unit);
        resultInfo.setSampleNo(container.specimenId);
        resultInfo.setIsSentToLIS(false);
    }

    private void sendQuery(LabQueryMsg queryMsg) throws IOException {

        initiateObjects();

        List<MachineTest> finalTestSelctionList= new ArrayList<MachineTest>();
        List<LabMessages.Analysis> analysisListMapped= new ArrayList<LabMessages.Analysis>();

        machine= machineService.getMachineByActorPath(getContext().parent().toString());
        queryMsg.machineName= machine.getName();
        LabOrderMsg orderMsg= new LabOrderMsg();

        ordersList= getOrderList(queryMsg);

        if (CollectionUtil.isCollectionEmpty(ordersList)) {

            String strError= sendErrorOnSelectionResponce(queryMsg);

            CoreEventLog eventLog= getEventLogObject(strError);
            coreEventLogService.addCoreEventLog(eventLog);

        }

        List<LabMessages.Analysis> analysisList= new ArrayList<LabMessages.Analysis>();

        Container container;

        // Check if the order list is not empty
        if (!CollectionUtil.isCollectionEmpty(ordersList)) {

            if (!machine.getIsPanelOrder()) {
                requesterTestList= new ArrayList<String>();
                requesterTestList= getRequesterList(queryMsg, analysisList);

                // Do the mapping between the requested test codes and the machine test codes to
                // get the host codes where the machine understand
                analysisListMapped= new ArrayList<LabMessages.Analysis>();
                finalTestSelctionList= machineTestService.getMachineTests(requesterTestList,
                    machine);

                String SpecimenDescriptor= "";

                SpecimenDescriptor= getResultMappingList(analysisList, finalTestSelctionList,
                    analysisListMapped,
                    SpecimenDescriptor);

                if (analysisListMapped.size() != 0) {
                    container= new Container(queryMsg.specimenIds, analysisListMapped,
                        queryMsg.specimenPosition,
                        ordersList.get(0).getPriority(),
                        DateUtil.formatDate((ordersList.get(0).getSpecimenCollectionDateAndTime()),
                            "dd/MM/yyyy HH:mm:ss"),
                        SpecimenDescriptor, CodesEnumAll.NEW.getValue(),
                        CodesEnumAll.ReportTypeQ.getValue().toString());

                    setPatientInformation(false);

                    Patient patient= new Patient(patientFirstName, patientLastName, patientLastName,
                        "", patientId,
                        dateOfBirth, gender, attendingPhysician);

                    Order order= new Order(patient, container, machine.getPassword(),
                        machine.getName(),
                        ordersList.get(0).getSenderName(), -1);

                    orderMsg= new LabOrderMsg(order);

                    conf.recipient.tell(orderMsg, self());
                } else {

                    // alaa : negative query case
                    if (analysisListMapped.size() == 0) {
                        MachineTypeEnum machineType= MachineTypeEnum
                            .valueOf(machine.getMachineType().getCode());

                        switch (machineType) {
                        case ABBOTT_ARCHITECT_CI4100:
                            SendNegativeQuery(queryMsg, analysisListMapped, SpecimenDescriptor);
                            break;

                        }

                    }

                }

            } else {
                requesterTestList= new ArrayList<String>();
                requesterTestList= getRequesterList(queryMsg, analysisList);

                // Do the mapping between the requested test codes and the machine test codes to
                // get the host codes where the machine understand
                analysisListMapped= new ArrayList<LabMessages.Analysis>();
                finalTestSelctionList= machineTestService.getMachineTests(requesterTestList,
                    machine);

                String SpecimenDescriptor= "";

                SpecimenDescriptor= getResultMappingList(analysisList, finalTestSelctionList,
                    analysisListMapped,
                    SpecimenDescriptor);

                if (!CollectionUtil.isArrayEmpty(requesterTestList.toArray())) {

                    MachineTypeEnum machineType= MachineTypeEnum
                        .valueOf(machine.getMachineType().getCode());

                    switch (machineType) {

                    case BECHMAN_COULTER_800_DXH:
                        orderMsg= createMessageSelectionForPanel(0);

                        machineOrderService.updateAllOrdersIsSentToMachine(
                            ordersList.get(0).getBarcode(),
                            ordersList.get(0).getPanelCode(), true, machine.getTenantId(),
                            machine.getBranchId());

                        conf.recipient.tell(orderMsg, self());
                        break;

                    }

                }
            }
        }
        if (CollectionUtil.isCollectionEmpty(ordersList)) {
            if (!machine.getIsPanelOrder()) {

                String SpecimenDescriptor= "";
                // alaa : negative query case
                if (analysisListMapped.size() == 0) {
                    MachineTypeEnum machineType= MachineTypeEnum
                        .valueOf(machine.getMachineType().getCode());

                    switch (machineType) {
                    case ABBOTT_ARCHITECT_CI4100:
                        SendNegativeQuery(queryMsg, analysisListMapped, SpecimenDescriptor);
                        break;

                    }

                }

            }
        }

    }

    private void SendNegativeQuery(LabQueryMsg queryMsg,
        List<LabMessages.Analysis> analysisListMapped,
        String SpecimenDescriptor) {
        LabOrderMsg orderMsg;
        Container container;
        container= new Container(queryMsg.specimenIds, analysisListMapped,
            queryMsg.specimenPosition, null, null,
            SpecimenDescriptor, "", "");

        setPatientInformation(true);

        Patient patient= new Patient(patientFirstName, patientLastName, patientLastName, "",
            patientId, dateOfBirth,
            gender, attendingPhysician);
        Order order= new Order(patient, container, machine.getPassword(), machine.getName(), null,
            -1);
        orderMsg= new LabOrderMsg(order);
        conf.recipient.tell(orderMsg, self());
    }

    private LabOrderMsg createMessageSelectionForPanel(int index) {
        LabOrderMsg orderMsg;
        List<LabMessages.Analysis> analysisList;
        Container container;
        String SpecimenDescriptor= "";

        LabMessages.Analysis analysis= new Analysis(ordersList.get(index).getPanelCode(), "", "",
            null);
        analysisList= new ArrayList<LabMessages.Analysis>();
        analysisList.add(analysis);

        container= new Container(ordersList.get(index).getBarcode(), analysisList, "",
            ordersList.get(0).getPriority(),
            DateUtil.formatDate((ordersList.get(0).getSpecimenCollectionDateAndTime()),
                "dd/MM/yyyy HH:mm:ss"),
            SpecimenDescriptor, CodesEnumAll.NEW.getValue(),
            CodesEnumAll.ReportTypeQ.getValue().toString());

        setPatientInformation(ordersList.get(index));

        Patient patient= new Patient(patientFirstName, patientLastName, patientLastName, "",
            patientId, dateOfBirth,
            gender, attendingPhysician);

        Order order= new Order(patient, container, machine.getPassword(), machine.getName(),
            ordersList.get(0).getSenderName(), -1);

        orderMsg= new LabOrderMsg(order);
        return orderMsg;
    }

    private String getUniversalTestID(Machine machine, String panelName) {

        String panelHostCode= machineTypePanelService
            .getPanelHostCode(machine.getMachineType().getRid(), panelName);

        // return "^^^" + panelHostCode + "^" + "4" + "^" + "0";
        return panelHostCode;

    }

    private void sendEmptyTS(LabQueryMsg queryMsg) {
        LabOrderMsg orderMsg;
        Container container;
        container= new Container(queryMsg.specimenIds, queryMsg.specimenPosition,
            DateUtil.formatDate(new Date(), "dd/MM/yyyy HH:mm:ss"), null, null);
        Patient patient= new Patient();
        Order order= new Order(patient, container, machine.getPassword(), machine.getName(), "",
            -1);
        orderMsg= new LabOrderMsg(order);
        conf.recipient.tell(orderMsg, self());
    }

    private String sendErrorOnSelectionResponce(LabQueryMsg queryMsg) {
        String strError= "\n-----------------------------------------------------------------------------------------------------\n" +
            "-----------------------------------------------------------------------------------------------------\n"

            + " [Error On Mapping Test] : " + " Machine : " + machine.getName() + "\n" +
            " Recive Query With barcode : " + queryMsg.specimenIds.toString() + "\n" +
            " No Any Test Exists For This Barcode No" + "\n" +
            "\n-------------------------------------------------------------------------------------------------------------\n" +
            "-----------------------------------------------------------------------------------------------------\n";

        if (runMode == RUN_MODE.DEBUG) {
            System.out.println(strError);
        }
        return strError;
    }

    private void setPatientInformation(boolean isEmpty) {

        patientFirstName= "";
        patientSecondName= "";
        patientLastName= "";
        patientId= "";
        gender= "";
        dateOfBirth= null;
        attendingPhysician= "";

        if (!isEmpty) {
            // Extract patient information to be injected in the order
            if (ordersList.get(0).getPatientFirstName() != null) {
                patientFirstName= ordersList.get(0).getPatientFirstName();
            }

            if (ordersList.get(0).getPatientLastName() != null) {
                patientLastName= ordersList.get(0).getPatientLastName();
            }

            if (ordersList.get(0).getPatientId() != null) {
                patientId= ordersList.get(0).getPatientId().toString();
            }

            if (ordersList.get(0).getGender() != null) {
                gender= ordersList.get(0).getGender();
            }

            if (ordersList.get(0).getDateOfBirth() != null) {
                dateOfBirth= ordersList.get(0).getDateOfBirth();
            }

            if (ordersList.get(0).getOrderingPhysician() != null) {
                attendingPhysician= ordersList.get(0).getOrderingPhysician();
            }
        } else {
            patientFirstName= "";
            patientLastName= "";
            patientId= "";
            gender= "";
            dateOfBirth= null;
            attendingPhysician= "";

        }
    }

    private void setPatientInformation(MachineOrder order) {

        patientFirstName= "";
        patientSecondName= "";
        patientLastName= "";
        patientId= "";
        gender= "";
        dateOfBirth= null;
        attendingPhysician= "";

        // Extract patient information to be injected in the order
        if (order.getPatientFirstName() != null) {
            patientFirstName= order.getPatientFirstName();
        }

        if (order.getPatientLastName() != null) {
            patientLastName= order.getPatientLastName();
        }

        if (order.getPatientId() != null) {
            patientId= order.getPatientId().toString();
        }

        if (order.getGender() != null) {
            gender= order.getGender();
        }

        if (order.getDateOfBirth() != null) {
            dateOfBirth= ordersList.get(0).getDateOfBirth();
        }

        if (order.getOrderingPhysician() != null) {
            attendingPhysician= ordersList.get(0).getOrderingPhysician();
        }
    }

    private String getResultMappingList(List<LabMessages.Analysis> analysisList,
        List<MachineTest> machineTestList,
        List<LabMessages.Analysis> analysisListMapped, String SpecimenDescriptor) {
        for (Analysis analysis : analysisList) {
            boolean testMapExist= false;
            // Do a loop on the result of the mapping query and replace each code with its
            // mapped to code
            for (MachineTest machineTest : machineTestList) {
                if (machineTest.getTestCatalog().getRequesterTestCode().equals(analysis.code) &&
                    machineTest.getIsActive()) {
                    if (machineTest.getTestCatalog().getSpecimenType() != null) {
                        SpecimenDescriptor= machineTest.getTestCatalog().getSpecimenType()
                            .getCode();
                    }
                    testMapExist= true;

                    if (analysis.code.equals("A1C")) {
                        Analysis a1= new Analysis();
                        // Analysis a2 = new Analysis();
                        Analysis a3= new Analysis();
                        a1.code= "881";
                        analysisListMapped.add(a1);
                        // a2.code = "891";
                        // analysisListMapped.add(a2);
                        a3.code= "871";
                        analysisListMapped.add(a3);

                    } else if (analysis.code.equals("TIBC")) {
                        Analysis a1= new Analysis();
                        Analysis a2= new Analysis();
                        Analysis a3= new Analysis();
                        Analysis aa= null;

                        a1.code= "661";

                        for (int i= 0; i < analysisList.size(); i++ ) {
                            if (analysisList.get(i).code.equals("IRON")) {
                                aa= analysis;
                                break;
                            } else {
                                aa= null;
                            }
                        }

                        if (aa == null) {
                            a1.code= "661";
                            analysisListMapped.add(a1);
                        }

                        a2.code= "779";
                        analysisListMapped.add(a2);
                        a3.code= "961";
                        analysisListMapped.add(a3);
                    } else {
                        analysis.code= machineTest.getHostCode();
                        analysisListMapped.add(analysis);
                    }

                }
            }
            // save the undefined code in the log event table
            // alaa : this case not correct merge between mapped test and disabled test
            // (semantic problem)
            if (testMapExist == false) {
                // log.error(" Test Code Not Mapped " + analysis.code);
                CoreEventLogService coreEventLogService= (CoreEventLogService) SpringUtil
                    .getBean("CoreEventLogService");
                CoreEventLog coreEventLog= new CoreEventLog();
                coreEventLog.setBranchId(machine.getBranchId());
                coreEventLog.setTenantId(machine.getTenantId());
                coreEventLog.setSource(getContext().self().toString());
                coreEventLog.setText(" Test Code Not Mapped " + analysis.code);
                coreEventLog.setMachine(machine);
                coreEventLog.setStatusId(4);
                Date date= new Date();
                coreEventLog.setSentDate(date);
                coreEventLogService.addCoreEventLog(coreEventLog);
            }
        }
        return SpecimenDescriptor;
    }

    private List<String> getRequesterList(LabQueryMsg queryMsg,
        List<LabMessages.Analysis> analysisList) {
        Result result;
        List<String> requestedTestList= new ArrayList<String>();
        Analysis analysis;
        for (MachineOrder orderInfo : ordersList) {
            machineOrderQueryResponse= new MachineOrderQueryResponse();
            result= new Result("", "", "", "");

            if (!machine.getIsPanelOrder()) {
                analysis= new Analysis(orderInfo.getPanelCode(), orderInfo.getTestCode().toString(),
                    "", "", result);
                // Add the test code for the order in a list
                machineQuery= mQueryService
                    .getQueryBySampleAndMachine(queryMsg.specimenIds.toString(), machine);
                analysisList.add(analysis);

                addMachineQueryResponce(orderInfo);
                // Add the required test codes in a list of strings
                requestedTestList.add(orderInfo.getTestCode());
            } else {
                analysis= new Analysis(orderInfo.getPanelCode(),
                    orderInfo.getPanelCode().toString(), "", "", result);
                // Add the required test codes in a list of strings
                requestedTestList.add(orderInfo.getPanelCode());
            }

        }

        return requestedTestList;
    }

    private void addMachineQueryResponce(MachineOrder orderInfo) {
        machineOrderQueryResponse.setMachineOrderId(orderInfo);
        machineOrderQueryResponse.setMachineQueryId(machineQuery);
        machineOrderQueryResponceService.addMachineOrderQueryResponse(machineOrderQueryResponse);
    }

    private List<MachineOrder> getOrderList(LabQueryMsg queryMsg) {

        machine= machineService.getMachineByActorPath(getContext().parent().toString());
        MachineTypeEnum machineType= MachineTypeEnum.valueOf(machine.getMachineType().getCode());
        List<MachineOrder> lstOrder= null;

        switch (machineType) {

        case ABBOTT_CELL_DYN_RUBY:

            if (queryMsg.specimenIds.equals("ALL")) {

                lstOrder= getAllBatchedOrders(queryMsg);

            } else {

                lstOrder= getPanelOrder(queryMsg, machine.getMachineType());
            }

            break;
        default:

            lstOrder= getMachineOrders(queryMsg);

            break;

        }

        return lstOrder;

    }

    @InterceptorFree
    private List<MachineOrder> getAllBatchedOrders(LabQueryMsg queryMsg) {
        List<MachineOrder> lstOrder;
        List<MachineTypePanel> lstMachineTypePanel;

        machine= machineService.getMachineByActorPath(getContext().parent().toString());
        lstMachineTypePanel= machineTypePanelService
            .getPanelByMachineType(machine.getMachineType().getRid());

        lstOrder= machineOrderService.getAllOrdersByPanelName(lstMachineTypePanel,
            machine.getTenantId(),
            machine.getBranchId());

        return lstOrder;
    }

    private List<MachineOrder> getMachineOrders(LabQueryMsg queryMsg) {
        List<MachineOrder> lstOrder;
        // Check if the required data are requested before,result received before or not
        // the both conditions
        if (machine.getRequestTestWithNoResultOnly() == true ||
            machine.getRequestNewTestOnly() == true) {
            lstOrder= machineOrderService.getOrderInformationBySampleNoWithOptions(
                queryMsg.specimenIds.toString(),
                machine.getRequestTestWithNoResultOnly(), machine.getRequestNewTestOnly());
        } else {

            lstOrder= machineOrderService.getOrderInformationBySampleNo(
                queryMsg.specimenIds.toString(),
                machine.getTenantId(), machine.getBranchId());

        }
        return lstOrder;
    }

    private List<MachineOrder> getPanelOrder(LabQueryMsg queryMsg, MachineType machineType) {
        List<MachineOrder> lstOrder= new ArrayList<MachineOrder>();
        List<MachineTypePanel> machineTypePanelList;
        machineTypePanelList= machineTypePanelService.getPanelByMachineType(machineType.getRid());

        for (int index= 0; index < machineTypePanelList.size(); index++ ) {
            MachineOrder newOrder= machineOrderService.getOrderInformationByPanelName(
                queryMsg.specimenIds.toString(),
                machine.getTenantId(), machine.getBranchId(),
                machineTypePanelList.get(index).getPanelHostCode());
            lstOrder.add(newOrder);

        }

        return lstOrder;

    }

    /*
     * private String executePOST(String requestBody, String targetUrl, String
     * token) throws IOException { HttpURLConnection connection = null; String
     * authorizationToken = token; try { URL url = new URL(targetUrl); connection =
     * (HttpURLConnection) url.openConnection();
     * connection.setRequestMethod("POST"); connection.setDoOutput(true);
     * connection.setRequestProperty("Content-Type", "application/json");
     * connection.setRequestProperty("Authorization", authorizationToken);
     * DataOutputStream outStream = new
     * DataOutputStream(connection.getOutputStream());
     * outStream.writeBytes(requestBody); outStream.flush(); outStream.close();
     * InputStream inStream = connection.getInputStream(); BufferedReader br = new
     * BufferedReader(new InputStreamReader(inStream)); System.out.
     * print("##########################SENT SUCCESSFULLY##########################"
     * ); return br.lines().collect(Collectors.joining()); } finally { if
     * (connection != null) connection.disconnect(); } }
     */

    private String executePOST(String requestBody, String targetUrl, String API_KEY)
        throws IOException {
        HttpURLConnection connection= null;
        String authorizationKey= API_KEY;
        try {
            URL url= new URL(targetUrl);
            connection= (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json ; charset=UTF-8");

            connection.setRequestProperty("API-KEY", authorizationKey);
            DataOutputStream outStream= new DataOutputStream(connection.getOutputStream());
            outStream.writeBytes(requestBody);
            outStream.flush();
            outStream.close();
            InputStream inStream= connection.getInputStream();
            BufferedReader br= new BufferedReader(new InputStreamReader(inStream));
            System.out
                .print("##########################SENT SUCCESSFULLY##########################");
            return br.lines().collect(Collectors.joining());
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    public static class MapUtil {

        public static String mapToString(Map<String, String> map) {
            StringBuilder stringBuilder= new StringBuilder();

            for (String key : map.keySet()) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append("&");
                }
                String value= map.get(key);
                try {
                    stringBuilder.append((key != null ? URLEncoder.encode(key, "UTF-8") : ""));
                    stringBuilder.append("=");
                    stringBuilder.append(value != null ? URLEncoder.encode(value, "UTF-8") : "");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("This method requires UTF-8 encoding support", e);
                }
            }

            return stringBuilder.toString();
        }
    }
}