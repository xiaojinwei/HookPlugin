package com.cj.twoplugin;

import android.os.Bundle;

import com.cj.jplugin.base.BasePluginActivity;

public class SecondActivity extends BasePluginActivity {
    @Override
    public String getApkPackageName() {
        return BuildConfig.APPLICATION_ID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
}
