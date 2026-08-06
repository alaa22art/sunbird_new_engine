package com.sunbird.core.common.data.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.sunbird.core.common.util.JSONUtil;
import com.sunbird.core.common.util.StringUtil;

/**
 * The TransField class implements the logic of translated field.
 *
 */
public class TransField implements Serializable, Map<String, String> {

	private static final long serialVersionUID = 1L;

	protected Map<String, String> transField = new HashMap<>();

	private static final String EMPTY_FIELD = "";

	@Override
	public int size() {
		String value = JSONUtil.convertTransFieldToJson(this);
		return StringUtil.isEmpty(value) ? 0 : value.length();
	}

	@Override
	public boolean isEmpty() {
		return transField.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return transField.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return transField.containsValue(value);
	}

	@Override
	public String get(Object key) {
		if (transField.get(key) == null) {
			transField.put((String) key, EMPTY_FIELD);
		}
		return transField.get(key);
	}

	@Override
	public String put(String key, String value) {
		return transField.put(key, value);
	}

	@Override
	public String remove(Object key) {
		return transField.remove(key);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void putAll(Map m) {
		transField.putAll(m);
	}

	@Override
	public void clear() {
		transField.clear();
	}

	@Override
	public Set<String> keySet() {
		return transField.keySet();
	}

	@Override
	public Collection<String> values() {
		return transField.values();
	}

	@Override
	public Set<Entry<String, String>> entrySet() {
		return transField.entrySet();
	}

	public String getKeyOrAny(String key) {
		String value = this.get(key);
		if (StringUtil.isEmpty(value)) {
			for (Entry<String, String> entry : this.entrySet()) {
				if (!StringUtil.isEmpty(entry.getValue())) {
					return entry.getValue();
				}
			}
		}
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((transField == null) ? 0 : transField.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TransField)) {
			return false;
		}
		TransField other = (TransField) obj;
		if (transField == null) {
			if (other.transField != null) {
				return false;
			}
		} else if (!this.keySet().equals(other.keySet())) { // compare keys
			return false;
		} else {
			for (Object key : this.keySet()) {
				if (!this.get(key).equals(other.get(key))) { // compare values
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return JSONUtil.convertTransFieldToJson(this);
	}

}
