package com.cj.jplugin.hook;

import android.content.Context;
import android.os.Build;

public class HookActivityProxy implements HookStartActivity {

    private HookStartActivity hook;

    public HookActivityProxy(Context context, Class<?> proxyClass) {
        if (Build.VERSION.SDK_INT <= 25) {
            this.hook = new HookStartActivityApi25(context,proxyClass);
        } else if (Build.VERSION.SDK_INT <= 27) {
            this.hook = new HookStartActivityApi26(context,proxyClass);
        }else {
            this.hook = new HookStartActivityApi28(context,proxyClass);
        }
    }

    @Override
    public void hookStartActivity() {
        hook.hookStartActivity();
    }

    @Override
    public void hookLaunchActivity() {
        hook.hookLaunchActivity();
    }
}
