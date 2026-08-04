package com.certacure.core.common.util;

import java.beans.Introspector;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.CaseFormat;

/**
 * 
 * StringUtilities.java
 * 
 * @author Wa'el Abu Rahmeh <waburahemh@certacuresolutions.com>
 * @since 21/05/2017
 */

public class StringUtil {

	private static final SecureRandom random = new SecureRandom();

	public static String joiner(String delimiter, String... values) {
		if (values == null) {
			return "";
		}
		return joiner(delimiter, Arrays.asList(values));
	}

	public static String joiner(String delimiter, Collection<String> values) {
		if (isEmpty(delimiter) || CollectionUtil.isCollectionEmpty(values)) {
			return "";
		}
		return values.stream().filter(v -> !isEmpty(v)).collect(Collectors.joining(delimiter));
	}

	/**
	 * Check whether the given string is null or its trimmed length is zero
	 * 
	 * @param data input string
	 * @return boolean
	 */
	public static boolean isEmpty(String data) {
		if (data == null || data.trim().length() == 0) {
			return true;
		}
		return false;
	}

	public static boolean isNotEmpty(String data) {
		if (isEmpty(data)) {
			return false;
		}
		return true;
	}

	// convert from UTF-8 -> internal Java String format
	public static String convertFromUTF8(String s) {
		String out = null;
		try {
			out = new String(s.getBytes("ISO-8859-1"), StandardCharsets.UTF_8.name());
		} catch (java.io.UnsupportedEncodingException e) {
			return s;
		}
		return out;
	}

	// convert from internal Java String format -> UTF-8
	public static String convertToUTF8(String s) {
		String out = null;
		try {
			out = new String(s.getBytes(StandardCharsets.UTF_8.name()), "ISO-8859-1");
		} catch (java.io.UnsupportedEncodingException e) {
			return s;
		}
		return out;
	}

	public static double validateNumber(String input) {
		if (isEmpty(input)) {
			throw new NumberFormatException();
		}
		try {
			double d = Double.parseDouble(input);
			return d;
		} catch (NumberFormatException e) {
			throw new NumberFormatException("Not a valid Number");
		}
	}

	public static String clearSpaces(String str) {
		return str.replaceAll("\\s", "");
	}

	public static String lowerFirst(String str) {
		return Introspector.decapitalize(str);
	}

	public static String toLowerCamelCase(String str) {
		return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str);
	}

	public static String toUpperUnderscore(String str) {
		//		return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str);
		return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, str); // returns "constantName"
	}

	public static String getTextStyleClass(String value) {
		if (StringUtil.isNotEmpty(value) && value.contains("::")) {
			String[] splitted = value.split("::");
			return "<span class='" + splitted[1] + "'>" + splitted[0] + "</span>";
		}

		return value;
	}

	public static String getRandomSpecialChars(int count) {
		return StringUtil.generateRandoms(count,
				(char) 33, (char) 47, //!"#$%&'()*+,-./
				(char) 58, (char) 64, //:;<=>?@
				(char) 91, (char) 96, //[\]^_`
				(char) 123, (char) 126); //{|}~
	}

	public static String getRandomAlphabets(int count, boolean upperCase) {
		char min = 'a';
		char max = 'z';
		if (upperCase) {
			min = 'A';
			max = 'Z';
		}
		return generateRandoms(count, min, max);
	}

	public static String getRandomNumbers(int count) {
		return generateRandoms(count, '0', '9');
	}

	public static String generateAlphanumeric(int length, boolean upperCase) {
		char min = 'a';
		char max = 'z';
		if (upperCase) {
			min = 'A';
			max = 'Z';
		}
		return generateRandoms(length, '0', '9', min, max);
	}

	/**
	 * 
	 * @param length length of desired random string
	 * @param charPairs pairs of chars starting with lower char first, ex: ['0', '9', 'A', 'Z'] will generate alphanumeric chars
	 * @return A string consisting of the char-pairs sent and of the length 'length'
	 */
	public static String generateRandoms(int length, char... charPairs) {
		if (charPairs == null || charPairs.length % 2 == 1) {
			throw new IllegalArgumentException("The charPairs argument is either null or not in pairs!");
		}
		int min = 127; //max of normal ASCII table
		int max = 0;
		for (int i = 0; i < charPairs.length; i++) {
			if (charPairs[i] < min) {
				min = charPairs[i];
			} else if (charPairs[i] > max) {
				max = charPairs[i];
			}
		}

		String generatedString = random	.ints(min, max + 1)
										.filter(c ->
											{
												for (int i = 0; i < charPairs.length; i += 2) {
													if (c >= charPairs[i] && c <= charPairs[i + 1]) {
														return true;
													}
												}
												return false;
											})
										.limit(length)
										.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
										.toString();
		return shuffleString(generatedString);
	}

	public static String shuffleString(String input) {
		if (input == null) {
			throw new IllegalArgumentException("Input may not be null");
		} else if (input.length() <= 1) {
			return input;
		}
		List<String> letters = Arrays.asList(input.split(""));
		Collections.shuffle(letters);
		return String.join("", letters);
	}

	/**
	 * Create a map with a number(start from 0) surrounded with curly brackets and a value. Keys are used for system labels to be
	 * replaced as a dynamic parameters in the labels.
	 * 
	 * @param parameterList
	 * 
	 * @return map
	 */
	public static Map<String, String> generateParameterMap(List<String> parameterList) {
		Map<String, String> parameterMap = new HashMap<>();
		if (CollectionUtil.isCollectionEmpty(parameterList)) {
			return parameterMap;
		}
		for (int i = 0; i < parameterList.size(); i++) {
			parameterMap.put("{" + i + "}", parameterList.get(i));
		}
		return parameterMap;
	}

}
