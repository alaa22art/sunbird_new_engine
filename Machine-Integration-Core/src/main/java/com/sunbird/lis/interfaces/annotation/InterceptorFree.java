package com.sunbird.lis.interfaces.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * InterceptorFree.java
 * 
 * Used in ServiceInterceptor to exclude CLASSES AND METHODS from AOP.
 * 
 **/
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface InterceptorFree {
}