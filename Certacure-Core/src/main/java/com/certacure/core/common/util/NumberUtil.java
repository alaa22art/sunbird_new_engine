package com.certacure.core.common.util;

import java.math.BigDecimal;

/**
 * NumberUtil.java
 * 
 * @author Abdullah Imran <aImran@certacuresolutions.com>
 * @since Dec/04/2018
 **/
public class NumberUtil {

	public static final BigDecimal MAX_PERCENTAGE = new BigDecimal("100");

	/**
	 * Get the smallest decimal number.
	 * 
	 * EX: Decimals: 2 -> number: 0.01
	 * Decimals: 4 -> number: 0.0001
	 * 
	 * @param decimals : amount of decimals
	 * @return BigDecimal
	 */
	public static BigDecimal getSmallestDecimal(Integer decimals) {
		if (decimals == null || decimals <= 0) {
			return BigDecimal.ZERO;
		}
		BigDecimal divisor = BigDecimal.ONE;
		for (int i = 0; i < decimals; i++) {
			divisor = divisor.divide(BigDecimal.TEN);
		}
		return divisor;
	}

	/**
	 * Validate if the incoming percentage is not more than 100% or less than 0%, if so then correct the percentage.
	 * 
	 * @param percentage : i.e. 91.52%
	 * @return a validated percentage
	 */
	public static BigDecimal validatePercentage(BigDecimal percentage) {

		if (percentage == null) {
			return BigDecimal.ZERO;
		}
		if (percentage.compareTo(MAX_PERCENTAGE) == 1) {
			percentage = MAX_PERCENTAGE;
		} else if (percentage.compareTo(BigDecimal.ZERO) == -1) {
			percentage = BigDecimal.ZERO;
		}
		return percentage;
	}

	/**
	 * Check if strNum is in numeric format
	 * 
	 * @param strNum
	 * @return true of numeric format
	 */
	public static boolean isNumeric(String strNum) {
		return strNum.matches("-?\\d+(\\.\\d+)?");
	}

	public static boolean between(BigDecimal value, BigDecimal from, BigDecimal to) {
		return value.compareTo(from) >= 0 && value.compareTo(to) <= 0;
	}

	public static boolean isZero(BigDecimal value) {
		return value.compareTo(BigDecimal.ZERO) == 0;
	}

	public static BigDecimal getNonNullOrZero(BigDecimal value) {
		return value != null ? value : BigDecimal.ZERO;
	}

	public static Long fromIntegerObjToLong(Object integerObj) {
		return integerObj != null && integerObj instanceof Integer ? ((Integer) integerObj).longValue() : null;
	}

}
