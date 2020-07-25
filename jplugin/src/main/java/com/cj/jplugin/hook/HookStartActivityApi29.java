package com.cj.jplugin.hook;

import android.content.Context;
import android.content.Intent;
import android.os.Message;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * 变更：
 * IActivityManager -> IActivityTaskManager
 * ActivityManager -> ActivityTaskManager
 */
public class HookStartActivityApi29 extends HookStartActivityApi28 {

    public HookStartActivityApi29(Context context, Class<?> proxyClass) {
        super(context, proxyClass);
    }

    @Override
    public void hookStartActivity() {
        try {
            Class<?> iActivityManagerClass = Class.forName("android.app.IActivityTaskManager");
            //获取Singleton实例
            Class<?> activityManagerClass = Class.forName("android.app.ActivityTaskManager");
            Field iActivityManagerSingletonField = activityManagerClass.getDeclaredField("IActivityTaskManagerSingleton");
            iActivityManagerSingletonField.setAccessible(true);
            Object singleton = iActivityManagerSingletonField.get(null);
            //从Singleton实例中获取IActivityTaskManager对象mInstance
            Class<?> singletonClass = Class.forName("android.util.Singleton");
            //********************
            Method getMethod = singletonClass.getDeclaredMethod("get");
            Object amTemp = getMethod.invoke(singleton);//调用get方法获取一下，否则Singleton.mInstance == null
            //********************
            Field mInstanceField = singletonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            Object am = mInstanceField.get(singleton);
            Object amProxy = Proxy.newProxyInstance(
                    HookStartActivityApi29.class.getClassLoader(),
                    new Class[]{iActivityManagerClass},
                    new StartActivityInvocationHandler(context, am, proxyClass)
            );
            //重新指定代理的IActivityTaskManager实例
            mInstanceField.set(singleton, amProxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
