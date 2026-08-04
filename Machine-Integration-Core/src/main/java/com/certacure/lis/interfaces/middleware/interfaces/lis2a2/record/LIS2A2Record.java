package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record;

import static com.certacure.lis.interfaces.middleware.util.LowLevelUtils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.DelimitedData;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.HeaderASTMRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.OrderASTMRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.PatientASTMRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.QueryASTMRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.ResultASTMRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.ASTM.TerminationASTMRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_Appointment;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_CommonOrder;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_DiagnosisRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_EventTypeRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_HeaderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_OrderRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_PatientRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_ResultRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_Visit1Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_V24_Visit2Record;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V24.HL7_v24_FinancialTransaction;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V25.HL7_V25_Comments;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V25.HL7_V25_MessageAcknowledgmentSegment;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V25.HL7_V25_ObservationRequest;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V25.HL7_V25_PatientDemographicQueryRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V25.HL7_V25_PatientDemographicResponseRecord;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V25.HL7_V25_TestDetails;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.HL7V25.HL7_V25_TimingQuantity;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.CompomentField;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.Field;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.PrimitiveField;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.RepeatField;
import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.field.TildeField;

public abstract class LIS2A2Record {

	public enum Type {
		H, P, O, OBR, NET, OBX, MSH, EVN, R, Q, L, M,C, PID, ORM, PV1, DG1, SCH, PV2, FT1, ORC, QPD, RCP, SPM, SAC, NTE , TQ1 , TCD, MSA
	}

	public static LIS2A2Record fromStringAstm(String record) {

		Type recordType = null;
		String crRemoved = "";

		try {
			
			if(record.indexOf(CR) != -1)
			{
				crRemoved = record.substring(0, record.lastIndexOf(CR));
				
			}else if(record.indexOf(CR) == -1)
			{
				crRemoved = record.substring(record.indexOf(STX)+2, record.length());
			}
			
			
			String[] parts = crRemoved.split(Pattern.quote("|"));
			Map<Integer, Field> fields = new HashMap<>();
			for (int i = 0; i < parts.length; i++) {

				if (i == 0) 
				{

					if (parts[i].contains("H"))
						fields.put(i, new Field("H"));
					else if (parts[i].contains("P"))
						fields.put(i, new Field("P"));
					else if (parts[i].contains("O"))
						fields.put(i, new Field("O"));
					else if (parts[i].contains("C"))
						fields.put(i, new Field("C"));
					else if (parts[i].contains("R"))
						fields.put(i, new Field("R"));
					else if (parts[i].contains("L"))
						fields.put(i, new Field("L"));
					else if (parts[i].contains("Q"))
						fields.put(i, new Field("Q"));
					else if (parts[i].contains("M"))
						fields.put(i, new Field("M"));
					else if (parts[i].contains("MSH"))
						fields.put(i, new Field("MSH"));
					else if (parts[i].contains("SPM"))
						fields.put(i, new Field("SPM"));
					else if (parts[i].contains("SAC"))
						fields.put(i, new Field("SAC"));
					else if (parts[i].contains("OBR"))
						fields.put(i, new Field("OBR"));
					else if (parts[i].contains("ORC"))
						fields.put(i, new Field("ORC"));
					else if (parts[i].contains("TQ1"))
						fields.put(i, new Field("TQ1"));
					else if (parts[i].contains("OBX"))
						fields.put(i, new Field("OBX"));
					else if (parts[i].contains("INV"))
						fields.put(i, new Field("INV"));
					else if (parts[i].contains("TCD"))
						fields.put(i, new Field("TCD"));
					else if (parts[i].contains("OBR"))
						fields.put(i, new Field("OBR"));

					// parts[i] .replaceAll(STX + "", "").replaceAll(',' + "", "").replaceAll('[' +
					// "", "")
					// .replaceAll("\\d", "").trim();

				} else {
					fields.put(i, new Field(parts[i].trim()));

				}
			}

			if (fields.get(0) == null)
				return null;

			recordType = Type.valueOf(fields.get(0).toPrimitiveField().asString());
			DelimitedData<Field> data = new DelimitedData<>("|", fields);
			switch (recordType) {
			case H:
				return new HeaderASTMRecord(data);
			case P:
				return new PatientASTMRecord(data);
			case O:
				return new OrderASTMRecord(data);
			case R:
				return new ResultASTMRecord(data);
			case Q:
				return new QueryASTMRecord(data);
			case L:
				return new TerminationASTMRecord(data);
			case C:
			case M:
				return new CommentRecord(data);
			case OBR:
				return new OBRRecord(data);
			// case ORC:
			// return new ORCRecord(data);
			// case TQ1:
			// return new TQ1Record(data);
			// case SPM:
			// return new SPMRecord(data);
			// case SAC:
			// return new SACRecord(data);
			case OBX:
				return new OBXRecord(data);
			default:
				// throw new RuntimeException("Unexpected record type: " + recordType.name());
				return null;
			}
		} catch (Exception ex) {
			throw new RuntimeException("Unexpected record type: " + recordType.name());
		}
	}

	public static LIS2A2Record fromString(String record) {

		Type recordType = null;

		try {
			
			String crRemoved = record.substring(0, record.lastIndexOf(CR));
			String[] parts = crRemoved.split(Pattern.quote("|"));
			Map<Integer, Field> fields = new HashMap<>();
			for (int i = 0; i < parts.length; i++) {

				if (i == 0) {

					if (parts[i].contains("MSH"))
						fields.put(i, new Field("MSH"));
					else if (parts[i].contains("PID"))
						fields.put(i, new Field("PID"));
					else if (parts[i].contains("ORM"))
						fields.put(i, new Field("ORM"));
					else if (parts[i].contains("ORU"))
						fields.put(i, new Field("ORU"));
					else if (parts[i].contains("SCH"))
						fields.put(i, new Field("SCH"));
					else if (parts[i].contains("OBR"))
						fields.put(i, new Field("OBR"));
					else if (parts[i].contains("FT1"))
						fields.put(i, new Field("FT1"));
					else if (parts[i].contains("ORC"))
						fields.put(i, new Field("ORC"));
					else if (parts[i].contains("EVN"))
						fields.put(i, new Field("EVN"));
					else if (parts[i].contains("PV1"))
						fields.put(i, new Field("PV1"));
					else if (parts[i].contains("PV2"))
						fields.put(i, new Field("PV2"));
					else if (parts[i].contains("DG1"))
						fields.put(i, new Field("DG1"));
					else if (parts[i].contains("QPD"))
						fields.put(i, new Field("QPD"));
					else if (parts[i].contains("TQ1"))
						fields.put(i, new Field("TQ1"));
					else if (parts[i].contains("Q"))
						fields.put(i, new Field("Q"));
					else if (parts[i].contains("OBX"))
						fields.put(i, new Field("OBX"));
					else if (parts[i].contains("SAC"))
						fields.put(i, new Field("SAC"));
					else if (parts[i].contains("RCP"))
						fields.put(i, new Field("RCP"));
					else if (parts[i].contains("SPM"))
						fields.put(i, new Field("SPM"));
					else if (parts[i].contains("NTE"))
						fields.put(i, new Field("NTE"));
					else if (parts[i].contains("TCD"))
						fields.put(i, new Field("TCD"));
					else if (parts[i].contains("MSA"))
						fields.put(i, new Field("MSA"));

					// parts[i] .replaceAll(STX + "", "").replaceAll(',' + "", "").replaceAll('[' +
					// "", "")
					// .replaceAll("\\d", "").trim();

				} else {
					fields.put(i, new Field(parts[i].trim()));

				}
			}

			if (fields.get(0) == null)
				
				return null;

			recordType = Type.valueOf(fields.get(0).toPrimitiveField().asString());

			DelimitedData<Field> data = new DelimitedData<>("|", fields);
			switch (recordType) {
			case MSH:
				return new HL7_V24_HeaderRecord(data);
			case P:
			case PID:
				return new HL7_V24_PatientRecord(data);
			case O:
			case ORM:
				return new OrderRecord(data);
			case OBR:
				return new HL7_V24_OrderRecord(data);
			case R:
				return new ResultRecord(data);
			case OBX:
				return new HL7_V24_ResultRecord(data);
				
			case SPM:
				return new HL7_V24_SpecimenRecord(data);
			case SAC:
				return new SpecimenContainerDetail(data);
			///case ORC:
			//	return new HL7_V25_ObservationRequest(data);
			case Q:
				return new QueryRecord(data);
			case L:
				return new TerminationRecord(data);
			case EVN:
				return new HL7_V24_EventTypeRecord(data);
			case DG1:
				return new HL7_V24_DiagnosisRecord(data);
			case TQ1:
				return new HL7_V25_TimingQuantity();
			case TCD:
				return new HL7_V25_TestDetails();
			case PV1:
				return new HL7_V24_Visit1Record(data);
			case PV2:
				return new HL7_V24_Visit2Record(data);
			case C:
				return new CommentRecord(data);
			case NTE:
				return new HL7_V25_Comments(data);
			case SCH:
				return new HL7_V24_Appointment(data);
			case FT1:
				return new HL7_v24_FinancialTransaction(data);
			case ORC:
				return new HL7_V24_CommonOrder(data);
			case QPD:
				return new HL7_V25_PatientDemographicQueryRecord(data);
			case RCP:
				return new HL7_V25_PatientDemographicResponseRecord(data);
			case MSA:
				return new HL7_V25_MessageAcknowledgmentSegment(data);
			default:
				// throw new RuntimeException("Unexpected record type: " + recordType.name());
				return null;
			}
		} catch (Exception ex) {
			throw new RuntimeException("Unexpected record type: Segment MEssage " + recordType.name());
		}
	}

	protected final DelimitedData<Field> data;

	protected abstract LIS2A2Record getNew(DelimitedData<Field> data);

	protected LIS2A2Record(DelimitedData<Field> data) {
		this.data = data;
	}

	public LIS2A2Record() {
		this(new DelimitedData<>("|"));
	}

	public String asString() {
		return data.asString() + CR;
	}

	public LIS2A2Record setField(int fieldIndex, Field field) {
		return getNew(data.setField(fieldIndex - 1, field));
	}

	public LIS2A2Record setField(int fieldIndex, String value) {
		Field newField = new Field(value);
		return setField(fieldIndex, newField);
	}

	public LIS2A2Record setComponent(int fieldIndex, int componentIndex, Field value) {
		Field oldField = data.get(fieldIndex - 1);
		CompomentField newField = oldField == null ? new CompomentField().setComponent(componentIndex, value)
				: ((CompomentField) oldField).setComponent(componentIndex, value);
		return getNew(data.setField(fieldIndex - 1, newField));
	}

	public LIS2A2Record setComponent(int fieldIndex, int componentIndex, Field value, String delimiter) {
		Field oldField = data.get(fieldIndex - 1);
		CompomentField newField = oldField == null ? new CompomentField(delimiter).setComponent(componentIndex, value)
				: ((CompomentField) oldField).setComponent(componentIndex, value);
		return getNew(data.setField(fieldIndex - 1, newField));
	}

	public LIS2A2Record setComponent(int fieldIndex, int componentIndex, String value) {
		return setComponent(fieldIndex, componentIndex, new PrimitiveField(value));
	}

	public LIS2A2Record setComponent(int fieldIndex, int componentIndex, String value, String delimiter) {
		return setComponent(fieldIndex, componentIndex, new PrimitiveField(value), delimiter);
	}

	public LIS2A2Record addRepeat(int fieldIndex, Field value) {
		Field oldField = data.get(fieldIndex - 1);
		RepeatField newField = oldField == null ? new RepeatField().addRepeat(value)
				: ((RepeatField) oldField).addRepeat(value);
		return getNew(data.setField(fieldIndex - 1, newField));
	}

	public LIS2A2Record addRepeatForSysmexSuit(int fieldIndex, Field value) {
		Field oldField = data.get(fieldIndex - 1);
		TildeField newField = oldField == null ? new TildeField().addRepeat(value)
				: ((TildeField) oldField).addRepeat(value);
		return getNew(data.setField(fieldIndex - 1, newField));
	}

	public LIS2A2Record addRepeat(int fieldIndex, String value) {
		return addRepeat(fieldIndex, new PrimitiveField(value));
	}

	public LIS2A2Record addRepeatForSysmexSuit(int fieldIndex, String value) {
		return addRepeatForSysmexSuit(fieldIndex, new Field(value));
	}

	public String getFieldValue(int fieldIndex) {
		if (data.get(fieldIndex) != null)
			return data.get(fieldIndex).toPrimitiveField().asString();
		return "";
	}

	public String getComponentValue(int fieldIndex, int componentIndex) {

		if (data.get(fieldIndex) != null)
			if (data.get(fieldIndex).toString().contains("~")) {
				return data.get(fieldIndex).toComponentField("~").getComponent(componentIndex);
			} else {
				return data.get(fieldIndex).toComponentField().getComponent(componentIndex);
			}

		return "";
	}

	public String getComponentValue(int fieldIndex, int componentIndex, int subComponentIndex) {

		if (data.get(fieldIndex) != null)
			if (data.get(fieldIndex).toString().contains("^")) {
				String strData = data.get(fieldIndex).toComponentField("^").getComponent(componentIndex);
				return data.get(fieldIndex).toComponentField("^").getComponent(componentIndex);
			} else if (!data.get(fieldIndex).toString().contains("^")) {
				String strData = data.get(fieldIndex).toComponentField("~").getComponent(subComponentIndex + 1) != null
						? data.get(fieldIndex).toComponentField("~").getComponent(subComponentIndex + 1)
						: "";
				return strData;
			}

		return "";
	}

	public String getComponentValue(int fieldIndex, int componentIndex, String delimiter) {
		if (data.get(fieldIndex - 1) != null)
			if (data.get(fieldIndex - 1).toString().contains(delimiter)) {
				return data.get(fieldIndex - 1).toComponentField(delimiter).getComponent(componentIndex);
			} else {
				return data.get(fieldIndex - 1).toComponentField().getComponent(componentIndex);
			}
		

		return "";
	}

	public String getComponentValue2(int fieldIndex, int componentIndex, String delimiter) {
		if (data.get(fieldIndex) != null)
			if (data.get(fieldIndex).toString().contains(delimiter)) {
				return data.get(fieldIndex).toComponentField(delimiter).getComponent(componentIndex);
			} else {
				return data.get(fieldIndex).toComponentField().getComponent(componentIndex);
			}

		return "";
	}

	public List<Field> getRepeats(int fieldIndex) {
		if (fieldIndex > data.getSize()) {
			return null;
		}
		return data.get(fieldIndex - 1).asRepeatField("/").getRepeats();
	}

	public List<Field> getRepeats(int fieldIndex, String Delimiter) {
		return data.get(fieldIndex - 1).asRepeatField(Delimiter).getRepeats();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		LIS2A2Record that = (LIS2A2Record) o;

		return data.equals(that.data);

	}

	@Override
	public int hashCode() {
		return data.hashCode();
	}
}