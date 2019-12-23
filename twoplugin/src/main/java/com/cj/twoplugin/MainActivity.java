package com.cj.twoplugin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.cj.jplugin.base.BasePluginActivity;

public class MainActivity extends BasePluginActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public String getApkPackageName() {
        return BuildConfig.APPLICATION_ID;
    }
}
