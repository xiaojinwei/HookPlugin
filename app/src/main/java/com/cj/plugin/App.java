package com.cj.plugin;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cj.jplugin.PluginManager;
import com.cj.plugin.utils.Util;

import java.io.File;

public class App extends Application {

    private final static String sApkName = "oneplugin-debug.apk";
    private final static String sApkName2 = "twoplugin-debug.apk";

    private static Context context;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        context = this;
        //插件初始化
        PluginManager.getInstance().init(this);
        PluginManager.getInstance().hookActivity(ProxyActivity.class);
        //加载one plugin
        File extractFile = Util.download(this,sApkName);
        PluginManager.getInstance().loadApk(extractFile.getPath());
        //加载two plugin
        File extractFile2 = Util.download(this,sApkName2);
        PluginManager.getInstance().loadApk(extractFile2.getPath());

    }


    @Override
    public void onCreate() {
        super.onCreate();

        init();
    }

    private void init() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                System.out.println("----------------------init--onActivityCreated : " + activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                System.out.println("----------------------init--onActivityStarted : " + activity);
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                System.out.println("----------------------init--onActivityResumed : " + activity);
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                System.out.println("----------------------init--onActivityPaused : " + activity);
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                System.out.println("----------------------init--onActivityStopped : " + activity);
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
                System.out.println("----------------------init--onActivitySaveInstanceState : " + activity);
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                System.out.println("----------------------init--onActivityDestroyed : " + activity);
            }
        });
    }

    public static Context getContext(){
        return context;
    }

}
