package com.certacure.lis.interfaces.middleware.flow_component.au480;

import static com.certacure.lis.interfaces.middleware.flow_component.au480.AU480Protocol.ETXBytes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.certacure.core.common.util.SpringUtil;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.service.MachineService;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class AU480Controller extends FlowComponent<AU480ControllerConf> {

	public enum ReturnResult {
		SUCCUSS,
		FAILED
	}

	private PartialFunction<Object, BoxedUnit> receivingState;

	private final StringBuilder msgBeingReceived = new StringBuilder();

	@Override
	public PartialFunction<Object, BoxedUnit> getBehaviour() {
		return receivingState;
	}

	private AU480Controller() {
		receivingState = getReceivingState();
	}

	private ReturnResult processRecivedMessage(String inputMsg) {
		Map<String, String> formattedMsg = new HashMap<String, String>();
		List<AU480Result> resultsWithTestsNo = new ArrayList<AU480Result>();

		AU480ResultData au480ResultDataResultDataObj = new AU480ResultData();
		if (inputMsg.length() < 34) {
			return ReturnResult.FAILED;
		}
		try {

			formattedMsg = formatMsg(inputMsg);
			resultsWithTestsNo = splitTests(formattedMsg.get("resultswithTestsNo"));
			au480ResultDataResultDataObj.setOriginalInput(inputMsg);
			au480ResultDataResultDataObj.setSampleNo(formattedMsg.get("sampleId"));
			au480ResultDataResultDataObj.setCupPosition(formattedMsg.get("cupPosition"));
			au480ResultDataResultDataObj.setRackNo(formattedMsg.get("rackNo"));
			au480ResultDataResultDataObj.setSequenceNo(formattedMsg.get("sequenceNo"));
			au480ResultDataResultDataObj.setResults(resultsWithTestsNo);

			conf.highLevelRecipient.tell(au480ResultDataResultDataObj, self());
			//context().become(receivingState);
			//goToIdleState();

		} catch (Exception ex) {

			msgBeingReceived.setLength(0);
			return ReturnResult.FAILED;
		}
		return ReturnResult.SUCCUSS;
	}

	public Machine getMachineInfoByPath() {
		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		Machine machine = machineService.getMachineByActorPath(
				getContext().parent().toString());
		return machine;
	}

	public Map<String, String> formatMsg(String inputMsg) {
		Map<String, String> formatedMsgMap = new HashMap<String, String>();
		int position = inputMsg.indexOf("D") + 2;

		int rackNoLength = 4;
		int cupPositionLength = 2;
		int sequenceNoLength = 4;
		int sampleIdLength = 26;

		String rackNo = inputMsg.substring(position, position + rackNoLength);
		formatedMsgMap.put("rackNo", rackNo.trim());
		position += rackNoLength;

		String cupPosition = inputMsg.substring(position, position + cupPositionLength);
		formatedMsgMap.put("cupPosition", cupPosition.trim());
		position += cupPositionLength + 1;

		String sequenceNo = inputMsg.substring(position, position + sequenceNoLength);
		formatedMsgMap.put("sequenceNo", sequenceNo.trim());
		position += sequenceNoLength + 1;

		String sampleId = inputMsg.substring(position, position + sampleIdLength);
		sampleId = String.format("%12s", sampleId.trim()).replace(' ', '0');
		formatedMsgMap.put("sampleId", sampleId);
		position += sampleIdLength;

		int resultsEndPosition = inputMsg.indexOf(ETXBytes.toString(), position + 50);
		String resultswithTestsNo = inputMsg.substring(position + 50, resultsEndPosition);
		formatedMsgMap.put("resultswithTestsNo", resultswithTestsNo.trim());

		return formatedMsgMap;
	}

	public List<AU480Result> splitTests(String resultswithTestsNo) {
		List<AU480Result> testsResults = new ArrayList<AU480Result>();
		int testLength = 10;
		int testsNo = resultswithTestsNo.length() / testLength;
		for (int i = 0; i < testsNo; i++) {
			String temp = resultswithTestsNo.substring(i * testLength, i * testLength + testLength);
			String testCode = temp.substring(0, temp.indexOf(" "));
			String testValue = temp.substring(temp.indexOf(" "));

			testsResults.add(new AU480Result(testCode.trim(), testValue.trim()));

		}
		return testsResults;
	}

	int i = 0;

	private PartialFunction<Object, BoxedUnit> getReceivingState() {
		return ReceiveBuilder

								.matchAny(__ ->
									{

										msgBeingReceived.append(__);
										System.out.print("---------" + ++i);
										System.out.println(msgBeingReceived);

										if (msgBeingReceived.indexOf("DB") != -1 && msgBeingReceived.indexOf("DE") != -1) {
											System.out.println("--------------start MEssage Completed----------------------");
											System.out.println(msgBeingReceived);
											System.out.println("---------------------end MEssage Completed------------------");
											String msgData = msgBeingReceived.toString();
											msgBeingReceived.setLength(0);
											msgData = msgData.replace("<STX>DB<ETX>", "");

											int endPosition = msgData.indexOf("<STX>DE<ETX>");
											msgData = msgData.substring(0, endPosition);

											while (msgData.indexOf("<STX>") != -1 && msgData.indexOf("<ETX>") != -1) {
												int ETXIndex = msgData.indexOf("<ETX>");
												ReturnResult iResult = processRecivedMessage(msgData.substring(0, ETXIndex + 5));

												if (iResult == ReturnResult.SUCCUSS) {
													System.out.println("4+++++++++++++++++++++++++++++++++++++++++++++++");
													System.out.println("SUCCUSS");
													System.out.println("4+++++++++++++++++++++++++++++++++++++++++++++++");
												} else {
													System.out.println("4+++++++++++++++++++++++++++++++++++++++++++++++");
													System.out.println("FAILED");
													System.out.println("4+++++++++++++++++++++++++++++++++++++++++++++++");
												}

												msgData = msgData.substring(msgData.indexOf("<ETX>") + 5);
											}

										}

									})

								.build();
	}

}