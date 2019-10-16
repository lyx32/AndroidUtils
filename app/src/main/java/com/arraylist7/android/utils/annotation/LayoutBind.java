package com.arraylist7.android.utils.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.CLASS;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * 用于自动绑定RecyclerView的注解
 */

@Target(ElementType.TYPE)
@Retention(RUNTIME)
public @interface LayoutBind {
    /**
     * 要自动绑定的layoutId
     * @return
     */
    int value();

}
