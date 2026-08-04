package com.certacure.lis.interfaces.middleware.flow_component.CS_2000;

import static com.certacure.lis.interfaces.middleware.flow_component.kx21n.Kx21nProtocol.STXBytes;

import com.certacure.core.common.util.SpringUtil;
import com.certacure.lis.interfaces.entities.Machine;
import com.certacure.lis.interfaces.middleware.core.FlowComponent;
import com.certacure.lis.interfaces.service.MachineService;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class CS2000Controller extends FlowComponent<CS2000ControllerConf> {

	public enum ReturnResult {
		SUCCUSS,
		FAILED
	}

	private PartialFunction<Object, BoxedUnit> receivingState;

	private final StringBuilder msgBeingReceived = new StringBuilder();

	public String[] arrResultCode = {
			"041",
			"044",
			"051"
	};

	@Override
	public PartialFunction<Object, BoxedUnit> getBehaviour() {
		return receivingState;
	}

	private CS2000Controller() {
		receivingState = getReceivingState();
	}

	private ReturnResult processRecivedMessage(String inputMsg) {
		//inputMsg = inputMsg.replace('*', '0');
		String[] arrResultMsg = inputMsg.split(STXBytes.toString());
		try {

			for (int i = 0; i < arrResultMsg.length; i++) {

				if (arrResultMsg[i].trim().length() == 0) {

					continue;

				}

				//long iSampleNumber = Long.parseLong(arrResultMsg[i].toString().substring(27, 46));
				String strSampleNumber = arrResultMsg[i].toString().substring(27, 46).trim();
				strSampleNumber = strSampleNumber.substring(0,strSampleNumber.length()-1);
				//String strSampleNumber = String.format("%012d", iSampleNumber);

				String[] arrResult = getResultArray(arrResultMsg[i].toString(), strSampleNumber);
				String[] arrFinalFormatedResult = formatResultArray(arrResult);
				CS2000ResultData Kx21nResultDataObj = new CS2000ResultData(arrFinalFormatedResult, strSampleNumber, inputMsg);

				conf.highLevelRecipient.tell(Kx21nResultDataObj, self());
				//context().become(receivingState);
				//goToIdleState();

			}
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

		String strResultText = inputMsg.substring(58, 85);
		String[] arrResultObj = new String[3];

		
		arrResultObj[0] = new String();
		arrResultObj[0] = Integer.valueOf(strResultText.substring(4, 9).trim()).toString();//6.5
		System.out.println(arrResultCode[0] + "========" + arrResultObj[0]);
		arrResultObj[1] = new String();
		arrResultObj[1] = Integer.valueOf(strResultText.substring(13, 18).trim()).toString();//5.66
		System.out.println(arrResultCode[1] + "========" + arrResultObj[1]);
		arrResultObj[2] = new String();
		arrResultObj[2] = Integer.valueOf(strResultText.substring(21, 26).trim()).toString();//11.6
		System.out.println(arrResultCode[2] + "========" + arrResultObj[2]);
		

		return arrResultObj;

	}

	private PartialFunction<Object, BoxedUnit> getReceivingState() {
		return ReceiveBuilder

								.matchAny(__ ->
									{

										msgBeingReceived.append(__);

									//	if (msgBeingReceived.length() % 90 == 0) 
										{
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

									})

								.build();
	}

}