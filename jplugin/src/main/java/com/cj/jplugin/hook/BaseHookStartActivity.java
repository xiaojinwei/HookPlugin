package com.cj.jplugin.hook;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

public abstract class BaseHookStartActivity implements HookStartActivity {
    protected static final String EXTRA_ORIGIN_INTENT = "extra_origin_intent";
    protected Context context;
    protected Class<?> proxyClass;//清单文件中注册的临时Activity，用于清单文件的检测
    protected PackageManager packageManager;

    public BaseHookStartActivity(Context context, Class<?> proxyClass) {
        this.context = context;
        this.proxyClass = proxyClass;
        packageManager = context.getPackageManager();
    }

    public boolean isExistIntentActivity(Intent intent){
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL);
        return resolveInfos != null && !resolveInfos.isEmpty();
    }

    public boolean isExistIntentService(Intent intent){
        List<ResolveInfo> resolveInfos = packageManager.queryIntentServices(intent, PackageManager.MATCH_ALL);
        return resolveInfos != null && !resolveInfos.isEmpty();
    }

    //SDK < 26 , SDK >= 26
    //SDK 26的Binder使用了AIDL
    public abstract void hookStartActivity();
    //SDK < 28 , SDK >= 28
    //SDK 28启动Activity使用了TRANSACTION
    public abstract void hookLaunchActivity();
}
