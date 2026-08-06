package com.sunbird.lis.interfaces.middleware.flow_component.scheduler;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.sunbird.core.common.helper.Email;
import com.sunbird.core.common.util.EmailUtil;
import com.sunbird.core.common.util.SpringUtil;
import com.sunbird.lis.interfaces.entities.DataInboundHL7Message;
import com.sunbird.lis.interfaces.entities.ElegabalityApprovalOrderEntity;
import com.sunbird.lis.interfaces.entities.Machine;
import com.sunbird.lis.interfaces.entities.OutboundErrorEmailEntity;
import com.sunbird.lis.interfaces.entities.PostDetailFinancialTransaction;
import com.sunbird.lis.interfaces.middleware.core.FlowComponent;
import com.sunbird.lis.interfaces.middleware.core.RecipientConf;
import com.sunbird.lis.interfaces.middleware.flow_component.lab_http.httpRequstTransaction;
import com.sunbird.lis.interfaces.service.DataInboundHL7MessageService;
import com.sunbird.lis.interfaces.service.ElegabalityApprovalOrderService;
import com.sunbird.lis.interfaces.service.MachineService;
import com.sunbird.lis.interfaces.service.OutboundErrorEmailService;
import com.sunbird.lis.interfaces.service.PostDetailFinancialTransactionService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.concurrent.duration.FiniteDuration;
import scala.runtime.BoxedUnit;

public class DatabaseOutboundEmailCollecter extends FlowComponent<RecipientConf> {

    Config config = ConfigFactory.load();

    private MachineService machineService;
    private Machine machine;
    private DataInboundHL7Message inboundHL7Message;
    private DataInboundHL7MessageService HL7MessageService;
    private OutboundErrorEmailService outboundErrorEmailService ;
    private List<OutboundErrorEmailEntity> lstErrorEmail;
    private Email email;
    private EmailUtil emailUtil ;
    
   
    private boolean isSendingEmailEnabled = (config.getString("system.certacure.notification.khcc.email.enable").equals("1")) ? true : false ; 
    private String notificationEmailAddress = config.getString("system.certacure.notification.khcc.notification.email");//CertaEngine_Mail@khcc.jo
    
   
    @Override
    protected void init() {
    	
        System.out.println("DB EMAIL ERROR SC COLLECTER BUILD");
        context().system().scheduler().schedule(
            new FiniteDuration(1, TimeUnit.MINUTES), new FiniteDuration(1, TimeUnit.MINUTES),
            self(),
            new DBFetchManager(), context().dispatcher(), self());
        machine = getMachine();
    }

    @Override
    protected PartialFunction<Object, BoxedUnit> getBehaviour() {
        return ReceiveBuilder
            .match(String.class, this::convertAndForward)

            .match(DBFetchManager.class, __ -> {
                System.out.println("DB EMAIL ERROR SC COLLECTER CALLED AGINE");
             
                // throw new RuntimeException("Restart needed");

             machine = getMachineInfoByPath();


            // if (machine.getIsActive() && machine.getConnected())
            	 
             if (machine.getIsActive() && isSendingEmailEnabled)
             {
            	 lstErrorEmail = FetchAllOutboundErrorEmail();
						
            	 OutboundErrorEmailEntity outboundErrorEmailEntityObj = null;


						//listAllPendingOrder = new ArrayList<ElegabalityApprovalEntity>();

						for (int index = 0; (index < 100) && (index < lstErrorEmail.size()); index++) {

							try {

								outboundErrorEmailEntityObj = lstErrorEmail.get(index);

								Map<String, String> templateValues = new HashMap<>();
								// email = new Email("CertaEngine_Mail@khcc.jo", "ADT Error",
								// lstErrorEmail.get(index).getMessageText());
								templateValues.put("eventDateTime",
										outboundErrorEmailEntityObj.getCreationDate().toString());
								templateValues.put("adtOperationType",
										outboundErrorEmailEntityObj.getadtOperationType() == null ? "No Data"
												: outboundErrorEmailEntityObj.getadtOperationType());
								templateValues.put("msgControlId", outboundErrorEmailEntityObj.getMessageControlID());
								templateValues.put("patientId", outboundErrorEmailEntityObj.getPatientId() == null ? "" :  outboundErrorEmailEntityObj.getPatientId() );
								templateValues.put("admissionNo",								
								outboundErrorEmailEntityObj.getAdmissionNo() == null ? "No Data" :
								outboundErrorEmailEntityObj.getAdmissionNo());
								
								
								templateValues.put("messageBody", outboundErrorEmailEntityObj.getMessageText());//outboundErrorEmailEntityObj.getMessageText()
								templateValues.put("msgDetails", outboundErrorEmailEntityObj.getBody());
								//"CERTA_COMMITTEE@khcc.jo""
								email = new Email(notificationEmailAddress, "ADT Error: " + outboundErrorEmailEntityObj.getMessageControlID() ,
										lstErrorEmail.get(index).getMessageText());
								email.setTemplateUri("email-new-user");
								email.setTemplateValueMap(templateValues);
								email.setSender(generateMailSender());
								EmailUtil.sendMailTemplate(email);

								outboundErrorEmailEntityObj.setIsSent(true);
								outboundErrorEmailEntityObj.setIsSuccess(true);
								outboundErrorEmailEntityObj.setIsFailed(false);

								outboundErrorEmailEntityObj.setNotes("Success");

								outboundErrorEmailService.save(outboundErrorEmailEntityObj);
								
							} catch (Exception ex) {
								outboundErrorEmailEntityObj.setIsSent(false);
								outboundErrorEmailEntityObj.setIsSuccess(false);
								outboundErrorEmailEntityObj.setIsFailed(true);
								outboundErrorEmailEntityObj.setNotes("Failed !! ," + ex.getMessage());

								outboundErrorEmailService.save(outboundErrorEmailEntityObj);
							}

						}


					}
				})


				.build();
	}
    
    public static JavaMailSenderImpl generateMailSender() {
		JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
		javaMailSenderImpl.setUsername(EmailUtil.DEFAULT_MAIL_SENDER.getUsername());
		javaMailSenderImpl.setPassword(EmailUtil.DEFAULT_MAIL_SENDER.getPassword());
		javaMailSenderImpl.setHost(EmailUtil.DEFAULT_MAIL_SENDER.getHost());
		javaMailSenderImpl.setPort(EmailUtil.DEFAULT_MAIL_SENDER.getPort());
		//set some default properties
		javaMailSenderImpl.setDefaultEncoding(EmailUtil.DEFAULT_MAIL_SENDER.getDefaultEncoding());
		javaMailSenderImpl.setJavaMailProperties(EmailUtil.DEFAULT_MAIL_SENDER.getJavaMailProperties());
		javaMailSenderImpl.setProtocol(EmailUtil.DEFAULT_MAIL_SENDER.getProtocol());
		return javaMailSenderImpl;
	}

    public Machine getMachineInfoByPath() {
        MachineService machineService= (MachineService) SpringUtil.getBean("MachineService");
        Machine machine= machineService.getMachineByActorPath(getContext().parent().toString());
        return machine;
    }



private  List<OutboundErrorEmailEntity>  FetchAllOutboundErrorEmail() 
{

        outboundErrorEmailService = (OutboundErrorEmailService) SpringUtil
                .getBean("OutboundErrorEmailService");

//        List<DataInboundHL7Message> lstAllHL7Message = HL7MessageService
//            .getAllPendingHL7Message(inboundHL7Message);

        List<OutboundErrorEmailEntity> outboundEmailErrorList = outboundErrorEmailService.getAllErrorEmail();

        System.out.println(outboundEmailErrorList);

        return outboundEmailErrorList;
    }

   
    private void convertAndForward(String strData) {

    }

    private Machine getMachine() {
        machineService= (MachineService) SpringUtil.getBean("MachineService");
        machine= machineService.getMachineByActorPath(getContext().parent().toString());
        return machine;
    }

    private class DBFetchManager implements Serializable {
        public DBFetchManager() {
            System.out.println("DB FETCH MANAGER CALLED");
        }

    }

    public  Date convertStringToDate(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        return dateFormat.parse(dateString);
    }

}
