package com.sunbird.core.common.helper;

public class PasswordStrengthWrapper {

	private Integer length;
	private Double score;
	private Boolean lower;
	private Boolean upper;
	private Boolean digits;
	private Boolean special;
	private PasswordStrength strength;

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public Boolean getLower() {
		return lower;
	}

	public void setLower(Boolean lower) {
		this.lower = lower;
	}

	public Boolean getUpper() {
		return upper;
	}

	public void setUpper(Boolean upper) {
		this.upper = upper;
	}

	public Boolean getDigits() {
		return digits;
	}

	public void setDigits(Boolean digits) {
		this.digits = digits;
	}

	public Boolean getSpecial() {
		return special;
	}

	public void setSpecial(Boolean special) {
		this.special = special;
	}

	public PasswordStrength getStrength() {
		return strength;
	}

	public void setStrength(PasswordStrength strength) {
		this.strength = strength;
	}

}
