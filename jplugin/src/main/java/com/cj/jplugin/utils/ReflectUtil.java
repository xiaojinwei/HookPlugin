package com.cj.jplugin.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectUtil {
    /**
     * 反射获取字段上的值
     * @param clazz
     * @param target
     * @param name
     * @return
     * @throws
     */
    public static Object getFieldValue(Class clazz, Object target, String name) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }

    /**
     * 获取Field
     * @param clazz
     * @param name
     * @return
     * @throws
     */
    public static Field getField(Class clazz, String name) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    /**
     * 返回设置字段上的值
     * @param clazz
     * @param target
     * @param name
     * @param value
     * @throws
     */
    public static void setFieldValue(Class clazz, Object target, String name, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    public static Method getMethod(Class clazz, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method method = clazz.getDeclaredMethod(name, parameterTypes);
        method.setAccessible(true);
        return method;
    }

    public static Object invoke(Class clazz,Object target, String name, Class<?>[] parameterTypes,Object[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = getMethod(clazz, name, parameterTypes);
        return method.invoke(target,args);
    }

    public static Constructor getConstructor(Class clazz, Class<?>... parameterTypes) throws NoSuchMethodException {
        Constructor constructor = clazz.getDeclaredConstructor(parameterTypes);
        constructor.setAccessible(true);
        return constructor;
    }

    public static Object newInstance(Class clazz, Class<?>[] parameterTypes,Object[] initargs) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor constructor = getConstructor(clazz, parameterTypes);
        Object obj = constructor.newInstance(initargs);
        return obj;
    }
}
