package com.cj.jplugin.hook;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class HookStartActivityApi25 extends BaseHookStartActivity {

    public static final int LAUNCH_ACTIVITY = 100;

    public HookStartActivityApi25(Context context, Class<?> proxyClass) {
        super(context, proxyClass);
    }

    @Override
    public void hookStartActivity() {
        try {
            Class<?> iamClass = Class.forName("android.app.IActivityManager");

            Class<?> amnClass = Class.forName("android.app.ActivityManagerNative");
            Field gDefaultField = amnClass.getDeclaredField("gDefault");
            gDefaultField.setAccessible(true);
            Object singleton = gDefaultField.get(null);

            Class<?> singletonClass = Class.forName("android.util.Singleton");
            Field mInstanceField = singletonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            Object iam = mInstanceField.get(singleton);//IActivityManager类型

            Object iamProxy = Proxy.newProxyInstance(iam.getClass().getClassLoader(),
                    iam.getClass().getInterfaces(),
                    new StartActivityInvocationHandler(context, iam, proxyClass)
            );
            //将Singleton中的mInstance替换成代理的iamProxy
            mInstanceField.set(singleton, iamProxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class StartActivityInvocationHandler implements InvocationHandler {
        private Object obj;
        private Context context;
        private Class<?> proxyClass;

        public StartActivityInvocationHandler(Context context, Object obj, Class<?> proxyClass) {
            this.context = context;
            this.obj = obj;
            this.proxyClass = proxyClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //替换Intent,过AndroidManifest.xml检测
            if ("startActivity".equals(method.getName())) {
                //1.首先获取原来的Intent
                Intent originIntent = (Intent) args[2];//第二个参数就是intent
                if(!isExistIntentActivity(originIntent)) {
                    //创建一个安全的Intent
                    Intent proxyIntent = new Intent(context, proxyClass);
                    //保存原来的Intent
                    proxyIntent.putExtra(EXTRA_ORIGIN_INTENT, originIntent);
                    //设置新的Intent过检测
                    args[2] = proxyIntent;
                }
            }
            return method.invoke(obj,args);
        }
    }

    @Override
    public void hookLaunchActivity() {
        try {
            // 获取ActivityThread
            Class<?> atClass = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThreadField = atClass.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThreadField.setAccessible(true);
            Object at = sCurrentActivityThreadField.get(null);
            //获取Handler mH
            Field mHField = atClass.getDeclaredField("mH");
            mHField.setAccessible(true);
            Object mH = mHField.get(at);
            //给mH设置Callback
            Class<?> handlerClass = Class.forName("android.os.Handler");
            Field mCallbackField = handlerClass.getDeclaredField("mCallback");
            mCallbackField.setAccessible(true);
            mCallbackField.set(mH,new HookCallback());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean handleCallbackMessage(Message msg){
        if (msg.what == LAUNCH_ACTIVITY){
            return handleLaunchActivity(msg);
        }
        return false;
    }

    private boolean handleLaunchActivity(Message msg) {
        try {
            //获取ActivityClientRecord
            Object record = msg.obj;
            Field intentField = record.getClass().getDeclaredField("intent");
            intentField.setAccessible(true);
            //代理的Intent，proxyIntent
            Intent intent = (Intent) intentField.get(record);
            if(intent.hasExtra(EXTRA_ORIGIN_INTENT)) {
                //获取原始的Intent
                Intent originIntent = intent.getParcelableExtra(EXTRA_ORIGIN_INTENT);
                //防止为null
                if (originIntent != null) {
                    //将原始的Intent重新设置回去，这样才能跳转到原始的Activity
                    intentField.set(record, originIntent);
                }

                //解决AppCompatActivity重新检测清单文件注册问题
                handleAppCompatActivityCheck(intent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 解决AppCompatActivity重新检测清单文件注册问题
     * 如果使用的Activity集成的是AppCompatActivity，它会重新检测该Activity是否注册，所以这里也需要hook
     * @param intent
     */
    protected void handleAppCompatActivityCheck(Intent intent){
        try {
            //解决AppCompatActivity重新检测清单文件注册问题
            Class<?> atClass = Class.forName("android.app.ActivityThread");
            Method getPackageManagerMethod = atClass.getDeclaredMethod("getPackageManager");
            getPackageManagerMethod.setAccessible(true);
            Object ipm = getPackageManagerMethod.invoke(null);
            Object ipmProxy = Proxy.newProxyInstance(ipm.getClass().getClassLoader(),
                    ipm.getClass().getInterfaces(), new PackageManagerInvocationHandler(ipm, intent.getComponent()));
            Field sPackageManagerField = atClass.getDeclaredField("sPackageManager");
            sPackageManagerField.setAccessible(true);
            sPackageManagerField.set(null, ipmProxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class PackageManagerInvocationHandler implements InvocationHandler{
        private Object obj;
        private ComponentName proxyClass;

        public PackageManagerInvocationHandler(Object obj,ComponentName proxyClass) {
            this.obj = obj;
            this.proxyClass = proxyClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("getActivityInfo".equals(method.getName())) {
                args[0] = proxyClass;
            }
            Object invoke = method.invoke(obj, args);
            /*if ("getActivityInfo".equals(method.getName())) {
                ai.parentActivityName = originClass.getClassName();
            }*/
            return invoke;
        }
    }

    class HookCallback implements Handler.Callback{

        @Override
        public boolean handleMessage(Message msg) {
            return handleCallbackMessage(msg);
        }

    }
}
