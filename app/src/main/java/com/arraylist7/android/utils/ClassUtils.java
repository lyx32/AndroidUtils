package com.arraylist7.android.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ClassUtils {


    private static boolean USE_CACHE = true;
    private static Map<String, Object> cacheMap = new HashMap<>();


    public static boolean isUseCache() {
        return USE_CACHE;
    }

    public static void setUseCache(boolean useCache) {
        USE_CACHE = useCache;
    }

    public static String getClassName(Class clazz) {
        return clazz.getSimpleName();
    }

    public static String getPackageNameAndClassName(Class clazz) {
        return clazz.getName();
    }


    /**
     * 设置静态类属性
     *
     * @param clazz     要设置的class
     * @param fieldName 属性名
     * @param val       值
     */
    public static void setValue(Class clazz, String fieldName, Object val) throws IllegalAccessException, NoSuchFieldException {
        setValue(clazz, null, fieldName, val);
    }

    /**
     * 设置实例属性
     *
     * @param instance  要设置的对象，
     * @param fieldName 属性名
     * @param val       值
     */
    public static void setValue(Object instance, String fieldName, Object val) throws IllegalAccessException, NoSuchFieldException {
        setValue(instance.getClass(), instance, fieldName, val);
    }

    /**
     * 设置属性
     *
     * @param clazz     要设置的class
     * @param instance  要设置的对象，如果是静态类则为null
     * @param fieldName 属性名
     * @param val       值
     */
    private static void setValue(Class clazz, Object instance, String fieldName, Object val) throws IllegalAccessException, NoSuchFieldException {
        if (null == clazz)
            throw new NullPointerException("clazz 不能为空");
        Field field = clazz.getDeclaredField(fieldName);
        if (null == field)
            field = clazz.getField(fieldName);
        if (null == field) {
            throw new NoSuchFieldError(fieldName + " 不存在!");
        }
        field.setAccessible(true);
        field.set(instance, val);
        String key = null == instance ? clazz.toString() + "_" + fieldName : instance.toString() + "_" + fieldName;
        cacheMap.remove(key);
        cacheMap.put(key, val);
    }

    /**
     * 获取静态类属性值
     *
     * @param clazz     要获取的class
     * @param fieldName 属性名
     * @return
     */
    public static <T> T getValue(Class clazz, String fieldName) throws IllegalAccessException, NoSuchFieldException {
        return getValue(clazz, null, fieldName);
    }

    /**
     * 获取实例属性值
     *
     * @param obj       要获取的实例
     * @param fieldName 属性名
     * @return
     */
    public static <T> T getValue(Object obj, String fieldName) throws IllegalAccessException, NoSuchFieldException {
        return getValue(obj.getClass(), obj, fieldName);
    }

    /**
     * 反射获取属性值
     *
     * @param clazz     要获取的class
     * @param obj       要或者的实例 静态类则为null
     * @param fieldName 属性名
     * @return
     */
    private static <T> T getValue(Class clazz, Object obj, String fieldName) throws IllegalAccessException, NoSuchFieldException {
        if (null == clazz)
            throw new NullPointerException("clazz 不能为空");
        String key = null == obj ? clazz.toString() + "_" + fieldName : obj.toString() + "_" + fieldName;

        Object cache = cacheMap.get(key);
        if (null != cache && USE_CACHE)
            return (T) cache;
        Field field = clazz.getDeclaredField(fieldName);
        if (null == field)
            field = clazz.getField(fieldName);
        if (null == field)
            throw new NoSuchFieldError(fieldName + " 不存在!");
        field.setAccessible(true);
        cache = field.get(obj);
        if (USE_CACHE)
            cacheMap.put(key, cache);
        return (T) cache;
    }


    /**
     * 调用静态方法
     *
     * @param clazz      要调用的class
     * @param methodName 方法名
     * @return
     * @throws InvocationTargetException
     */
    public static <T> T invoke(Class clazz, String methodName) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return invoke(clazz, null, methodName, null);
    }

    /**
     * 调用静态方法
     *
     * @param clazz      要调用的class
     * @param methodName 方法名
     * @param params     方法参数
     * @return
     * @throws InvocationTargetException
     */
    public static <T> T invoke(Class clazz, String methodName, Object params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return invoke(clazz, null, methodName, StringUtils.asArray(params));
    }

    /**
     * 调用静态方法
     *
     * @param clazz      要调用的class
     * @param methodName 方法名
     * @param params     方法参数
     * @return
     * @throws InvocationTargetException
     */
    public static <T> T invoke(Class clazz, String methodName, Object[] params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return invoke(clazz, null, methodName, params);
    }

    /**
     * 调用静态方法
     *
     * @param clazz              要调用的class
     * @param methodName         方法名
     * @param parameterTypeClass 方法参数类型的class
     * @param params             方法参数
     * @return
     * @throws InvocationTargetException
     */
    public static <T> T invoke(Class clazz, String methodName, Class<?> parameterTypeClass, Object params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return invoke(clazz, null, methodName, StringUtils.asArray(parameterTypeClass), StringUtils.asArray(params));
    }

    /**
     * 调用静态方法
     *
     * @param clazz              要调用的class
     * @param methodName         方法名
     * @param parameterTypeClass 方法参数类型的class
     * @param params             方法参数
     * @return
     * @throws InvocationTargetException
     */
    public static <T> T invoke(Class clazz, String methodName, Class<?>[] parameterTypeClass, Object[] params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return invoke(clazz, null, methodName, parameterTypeClass, params);
    }

    /**
     * 调用方法
     *
     * @param obj        要调用的实例
     * @param methodName 方法名
     * @return
     * @throws InvocationTargetException
     */
    public static <T> T invoke(Object obj, String methodName) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return invoke(obj.getClass(), obj, methodName, null);
    }


    /**
     * 调用实例方法
     *
     * @param obj        要调用的实例
     * @param methodName 方法名
     * @param params     方法参数
     * @return
     * @throws InvocationTargetException
     */
    public static <T> T invoke(Object obj, String methodName, Object params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return invoke(obj.getClass(), obj, methodName, StringUtils.asArray(params));
    }

    /**
     * 调用实例方法
     *
     * @param obj        要调用的实例
     * @param methodName 方法名
     * @param params     方法参数
     * @return
     * @throws InvocationTargetException
     */
    public static <T> T invoke(Object obj, String methodName, Object[] params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return invoke(obj.getClass(), obj, methodName, params);
    }

    /**
     * 调用实例方法
     *
     * @param obj        要调用的实例
     * @param methodName 方法名
     * @param paramsType 方法参数类型
     * @param params     方法参数
     * @return
     * @throws InvocationTargetException
     */
    public static <T> T invoke(Object obj, String methodName, Class<?> paramsType, Object params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return invoke(obj.getClass(), obj, methodName, StringUtils.asArray(paramsType), StringUtils.asArray(params));
    }


    /**
     * 调用实例方法
     *
     * @param obj                要调用的实例
     * @param methodName         方法名
     * @param parameterTypeClass 方法参数类型的class
     * @param params             方法参数
     * @return
     * @throws InvocationTargetException
     */
    public static <T> T invoke(Object obj, String methodName, Class<?>[] parameterTypeClass, Object[] params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return invoke(obj.getClass(), obj, methodName, parameterTypeClass, params);
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
     */
    private static <T> T invoke(Class clazz, Object obj, String methodName, Object[] params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Class[] parameterTypeClass = null;
        if (null != params) {
            parameterTypeClass = new Class[params.length];
            for (int i = 0; i < params.length; i++) {
                Object paramObj = params[i];
                Class<?> paramObjClass = paramObj.getClass();
                if (paramObjClass == Integer.class || paramObjClass == int.class)
                    paramObjClass = int.class;
                else if (paramObjClass == Float.class || paramObjClass == float.class)
                    paramObjClass = float.class;
                else if (paramObjClass == Double.class || paramObjClass == double.class)
                    paramObjClass = double.class;
                else if (paramObjClass == Long.class || paramObjClass == long.class)
                    paramObjClass = long.class;
                else if (paramObjClass == Short.class || paramObjClass == short.class)
                    paramObjClass = short.class;
                else if (paramObjClass == Boolean.class || paramObjClass == boolean.class)
                    paramObjClass = boolean.class;
                else if (paramObjClass == Byte.class || paramObjClass == byte.class)
                    paramObjClass = byte.class;
                parameterTypeClass[i] = paramObjClass;
            }
        }
        return invoke(clazz, obj, methodName, parameterTypeClass, params);

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
     */
    private static <T> T invoke(Class clazz, Object obj, String methodName, Class[] parameterTypeClass, Object[] params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if (null == clazz)
            throw new NoSuchFieldError("clazz 不能为空");
        Method method = clazz.getDeclaredMethod(methodName, parameterTypeClass);
        if (null == method)
            method = clazz.getMethod(methodName, parameterTypeClass);
        if (null == method) {
            throw new NullPointerException(clazz.toString() + " 中不存在 " + methodName + " 方法！");
        }
        Object result = null;
        method.setAccessible(true);
        if (null != params && 0 != params.length)
            result = method.invoke(obj, params);
        else
            result = method.invoke(obj);
        return (T) result;
    }

    public static void clear() {
        cacheMap.clear();
    }
}
