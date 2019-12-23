package com.cj.jplugin.hook;

public interface HookStartActivity {
    /**
     * AMS检测之前
     */
    void hookStartActivity();

    /**
     * AMS检测之后
     */
    void hookLaunchActivity();
}
