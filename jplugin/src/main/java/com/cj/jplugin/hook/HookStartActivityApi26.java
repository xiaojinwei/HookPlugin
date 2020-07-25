package com.cj.jplugin.hook;

import android.content.Context;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * 变更：
 * ActivityManagerNative -> ActivityManager 使用了AIDL
 */
public class HookStartActivityApi26 extends HookStartActivityApi25 {

    public HookStartActivityApi26(Context context, Class<?> proxyClass) {
        super(context, proxyClass);
    }

    @Override
    public void hookStartActivity() {
        try {
            Class<?> iActivityManagerClass = Class.forName("android.app.IActivityManager");
            //获取Singleton实例
            Class<?> activityManagerClass = Class.forName("android.app.ActivityManager");
            Field iActivityManagerSingletonField = activityManagerClass.getDeclaredField("IActivityManagerSingleton");
            iActivityManagerSingletonField.setAccessible(true);
            Object singleton = iActivityManagerSingletonField.get(null);
            //从Singleton实例中获取IActivityManager对象mInstance
            Class<?> singletonClass = Class.forName("android.util.Singleton");
            Field mInstanceField = singletonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            Object am = mInstanceField.get(singleton);
            Object amProxy = Proxy.newProxyInstance(
                    HookStartActivityApi26.class.getClassLoader(),
                    new Class[]{iActivityManagerClass},
                    new StartActivityInvocationHandler(context, am, proxyClass)
            );
            //重新指定代理的IActivityManager实例
            mInstanceField.set(singleton, amProxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
