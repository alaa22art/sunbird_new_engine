package com.certacure.lis.interfaces.middleware.flow_component.scheduler;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.certacure.core.common.util.SpringUtil;
import com.certacure.lis.interfaces.entities.ElegabalityApprovalEntity;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.entities.PostDetailFinancialTransaction;
import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.core.RecipientConf;
import com.certacure.lis.interfaces.middleware.flow_component.lab_http.httpRequstTransaction;
import com.certacure.lis.interfaces.service.ElegabalityApprovalService;
import com.certacure.lis.interfaces.service.MachineService;
import com.certacure.lis.interfaces.service.PostDetailFinancialTransactionService;
import com.typesafe.config.Config;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.concurrent.duration.FiniteDuration;
import scala.runtime.BoxedUnit;

public class DBScheduleAppointmentCollector extends FlowComponent<RecipientConf> {

    private Config config= null;

    private MachineService machineService;
    private Machine machine;
    private ElegabalityApprovalService elegabalityApprovalService ;
    private PostDetailFinancialTransaction postDetailFinancialTransaction;

    private PostDetailFinancialTransactionService postDetailFinancialTransactionService;


    @Override
    protected void init() {
        System.out.println("DB APP COLLECTER BUILD");

        postDetailFinancialTransactionService = (PostDetailFinancialTransactionService) SpringUtil
    			.getBean("PostDetailFinancialTransactionService");

        context().system().scheduler().schedule(
            new FiniteDuration(20, TimeUnit.SECONDS), new FiniteDuration(20, TimeUnit.SECONDS),
            self(),
            new DBFetchManager(), context().dispatcher(), self());


        machine = getMachine();
    }

    @Override
    protected PartialFunction<Object, BoxedUnit> getBehaviour() {
        return ReceiveBuilder
            .match(String.class, this::convertAndForward)

            .match(DBFetchManager.class, __ -> {
                System.out.println("DB APP COLLECTER CALLED AGINE");
                List<ElegabalityApprovalEntity> listAllPendingOrder = null;


                // throw new RuntimeException("Restart needed");

					/*if (machine.getIsActive()) {
						listAllPendingOrder = FetchAllPendingOutboundMessages();

						for (ElegabalityApprovalEntity elegObj : listAllPendingOrder) {
							List<PostDetailFinancialTransaction> listPostDetailsTrans =
									postDetailFinancialTransactionService
									.getOrdersByCertaOrderIdAndActionItem( elegObj.getOrderId().toString(),
											elegObj.getOrderActionId().toString());

							if (listPostDetailsTrans.size() > 0)
							{
								if(elegObj.getVistaOrderID() == null)
								{

									elegabalityApprovalService.createElegabalityOrderRecord(elegObj);

								}

								if (listPostDetailsTrans.get(0).getServSection().equals("RA")) {
									postDetailFinancialTransaction = listPostDetailsTrans.get(0);

									elegObj
											.setAppointment_id(postDetailFinancialTransaction.getAppointmentID());
								}

								conf.recipient.tell(elegObj, self());
							}



							// send message to next actors -> StringToJSONConverter
							//conf.recipient.tell(elegObj, self());
						}

						List<ElegabalityApprovalEntity> lstElegApp =  FetchAllAppointmentApproval();

						if(lstElegApp.size() > 0)
						{
							conf.recipient.tell(lstElegApp.get(0), self());
						}

					}
					*/
                
                conf.recipient.tell("", self());
				})


				.build();
	}


private List<ElegabalityApprovalEntity> FetchAllAppointmentApproval() {


	elegabalityApprovalService = (ElegabalityApprovalService) SpringUtil
            .getBean("ElegabalityApprovalService");

//    List<DataInboundHL7Message> lstAllHL7Message = HL7MessageService
//        .getAllPendingHL7Message(inboundHL7Message);

    List<ElegabalityApprovalEntity> elegabalityApprovalEntities = elegabalityApprovalService
            .getAllPendingElegablityAppointmentApproval();

    System.out.println(elegabalityApprovalEntities);

    return elegabalityApprovalEntities;

	}

private  List<ElegabalityApprovalEntity>  FetchAllPendingOutboundMessages() {


        elegabalityApprovalService = (ElegabalityApprovalService) SpringUtil
                .getBean("ElegabalityApprovalService");

//        List<DataInboundHL7Message> lstAllHL7Message = HL7MessageService
//            .getAllPendingHL7Message(inboundHL7Message);

        List<ElegabalityApprovalEntity> elegabalityApprovalEntities = elegabalityApprovalService
                .getAllPendingElegablityApproval();

        System.out.println(elegabalityApprovalEntities);

        return elegabalityApprovalEntities;
    }

    private void convertAndForward(httpRequstTransaction httpRequstTransObject) {
        conf.recipient.tell(httpRequstTransObject, self());
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
            System.out.println("DB APP FETCH MANAGER CALLED");
        }

    }

    public  Date convertStringToDate(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        return dateFormat.parse(dateString);
    }

}
