package com.sunbird.lis.interfaces.middleware.flow_component.kx21n;

import static com.sunbird.lis.interfaces.middleware.util.LowLevelUtils.*;

import com.sunbird.lis.interfaces.middleware.flow_component.socket.SocketProtocol.BytesMessage;

public class Kx21nProtocol {

	public static final BytesMessage ACKBytes = new BytesMessage((byte) ACK);
	public static final BytesMessage NAKBytes = new BytesMessage((byte) NAK);
	public static final BytesMessage EOTBytes = new BytesMessage((byte) EOT);
	public static final BytesMessage ENQBytes = new BytesMessage((byte) ENQ);
	public static final BytesMessage STXBytes = new BytesMessage((byte) STX);
	public static final BytesMessage ETXBytes = new BytesMessage((byte) ETX);
}