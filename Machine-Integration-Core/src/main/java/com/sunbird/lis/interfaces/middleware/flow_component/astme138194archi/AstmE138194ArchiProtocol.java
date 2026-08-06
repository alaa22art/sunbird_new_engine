package com.sunbird.lis.interfaces.middleware.flow_component.astme138194archi;

import static com.sunbird.lis.interfaces.middleware.util.LowLevelUtils.*;

import com.sunbird.lis.interfaces.middleware.flow_component.socket.SocketProtocol.BytesMessage;

public class AstmE138194ArchiProtocol {

	public static final BytesMessage ACKBytes = new BytesMessage((byte) ACK);
	public static final BytesMessage NAKBytes = new BytesMessage((byte) NAK);
	public static final BytesMessage EOTBytes = new BytesMessage((byte) EOT);
	public static final BytesMessage ENQBytes = new BytesMessage((byte) ENQ);
	public static final BytesMessage VTBytes = new BytesMessage((byte) VT);
	public static final BytesMessage FSBytes = new BytesMessage((byte) FS);
	
	
	
	public static final String componentDelimiter = "^";
	public static final String fieldDelimiter = "|";
	public static final String repeatDelimiter = "/";
	public static final String escapeDelimiter = "~";
}