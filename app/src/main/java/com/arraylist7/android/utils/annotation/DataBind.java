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
public @interface DataBind {

    /**
     * 将被注解的属性通过setText的方式设置到对应的View上
     * @return
     */
    int setText() default -1;


    /**
     * 将被注解的属性通过setTag的方式设置到对应的View上
     * @return
     */
    int setTag() default -1;


    /**
     * 将被注解的属性通过图片框架的方式加载到对应的View上
     * @return
     */
    int setImage() default -1;

    /**
     * 图片的宽度
     * @return
     */
    int imageWidth() default -1;
    /**
     * 图片的高度
     * @return
     */
    int imageHeight() default -1;
    /**
     * 是否需要选中（y,yes,true,t，都表示true）
     * @return
     */
    int check() default -1;

}