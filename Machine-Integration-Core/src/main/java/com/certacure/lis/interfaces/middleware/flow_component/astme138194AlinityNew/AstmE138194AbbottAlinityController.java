package com.certacure.lis.interfaces.middleware.flow_component.astme138194AlinityNew;

import static com.certacure.lis.interfaces.middleware.flow_component.astme138194AlinityNew.AstmE138194AbbottAlinityProtocol.*;

import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.*;

import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.getCheckSum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.IntStream;

import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.flow_component.socket.SocketProtocol.BytesMessage;


import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class AstmE138194AbbottAlinityController extends FlowComponent<AstmE138194AbbottAlinityControllerConf> {

	private enum messageTransactionEndType 
	{
		TRANSACTION_DATA_CONTAINS_EOT, 
		TRANSACTION_DATA_END_WITH_EOT, 
		TRANSACTION_DATA_NOT_ENDED,
		TRANSACTION_DATA_STARTED

	}

	private PartialFunction<Object, BoxedUnit> idleState;
	private PartialFunction<Object, BoxedUnit> receivingState;
	private PartialFunction<Object, BoxedUnit> sendingState;

	private final List<String> msgBeingSent = new ArrayList<>();
	private int sendingFrameNumber;
	private final StringBuilder msgBeingReceived = new StringBuilder();

	Timer timer;

	@Override
	public PartialFunction<Object, BoxedUnit> getBehaviour() {
		return idleState;
	}

	private AstmE138194AbbottAlinityController() {
		// timer = new Timer();
		// restartTimer();
		idleState = getIdleState();
		receivingState = getReceivingState();
		sendingState = getSendingState();
	}

	private void restartTimer() {
		timer.schedule(new RemindTask(), 0, // initial delay
				5 * 1000); // subsequent rate
	}

	private PartialFunction<Object, BoxedUnit> getIdleState() {
		return ReceiveBuilder.match(String.class, this::goToSendingState)
				.matchEquals(ENQBytes, __ -> goToReceivingState())
				.matchEquals(EOTBytes, __ -> System.out.println("ERROR IN TRANSMISION ")).build();
	}

	private void goToSendingState(String msgToSend) {
		log.debug("Transitioning to sending state");
		sendingFrameNumber = 1;
		msgBeingSent.clear();
		Collections.addAll(msgBeingSent, msgToSend.split("(?<=" + CR + ")"));
		conf.lowLevelRecipient.tell(ENQBytes, self());
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
			} else {
				sendNextFrameInBuffer();
			}
		}).matchAny(__ -> stash()).build();
	}

	private PartialFunction<Object, BoxedUnit> getReceivingState() {
		return ReceiveBuilder

				.matchEquals(EOTBytes, __ -> {
					changeStateAndProcessCompletedMessage();
				}).match(BytesMessage.class, bytesMessage -> {
					messageTransactionEndType transState = isMEssageTransactionEnd(bytesMessage);

					if (transState.equals( messageTransactionEndType.TRANSACTION_DATA_CONTAINS_EOT)) {
						changeStateAndProcessCompletedMessage();
					} else if (transState.equals(messageTransactionEndType.TRANSACTION_DATA_END_WITH_EOT)) {
						changeStateAndProcessCompletedMessage();

					} else if (transState.equals(messageTransactionEndType.TRANSACTION_DATA_STARTED)) {
						addInputDataToBuilder(bytesMessage);
					} else {
						addInputDataToBuilder(bytesMessage);
					}

				})
				.matchAny(__ -> {
					stash();
				}).build();
	}

	private void addInputDataToBuilder(BytesMessage bytesMessage) {
		conf.lowLevelRecipient.tell(ACKBytes, self());
		System.out.println(extractPayload(bytesMessage.bytes));
		msgBeingReceived.append(extractPayload(bytesMessage.bytes));
	}

	private void changeStateAndProcessCompletedMessage() {
		conf.highLevelRecipient.tell(msgBeingReceived.toString(), self());
		msgBeingReceived.setLength(0);
		goToIdleState();
	}

	private void goToIdleState() {
		log.debug("Transitioning to idle state");
		unstashAll();
		context().become(idleState);
	}

	public messageTransactionEndType isMEssageTransactionEnd(BytesMessage byteMessage) {
		if (byteMessage.bytes.equals(EOTBytes.bytes)) {
			return messageTransactionEndType.TRANSACTION_DATA_END_WITH_EOT;
		} else if (byteMessage.bytes.contains(EOTBytes.bytes)) {
			return messageTransactionEndType.TRANSACTION_DATA_CONTAINS_EOT;
		} else if (byteMessage.bytes.equals(ENQBytes.bytes)) {
			return messageTransactionEndType.TRANSACTION_DATA_STARTED;
		} else {
			return messageTransactionEndType.TRANSACTION_DATA_NOT_ENDED;
		}
	}

	private String extractPayload(List<Byte> frameBytes) {
		int index = frameBytes.indexOf((byte) ETX);
		if (index == -1) {
			index = frameBytes.indexOf((byte) ETB);
		}
		if (index == -1) {
			index = frameBytes.size() - 1;
		}

		// List<Byte> payloadBytes = frameBytes.subList(2, index);
		List<Byte> payloadBytes = frameBytes;
		byte[] bytes = new byte[payloadBytes.size()];
		IntStream.range(0, payloadBytes.size()).forEach(i -> bytes[i] = payloadBytes.get(i));
		return new String(bytes);
	}

	/*
	 * private String extractPayload(List<Byte> frameBytes) { int index =
	 * frameBytes.indexOf((byte) ETX); int indexETB = frameBytes.indexOf((byte)
	 * ETB); if (indexETB != -1 && index == -1) { index = frameBytes.indexOf((byte)
	 * ETB); } if (index != -1) { List<Byte> payloadBytes;//= frameBytes.subList(2,
	 * index); if (frameBytes.get(0) == 2) { payloadBytes = frameBytes.subList(2,
	 * index); } else { payloadBytes = frameBytes.subList(0, index); } byte[] bytes
	 * = new byte[payloadBytes.size()]; IntStream.range(0,
	 * payloadBytes.size()).forEach(i -> bytes[i] = payloadBytes.get(i)); return new
	 * String(bytes); } else { List<Byte> payloadBytes; if (frameBytes.get(0) == 2)
	 * { payloadBytes = frameBytes.subList(2, frameBytes.size()); } else {
	 * payloadBytes = frameBytes; } byte[] bytes = new byte[payloadBytes.size()];
	 * IntStream.range(0, payloadBytes.size()).forEach(i -> bytes[i] =
	 * payloadBytes.get(i)); return new String(bytes); } }
	 */

	private void sendNextFrameInBuffer() {
		String nextFrame = msgBeingSent.remove(0);
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

	private BytesMessage getFrame(String payload, char payloadDelimiter) {
		String frameForChecksum = "" + STX + sendingFrameNumber + payload + payloadDelimiter;
		String msg = frameForChecksum + getCheckSum(frameForChecksum) + CR + LF;
		return new BytesMessage(msg);
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

	class RemindTask extends TimerTask {

		@Override
		public void run() {
			System.out.println("Time's up!");
			conf.lowLevelRecipient.tell(ENQBytes, self());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			conf.lowLevelRecipient.tell(EOTBytes, self());

		}
	}

}