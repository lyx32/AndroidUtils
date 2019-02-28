package com.arraylist7.android.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClassUtils {

    public static <T> T newInstance(String clazz) {
        Object obj = null;
        try {
            Class cls = Class.forName(clazz);
            obj = cls.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return (T)obj;
    }

    public static <T> T newInstance(Class clazz) {
        Object obj = null;
        try {
            obj = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T)obj;
    }

    public static String getPackageNameAndClassName(Class clazz) {
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

    public static Method[] getMethods(Class clazz) {
        if (null != clazz)
            return clazz.getMethods();
        return null;
    }

    public static Method[] getDeclaredMethods(Class clazz) {
        if (null != clazz)
            return clazz.getDeclaredMethods();
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


    public static Method getMethod(Class clazz, String name) {
        if (null != clazz) {
            try {
                return clazz.getMethod(name);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Method getDeclaredMethod(Class clazz, String name) {
        if (null != clazz) {
            try {
                return clazz.getDeclaredMethod(name);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 反射设置静态类属性
     *
     * @param clazz     要设置的class
     * @param fieldName 属性名
     * @param val       值
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void setValue(Class clazz, String fieldName, Object val) throws IllegalAccessException, InstantiationException {
        setValue(clazz, null, fieldName, val);
    }

    /**
     * 反射设置实例属性
     *
     * @param instance  要设置的对象，如果是静态类则为null
     * @param fieldName 属性名
     * @param val       值
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void setValue(Object instance, String fieldName, Object val) throws IllegalAccessException, InstantiationException {
        setValue(instance.getClass(), instance, fieldName, val);
    }

    /**
     * 反射设置属性
     *
     * @param clazz     要设置的class
     * @param instance  要设置的对象，如果是静态类则为null
     * @param fieldName 属性名
     * @param val       值
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static void setValue(Class clazz, Object instance, String fieldName, Object val) throws IllegalAccessException {
        if (null == clazz)
            throw new NullPointerException("clazz 不能为空");
        Field field = getDeclaredField(clazz, fieldName);
        if (null == field)
            field = getField(clazz, fieldName);
        if (null == field) {
            throw new NoSuchFieldError(fieldName + " 不存在!");
        }
        field.setAccessible(true);
        field.set(instance, val);
    }

    /**
     * 反射获取静态类属性值
     *
     * @param clazz     要获取的class
     * @param fieldName 属性名
     * @return
     * @throws IllegalAccessException
     */
    public static <T> T getValue(Class clazz, String fieldName) throws IllegalAccessException {
        return getValue(clazz, null, fieldName);
    }

    /**
     * 反射获取实例属性值
     *
     * @param obj       要或者的实例
     * @param fieldName 属性名
     * @return
     * @throws IllegalAccessException
     */
    public static <T> T getValue(Object obj, String fieldName) throws IllegalAccessException {
        return getValue(obj.getClass(), obj, fieldName);
    }

    /**
     * 反射获取属性值
     *
     * @param clazz     要获取的class
     * @param obj       要或者的实例 静态类则为null
     * @param fieldName 属性名
     * @return
     * @throws IllegalAccessException
     */
    public static <T> T getValue(Class clazz, Object obj, String fieldName) throws IllegalAccessException {
        if (null == clazz)
            throw new NullPointerException("clazz 不能为空");
        Field field = getDeclaredField(clazz, fieldName);
        if (null == field)
            field = getField(clazz, fieldName);
        if (null == field)
            throw new NoSuchFieldError(fieldName + " 不存在!");
        field.setAccessible(true);
        return (T) field.get(obj);
    }


    /**
     * 反射调用方法
     *
     * @param clazz      要调用的class
     * @param obj        要调用的实例，静态类则为null
     * @param methodName 方法名
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static <T> T invoke(Class clazz, Object obj, String methodName) throws InvocationTargetException, IllegalAccessException {
        return invoke(clazz, obj, methodName, null);
    }


    /**
     * 反射调用静态方法
     *
     * @param clazz      要调用的class
     * @param methodName 方法名
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static <T> T invoke(Class clazz, String methodName) throws InvocationTargetException, IllegalAccessException {
        return invoke(clazz, null, methodName, null);
    }

    /**
     * 反射调用静态方法
     *
     * @param clazz      要调用的class
     * @param methodName 方法名
     * @param params     方法参数
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static <T> T invoke(Class clazz, String methodName, Object[] params) throws InvocationTargetException, IllegalAccessException {
        return invoke(clazz, null, methodName, params);
    }


    /**
     * 反射调用实例方法
     *
     * @param obj        要调用的实例
     * @param methodName 方法名
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static <T> T invoke(Object obj, String methodName) throws InvocationTargetException, IllegalAccessException {
        return invoke(obj.getClass(), obj, methodName, null);
    }

    /**
     * 反射调用实例方法
     *
     * @param obj        要调用的实例
     * @param methodName 方法名
     * @param params     方法参数
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static <T> T invoke(Object obj, String methodName, Object[] params) throws InvocationTargetException, IllegalAccessException {
        return invoke(obj.getClass(), obj, methodName, params);
    }

    /**
     * 反射调用方法
     *
     * @param clazz      要调用的class
     * @param obj        要调用的实例，静态类则为null
     * @param methodName 方法名
     * @param params     方法参数
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static <T> T invoke(Class clazz, Object obj, String methodName, Object[] params) throws InvocationTargetException, IllegalAccessException {
        if (null == clazz)
            throw new NoSuchFieldError("clazz 不能为空");
        Method method = getDeclaredMethod(clazz, methodName);
        if (null == method)
            method = getMethod(clazz, methodName);
        Object result = null;
        if (null != method) {
            method.setAccessible(true);
            if (null != params && 0 != params.length)
                result = method.invoke(obj, params);
            else
                result = method.invoke(obj);
        }
        return (T) result;
    }
}
