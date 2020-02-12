package com.arraylist7.android.utils;

import android.view.animation.Animation;

import androidx.annotation.NonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassUtils {


    private static Map<String, SoftReference<Field>> fieldCache = new HashMap<>();

    public static String getClassName(Class clazz) {
        return clazz.getSimpleName();
    }

    public static String getPackageNameAndClassName(Class clazz) {
        return clazz.getName();
    }


    public static <A extends Annotation> List<Annotation> getFieldAnnotation(Class<?> clazz, Class<A> annotationClass) {
        List<Annotation> list = new ArrayList<>();
        List<Field> fields = StringUtils.asList(clazz.getFields());
        Field[] fieldList2 = clazz.getDeclaredFields();
        for (Field f : fieldList2) {
            if (!fields.contains(f)) {
                fields.add(f);
            }
        }
        for (Field field : fields) {
            Annotation annotation = field.getAnnotation(annotationClass);
            if (null != annotation)
                list.add(annotation);
        }
        return list;
    }


    /**
     * 拷贝类
     *
     * @param sourceObj 被拷贝的类
     * @param newClass  新类
     * @param <T>
     * @return
     */
    public static <T> T copy(@NonNull Object sourceObj, @NonNull Class<T> newClass) {
        T newObj = null;
        try {
            newObj = newClass.newInstance();
            List<Field> newFieldList = StringUtils.asList(newClass.getFields());
            Field[] newFieldList2 = newClass.getDeclaredFields();
            for (Field f : newFieldList2) {
                if (!newFieldList.contains(f)) {
                    newFieldList.add(f);
                }
            }

            Class sourceClass = sourceObj.getClass();
            for (Field f : newFieldList) {
                Field sourceField = sourceClass.getField(f.getName());
                if (null == sourceField)
                    sourceField = sourceClass.getDeclaredField(f.getName());
                if (null == sourceField)
                    continue;
                Object sourceVal = getValue(sourceObj, f.getName());
                // 如果类型一致
                if (sourceVal.getClass() == f.getClass()) {
                    setValue(newObj, f.getName(), sourceVal);
                } else {
                    // 如果类型不一致，则默认当作内容一致去处理
                    Object newVal = copy(sourceVal, f.getClass().newInstance());
                    setValue(newObj, f.getName(), newVal);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) newObj;
    }

    /**
     * 拷贝类
     *
     * @param sourceObj 被拷贝的类
     * @param newObj    新类
     * @param <T>
     * @return
     */
    public static <T> T copy(@NonNull Object sourceObj, @NonNull Object newObj) {
        try {
            Class newClass = newObj.getClass();
            List<Field> newFieldList = StringUtils.asList(newClass.getFields());
            Field[] newFieldList2 = newClass.getDeclaredFields();
            for (Field f : newFieldList2) {
                if (!newFieldList.contains(f)) {
                    newFieldList.add(f);
                }
            }
            Class sourceClass = sourceObj.getClass();
            for (Field f : newFieldList) {
                Field sourceField = sourceClass.getField(f.getName());
                if (null == sourceField)
                    sourceField = sourceClass.getDeclaredField(f.getName());
                if (null == sourceField)
                    continue;
                Object sourceVal = getValue(sourceObj, f.getName());
                // 如果类型一致
                if (sourceVal.getClass() == f.getClass()) {
                    setValue(newObj, f.getName(), sourceVal);
                } else {
                    // 如果类型不一致，则默认当作内容一致去处理
                    Object newVal = copy(sourceVal, f.getClass().newInstance());
                    setValue(newObj, f.getName(), newVal);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) newObj;
    }

    public static byte[] getByte(Serializable serializable) {
        ObjectOutputStream oos = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(serializable);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(oos);
        }
        return null;
    }

    public static boolean isInt(Class<?> clazz) {
        return int.class.isAssignableFrom(clazz) || Integer.class.isAssignableFrom(clazz);
    }

    public static boolean isLong(Class<?> clazz) {
        return long.class.isAssignableFrom(clazz) || Long.class.isAssignableFrom(clazz);
    }

    public static boolean isDouble(Class<?> clazz) {
        return double.class.isAssignableFrom(clazz) || Double.class.isAssignableFrom(clazz);
    }

    public static boolean isShort(Class<?> clazz) {
        return short.class.isAssignableFrom(clazz) || Short.class.isAssignableFrom(clazz);
    }

    public static boolean isByte(Class<?> clazz) {
        return byte.class.isAssignableFrom(clazz) || Byte.class.isAssignableFrom(clazz);
    }

    public static boolean isString(Class<?> clazz) {
        return String.class.isAssignableFrom(clazz) || CharSequence.class.isAssignableFrom(clazz);
    }

    public static boolean isChar(Class<?> clazz) {
        return char.class.isAssignableFrom(clazz);
    }

    public static boolean isBoolean(Class<?> clazz) {
        return boolean.class.isAssignableFrom(clazz) || Boolean.class.isAssignableFrom(clazz);
    }

    public static boolean isFloat(Class<?> clazz) {
        return float.class.isAssignableFrom(clazz) || Float.class.isAssignableFrom(clazz);
    }

    public static boolean isSerializable(Class<?> clazz) {
        return Serializable.class.isAssignableFrom(clazz);
    }

    public static boolean isNumber(Class<?> clazz) {
        return isShort(clazz) || isInt(clazz) || isLong(clazz);
    }

    public static boolean isDecimal(Class<?> clazz) {
        return isFloat(clazz) || isDouble(clazz);
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
        String key = null == instance ? clazz.getName() + "_" + fieldName : instance.getClass().getName() + "_" + fieldName;
        SoftReference<Field> softField = fieldCache.get(key);
        if (null == softField || null != softField.get()) {
            Field field = clazz.getDeclaredField(fieldName);
            if (null == field)
                field = clazz.getField(fieldName);
            if (null == field) {
                throw new NoSuchFieldError(fieldName + " 不存在!");
            }
            if (field.getModifiers() != Modifier.PUBLIC) {
                field.setAccessible(true);
            }
            fieldCache.put(key, softField = new SoftReference<>(field));
        }
        softField.get().set(instance, val);
    }

    /**
     * 设置静态类属性
     *
     * @param clazz      要设置的class
     * @param fieldNames 属性名
     * @param vals       值
     */
    public static void setValue(Class clazz, String[] fieldNames, Object[] vals) throws IllegalAccessException, NoSuchFieldException {
        setValue(clazz, null, fieldNames, vals);
    }

    /**
     * 设置实例属性
     *
     * @param instance   要设置的对象，
     * @param fieldNames 属性名
     * @param vals       值
     */
    public static void setValue(Object instance, String[] fieldNames, Object[] vals) throws IllegalAccessException, NoSuchFieldException {
        setValue(instance.getClass(), instance, fieldNames, vals);
    }

    /**
     * 设置属性
     *
     * @param clazz      要设置的class
     * @param instance   要设置的对象，如果是静态类则为null
     * @param fieldNames 属性名
     * @param vals       值
     */
    private static void setValue(Class clazz, Object instance, String[] fieldNames, Object[] vals) throws IllegalAccessException, NoSuchFieldException {
        if (null == clazz)
            throw new NullPointerException("clazz 不能为空");
        if (StringUtils.isNullOrEmpty(fieldNames) || StringUtils.isNullOrEmpty(vals)) {
            throw new RuntimeException("fieldNames 与 vals 不匹配");
        }
        if (StringUtils.len(fieldNames) != StringUtils.len(vals)) {
            throw new RuntimeException("fieldNames 与 vals 不匹配");
        }
        int index = 0;
        for (String fieldName : fieldNames) {
            String key = null == instance ? clazz.getName() + "_" + fieldName : instance.getClass().getName() + "_" + fieldName;
            SoftReference<Field> softField = fieldCache.get(key);
            if (null == softField || null == softField.get()) {
                Field field = clazz.getDeclaredField(fieldName);
                if (null == field)
                    field = clazz.getField(fieldName);
                if (null == field) {
                    throw new NoSuchFieldError(fieldName + " 不存在!");
                }
                if (field.getModifiers() != Modifier.PUBLIC) {
                    field.setAccessible(true);
                }
                fieldCache.put(key, softField = new SoftReference<>(field));
            }
            softField.get().set(instance, vals[index]);
            index++;
        }
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
        String key = null == obj ? clazz.getName() + "_" + fieldName : obj.getClass().getName() + "_" + fieldName;
        SoftReference<Field> softField = fieldCache.get(key);
        if (null == softField || null == softField.get()) {
            Field field = clazz.getDeclaredField(fieldName);
            if (null == field)
                field = clazz.getField(fieldName);
            if (null == field)
                throw new NoSuchFieldError(fieldName + " 不存在!");
            if (field.getModifiers() != Modifier.PUBLIC) {
                field.setAccessible(true);
            }
            fieldCache.put(key, softField = new SoftReference<>(field));
        }
        return (T) softField.get().get(obj);
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
        if (method.getModifiers() != Modifier.PUBLIC) {
            method.setAccessible(true);
        }
        if (null != params && 0 != params.length)
            result = method.invoke(obj, params);
        else
            result = method.invoke(obj);
        return (T) result;
    }

}
