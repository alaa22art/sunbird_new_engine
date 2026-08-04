package com.certacure.lis.interfaces.middleware.driver;

import java.util.function.BiFunction;

import com.certacure.lis.interfaces.middleware.core.ConfMsg;
import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.core.RecipientConf;
import com.certacure.lis.interfaces.middleware.flow_component.astm_to_mylis.LIS2A2ToLabConverter;
import com.certacure.lis.interfaces.middleware.flow_component.astm_to_mylis.LabToLIS2A2Converter;
import com.certacure.lis.interfaces.middleware.flow_component.json.JSONToLIS2A2Converter;
import com.certacure.lis.interfaces.middleware.flow_component.json.LIS2A2ToJSONConverter;
import com.certacure.lis.interfaces.middleware.flow_component.astme138102.AstmE138102Controller;
import com.certacure.lis.interfaces.middleware.flow_component.astme138102.AstmE138102ControllerConf;
import com.certacure.lis.interfaces.middleware.flow_component.astme138191.AstmE138191Controller;
import com.certacure.lis.interfaces.middleware.flow_component.astme138191.AstmE138191ControllerConf;
import com.certacure.lis.interfaces.middleware.flow_component.astme138191C6000.AstmE138191C6000Controller;
import com.certacure.lis.interfaces.middleware.flow_component.astme138191C6000.AstmE138191C6000ControllerConf;
import com.certacure.lis.interfaces.middleware.flow_component.astme138191E411.AstmE138191E411Controller;
import com.certacure.lis.interfaces.middleware.flow_component.astme138191E411.AstmE138191E411ControllerConf;
import com.certacure.lis.interfaces.middleware.flow_component.astme138191a.AstmE138191AController;
import com.certacure.lis.interfaces.middleware.flow_component.astme138191a.AstmE138191AControllerConf;
import com.certacure.lis.interfaces.middleware.flow_component.astme138194.AstmE138194Controller;
import com.certacure.lis.interfaces.middleware.flow_component.astme138194.AstmE138194ControllerConf;
import com.certacure.lis.interfaces.middleware.flow_component.astme138194AlinityNew.AstmE138194AbbottAlinityController;
import com.certacure.lis.interfaces.middleware.flow_component.astme138194AlinityNew.*;
import com.certacure.lis.interfaces.middleware.flow_component.CS_2000.*;
import com.certacure.lis.interfaces.middleware.flow_component.DBToJSONOverHttp.DBToJSONOverHttpController;
import com.certacure.lis.interfaces.middleware.flow_component.DBToJSONOverHttp.DBToJSONOverHttpControllerConf;
import com.certacure.lis.interfaces.middleware.flow_component.astme138194BeckmanAccess2.AstmE138194BeckmanAccess2Controller;
import com.certacure.lis.interfaces.middleware.flow_component.astme138194BeckmanAccess2.AstmE138194BeckmanAccess2ControllerConf;
import com.certacure.lis.interfaces.middleware.flow_component.astme138194C111.AstmE138194C111Controller;
import com.certacure.lis.interfaces.middleware.flow_component.astme138194C111.AstmE138194C111ControllerConf;
import com.certacure.lis.interfaces.middleware.flow_component.astme138194Emerald22.AstmE138194Emerald22Controller;
import com.certacure.lis.interfaces.middleware.flow_component.astme138194Emerald22.AstmE138194Emerald22ControllerConf;
import com.certacure.lis.interfaces.middleware.flow_component.astme138194archi.AstmE138194ArchiController;
import com.certacure.lis.interfaces.middleware.flow_component.astme138194archi.AstmE138194ArchiControllerConf;
import com.certacure.lis.interfaces.middleware.flow_component.astme138194ruby.AstmE138194RubyController;
import com.certacure.lis.interfaces.middleware.flow_component.astme138194ruby.AstmE138194RubyControllerConf;
import com.certacure.lis.interfaces.middleware.flow_component.astme138195.AstmE138195Controller;
import com.certacure.lis.interfaces.middleware.flow_component.astme138195.AstmE138195ControllerConf;
import com.certacure.lis.interfaces.middleware.flow_component.astme138195ACCESS2.AstmE138195access2Controller;
import com.certacure.lis.interfaces.middleware.flow_component.astme138195ACCESS2.AstmE138195access2ControllerConf;
import com.certacure.lis.interfaces.middleware.flow_component.astme138195C311.AstmE138195C311Controller;
import com.certacure.lis.interfaces.middleware.flow_component.astme138195C311.AstmE138195C311ControllerConf;
import com.certacure.lis.interfaces.middleware.flow_component.astme138195C6000.AstmE138195C6000Controller;
import com.certacure.lis.interfaces.middleware.flow_component.astme138195C6000.AstmE138195C6000ControllerConf;
import com.certacure.lis.interfaces.middleware.flow_component.astme138195Liaison.AstmE138195LiaisonController;
import com.certacure.lis.interfaces.middleware.flow_component.astme138195Liaison.AstmE138195LiaisonControllerConf;
import com.certacure.lis.interfaces.middleware.flow_component.astme138197.AstmE138197Controller;
import com.certacure.lis.interfaces.middleware.flow_component.astme138197.AstmE138197ControllerConf;
import com.certacure.lis.interfaces.middleware.flow_component.au480.AU480Controller;
import com.certacure.lis.interfaces.middleware.flow_component.au480.AU480ControllerConf;
import com.certacure.lis.interfaces.middleware.flow_component.hl7.cobasPure.Hl7PureController;
import com.certacure.lis.interfaces.middleware.flow_component.hl7.cobasPure.Hl7PureControllerConf;
import com.certacure.lis.interfaces.middleware.flow_component.hl7.cobasPro.*;
import com.certacure.lis.interfaces.middleware.flow_component.hl724ServerOverTcp.Hl724ServerOverTcpController;
import com.certacure.lis.interfaces.middleware.flow_component.hl724ServerOverTcp.Hl724ServerOverTcpControllerConf;

import com.certacure.lis.interfaces.middleware.flow_component.hl725ServerOverTcp.Hl725ServerOverTcpController;
import com.certacure.lis.interfaces.middleware.flow_component.hl725ServerOverTcp.Hl725ServerOverTcpControllerConf;

import com.certacure.lis.interfaces.middleware.flow_component.hl7_to_string.*;
import com.certacure.lis.interfaces.middleware.flow_component.astm_to_string.*;
import com.certacure.lis.interfaces.middleware.flow_component.hl724ClientOverTcp.Hl724ClientOverTcpController;
import com.certacure.lis.interfaces.middleware.flow_component.hl724ClientOverTcp.Hl724ClientOverTcpControllerConf;

import com.certacure.lis.interfaces.middleware.flow_component.hl724ServerOverTcpToDBQueue.Hl724ServerOverTcpToDBQueueControllerConf;
import com.certacure.lis.interfaces.middleware.flow_component.hl724ServerOverTcpToDBQueue.Hl724ServerOverTcpToDBQueueController;


import com.certacure.lis.interfaces.middleware.flow_component.EmailNotificationSenderDriver.*;
import com.certacure.lis.interfaces.middleware.flow_component.LabOrderCollecterScheduler.*;

import com.certacure.lis.interfaces.middleware.flow_component.JSONToDBQueue.JSONToDBQueueConverter;
import com.certacure.lis.interfaces.middleware.flow_component.scheduler.DatabaseApprovalOrderCollecter;
import com.certacure.lis.interfaces.middleware.flow_component.scheduler.DBScheduleAppointmentCollector;
import com.certacure.lis.interfaces.middleware.flow_component.scheduler.DatabaseOutboundEmailCollecter;
import com.certacure.lis.interfaces.middleware.flow_component.scheduler.APIToDBCollecter;

import com.certacure.lis.interfaces.middleware.flow_component.hl7.cobasPure.*;
import com.certacure.lis.interfaces.middleware.flow_component.hl7.attalica.*;
import com.certacure.lis.interfaces.middleware.flow_component.JSONToRCM.*;
import com.certacure.lis.interfaces.middleware.flow_component.sysmex_suit.*;
import com.certacure.lis.interfaces.middleware.flow_component.hl7_23_swelab_bm_500.*;
import com.certacure.lis.interfaces.middleware.flow_component.hl7_23_vidas.*;
import com.certacure.lis.interfaces.middleware.flow_component.hl7_231_dymind_dh_76.*;

import com.certacure.lis.interfaces.middleware.flow_component.hl724ClientOverTcp.InboundMSGToHL7Converter;

import com.certacure.lis.interfaces.middleware.flow_component.kx21n.Kx21nController;
import com.certacure.lis.interfaces.middleware.flow_component.kx21n.Kx21nControllerConf;
import com.certacure.lis.interfaces.middleware.flow_component.lab_http.HttpSender;
import com.certacure.lis.interfaces.middleware.flow_component.lab_http.HttpSenderConf;
import com.certacure.lis.interfaces.middleware.flow_component.lab_http.LabHttpClient;
import com.certacure.lis.interfaces.middleware.flow_component.lab_http.LabHttpClientConf;
import com.certacure.lis.interfaces.middleware.flow_component.scheduler.DatabaseApprovalCollecter;
import com.certacure.lis.interfaces.middleware.flow_component.scheduler.DatabaseScheduleJSONCollecter;
import com.certacure.lis.interfaces.middleware.flow_component.socket.SocketClient;
import com.certacure.lis.interfaces.middleware.flow_component.socket.SocketClientConf;
import com.certacure.lis.interfaces.middleware.flow_component.socket.SocketServer;
import com.certacure.lis.interfaces.middleware.flow_component.socket.SocketServerConf;
import com.typesafe.config.Config;

import akka.actor.ActorContext;

public enum ComponentType {

	SocketClient(SocketClient.class, SocketClientConf::create),
	SocketServer(SocketServer.class, SocketServerConf::create),
	AstmE138191E411Controller(AstmE138191E411Controller.class, AstmE138191E411ControllerConf::create),
	AstmE138191Controller(AstmE138191Controller.class, AstmE138191ControllerConf::create),
	AstmE138195C6000Controller(AstmE138195C6000Controller.class, AstmE138195C6000ControllerConf::create),
	AstmE138195Controller(AstmE138195Controller.class, AstmE138195ControllerConf::create),
	AstmE138195AController(AstmE138195Controller.class, AstmE138195ControllerConf::create),
	AstmE138195access2Controller(AstmE138195access2Controller.class, AstmE138195access2ControllerConf::create),
	AstmE138195C311Controller(AstmE138195C311Controller.class, AstmE138195C311ControllerConf::create),
	AstmE138195LiaisonController(AstmE138195LiaisonController.class, AstmE138195LiaisonControllerConf::create),
	AstmE138191C6000Controller(AstmE138191C6000Controller.class, AstmE138191C6000ControllerConf::create),
	AstmE138191AController(AstmE138191AController.class, AstmE138191AControllerConf::create),
	AstmE138197Controller(AstmE138197Controller.class, AstmE138197ControllerConf::create),
	AstmE138194Controller(AstmE138194Controller.class, AstmE138194ControllerConf::create),
	AstmE138194ArchiController(AstmE138194ArchiController.class, AstmE138194ArchiControllerConf::create),
	AstmE138194AbbottAlinityController(AstmE138194AbbottAlinityController.class, AstmE138194AbbottAlinityControllerConf::create),
	AstmE138194BeckmanAccess2Controller(AstmE138194BeckmanAccess2Controller.class, AstmE138194BeckmanAccess2ControllerConf::create),
	AstmE138194Emerald22Controller(AstmE138194Emerald22Controller.class, AstmE138194Emerald22ControllerConf::create),
	AstmE138194C111Controller(AstmE138194C111Controller.class, AstmE138194C111ControllerConf::create),
	AstmE138194RubyController(AstmE138194RubyController.class, AstmE138194RubyControllerConf::create),
	AstmE138102Controller(AstmE138102Controller.class, AstmE138102ControllerConf::create),
	Kx21nController(Kx21nController.class, Kx21nControllerConf::create),
	CS2000Controller(CS2000Controller.class, CS2000ControllerConf::create),
	HL724ServerOverTcpController(Hl724ServerOverTcpController.class, Hl724ServerOverTcpControllerConf::create),
	HL725ServerOverTcpController(Hl725ServerOverTcpController.class, Hl725ServerOverTcpControllerConf::create),
	Hl724ServerOverTcpToDBQueueController(Hl724ServerOverTcpToDBQueueController.class, Hl724ServerOverTcpToDBQueueControllerConf::create),
	HL724ClientOverTcpController(Hl724ClientOverTcpController.class, Hl724ClientOverTcpControllerConf::create),
	Hl7PureController(Hl7PureController.class, Hl7PureControllerConf::create),
	Hl7SiemensAttalicaController(Hl7SiemensAttalicaController.class, Hl7SiemensAttalicaControllerConf::create),
	Hl7RocheCobasProController(Hl7RocheCobasProController.class, Hl7RocheCobasProControllerConf::create),
	
	AU480Controller(AU480Controller.class, AU480ControllerConf::create),
	DBToJSONOverHttpController(DBToJSONOverHttpController.class, DBToJSONOverHttpControllerConf::create),
	DatabaseApprovalCollecter(DatabaseApprovalCollecter.class, RecipientConf::create),
	AppointmentJobController(AppointmentJobController.class ,AppointmentJobControllerConf::create),
	
	
	//
	LabOrderController(LabOrderController.class, LabOrderControllerConf::create),
	DatabaseOutboundEmailSenderController(DatabaseOutboundEmailSenderController.class, DatabaseOutboundEmailSenderControllerConf::create),

	// Sysmex Suit
	SysmexSuitAstmE138191Controller(SysmexSuitAstmE138191Controller.class, SysmexSuitAstmE138191ControllerConf::create),
	SysmexSuitLabToLIS2A2Converter(SysmexSuitLabToLIS2A2Converter.class, RecipientConf::create),
	SysmexSuitLIS2A2ToLabConverter(SysmexSuitLIS2A2ToLabConverter.class, RecipientConf::create),
	SysmexSuitLIS2A2ToStringConverter(SysmexSuitLIS2A2ToStringConverter.class, RecipientConf::create),
	SysmexSuitStringToLIS2A2Converter(SysmexSuitStringToLIS2A2Converter.class, RecipientConf::create),

	// HL7231DYMINDDH76
	HL7231DymindDH76Controller(HL7231DymindDH76Controller.class, HL7231DymindDH76ControllerConf::create),
	HL7231DymindDH76LabToLIS2A2Converter(HL7231DymindDH76LabToLIS2A2Converter.class, RecipientConf::create),
	HL7231DymindDH76LIS2A2ToLabConverter(HL7231DymindDH76LIS2A2ToLabConverter.class, RecipientConf::create),
	HL7231DymindDH76LIS2A2ToStringConverter(HL7231DymindDH76LIS2A2ToStringConverter.class, RecipientConf::create),
	HL7231DymindDH76StringToLIS2A2Converter(HL7231DymindDH76StringToLIS2A2Converter.class, RecipientConf::create),
	

	// HL723SwelabBM500
	HL723SwelabBM500Controller(HL723SwelabBM500Controller.class, HL723SwelabBM500ControllerConf::create),
	HL723SwelabBM500LabToLIS2A2Converter(HL723SwelabBM500LabToLIS2A2Converter.class, RecipientConf::create),
	HL723SwelabBM500LIS2A2ToLabConverter(HL723SwelabBM500LIS2A2ToLabConverter.class, RecipientConf::create),
	HL723SwelabBM500LIS2A2ToStringConverter(HL723SwelabBM500LIS2A2ToStringConverter.class, RecipientConf::create),
	HL723SwelabBM500StringToLIS2A2Converter(HL723SwelabBM500StringToLIS2A2Converter.class, RecipientConf::create),
	
	// HL723Vidas
	HL723VidasController(HL723VidasController.class, HL723VidasControllerConf::create),
	HL723VidasLabToLIS2A2Converter(HL723VidassLabToLIS2A2Converter.class, RecipientConf::create),
	HL723VidasLIS2A2ToLabConverter(HL723VidasLIS2A2ToLabConverter.class, RecipientConf::create),
	HL723VidasLIS2A2ToStringConverter(HL723VidasLIS2A2ToStringConverter.class, RecipientConf::create),
	HL723VidasStringToLIS2A2Converter(HL723VidasStringToLIS2A2Converter.class, RecipientConf::create),
	
	JSONToLIS2A2Converter(JSONToLIS2A2Converter.class, RecipientConf::create),
	LIS2A2ToJSONConverter(LIS2A2ToJSONConverter.class, RecipientConf::create),
	LIS2A2ToLabConverter(LIS2A2ToLabConverter.class, RecipientConf::create),
	LabToLIS2A2Converter(LabToLIS2A2Converter.class, RecipientConf::create),
	LIS2A2ToStringConverter(LIS2A2ToStringConverter.class, RecipientConf::create),
	LIS2A2ToAstmStringConverter(LIS2A2ToAstmStringConverter.class, RecipientConf::create),
	StringToLIS2A2Converter(StringToLIS2A2Converter.class, RecipientConf::create),
	StringHL723ToLIS2A2Converter(StringHL723ToLIS2A2Converter.class, RecipientConf::create),
	StringAstmToLIS2A2Converter(StringAstmToLIS2A2Converter.class, RecipientConf::create),
	StringAstmAnalityToLIS2A2Converter(StringAstmAnalityToLIS2A2Converter.class, RecipientConf::create),
	HL725StringToLIS2A2Converter(HL725StringToLIS2A2Converter.class, RecipientConf::create),
	StringHL7251ToLIS2A2Converter(StringHL7251ToLIS2A2Converter.class, RecipientConf::create),
	StringToJSONConverter(StringToJSONConverter.class, RecipientConf::create),
	DatabaseScheduleJSONCollecter(DatabaseScheduleJSONCollecter.class, RecipientConf::create),
	LabHttpClient(LabHttpClient.class, LabHttpClientConf::create),
    HttpSender(HttpSender.class, HttpSenderConf::create),
    InboundMSGToHL7Converter(InboundMSGToHL7Converter.class, RecipientConf::create),
    JSONToDBQueueConverter(JSONToDBQueueConverter.class, RecipientConf::create),
	DatabaseApprovalOrderCollecter(DatabaseApprovalOrderCollecter.class ,RecipientConf::create),
	DBScheduleAppointmentCollector(DBScheduleAppointmentCollector.class ,RecipientConf::create),
	//DatabaseOutboundEmailSenderController(DatabaseOutboundEmailSenderController.class ,RecipientConf::create);

	DatabaseOutboundEmailCollecter(DatabaseOutboundEmailCollecter.class ,RecipientConf::create),
	APIToDBCollecter(APIToDBCollecter.class,RecipientConf::create);
	
	

	public final Class<?> klass;
	public final BiFunction<Config, ActorContext, ConfMsg> configFuction;

	ComponentType(Class<? extends FlowComponent<?>> klass,
			BiFunction<Config, ActorContext, ConfMsg> configFuction) {
		this.klass = klass;
		this.configFuction = configFuction;
	}
}
