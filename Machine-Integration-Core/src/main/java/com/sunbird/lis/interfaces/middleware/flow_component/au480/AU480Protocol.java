package com.certacure.lis.interfaces.middleware.flow_component.au480;

import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.*;


import com.certacure.lis.interfaces.middleware.flow_component.socket.SocketProtocol.BytesMessage;

public class AU480Protocol {

	public static final BytesMessage ACKBytes = new BytesMessage((byte) ACK);
	public static final BytesMessage NAKBytes = new BytesMessage((byte) NAK);
	public static final BytesMessage EOTBytes = new BytesMessage((byte) EOT);
	public static final BytesMessage ENQBytes = new BytesMessage((byte) ENQ);
	public static final BytesMessage STXBytes = new BytesMessage((byte) STX);
	public static final BytesMessage ETXBytes = new BytesMessage((byte) ETX);
}