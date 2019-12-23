package com.cj.oneplugin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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

    public void click(View view){
        Intent intent = new Intent(this,SecondActivity.class);
        startActivity(intent);
    }
}
