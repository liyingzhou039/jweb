package com.jweb.system.persistent.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Field {
	//描述
	String name() default "";
	//数据库
	String type() default "";
	int size() default 75;
	int digits() default 0;
	//验证
	boolean required() default false;
	String[] validType() default {};
}
