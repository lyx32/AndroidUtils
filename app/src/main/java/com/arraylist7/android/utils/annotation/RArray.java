package com.arraylist7.android.utils.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RArray {
    int value() default -1;

    /**
     * 是否是资源类型，主要用于图片
     * @return
     */
    boolean isResources() default false;
}
