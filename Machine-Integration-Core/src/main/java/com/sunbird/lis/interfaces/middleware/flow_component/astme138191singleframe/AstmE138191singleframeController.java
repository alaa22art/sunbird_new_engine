package com.certacure.lis.interfaces.middleware.flow_component.astme138191singleframe;

import static com.certacure.lis.interfaces.middleware.flow_component.astme138191singleframe.AstmE138191singleframeProtocol.ACKBytes;
import static com.certacure.lis.interfaces.middleware.flow_component.astme138191singleframe.AstmE138191singleframeProtocol.ENQBytes;
import static com.certacure.lis.interfaces.middleware.flow_component.astme138191singleframe.AstmE138191singleframeProtocol.EOTBytes;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.CR;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.ETB;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.ETX;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.LF;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.STX;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.getCheckSum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.Timer;

import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.flow_component.socket.SocketProtocol.BytesMessage;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2QueryMsg;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class AstmE138191singleframeController extends FlowComponent<AstmE138191singleframeControllerConf> {

	private PartialFunction<Object, BoxedUnit> idleState;
	private PartialFunction<Object, BoxedUnit> receivingState;
	private PartialFunction<Object, BoxedUnit> sendingState;

	private final List<String> msgBeingSent = new ArrayList<>();
	private int sendingFrameNumber;
	private final StringBuilder msgBeingReceived = new StringBuilder();
	private static int counter = 0;
	Timer senderTimer;
	private String resendMessage = "";
	private List<String> lstResendMessage;

	@Override
	public PartialFunction<Object, BoxedUnit> getBehaviour() {
		return idleState;
	}

	private AstmE138191singleframeController() {
		idleState = getIdleState();
		receivingState = getReceivingState();
		sendingState = getSendingState();
	}

	private PartialFunction<Object, BoxedUnit> getIdleState() {
		return ReceiveBuilder
								.match(String.class,
										this::goToSendingState)
								.matchEquals(ENQBytes, __ -> goToReceivingState())
								.match(LIS2A2QueryMsg.class, __ ->
									{
										conf.lowLevelRecipient.tell(ENQBytes, self());
										/*
										 * String strError =
										 * "\n----------------------------------------------------------------------------------------------------------------------------------------\n"
										 * + " [Error On Mapping Test] : " + " Machine : " + machine.getName()
										 * + " Receive a Result For Order with Sample ID : "
										 * + order.container.specimenId
										 * + " And Test Code: "
										 * + analysis.code
										 * + " it is not Defined Or Not Mapped Correctly " +
										 * "\n----------------------------------------------------------------------------------------------------------------------------------------\n";
										 */
									})
								.build();
	}

	private void goToSendingState(String msgToSend) {
		log.debug("Transitioning to sending state");
		sendingFrameNumber = 1;
		msgBeingSent.clear();
		Collections.addAll(msgBeingSent, msgToSend.split("(?<=" + CR + ")"));
		if (msgBeingSent.get(0).isEmpty() && msgBeingSent.size() == 1) {
			conf.lowLevelRecipient.tell(EOTBytes, self());
			goToIdleState();
			context().become(idleState);
		} else {
			sendNextFrameInBuffer();
			context().become(sendingState);
		}
	}

	private void goToReceivingState() {
		log.debug("Transitioning to receiving state");
		msgBeingReceived.setLength(0);
		conf.lowLevelRecipient.tell(ACKBytes, self());
		context().become(receivingState);
	}

	private PartialFunction<Object, BoxedUnit> getSendingState() {
		return ReceiveBuilder
								//__ is a variable name for what has been received will not be used inside the code
								.matchEquals(ACKBytes, __ ->
									{
										if (msgBeingSent.isEmpty()) {
											conf.lowLevelRecipient.tell(EOTBytes, self());
											goToIdleState();
											resendMessage = "";
											senderTimer.stop();
											counter = 0;
										} else {
											resendMessage = "";
											senderTimer.stop();
											counter = 0;
											sendNextFrameInBuffer();
										}
									})
								.match(LIS2A2QueryMsg.class, __ ->
									{
										System.out.println("WWWWWWWWWWWWWWWWWQQQQQQQQQQQQQQQQWWWWWWWWWWWWWWW");
									})
								.matchAny(__ ->
									{
										stash();
									})
								.build();
	}

	private PartialFunction<Object, BoxedUnit> getReceivingState() {
		return ReceiveBuilder
								.matchEquals(EOTBytes, __ ->
									{
										conf.highLevelRecipient.tell(msgBeingReceived.toString(), self());
										msgBeingReceived.setLength(0);
										goToIdleState();
									})
								.match(BytesMessage.class, bytesMessage ->
									{
										List<Byte> lstOfReceivedBytes = bytesMessage.bytes;
										msgBeingReceived.append(extractPayload(bytesMessage.bytes));
										System.out.println(extractPayload(bytesMessage.bytes));
										conf.lowLevelRecipient.tell(ACKBytes, self());
										//Check if the last character is EOT to end the transmission
										if (lstOfReceivedBytes.get(lstOfReceivedBytes.size() - 1) == 4) {
											conf.highLevelRecipient.tell(msgBeingReceived.toString(), self());
											msgBeingReceived.setLength(0);
											goToIdleState();
										}
									})
								.match(LIS2A2QueryMsg.class, __ ->
									{
										System.out.println("WWWWWWWWWWWWWWWWWQQQQQQQQQQQQQQQQWWWWWWWWWWWWWWW");
									})
								.matchAny(__ ->
									{
										stash();
									})
								.build();
	}

	private void goToIdleState() {
		log.debug("Transitioning to idle state");
		unstashAll();
		context().become(idleState);
	}

	private String extractPayload(List<Byte> frameBytes) {
		int index = frameBytes.indexOf((byte) ETX);
		int indexETB = frameBytes.indexOf((byte) ETB);
		if (indexETB != -1 && index == -1) {
			index = frameBytes.indexOf((byte) ETB);
		}
		if (index != -1) {
			List<Byte> payloadBytes;//= frameBytes.subList(2, index);
			if (frameBytes.get(0) == 2) {
				payloadBytes = frameBytes.subList(2, index);
			} else {
				payloadBytes = frameBytes.subList(0, index);
			}
			byte[] bytes = new byte[payloadBytes.size()];
			IntStream.range(0, payloadBytes.size()).forEach(i -> bytes[i] = payloadBytes.get(i));
			return new String(bytes);
		} else {
			List<Byte> payloadBytes;
			if (frameBytes.get(0) == 2) {
				payloadBytes = frameBytes.subList(2, frameBytes.size());
			} else {
				payloadBytes = frameBytes;
			}
			byte[] bytes = new byte[payloadBytes.size()];
			IntStream.range(0, payloadBytes.size()).forEach(i -> bytes[i] = payloadBytes.get(i));
			return new String(bytes);
		}
	}

	private void sendAgainMessage(List<String> Message) {
		String nextFrame = "";
		for (String str : Message) {
			nextFrame += str;
		}
		msgBeingSent.clear();
		log.debug("Sending next frame in buffer - " + nextFrame);
		if (nextFrame.length() > conf.maxFrameSize) {
			log.debug("Frame length is longer than " + conf.maxFrameSize + ", splitting frame");
			String partToSend = nextFrame.substring(0, conf.maxFrameSize);
			String remainingPart = nextFrame.substring(conf.maxFrameSize);
			msgBeingSent.add(0, remainingPart);
			conf.lowLevelRecipient.tell(getFrame(partToSend, ETB), self());
		} else {
			conf.lowLevelRecipient.tell(getFrame(nextFrame, ETX), self());
		}
		sendingFrameNumber = (sendingFrameNumber + 1) % 8;
	}

	private void sendNextFrameInBuffer() {
		String nextFrame = "";
		for (String str : msgBeingSent) {
			nextFrame += str;
		}
		msgBeingSent.clear();
		log.debug("Sending next frame in buffer - " + nextFrame);
		if (nextFrame.length() > conf.maxFrameSize) {
			log.debug("Frame length is longer than " + conf.maxFrameSize + ", splitting frame");
			String partToSend = nextFrame.substring(0, conf.maxFrameSize);
			String remainingPart = nextFrame.substring(conf.maxFrameSize);
			msgBeingSent.add(0, remainingPart);
			lstResendMessage = msgBeingSent;
			conf.lowLevelRecipient.tell(getFrame(partToSend, ETB), self());
		} else {
			conf.lowLevelRecipient.tell(getFrame(nextFrame, ETX), self());
			lstResendMessage = msgBeingSent;
		}
		sendingFrameNumber = (sendingFrameNumber + 1) % 8;
		//MainClass(lstResendMessage);
	}

	private BytesMessage getFrame(String payload, char payloadDelimiter) {
		String frameForChecksum = "" + STX + sendingFrameNumber + payload + payloadDelimiter;
		String msg = frameForChecksum + getCheckSum(frameForChecksum) + CR + LF;
		return new BytesMessage(msg);
	}

	/*
	 * private void MainClass(List<String> toSendMessage) {
	 * ActionListener a = new ActionListener() {
	 * 
	 * @Override
	 * public void actionPerformed(ActionEvent e) {
	 * System.out.println("Counter = " + counter);
	 * if (++counter < 3) {
	 * sendAgainMessage(toSendMessage);
	 * } else {
	 * senderTimer.stop();
	 * goToIdleState();
	 * }
	 * }
	 * };
	 * 
	 * senderTimer = new Timer(6000, a);
	 * senderTimer.start();
	 * 
	 * }
	 */
}