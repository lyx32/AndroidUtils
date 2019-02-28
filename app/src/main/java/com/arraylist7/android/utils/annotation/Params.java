package com.arraylist7.android.utils.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 自动绑定参数
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Params {
    /**
     * 要绑定的参数key
     * @return
     */
    String value();

    /**
     * 将此参数值绑定到指定wiew的setText上
     * @return
     */
    int setText() default -1;

    /**
     * 将此参数值绑定到指定wiew的setTag
     * @return
     */
    int setTag() default -1;
}