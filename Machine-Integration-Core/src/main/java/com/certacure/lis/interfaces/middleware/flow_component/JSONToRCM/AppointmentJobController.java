package com.certacure.lis.interfaces.middleware.flow_component.JSONToRCM;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.stream.IntStream;

import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.middleware.flow_component.astme138194archi.AstmE138194ArchiProtocol;
import com.certacure.lis.interfaces.middleware.flow_component.lab_http.httpRequstTransaction;
import com.certacure.lis.interfaces.middleware.flow_component.socket.SocketProtocol.BytesMessage;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class AppointmentJobController extends FlowComponent<AppointmentJobControllerConf> {

	private final List<String> msgBeingSent = new ArrayList<>();

	private final StringBuilder msgBeingReceived = new StringBuilder();

	Config config = ConfigFactory.load();

	Timer timer;

	@Override
	public PartialFunction<Object, BoxedUnit> getBehaviour() {
		return getReceivingState();
	}

	private AppointmentJobController() {

	}

	private void goToSendingState(httpRequstTransaction httpRequstTransactionObj) {

	}

	public String getCurrentLocalDateTimeStamp() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
	}

	private PartialFunction<Object, BoxedUnit> getReceivingState() {
		return ReceiveBuilder.match(BytesMessage.class, bytesMessage -> {
			System.out.println("Data Arrive >>>>>>>>>>>>>>>>>>" + extractPayload(bytesMessage.bytes));
			msgBeingReceived.append(extractPayload(bytesMessage.bytes));
			changeStateAndProcessCompletedMessage();
		}).match(httpRequstTransaction.class, this::goToSendingState)

				.matchAny(__ -> {
					stash();
				}).build();
	}

	private void changeStateAndProcessCompletedMessage() {
		// TODO Auto-generated method stub

	}

	private String extractPayload(List<Byte> frameBytes) {

		// List<Byte> payloadBytes = frameBytes.subList(2, index);
		List<Byte> payloadBytes = frameBytes;
		byte[] bytes = new byte[payloadBytes.size()];
		IntStream.range(0, payloadBytes.size()).forEach(i -> bytes[i] = payloadBytes.get(i));
		return new String(bytes);
	}

}