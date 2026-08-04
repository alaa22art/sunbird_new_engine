package com.certacure.lis.interfaces.middleware.flow_component.scheduler;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.certacure.core.common.helper.Email;
import com.certacure.core.common.util.EmailUtil;
import com.certacure.core.common.util.SpringUtil;
import com.certacure.lis.interfaces.entities.DataInboundHL7Message;
import com.certacure.lis.interfaces.entities.ElegabalityApprovalOrderEntity;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.entities.OutboundErrorEmailEntity;
import com.certacure.lis.interfaces.entities.PostDetailFinancialTransaction;
import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.core.RecipientConf;
import com.certacure.lis.interfaces.middleware.flow_component.lab_http.httpRequstTransaction;
import com.certacure.lis.interfaces.service.DataInboundHL7MessageService;
import com.certacure.lis.interfaces.service.ElegabalityApprovalOrderService;
import com.certacure.lis.interfaces.service.MachineService;
import com.certacure.lis.interfaces.service.OutboundErrorEmailService;
import com.certacure.lis.interfaces.service.PostDetailFinancialTransactionService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.concurrent.duration.FiniteDuration;
import scala.runtime.BoxedUnit;

public class APIToDBCollecter extends FlowComponent<RecipientConf> {

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
    	
        System.out.println("LIS API ORDER COLLECTER COLLECTER BUILD");
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
                System.out.println("LIS API ORDER COLLECTER CALLED AGINE");
             
                // throw new RuntimeException("Restart needed");

             machine = getMachineInfoByPath();


            // if (machine.getIsActive() && machine.getConnected())
            	 
             if (machine.getIsActive() && isSendingEmailEnabled)
             {
            	}
             System.out.println(" ++++++++++++++++++++++++++++ ");
				}
            )


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
