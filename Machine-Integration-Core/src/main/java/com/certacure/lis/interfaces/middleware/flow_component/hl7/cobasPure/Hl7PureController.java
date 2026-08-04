package com.certacure.lis.interfaces.middleware.flow_component.hl7.cobasPure;

import static com.certacure.lis.interfaces.middleware.flow_component.hl7.cobasPure.Hl7PureProtocol.FSBytes;
import static com.certacure.lis.interfaces.middleware.flow_component.hl7.cobasPure.Hl7PureProtocol.VTBytes;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.CR;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import com.certacure.core.common.util.SpringUtil;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.flow_component.socket.SocketProtocol.BytesMessage;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2QueryMsg;
import com.certacure.lis.interfaces.middleware.parser.hl7.HL7Parser;
import com.certacure.lis.interfaces.service.MachineService;

import akka.japi.pf.ReceiveBuilder;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v25.message.OML_O33;
import ca.uhn.hl7v2.parser.PipeParser;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class Hl7PureController extends FlowComponent<Hl7PureControllerConf> {

	public enum RETURNED_RESULT {
		SUCCUSS, FAILED
	}

	private PartialFunction<Object, BoxedUnit> idleState;
	private PartialFunction<Object, BoxedUnit> receivingState;
	private PartialFunction<Object, BoxedUnit> sendingState;

	private final List<String> msgBeingSent = new ArrayList<>();
	private int sendingFrameNumber;
	private static int counter = 0;
	private Timer senderTimer;
	private String resendMessage = "";
	private List<String> lstResendMessage;

	private HL7Parser hl7Parser;
	private PipeParser parser;

	private final StringBuilder msgBeingReceived = new StringBuilder();

	@Override
	public PartialFunction<Object, BoxedUnit> getBehaviour() {
		return idleState;
	}

	private Hl7PureController() {
		idleState = getIdleState();
		receivingState = getReceivingState();
		sendingState = getSendingState();
	}

	private void goToIdleState() {
		log.debug("Transitioning to idle state");
		unstashAll();
		context().become(idleState);
	}

	private void sendMessage(String msgToSend) {

		BytesMessage byteArray[] = { new BytesMessage(VTBytes.toString() + msgToSend + VTBytes.toString()) };

		msgBeingSent.clear();
		log.debug("Sending next frame in buffer - " + msgToSend);
		// conf.lowLevelRecipient.tell(VTBytes, self());
		conf.lowLevelRecipient.tell(new BytesMessage(VTBytes.toString() + msgToSend + FSBytes.toString() + CR), self());
		// conf.lowLevelRecipient.tell(FSBytes, self());
		// context().become(sendingState);

		// conf.lowLevelRecipient.tell(new BytesMessage("\r"), self());

	}

	private PartialFunction<Object, BoxedUnit> getSendingState() {

		return ReceiveBuilder

				.matchAny(__ -> {

					System.out.println("1++++++++++++++++++++++++++++++++++++++++++++++++++++");
					System.out.println("1++++++++++++++++++++++++++++++++++++++++++++++++++++");
					System.out.println(__.toString());
					System.out.println("1++++++++++++++++++++++++++++++++++++++++++++++++++++");
					System.out.println("1++++++++++++++++++++++++++++++++++++++++++++++++++++");
				})
				// __ is a variable name for what has been received will not be used inside the
				// code
				/*
				 * .matchEquals(ACKBytes, __ -> { if (msgBeingSent.isEmpty()) {
				 * conf.lowLevelRecipient.tell(EOTBytes, self()); goToIdleState(); resendMessage
				 * = ""; //senderTimer.stop(); counter = 0;
				 * 
				 * } else { resendMessage = ""; //senderTimer.stop(); counter = 0;
				 * sendNextFrameInBuffer(); } })
				 */
				.match(LIS2A2QueryMsg.class, __ -> {
					System.out.println("WWWWWWWWWWWWWWWWWQQQQQQQQQQQQQQQQWWWWWWWWWWWWWWW");
				}).matchAny(__ -> {
					stash();
				}).build();
	}

	private PartialFunction<Object, BoxedUnit> getIdleState() {
		return ReceiveBuilder.match(OML_O33.class, this::goToSendingState).matchAny(__ -> {
			msgBeingReceived.append(__);

			if (msgBeingReceived.indexOf(VTBytes.toString()) != -1
					&& msgBeingReceived.indexOf(FSBytes.toString()) != -1) {
				System.out.println("1++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("1++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println(msgBeingReceived.toString());
				System.out.println("1++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("1++++++++++++++++++++++++++++++++++++++++++++++++++++");

				// HapiParser hapi = new HapiParser();
				// hapi.parseMessage(msgBeingReceived.substring(1,
				// msgBeingReceived.lastIndexOf(resendMessage)-1).toString());

				String[] arrMsg = msgBeingReceived.toString().split("<CR>");

				String strASTMMsg = getASTMessage(arrMsg);

				if (strASTMMsg.contains("OBX|")) {
					List<PureResultData> lstPureResultData = processRecivedMessage(strASTMMsg);

					for (int index = 0; index < lstPureResultData.size(); index++) {
						conf.highLevelRecipient.tell(lstPureResultData.get(index), self());
					}

				} else if (strASTMMsg.contains("QPD|")) {

					conf.highLevelRecipient.tell(strASTMMsg, self());

				}

				// Message ack = hl7Message.generateACK();
				// conf.lowLevelRecipient.tell(ack, self());

				// valudateMessage(msgBeingReceived.toString());

				/*
				 * String strResult = msgBeingReceived.toString();
				 * msgBeingReceived.setLength(0); ReturnResult iResult =
				 * processRecivedMessage(strResult);
				 * 
				 * if (iResult == ReturnResult.SUCCUSS) {
				 * System.out.println("4+++++++++++++++++++++++++++++++++++++++++++++++");
				 * System.out.println("SUCCUSS");
				 * System.out.println("4+++++++++++++++++++++++++++++++++++++++++++++++"); }
				 * else {
				 * System.out.println("4+++++++++++++++++++++++++++++++++++++++++++++++");
				 * System.out.println("FAILED");
				 * System.out.println("4+++++++++++++++++++++++++++++++++++++++++++++++");
				 * 
				 * }
				 */

				msgBeingReceived.setLength(0);

				context().become(idleState);

			}
		})

				/*
				 * .match(OML_O33.class, this::goToSendingState) //.match(BytesMessage.class,
				 * this::goToSendingState) .matchAny(__ -> {
				 * System.out.println("Before Go To Recive State");
				 * System.out.println(__.toString());
				 * 
				 * if(__.toString().indexOf(VTBytes.toString()) !=-1) { goToReceivingState();
				 * //getReceivingState(); }else { goToSendingState(__.toString()); }
				 * 
				 * 
				 * 
				 * }) /*.match(String.class,this::goToSendingState) .matchEquals(, __ ->
				 * goToReceivingState()) .match(LIS2A2QueryMsg.class, __ -> {
				 * conf.lowLevelRecipient.tell(ENQBytes, self());
				 * 
				 * })
				 */

				.build();
	}

	private void goToSendingState(OML_O33 omlMessage) {
		String msgToSend = "";
		try {
			msgToSend = omlMessage.encode();
		} catch (HL7Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.debug("Transitioning to sending state");

		// msgBeingSent.clear();
		// conf.lowLevelRecipient.tell(ENQBytes, self());
		// Collections.addAll(msgBeingSent, msgToSend.split("(?<=" + CR + ")"));
		// if (msgBeingSent.get(0).isEmpty() && msgBeingSent.size() == 1) {
		// conf.lowLevelRecipient.tell(EOTBytes, self());
		// goToIdleState();
		// context().become(idleState);
		// } else {
		sendMessage(msgToSend);
		// context().become(sendingState);
		// }
	}

	private List<PureResultData> processRecivedMessage(String inputMsg) {

		String[] arrData;
		PureResultData pureResultdObject;
		List<PureResultData> pureResultdList = new ArrayList<PureResultData>();

		String hl7MsgAsString = inputMsg;
		String strBarcode = getBarcodeValue(inputMsg);
		String[] arrMessages = hl7MsgAsString.split("(?=(OBR)(\\|)(\\d))");
		String[] strOrderFields = null;
		String[] strResultFields = null;
		String[] strResultComponent = null;

		for (int index = 1; index < arrMessages.length; index++) {
			String[] strFrames = arrMessages[index].split("(?=(OBX)(\\|)(\\d))");

			if (arrMessages[index].startsWith("OBR|")) {
				strOrderFields = strFrames[0].split("(?:(\\|))");
				strResultFields = strFrames[1].split("(?:(\\|))");
				strResultComponent = strResultFields[3].split("(?:(\\^))");
				strBarcode = strOrderFields[2];// .split("(?:(\\&))")[0];

			}

			pureResultdObject = new PureResultData(strBarcode, strResultComponent[0], strResultFields[5], inputMsg);
			pureResultdList.add(pureResultdObject);
		}

		return pureResultdList;
	}

	private String getBarcodeValue(String inputMsg) {
		String[] strData = inputMsg.split("\\r\\n");
		String[] strFiledsData;
		String strBarcode = "-----";
		for (int index = 0; index < strData.length; index++) {
			if (strData[index].startsWith("OBR")) {
				strFiledsData = strData[index].split("\\|");
				strBarcode = strFiledsData[2].split("&")[0];
				break;
			}

			if (strData[index].startsWith("Q|")) {
				strFiledsData = strData[index].split("\\|");
				strBarcode = strFiledsData[2].split("&")[0];
				break;
			}

		}

		return strBarcode;
	}

	public Machine getMachineInfoByPath() {
		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		Machine machine = machineService.getMachineByActorPath(getContext().parent().toString());
		return machine;
	}

	private PartialFunction<Object, BoxedUnit> getReceivingState() {
		return ReceiveBuilder.matchAny(__ -> {
			msgBeingReceived.append(__);

			if (msgBeingReceived.indexOf(VTBytes.toString()) != -1
					&& msgBeingReceived.indexOf(FSBytes.toString()) != -1) {
				System.out.println("1++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("1++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println(msgBeingReceived.toString());
				System.out.println("1++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println("1++++++++++++++++++++++++++++++++++++++++++++++++++++");

				String[] arrMsg = msgBeingReceived.toString().split("<CR>");

				String strASTMMsg = getASTMessage(arrMsg);

				if (strASTMMsg.contains("R|")) {
					List<PureResultData> lstPureResultData = processRecivedMessage(strASTMMsg);

					for (int index = 0; index < lstPureResultData.size(); index++) {
						conf.highLevelRecipient.tell(lstPureResultData.get(index), self());
					}

				} else if (strASTMMsg.contains("Q|")) {

					conf.highLevelRecipient.tell(strASTMMsg, self());

				}

				// Message ack = hl7Message.generateACK();
				// conf.lowLevelRecipient.tell(ack, self());

				// valudateMessage(msgBeingReceived.toString());

				/*
				 * String strResult = msgBeingReceived.toString();
				 * msgBeingReceived.setLength(0); ReturnResult iResult =
				 * processRecivedMessage(strResult);
				 * 
				 * if (iResult == ReturnResult.SUCCUSS) {
				 * System.out.println("4+++++++++++++++++++++++++++++++++++++++++++++++");
				 * System.out.println("SUCCUSS");
				 * System.out.println("4+++++++++++++++++++++++++++++++++++++++++++++++"); }
				 * else {
				 * System.out.println("4+++++++++++++++++++++++++++++++++++++++++++++++");
				 * System.out.println("FAILED");
				 * System.out.println("4+++++++++++++++++++++++++++++++++++++++++++++++");
				 * 
				 * }
				 */

				msgBeingReceived.setLength(0);

				context().become(idleState);

			}
		})

				.build();

	}

	private String getASTMessage(String[] arrMsg) {

		String strASTMMessage = "";

		for (int index = 0; index < arrMsg.length; index++) {
			if (arrMsg[index].contains("MSH")) {
				strASTMMessage += arrMsg[index];// .replace("MSH", "H");
				strASTMMessage += "\r";

			} else if (arrMsg[index].contains("PID")) {
				strASTMMessage += arrMsg[index];// .replace("PID", "P");
				strASTMMessage += "\r";

			} else if (arrMsg[index].contains("RCP")) {
				strASTMMessage += arrMsg[index];// .replace("RCP", "L");
				strASTMMessage += "\r";

			} else if (arrMsg[index].contains("QPD")) {
				strASTMMessage += arrMsg[index];// .replace("QPD", "Q");
				strASTMMessage += "\r";

			} else if (arrMsg[index].contains("OBR")) {
				strASTMMessage += arrMsg[index];// .replace("OBR", "O");
				strASTMMessage += "\r";

			} else if (arrMsg[index].contains("SPM")) {
				strASTMMessage += arrMsg[index];// .replace("SPM", "SPM");
				strASTMMessage += "\r";

			} else if (arrMsg[index].contains("OBX")) {
				strASTMMessage += arrMsg[index];// .replace("OBX", "R");
				strASTMMessage += "\r";
			}
		}

		return strASTMMessage;
	}
}