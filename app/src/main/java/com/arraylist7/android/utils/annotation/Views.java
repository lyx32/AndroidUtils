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
     * 是否从@Params 中提取参数作为setText的值
     * @return
     */
    String setText() default "";

    /**
     * 是否从@Params 中提取参数作为setTag的值
     * @return
     */
    String setTag() default "";
}
