package com.sunbird.lis.interfaces.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.helper.SearchCriterion;
import com.sunbird.core.base.helper.SearchCriterion.FilterOperator;
import com.sunbird.core.base.service.GenericService;
import com.sunbird.core.common.helper.FilterablePageRequest;
import com.sunbird.core.common.util.CollectionUtil;
import com.sunbird.lis.interfaces.annotation.InterceptorFree;
import com.sunbird.lis.interfaces.entities.LabBranch;
import com.sunbird.lis.interfaces.entities.LkpMessageTransactionDirection;
import com.sunbird.lis.interfaces.entities.LkpMessageTransactionType;
import com.sunbird.lis.interfaces.entities.Machine;
import com.sunbird.lis.interfaces.entities.MessageTransaction;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_HeaderRecord;
import com.sunbird.lis.interfaces.repo.MachineRepo;
import com.sunbird.lis.interfaces.repo.MessageTransactionRepo;

@Service("MessageTransactionService")
public class MessageTransactionService extends GenericService<MessageTransaction, MessageTransactionRepo> {

	@Autowired
	private MessageTransactionRepo repo;

	@Autowired
	private LkpService lkpService;

	@Autowired
	private LabBranchService branchService;

	@Autowired
	private MachineService machineService;

	// private String note = "Inbound Message Stored";

	private String strMEssageBody;

	private LkpMessageTransactionDirection lkpDirection;
	private LkpMessageTransactionType lkpDirectionType;

	private MessageTransaction messageTransaction;

	@Override
	protected MessageTransactionRepo getRepository() {
		return repo;
	}

	@InterceptorFree
	public MessageTransaction add(MessageTransaction message) {
		message = repo.save(message);

		return message;
	}

	public Page<MessageTransaction> getMessageTransactionPage(FilterablePageRequest fpr) {
		Page<MessageTransaction> page = getRepository().find(fpr.getFilters(), fpr.getPageRequest(),
				MessageTransaction.class, "messageType", "machine", "messageDirection");
		if (CollectionUtil.isCollectionEmpty(page.getContent())) {
			return page;
		}
		List<LabBranch> branches = branchService.find(Arrays.asList(new SearchCriterion("rid",
				page.getContent().stream().map(m -> m.getBranchId()).distinct().collect(Collectors.toList()),
				FilterOperator.in)), LabBranch.class);
		for (MessageTransaction mt : page.getContent()) {
			mt.addTransient("branch",
					branches.stream().filter(b -> b.getRid().equals(mt.getBranchId())).findFirst().orElse(null));
		}
		return page;
	}

	@InterceptorFree
	public MessageTransaction getByID(Long rid) {

		Optional<MessageTransaction> mesgOpt = repo.findById(rid);
		return mesgOpt.orElse(null);

	}

	private LkpMessageTransactionDirection getMessageDirectionObj(String msgDirection) {

		lkpDirection = lkpService.findOneAnyLkp(
				java.util.Arrays.asList(new SearchCriterion("code", msgDirection, FilterOperator.eq)),
				LkpMessageTransactionDirection.class);

		return lkpDirection;

	}

	@InterceptorFree
	public MessageTransaction addMessageTransaction(Machine machine, String strMessageText, String strDir,
			String strType,String adtString ,boolean isSuccess, boolean isProcess, boolean isSent, String notes, String msgID)

	{ // machine, msgAsString ,lkpMessageTransactionDirection,
		// lkpMessageTransactionType
		messageTransaction = new MessageTransaction();

		messageTransaction.setMachine(machine);
		messageTransaction.setNotes(notes);
		messageTransaction.setBranchId(machine.getBranchId());
		messageTransaction.setTenantId(machine.getTenantId());
		messageTransaction.setMessageDirection(getMessageDirectionObj(strDir));
		messageTransaction.setMessageType(getMessageTypeObj(strType));
		messageTransaction.setMessageBody(strMessageText);
		messageTransaction.setIsValidated(isProcess);
		messageTransaction.setIsSuccuss(isSuccess);
		messageTransaction.setMessageControlID(msgID);
		messageTransaction.setIsSent(isSent);
		messageTransaction.setAdtOperationType(adtString);
		repo.save(messageTransaction);
		return messageTransaction;
	}
	
	

	@InterceptorFree
	public MessageTransaction addMessageTransaction(Machine machine, String strMessageText,
			LkpMessageTransactionDirection lkpMessageTransactionDirection,
			LkpMessageTransactionType lkpMessageTransactionType, boolean isSuccess, boolean isValudated, boolean isSent,
			String notes, String strACKText, String msgID)

	{ // machine, msgAsString ,lkpMessageTransactionDirection,
		// lkpMessageTransactionType
		messageTransaction = new MessageTransaction();

		messageTransaction.setMachine(machine);
		messageTransaction.setNotes(notes);
		messageTransaction.setBranchId(machine.getBranchId());
		messageTransaction.setTenantId(machine.getTenantId());
		messageTransaction.setMessageDirection(lkpMessageTransactionDirection);
		messageTransaction.setMessageType(lkpMessageTransactionType);
		messageTransaction.setMessageBody(strMessageText);
		messageTransaction.setIsValidated(isValudated);
		messageTransaction.setIsSuccuss(isSuccess);
		messageTransaction.setMessageControlID(msgID);
		messageTransaction.setIsSent(isSent);
		messageTransaction.setAckMessageText(strACKText);
		repo.save(messageTransaction);
		return messageTransaction;
	}

	@InterceptorFree
	public MessageTransaction addMessageTransaction(Machine machine, String msgAsString, String strDir, String strType,
			boolean isSuccess, boolean isProcess, boolean isSent, String notes, String msgID, String ackStr,
			String patientID, String nationalID, String responseS) { // machine, msgAsString
																		// ,lkpMessageTransactionDirection,
																		// lkpMessageTransactionType
		messageTransaction = new MessageTransaction();
		messageTransaction.setMachine(machine);
		messageTransaction.setNotes(notes);
		messageTransaction.setBranchId(machine.getBranchId());
		messageTransaction.setTenantId(machine.getTenantId());
		messageTransaction.setMessageDirection(getMessageDirectionObj(strDir));
		messageTransaction.setMessageType(getMessageTypeObj(strType));
		messageTransaction.setMessageBody(msgAsString);
		messageTransaction.setIsValidated(isSuccess);
		messageTransaction.setIsSuccuss(isProcess);
		messageTransaction.setMessageControlID(msgID);
		messageTransaction.setIsSent(isSent);
		messageTransaction.setAckMessageText(ackStr);
		messageTransaction.setPatientID(patientID);
		messageTransaction.setResponse(responseS);
		messageTransaction.setNationalID(nationalID);
		repo.save(messageTransaction);
		return messageTransaction;
	}

	@InterceptorFree
	public MessageTransaction addMessageTransaction(Machine machine, String strMessageText,
			LkpMessageTransactionDirection lkpMessageTransactionDirection,
			LkpMessageTransactionType lkpMessageTransactionType, boolean isSuccess, boolean isValudated, boolean isSent,
			String notes, String strACKText, String msgID, String patientID, String nationalID, String responseS,
			String typeName) { // machine, msgAsString ,lkpMessageTransactionDirection,
								// lkpMessageTransactionType
		messageTransaction = new MessageTransaction();
		messageTransaction.setMachine(machine);
		messageTransaction.setNotes(notes);
		messageTransaction.setBranchId(machine.getBranchId());
		messageTransaction.setTenantId(machine.getTenantId());
		messageTransaction.setMessageDirection(lkpMessageTransactionDirection);
		messageTransaction.setMessageType(lkpMessageTransactionType);
		messageTransaction.setMessageBody(strMessageText);
		messageTransaction.setIsValidated(isValudated);
		messageTransaction.setIsSuccuss(isSuccess);
		messageTransaction.setMessageControlID(msgID);
		messageTransaction.setIsSent(isSent);
		messageTransaction.setAckMessageText(strACKText);
		messageTransaction.setPatientID(patientID);
		messageTransaction.setResponse(responseS);
		messageTransaction.setNationalID(nationalID);
		messageTransaction.setAdtOperationType(typeName);
		repo.save(messageTransaction);
		return messageTransaction;
	}

	@InterceptorFree
	public MessageTransaction addMessageTransaction(Machine machine, String strMessageText,
			LkpMessageTransactionDirection lkpMessageTransactionDirection,
			LkpMessageTransactionType lkpMessageTransactionType, boolean isSuccess, boolean isValudated, boolean isSent,
			String notes, String strACKText, String msgID, String patientID, String nationalID, String typeName) { // machine,
																													// msgAsString
																													// ,lkpMessageTransactionDirection,
																													// lkpMessageTransactionType
		messageTransaction = new MessageTransaction();
		messageTransaction.setMachine(machine);
		messageTransaction.setNotes(notes);
		messageTransaction.setBranchId(machine.getBranchId());
		messageTransaction.setTenantId(machine.getTenantId());
		messageTransaction.setMessageDirection(lkpMessageTransactionDirection);
		messageTransaction.setMessageType(lkpMessageTransactionType);
		messageTransaction.setMessageBody(strMessageText);
		messageTransaction.setIsValidated(isValudated);
		messageTransaction.setIsSuccuss(isSuccess);
		messageTransaction.setMessageControlID(msgID);
		messageTransaction.setIsSent(isSent);
		messageTransaction.setAckMessageText(strACKText);
		messageTransaction.setPatientID(patientID);
		messageTransaction.setAdtOperationType(typeName);
		messageTransaction.setNationalID(nationalID);
		repo.save(messageTransaction);
		return messageTransaction;
	}

	@InterceptorFree
	public MessageTransaction addMessageTransaction(Machine machine, String msgAsString, String strDir, String strType,
			boolean isSuccess, boolean isProcess, boolean isSent, String notes, String msgID, String ackStr)

	{ // machine, msgAsString ,lkpMessageTransactionDirection,
		// lkpMessageTransactionType
		messageTransaction = new MessageTransaction();

		messageTransaction.setMachine(machine);
		messageTransaction.setNotes(notes);
		messageTransaction.setBranchId(machine.getBranchId());
		messageTransaction.setTenantId(machine.getTenantId());
		messageTransaction.setMessageDirection(getMessageDirectionObj(strDir));
		messageTransaction.setMessageType(getMessageTypeObj(strType));
		messageTransaction.setMessageBody(msgAsString);
		messageTransaction.setIsValidated(isSuccess);
		messageTransaction.setIsSuccuss(isProcess);
		messageTransaction.setMessageControlID(msgID);
		messageTransaction.setIsSent(isSent);
		messageTransaction.setAckMessageText(ackStr);
		repo.save(messageTransaction);
		return messageTransaction;
	}

	private LkpMessageTransactionType getMessageTypeObj(String strType) {
		lkpDirectionType = lkpService.findOneAnyLkp(
				java.util.Arrays.asList(new SearchCriterion("code", strType, FilterOperator.eq)),
				LkpMessageTransactionType.class);

		return lkpDirectionType;
	}

	@InterceptorFree
	public void flushChangesToLog(MessageTransaction messageTransLog) {
		repo.save(messageTransLog);

	}

}
