package com.certacure.lis.interfaces.middleware.flow_component.astme138194Emerald22;

import java.util.ArrayList;
import java.util.List;

import com.certacure.core.common.util.SpringUtil;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.service.MachineService;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class AstmE138194Emerald22Controller extends FlowComponent<AstmE138194Emerald22ControllerConf> {

	public enum ReturnResult {
		SUCCUSS,
		FAILED
	}

	private PartialFunction<Object, BoxedUnit> receivingState;

	private final StringBuilder msgBeingReceived = new StringBuilder();

	public String[] arrResultCode = {
			"WBC",
			"RBC",
			"HGB",
			"HCT",
			"MCV",
			"MCH",
			"MCHC",
			"PLT",
			"LYM%(W-SCR)",
			"MXD%(W-MCR)",
			"NEUT%(W-LCR)",
			"LYM#(W-SCC)",
			"MXD#(W-MCC)",
			"NEUT#(W-LCC)",
			"RDW-SD",
			"RDW-CV",
			"PDW",
			"MPV",
			"P-LCR"
	};

	@Override
	public PartialFunction<Object, BoxedUnit> getBehaviour() {
		return receivingState;
	}

	private AstmE138194Emerald22Controller() {
		receivingState = getReceivingState();
	}

	private ReturnResult processRecivedMessage(String inputMsg) {
		//inputMsg = inputMsg.replace('*', '0');
		String[] arrResultMsg = inputMsg.split("<CR>");
		try {

			String[] arrData = arrResultMsg[7].toString().split(";");
			long iSampleNumber = Long.parseLong(arrData[1]);

			String strSampleNumber = String.format("%012d", iSampleNumber);

			String[] arrResult = getResultArray(arrResultMsg);
			//String[] arrFinalFormatedResult = formatResultArray(arrResult);
			Emerald22ResultData emerald22ResultDataObj = new Emerald22ResultData(arrResult, strSampleNumber, inputMsg);

			conf.highLevelRecipient.tell(emerald22ResultDataObj, self());
			//context().become(receivingState);
			//goToIdleState();

			//}
		} catch (Exception ex) {

			msgBeingReceived.setLength(0);
			return ReturnResult.FAILED;
		}
		return ReturnResult.SUCCUSS;
	}

	private String[] getResultArray(String[] arrResultMsg) {

		List<String> lstResult = new ArrayList();

		for (int index = 0; index < arrResultMsg.length; index++) {
			if (arrResultMsg[index].contains("CYCLE;")) {
				for (int index2 = 1; index2 < 21; index2++) {
					int iCurrentResultIndex = index + index2;
					int indexOfSep = -1;
					indexOfSep = arrResultMsg[iCurrentResultIndex].indexOf(";");

					if (indexOfSep != -1) {
						String[] arrLineValue = arrResultMsg[iCurrentResultIndex].split(";");
						lstResult.add(arrLineValue[1]);
					}
				}
			} else {
				System.out.println("NO DATA FOUND");

			}

		}

		return lstResult.stream().toArray(String[]::new);

	}

	public Machine getMachineInfoByPath() {
		MachineService machineService = (MachineService) SpringUtil.getBean("MachineService");
		Machine machine = machineService.getMachineByActorPath(
				getContext().parent().toString());
		return machine;
	}

	public String[] formatResultArray(String[] arrResultValue) {

		float f_TempVar = 0;
		String s_Temp = "";

		for (int iCount = 0; iCount < arrResultValue.length; iCount++) { //0000000470047100135004040085800287003340028001770009700726000080000500034004000123001290010100260
			switch (iCount) {
				case 0:
					f_TempVar = Float.parseFloat(arrResultValue[iCount]) / 10;
					// just two decimal places
					s_Temp = String.format("%.1f", f_TempVar);
					arrResultValue[iCount] = s_Temp;
					System.out.println(arrResultCode[0] + "+++++++++" + arrResultValue[0]);
					break;
				case 1:
					f_TempVar = Float.parseFloat(arrResultValue[iCount]) / 100;
					// just two decimal places
					s_Temp = String.format("%.2f", f_TempVar);
					arrResultValue[iCount] = s_Temp;
					System.out.println(arrResultCode[1] + "+++++++++" + arrResultValue[1]);
					break;
				case 2:
					f_TempVar = Float.parseFloat(arrResultValue[iCount]) / 10;
					// just two decimal places
					s_Temp = String.format("%.1f", f_TempVar);
					arrResultValue[iCount] = s_Temp;
					System.out.println(arrResultCode[2] + "+++++++++" + arrResultValue[2]);
					break;
				case 3:
					f_TempVar = Float.parseFloat(arrResultValue[iCount]) / 10;
					// just two decimal places
					s_Temp = String.format("%.1f", f_TempVar);
					arrResultValue[iCount] = s_Temp;
					System.out.println(arrResultCode[3] + "+++++++++" + arrResultValue[3]);
					break;
				case 4:
					f_TempVar = Float.parseFloat(arrResultValue[iCount]) / 10;
					// just two decimal places
					s_Temp = String.format("%.1f", f_TempVar);
					arrResultValue[iCount] = s_Temp;
					System.out.println(arrResultCode[4] + "+++++++++" + arrResultValue[4]);
					break;
				case 5:
					f_TempVar = Float.parseFloat(arrResultValue[iCount]) / 10;
					// just two decimal places
					s_Temp = String.format("%.1f", f_TempVar);
					arrResultValue[iCount] = s_Temp;
					System.out.println(arrResultCode[5] + "+++++++++" + arrResultValue[5]);
					break;
				case 6:
					f_TempVar = Float.parseFloat(arrResultValue[iCount]) / 10;
					// just two decimal places
					s_Temp = String.format("%.1f", f_TempVar);
					arrResultValue[iCount] = s_Temp;
					System.out.println(arrResultCode[6] + "+++++++++" + arrResultValue[6]);
					break;
				case 7:
					f_TempVar = Float.parseFloat(arrResultValue[iCount]);
					// just two decimal places
					s_Temp = String.format("%.0f", f_TempVar);
					arrResultValue[iCount] = s_Temp;
					System.out.println(arrResultCode[7] + "+++++++++" + arrResultValue[7]);
					break;
				case 8:
					f_TempVar = Float.parseFloat(arrResultValue[iCount]) / 1000;
					// just zero decimal places
					s_Temp = String.format("%.3f", f_TempVar);
					arrResultValue[iCount] = s_Temp;
					System.out.println(arrResultCode[8] + "+++++++++" + arrResultValue[8]);
					break;
				case 9:
					f_TempVar = Float.parseFloat(arrResultValue[iCount]) / 1000;
					// just two decimal places
					s_Temp = String.format("%.3f", f_TempVar);
					arrResultValue[iCount] = s_Temp;
					System.out.println(arrResultCode[9] + "+++++++++" + arrResultValue[9]);
					break;
				case 10:
					f_TempVar = Float.parseFloat(arrResultValue[iCount]) / 1000;
					// just two decimal places
					s_Temp = String.format("%.3f", f_TempVar);
					arrResultValue[iCount] = s_Temp;
					System.out.println(arrResultCode[10] + "+++++++++" + arrResultValue[10]);
					break;
				case 11:
					f_TempVar = Float.parseFloat(arrResultValue[iCount]) / 10;
					// just two decimal places
					s_Temp = String.format("%.1f", f_TempVar);
					arrResultValue[iCount] = s_Temp;
					System.out.println(arrResultCode[11] + "+++++++++" + arrResultValue[11]);
					break;
				case 12:
					f_TempVar = Float.parseFloat(arrResultValue[iCount]) / 10;
					// just two decimal places
					s_Temp = String.format("%.1f", f_TempVar);
					arrResultValue[iCount] = s_Temp;
					System.out.println(arrResultCode[12] + "+++++++++" + arrResultValue[12]);
					break;
				case 13:
					f_TempVar = Float.parseFloat(arrResultValue[iCount]) / 10;
					// just two decimal places
					s_Temp = String.format("%.1f", f_TempVar);
					arrResultValue[iCount] = s_Temp;
					System.out.println(arrResultCode[13] + "+++++++++" + arrResultValue[13]);
					break;
				case 14:
					f_TempVar = Float.parseFloat(arrResultValue[iCount]) / 10;
					// just two decimal places
					s_Temp = String.format("%.1f", f_TempVar);
					arrResultValue[iCount] = s_Temp;
					System.out.println(arrResultCode[14] + "+++++++++" + arrResultValue[14]);
					break;
				case 15:

					//f_TempVar = float.Parse(arrResultValue[iCount].Substring(1, arrResultValue[iCount].Length-1)) / 1000;
					// just two decimal places
					f_TempVar = Float.parseFloat(arrResultValue[iCount]) / 10;
					s_Temp = String.format("%.1f", f_TempVar);
					arrResultValue[iCount] = s_Temp;
					System.out.println(arrResultCode[15] + "+++++++++" + arrResultValue[15]);
					break;
				case 16:
					f_TempVar = Float.parseFloat(arrResultValue[iCount]) / 10;
					// just two decimal places
					s_Temp = String.format("%.1f", f_TempVar);
					arrResultValue[iCount] = s_Temp;
					System.out.println(arrResultCode[16] + "+++++++++" + arrResultValue[16]);
					break;
				case 17:
					f_TempVar = Float.parseFloat(arrResultValue[iCount]) / 10;
					// just two decimal places
					s_Temp = String.format("%.1f", f_TempVar);
					arrResultValue[iCount] = s_Temp;
					System.out.println(arrResultCode[17] + "+++++++++" + arrResultValue[17]);
					break;
				case 18:
					f_TempVar = Float.parseFloat(arrResultValue[iCount]) / 1000;
					// just two decimal places
					s_Temp = String.format("%.3f", f_TempVar);
					arrResultValue[iCount] = s_Temp;
					System.out.println(arrResultCode[18] + "+++++++++" + arrResultValue[18]);
					break;
				default:
					f_TempVar = Float.parseFloat(arrResultValue[iCount]);
					// just one decimal places
					s_Temp = String.format("%.0f", f_TempVar);
					arrResultValue[iCount] = s_Temp;
					break;

			}

		}

		return arrResultValue;
	}

	private String[] getResultArray(String inputMsg, String strSampleNo) {

		String strResultText = inputMsg.substring(34, (inputMsg.length() - 1));
		String[] arrResultObj = new String[19];

		arrResultObj[0] = new String();
		arrResultObj[0] = Integer.valueOf(strResultText.substring(0, 4)).toString();//6.5
		System.out.println(arrResultCode[0] + "========" + arrResultObj[0]);
		arrResultObj[1] = new String();
		arrResultObj[1] = Integer.valueOf(strResultText.substring(5, 9)).toString();//5.66
		System.out.println(arrResultCode[1] + "========" + arrResultObj[1]);
		arrResultObj[2] = new String();
		arrResultObj[2] = Integer.valueOf(strResultText.substring(10, 14)).toString();//11.6
		System.out.println(arrResultCode[2] + "========" + arrResultObj[2]);
		arrResultObj[3] = new String();
		arrResultObj[3] = Integer.valueOf(strResultText.substring(15, 19)).toString();//0.393
		System.out.println(arrResultCode[3] + "========" + arrResultObj[3]);
		arrResultObj[4] = new String();
		arrResultObj[4] = Integer.valueOf(strResultText.substring(20, 24)).toString();//69.4
		System.out.println(arrResultCode[4] + "========" + arrResultObj[4]);
		arrResultObj[5] = new String();
		arrResultObj[5] = Integer.valueOf(strResultText.substring(25, 29)).toString();//20.5
		System.out.println(arrResultCode[5] + "========" + arrResultObj[5]);
		arrResultObj[6] = new String();
		arrResultObj[6] = Integer.valueOf(strResultText.substring(30, 34)).toString();//29.5
		System.out.println(arrResultCode[6] + "========" + arrResultObj[6]);
		arrResultObj[7] = new String();
		arrResultObj[7] = Integer.valueOf(strResultText.substring(35, 39)).toString();//293
		System.out.println(arrResultCode[7] + "========" + arrResultObj[7]);
		arrResultObj[8] = new String();
		arrResultObj[8] = Integer.valueOf(strResultText.substring(40, 44)).toString();//38.7
		System.out.println(arrResultCode[8] + "========" + arrResultObj[8]);
		arrResultObj[9] = new String();
		arrResultObj[9] = Integer.valueOf(strResultText.substring(45, 49)).toString();//10.1
		System.out.println(arrResultCode[9] + "========" + arrResultObj[9]);
		arrResultObj[10] = new String();
		arrResultObj[10] = Integer.valueOf(strResultText.substring(50, 54)).toString();//51.2
		System.out.println(arrResultCode[10] + "========" + arrResultObj[10]);
		arrResultObj[11] = new String();
		arrResultObj[11] = Integer.valueOf(strResultText.substring(55, 59)).toString();//2.4
		System.out.println(arrResultCode[11] + "========" + arrResultObj[11]);
		arrResultObj[12] = new String();
		arrResultObj[12] = Integer.valueOf(strResultText.substring(60, 64)).toString();//0.6
		System.out.println(arrResultCode[12] + "========" + arrResultObj[12]);
		arrResultObj[13] = new String();
		arrResultObj[13] = Integer.valueOf(strResultText.substring(65, 69)).toString();//3.2
		System.out.println(arrResultCode[13] + "========" + arrResultObj[13]);
		arrResultObj[14] = new String();
		arrResultObj[14] = Integer.valueOf(strResultText.substring(70, 74)).toString();//40.5
		System.out.println(arrResultCode[14] + "========" + arrResultObj[14]);
		arrResultObj[15] = new String();
		arrResultObj[15] = Integer.valueOf(strResultText.substring(75, 79)).toString();//16.2
		System.out.println(arrResultCode[15] + "========" + arrResultObj[15]);
		arrResultObj[16] = new String();
		arrResultObj[16] = Integer.valueOf(strResultText.substring(80, 84)).toString();//15.8
		System.out.println(arrResultCode[16] + "========" + arrResultObj[16]);
		arrResultObj[17] = new String();
		arrResultObj[17] = Integer.valueOf(strResultText.substring(85, 89)).toString();//10.6
		System.out.println(arrResultCode[17] + "========" + arrResultObj[17]);

		arrResultObj[18] = new String();
		arrResultObj[18] = Integer.valueOf(strResultText.substring(90, 94)).toString();//32.2
		System.out.println(arrResultCode[18] + "========" + arrResultObj[18]);

		return arrResultObj;

	}

	private PartialFunction<Object, BoxedUnit> getReceivingState() {
		return ReceiveBuilder

								.matchAny(__ ->
									{

										msgBeingReceived.append(__);

										if (msgBeingReceived.toString().contains("END_")) {

											//msgBeingReceived.append(extractPayload(bytesMessage.bytes));
											System.out.println("2++++++++++++++++++++++++++++++++++++++++++++++++++++");
											//System.out.println(extractPayload(bytesMessage.bytes));
											System.out.println("2++++++++++++++++++++++++++++++++++++++++++++++++++++");
											//conf.highLevelRecipient.tell(msgBeingReceived.toString(), self());

											String strResult = msgBeingReceived.toString();
											msgBeingReceived.setLength(0);
											ReturnResult iResult = processRecivedMessage(strResult);

											if (iResult == ReturnResult.SUCCUSS) {
												System.out.println("4+++++++++++++++++++++++++++++++++++++++++++++++");
												System.out.println("SUCCUSS");
												System.out.println("4+++++++++++++++++++++++++++++++++++++++++++++++");
											} else {
												System.out.println("4+++++++++++++++++++++++++++++++++++++++++++++++");
												System.out.println("FAILED");
												System.out.println("4+++++++++++++++++++++++++++++++++++++++++++++++");

											}

											msgBeingReceived.setLength(0);
											//goToIdleState();
										}

									}

								)

								.build();
	}

}