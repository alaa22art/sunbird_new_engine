package com.certacure.lis.interfaces.middleware.flow_component.astme138102;

import static com.certacure.lis.interfaces.middleware.flow_component.astme138102.AstmE138102Protocol.ACKBytes;
import static com.certacure.lis.interfaces.middleware.flow_component.astme138102.AstmE138102Protocol.ENQBytes;
import static com.certacure.lis.interfaces.middleware.flow_component.astme138102.AstmE138102Protocol.EOTBytes;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.CR;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.ETB;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.ETX;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.LF;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.STX;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.getCheckSum;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.Timer;

import org.slf4j.MDC;

import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.flow_component.socket.SocketProtocol.BytesMessage;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2QueryMsg;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class AstmE138102Controller extends FlowComponent<AstmE138102ControllerConf> {

	private PartialFunction<Object, BoxedUnit> idleState;
	private PartialFunction<Object, BoxedUnit> receivingState;
	private PartialFunction<Object, BoxedUnit> sendingState;

	private final List<String> msgBeingSent = new ArrayList<>();
	private int sendingFrameNumber;
	private final StringBuilder msgBeingReceived = new StringBuilder();
	private static int counter = 0;
	Timer senderTimer;
	private String resendMessage = "";

	@Override
	public PartialFunction<Object, BoxedUnit> getBehaviour() {
		return idleState;
	}

	private AstmE138102Controller() {
		idleState = getIdleState();
		receivingState = getReceivingState();
		sendingState = getSendingState();
	}

	private PartialFunction<Object, BoxedUnit> getIdleState() {
		return ReceiveBuilder.match(String.class, s -> {
			goToSendingState(s);
		})
				// this::goToSendingState)
				.matchEquals(ENQBytes, __ -> {
					goToReceivingState();
				}).matchEquals(EOTBytes, __ -> {
					System.out.println("ERRORRRRRRRRRRRRRRRRRRRRRRRRRRR");
				}).match(LIS2A2QueryMsg.class, __ -> {
					conf.lowLevelRecipient.tell(ENQBytes, self());
					System.out.println("WWWWWWWWWWWWWWWWWQQQQQQQQQQQQQQQQWWWWWWWWWWWWWWW");
				}).build();
	}

	private void goToSendingState(String msgToSend) {
		log.debug("Transitioning to sending state");
		sendingFrameNumber = 1;
		msgBeingSent.clear();
		Collections.addAll(msgBeingSent, msgToSend.split("(?<=" + CR + ")"));
		if (msgBeingSent.isEmpty()) {
			conf.lowLevelRecipient.tell(EOTBytes, self());
			goToIdleState();
			senderTimer.stop();
		} else {
			sendNextFrameInBuffer();
		}
		context().become(sendingState);
	}

	private void goToReceivingState() {
		log.debug("Transitioning to receiving state");
		msgBeingReceived.setLength(0);
		conf.lowLevelRecipient.tell(ACKBytes, self());
		context().become(receivingState);
	}

	private PartialFunction<Object, BoxedUnit> getSendingState() {
		return ReceiveBuilder.matchEquals(ACKBytes, __ -> {
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
		}).match(LIS2A2QueryMsg.class, __ -> {
			System.out.println("WWWWWWWWWWWWWWWWWQQQQQQQQQQQQQQQQWWWWWWWWWWWWWWW");
		}).matchAny(__ -> {
			stash();
		}).build();
	}

	private PartialFunction<Object, BoxedUnit> getReceivingState() {
		return ReceiveBuilder.matchEquals(EOTBytes, __ -> {

			System.out
					.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + "\\n");

			System.out.println(msgBeingReceived.toString() + "\\n");

			System.out
					.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" + "\\n");
			conf.highLevelRecipient.tell(msgBeingReceived.toString(), self());
			msgBeingReceived.setLength(0);
			goToIdleState();
		}).match(BytesMessage.class, bytesMessage -> {
			System.out.println(extractPayload(bytesMessage.bytes));
			msgBeingReceived.append(extractPayload(bytesMessage.bytes));
			if (containsETXorETB(bytesMessage.bytes) == true) {
				conf.lowLevelRecipient.tell(ACKBytes, self());
			}
		}).match(LIS2A2QueryMsg.class, __ -> {
			System.out.println("WWWWWWWWWWWWWWWWWQQQQQQQQQQQQQQQQWWWWWWWWWWWWWWW");
		}).matchAny(__ -> {
			stash();
		}).build();
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
			List<Byte> payloadBytes;// = frameBytes.subList(2, index);
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

	private boolean containsETXorETB(List<Byte> frameBytes) {
		int index = frameBytes.indexOf((byte) ETX);
		int indexETB = frameBytes.indexOf((byte) ETB);
		if (index != -1 || indexETB != -1) {
			return true;
		} else {
			return false;
		}
	}

	private void sendAgainMessage(String Message) {
		log.debug("Sending next frame in buffer - " + Message);
		if (Message.length() > conf.maxFrameSize) {
			log.debug("Frame length is longer than " + conf.maxFrameSize + ", splitting frame");
			String partToSend = Message.substring(0, conf.maxFrameSize);
			String remainingPart = Message.substring(conf.maxFrameSize);
			msgBeingSent.add(0, remainingPart);
			resendMessage = Message.toString();
			conf.lowLevelRecipient.tell(Message, self());
		} else {
			resendMessage = Message;
			conf.lowLevelRecipient.tell(Message, self());
		}
	}

	private void sendNextFrameInBuffer() {
		MDC.put("message", msgBeingSent.get(0));
		final String s = MDC.get("message");
		String nextFrame = msgBeingSent.remove(0);

		log.debug("Sending next frame in buffer - " + nextFrame + context());

		if (nextFrame.length() > conf.maxFrameSize) {
			log.debug("Frame length is longer than " + conf.maxFrameSize + ", splitting frame");
			String partToSend = nextFrame.substring(0, conf.maxFrameSize);
			String remainingPart = nextFrame.substring(conf.maxFrameSize);
			msgBeingSent.add(0, remainingPart);
			resendMessage = getFrame(partToSend, ETB).toString();
			conf.lowLevelRecipient.tell(getFrame(partToSend, ETB), self());
		} else {
			resendMessage = getFrame(nextFrame, ETX).toString();
			conf.lowLevelRecipient.tell(getFrame(nextFrame, ETX), self());
		}
		sendingFrameNumber = (sendingFrameNumber + 1) % 8;
		MainClass(resendMessage);
	}

	private BytesMessage getFrame(String payload, char payloadDelimiter) {
		String frameForChecksum = "" + STX + sendingFrameNumber + payload + payloadDelimiter;
		String msg = frameForChecksum + getCheckSum(frameForChecksum) + CR + LF;
		return new BytesMessage(msg);
	}

	private void MainClass(String toSendMessage) {
		ActionListener a = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Counter = " + counter);
				if (++counter < 3) {
					sendAgainMessage(toSendMessage);
				} else {
					counter = 0;
					senderTimer.stop();
					goToIdleState();
				}
			}
		};

		senderTimer = new Timer(6000, a);
		senderTimer.start();

	}
}