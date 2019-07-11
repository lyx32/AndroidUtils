package com.arraylist7.android.utils.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RColor {
    int value() default -1;

    /**
     * 将此颜色作为textColor设置给指定的view
     * @return
     */
    int setTextColor() default -1;

    /**
     * 将此颜色作为background设置给指定的view
     * @return
     */
    int setBackgroundColor() default -1;
}
