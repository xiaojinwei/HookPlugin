package com.cj.oneplugin;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.cj.jplugin.base.BasePluginActivity;

public class SecondActivity extends BasePluginActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second2);
        System.out.println("--------------------oneplugin-SecondActivity-onCreate");
    }

    @Override
    public String getApkPackageName() {
        return BuildConfig.APPLICATION_ID;
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("--------------------oneplugin-SecondActivity-onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("--------------------oneplugin-SecondActivity-onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("--------------------oneplugin-SecondActivity-onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("--------------------oneplugin-SecondActivity-onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("--------------------oneplugin-SecondActivity-onDestroy");
    }

}
