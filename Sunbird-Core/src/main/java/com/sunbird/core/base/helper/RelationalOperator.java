package com.certacure.core.base.helper;

import java.util.HashMap;
import java.util.Map;

public enum RelationalOperator {

	EQUAL("="),
	NOT_EQUAL("!="),
	LESS_THAN("<"),
	LESS_THAN_OR_EQUAL("<="),
	GREATER_THAN(">"),
	GREATER_THAN_OR_EQUAL(">="),
	IN("in"),
	NOT_IN("notin");

	private String text;

	private static final Map<String, RelationalOperator> BY_TEXT = new HashMap<>();

	private RelationalOperator(String text) {
		this.setText(text);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public static RelationalOperator valueOfText(String text) {
		if (BY_TEXT.isEmpty()) {
			for (RelationalOperator e : values()) {
				BY_TEXT.put(e.text, e);
			}
		}
		return BY_TEXT.get(text);
	}

}
