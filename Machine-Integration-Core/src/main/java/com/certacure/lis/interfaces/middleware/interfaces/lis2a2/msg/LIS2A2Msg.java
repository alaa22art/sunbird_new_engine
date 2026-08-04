package com.certacure.lis.interfaces.middleware.interfaces.lis2a2.msg;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;

import com.certacure.lis.interfaces.middleware.interfaces.lis2a2.record.LIS2A2Record;

public abstract class LIS2A2Msg {

	protected final List<LIS2A2Record> records;

	public LIS2A2Msg(List<LIS2A2Record> records) {
		this.records = unmodifiableList(records);

	}

	@SuppressWarnings("unchecked")
	public <T extends LIS2A2Record> List<T> getRecords(LIS2A2Record parentLevel, Class<T> recordType) {
		List<T> result = new ArrayList<>();
		for (int i = records.indexOf(parentLevel) + 1; i < records.size() && !parentLevel.getClass().equals(records.get(i).getClass()); i++)
			if (records.get(i).getClass().equals(recordType))
				result.add((T) records.get(i));
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends LIS2A2Record> List<T> getRecords(Class<T> recordType) {
		List<T> result = new ArrayList<>();
		for (int i = 0; i < records.size(); i++)
			if (records.get(i).getClass().equals(recordType))
				result.add((T) records.get(i));
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends LIS2A2Record> List<T> getAllRecords() {
		List<T> result = new ArrayList<>();
		for (int i = 0; i < records.size(); i++)
			//if (records.get(i).getClass().equals(recordType))
				result.add((T) records.get(i));
		return result;
	}

	public String asString() {
		return records.stream().map(LIS2A2Record::asString).collect(joining());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		LIS2A2Msg lis2A2Msg = (LIS2A2Msg) o;

		return records.equals(lis2A2Msg.records);

	}

	@Override
	public int hashCode() {
		return records.hashCode();
	}
}