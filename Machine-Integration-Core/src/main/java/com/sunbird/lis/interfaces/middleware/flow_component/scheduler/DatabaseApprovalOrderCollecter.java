package com.certacure.lis.interfaces.middleware.flow_component.scheduler;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.certacure.core.common.util.SpringUtil;
import com.certacure.lis.interfaces.entities.DataInboundHL7Message;
import com.certacure.lis.interfaces.entities.ElegabalityApprovalOrderEntity;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.entities.PostDetailFinancialTransaction;
import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.core.RecipientConf;
import com.certacure.lis.interfaces.middleware.flow_component.lab_http.httpRequstTransaction;
import com.certacure.lis.interfaces.service.DataInboundHL7MessageService;
import com.certacure.lis.interfaces.service.ElegabalityApprovalOrderService;
import com.certacure.lis.interfaces.service.MachineService;
import com.certacure.lis.interfaces.service.PostDetailFinancialTransactionService;
import com.typesafe.config.Config;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.concurrent.duration.FiniteDuration;
import scala.runtime.BoxedUnit;

public class DatabaseApprovalOrderCollecter extends FlowComponent<RecipientConf> {

    private Config config= null;

    private MachineService machineService;
    private Machine machine;
    private DataInboundHL7Message inboundHL7Message;
    private DataInboundHL7MessageService HL7MessageService;
    private ElegabalityApprovalOrderService elegabalityApprovalOrderService ;
    private PostDetailFinancialTransaction postDetailFinancialTransaction;

    private PostDetailFinancialTransactionService postDetailFinancialTransactionService;

    @Override
    protected void init() {
        System.out.println("DB ORDER COLLECTER BUILD");

        postDetailFinancialTransactionService = (PostDetailFinancialTransactionService) SpringUtil
    			.getBean("PostDetailFinancialTransactionService");

        context().system().scheduler().schedule(
            new FiniteDuration(10, TimeUnit.SECONDS), new FiniteDuration(10, TimeUnit.SECONDS),
            self(),
            new DBFetchManager(), context().dispatcher(), self());


        machine = getMachine();
    }

    @Override
    protected PartialFunction<Object, BoxedUnit> getBehaviour() {
        return ReceiveBuilder
            .match(String.class, this::convertAndForward)

            .match(DBFetchManager.class, __ -> {
                System.out.println("DB ORDER COLLECTER CALLED AGINE");
                List<ElegabalityApprovalOrderEntity> listAllOrderPendingOrder = null;
                List<PostDetailFinancialTransaction> listPostDetailsTrans  = null;


                // throw new RuntimeException("Restart needed");

             machine = getMachineInfoByPath();


             if (machine.getIsActive() && machine.getConnected())
					{
						listAllOrderPendingOrder = FetchAllPendingOutboundMessages();
						ElegabalityApprovalOrderEntity elegOrderObj = null;


						//listAllPendingOrder = new ArrayList<ElegabalityApprovalEntity>();

						for (int index = 0 ;( index < 100) && (index < listAllOrderPendingOrder.size()); index++) {

							elegOrderObj = listAllOrderPendingOrder.get(index);

							if(elegOrderObj.getOrderId() != null && elegOrderObj.getOrderActionId() != null)
							{
								listPostDetailsTrans =
										postDetailFinancialTransactionService
										.getOrdersByCertaOrderIdAndActionItem( elegOrderObj.getOrderId().toString(),
												elegOrderObj.getOrderActionId().toString());
							}else {

								elegOrderObj.setIsFailed(true);
								elegOrderObj.setIsSuccess(false);

								elegOrderObj.setNotes(" Order Not Found !!! , Check Order Details : Order ID [" + elegOrderObj.getOrderId().toString() + "]" + "," + "[" + elegOrderObj.getOrderActionId().toString() +"]");

								elegabalityApprovalOrderService.save(elegOrderObj);

								continue;

							}




							if (listPostDetailsTrans.size() > 0)
							{


								EligibilityOrderMapper  eligibilityOrderMapperObject = new EligibilityOrderMapper();

								eligibilityOrderMapperObject.eligibilityOrderId = elegOrderObj.getRid();

								eligibilityOrderMapperObject.nationalCode = elegOrderObj.getPatientInfoNationalCode();
								eligibilityOrderMapperObject.patientCode = elegOrderObj.getPatientInfoCode();
								eligibilityOrderMapperObject.patientClass = elegOrderObj.getVisitInfoPatientType().toString();
								eligibilityOrderMapperObject.attendingDoctorId = listPostDetailsTrans.get(0).getAttendingDoctorID();
								eligibilityOrderMapperObject.sectionCode = elegOrderObj.getOrderSectionCode();
								eligibilityOrderMapperObject.isEligibilOrder = elegOrderObj.getIsEligible();
								eligibilityOrderMapperObject.transactionType = "ORDER";
								eligibilityOrderMapperObject.itemChargeCode = listPostDetailsTrans.get(0).getOrderByID();
								eligibilityOrderMapperObject.isAppointment = false;
								eligibilityOrderMapperObject.ipPrinterAddress =  elegOrderObj.getIpPrinterAddress();

								if (listPostDetailsTrans.get(0).getServSection().equals("RA"))
								{
									eligibilityOrderMapperObject.vistaOrderId = listPostDetailsTrans.get(0).getAppointmentID();

								}else
								{
									eligibilityOrderMapperObject.vistaOrderId = listPostDetailsTrans.get(0).getVistaOrderID();
								}


								conf.recipient.tell(eligibilityOrderMapperObject, self());
							}else

							{

								elegOrderObj.setIsFailed(true);
								elegOrderObj.setIsSuccess(false);

								elegOrderObj.setNotes("Valudation Error !!! ,  Certa Order Id : "
										+ elegOrderObj.getOrderId().toString() +
												", " + " And Certa Action Id : " + elegOrderObj.getOrderActionId().toString() + "Not Exist");

								elegabalityApprovalOrderService.save(elegOrderObj);



							}



							// send message to next actors -> StringToJSONConverter
							//conf.recipient.tell(elegObj, self());
						}


					}
				})


				.build();
	}

    public Machine getMachineInfoByPath() {
        MachineService machineService= (MachineService) SpringUtil.getBean("MachineService");
        Machine machine= machineService.getMachineByActorPath(getContext().parent().toString());
        return machine;
    }



private  List<ElegabalityApprovalOrderEntity>  FetchAllPendingOutboundMessages() {


        elegabalityApprovalOrderService = (ElegabalityApprovalOrderService) SpringUtil
                .getBean("ElegabalityApprovalOrderService");

//        List<DataInboundHL7Message> lstAllHL7Message = HL7MessageService
//            .getAllPendingHL7Message(inboundHL7Message);

        List<ElegabalityApprovalOrderEntity> elegabalityApprovalOrderList = elegabalityApprovalOrderService.getAllPendingElegablityApproval();

        System.out.println(elegabalityApprovalOrderList);

        return elegabalityApprovalOrderList;
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
            System.out.println("DB FETCH MANAGER CALLED");
        }

    }

    public  Date convertStringToDate(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        return dateFormat.parse(dateString);
    }

}
