package com.arraylist7.android.utils.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RString {
	int value() default -1;

	/**
	 * 将此值绑定到指定wiew的setText上
	 * @return
	 */
	int setText() default -1;

	/**
	 * 将此值绑定到指定wiew的setTag
	 * @return
	 */
	int setTag() default -1;
}
