package com.sunbird.core.common.helper;

import java.math.BigDecimal;

/**
 * 
 * CustomComparable.java
 */

public interface CustomComparable<T> extends Comparable<T> {

	BigDecimal getFromValue();

	void setFromValue(BigDecimal fromValue);

	BigDecimal getToValue();

	void setToValue(BigDecimal toValue);

	BigDecimal getStep();
}
