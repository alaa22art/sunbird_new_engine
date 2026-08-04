package com.certacure.core.common.annotation;

import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.certacure.core.common.util.CollectionUtil;
import com.certacure.core.common.util.StringUtil;

public class MapNotNullValidator implements ConstraintValidator<MapNotNull, Map<String, String>> {

	@Override
	public boolean isValid(Map<String, String> value, ConstraintValidatorContext context) {
		if (CollectionUtil.isMapEmpty(value)) {
			return false;
		}
		for (Map.Entry<String, String> entry : value.entrySet()) {
			if (!StringUtil.isEmpty(entry.getValue())) {
				return true;
			}
		}
		return false;
	}

}
