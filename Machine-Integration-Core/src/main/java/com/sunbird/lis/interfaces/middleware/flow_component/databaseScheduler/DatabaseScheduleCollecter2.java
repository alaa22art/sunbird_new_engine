package com.certacure.lis.interfaces.middleware.flow_component.databaseScheduler;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import com.certacure.lis.interfaces.entities.DataInboundHL7Message;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.core.RecipientConf;
import com.certacure.lis.interfaces.middleware.interfaces.LabMessages.LabOrderMsg;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2OrderMsg;
import com.certacure.lis.interfaces.service.DataInboundHL7MessageService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.Cancellable;
import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.concurrent.duration.FiniteDuration;
import scala.runtime.BoxedUnit;


	public class DatabaseScheduleCollecter2 extends FlowComponent<RecipientConf> {
		
		private Config config = null;
		private Machine machine = null;
		private DataInboundHL7Message inboundHL7Messages = null;
		private DataInboundHL7MessageService inboundHL7MessagesService = null;
		private Cancellable scheduleDBEventTrigger= null;
		

		@Override
		protected PartialFunction<Object, BoxedUnit> getBehaviour() {
			return ReceiveBuilder
									.match(Machine.class, this::convertAndForward)
									
									.match(MessagesCollecter.class, __ ->
									{
										new MessagesCollecter();
										//throw new RuntimeException("Restart needed");
									}).build();
		}
		
		
		@Override
		public void postStop() {
			// TODO Auto-generated method stub
			
			//scheduleDBEventTrigger.cancel();
			
			super.postStop();
		}
		
		@Override
		protected void init() throws Exception {
			
			/*scheduleDBEventTrigger = context().system().scheduler().schedule(new FiniteDuration(5, TimeUnit.SECONDS),new FiniteDuration(5, TimeUnit.SECONDS),
					 self(),new MessagesCollecter(), context().dispatcher(), self());*/
			
			//unstashAll();
			// TODO Auto-generated method stub
			super.init();
			
		
		
			
			System.out.println("INITIATE CALLED");
				
		}
		
		public DatabaseScheduleCollecter2()
		{
			System.out.println("-------------STARTED----------");
			
			
		}
		
		
		private void convertAndForward(Machine machine) {
			
			
		
			
			
		
			 
			
			
			machine = this.machine;
			
			System.out.println(machine.toString());
			//check connection point connectivity 
			
			if(machine.getIsPortOpen())
			{
				
				
			}else 
			{
				System.out.println("Connection Point not opend");
			}
			
			//conf.recipient.tell(astmOrderMsg, self());
		}
		
		
		private  class MessagesCollecter implements Serializable {
			
			
			public MessagesCollecter()
			{
				
				System.out.println("scheduler CALLED : " + new DateTime().toString());
				

			}

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		}

}
