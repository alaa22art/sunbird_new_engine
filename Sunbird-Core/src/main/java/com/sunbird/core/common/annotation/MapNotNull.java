package com.sunbird.core.common.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = { MapNotNullValidator.class })
public @interface MapNotNull {

	String message() default "Must not be null";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
