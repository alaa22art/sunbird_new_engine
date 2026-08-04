package com.certacure.lis.interfaces.middleware.flow_component.hl7.cobasPure;

import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.ACK;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.ENQ;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.EOT;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.NAK;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.VT;
import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.FS;

import com.certacure.lis.interfaces.middleware.flow_component.socket.SocketProtocol.BytesMessage;

public class Hl7PureProtocol {


	public static final BytesMessage VTBytes = new BytesMessage((byte) VT);
	public static final BytesMessage FSBytes = new BytesMessage((byte) FS);
	
	
	public static final String componentDelimiter = "^";
	public static final String fieldDelimiter = "|";
	public static final String repeatDelimiter = "/";
	public static final String escapeDelimiter = "~";
}