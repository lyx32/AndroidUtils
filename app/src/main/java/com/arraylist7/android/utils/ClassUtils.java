package com.arraylist7.android.utils;

import java.io.File;
import java.lang.reflect.Field;

public class ClassUtils {

    public static Object forClass(Class clazz) {
        Object obj = null;
        try {
            obj = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static String getClassAndPackageName(Class clazz) {
        return clazz.getName();
    }

    public static String getClassName(Class clazz) {
        return clazz.getSimpleName();
    }

    public static Field[] getFields(Class clazz) {
        if (null != clazz)
            return clazz.getFields();
        return null;
    }

    public static Field[] getDeclaredFields(Class clazz) {
        if (null != clazz)
            return clazz.getDeclaredFields();
        return null;
    }


    public static Field getField(Class clazz, String name) {
        if (null != clazz) {
            try {
                return clazz.getField(name);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Field getDeclaredField(Class clazz, String name) {
        if (null != clazz) {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static void setValue(Field field, Object object, Object value) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(object, value);
    }

    public static void setValue(Class clazz, String fieldName, Object val) throws Throwable {
        if (null == clazz)
            throw new NoSuchFieldError("clazz 不能为空");
        Object obj = clazz.newInstance();
        Field field = getField(clazz, fieldName);
        if (null == field)
            field = getDeclaredField(clazz, fieldName);
        if (null == field) {
            throw new NoSuchFieldError(fieldName + " 不存在!");
        }
        setValue(field, obj, val);
    }


    public static Object getValue(Field field, Object object) throws IllegalAccessException {
        field.setAccessible(true);
        return field.get(object);
    }


    public static Object getValue(Object obj, String fieldName) throws Throwable {
        if (null == obj)
            throw new NoSuchFieldError("obj 不能为空");
        Class clazz = obj.getClass();
        Field field = getField(clazz, fieldName);
        if (null == field)
            field = getDeclaredField(clazz, fieldName);
        if (null == field)
            throw new NoSuchFieldError(fieldName + " 不存在!");
        return getValue(field, obj);
    }


}
