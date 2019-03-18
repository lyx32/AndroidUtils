package com.arraylist7.android.utils.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解绑定View
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Views {
    /**
     * 要绑定的id
     * @return
     */
    int value();

    /**
     * 为R.string.xxx 中的参数赋值<br/>
     * 该参数与rString同时出现，用String.formart(rString(),rStringParams())来作为setText的值
     * @return
     */
    String[] rStringParams() default {};

    /**
     * 用R.string.xxx作为text的值
     * @return
     */
    int rString() default -1;

    /**
     * 是否从@Params 中提取参数作为setTag的值
     * @return
     */
    String setTag() default "";

    /**
     * 是否从@Params 中提取参数作为setText的值
     * @return
     */
    String setText() default "";
}
