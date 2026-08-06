package com.certacure.machine.web.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import com.certacure.core.base.entity.UserAccount;
import com.certacure.core.common.util.SecurityUtil;
import com.certacure.machine.web.helper.WebSocketClient;
import com.certacure.machine.web.helper.WebSocketType;
import com.certacure.machine.web.helper.WebSocketWrapper;

@Component
public class WebSocketUtil {

	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	private final String DESTINATION = "/topic/data";

	private List<WebSocketClient> authorizedClients = Collections.synchronizedList(new ArrayList<>());

	public void addAuthorizedClient(String branchKey, String clientName) {
		Boolean exists = Boolean.FALSE;
		synchronized (authorizedClients) {
			for (WebSocketClient client : authorizedClients) {
				if (client.getClientName().equals(clientName)) {
					client.increaseCount();
					exists = Boolean.TRUE;
				}
			}
			if (!exists) {
				authorizedClients.add(new WebSocketClient(branchKey, clientName));
			}
		}

	}

	public void removeAuthorizedClient(String clientName) {
		synchronized (authorizedClients) {
			Iterator<WebSocketClient> iterator = authorizedClients.iterator();
			while (iterator.hasNext()) {
				WebSocketClient client = iterator.next();
				if (client.getClientName().equals(clientName)) {
					client.decreaseCount(iterator);
					break;
				}
			}
		}
	}

	public void sendToBranchUsers(WebSocketType webSocketType, Object data) {
		WebSocketWrapper webSocketWrapper = new WebSocketWrapper(webSocketType, data);
		UserAccount user = SecurityUtil.getCurrentUser();
		String branchKey = user.getTenantId() + "-" + user.getBranchId();
		synchronized (authorizedClients) {
			for (WebSocketClient client : authorizedClients) {
				if (client.getBranchKey().equals(branchKey)) {
					messagingTemplate.convertAndSendToUser(client.getClientName(), DESTINATION, webSocketWrapper);
				}
			}
		}
	}

	public void sendToAllUsers(WebSocketType webSocketType, Object data) {
		WebSocketWrapper webSocketWrapper = new WebSocketWrapper(webSocketType, data);
		synchronized (authorizedClients) {
			for (WebSocketClient client : authorizedClients) {
				messagingTemplate.convertAndSendToUser(client.getClientName(), DESTINATION, webSocketWrapper);
			}
		}
	}

}
