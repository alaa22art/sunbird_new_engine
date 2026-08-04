package com.certacure.machine.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.certacure.core.base.entity.UserAccount;
import com.certacure.machine.web.util.WebSocketUtil;

@Component
public class WebSocketChatEventListener {

	@Autowired
	private WebSocketUtil wsUtil;

	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent event) {
		OAuth2Authentication authentication = (OAuth2Authentication) event.getUser();
		Authentication userAuthentication = authentication.getUserAuthentication();
		UserAccount user = (UserAccount) userAuthentication.getPrincipal();
		wsUtil.addAuthorizedClient(user.getTenantId() + "-" + user.getBranchId(), user.getUsername());
	}

	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		wsUtil.removeAuthorizedClient(event.getUser().getName());
	}

}
