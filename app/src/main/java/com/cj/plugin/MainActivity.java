package com.cj.plugin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.cj.jplugin.PluginManager;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void click(View view) {
        try {
            Class clazz = getClassLoader().loadClass("com.cj.oneplugin.SecondActivity");
            Intent intent = new Intent(this,clazz);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void click2(View view) {
        try {
            Intent intent = new Intent();
            intent.setClassName(this,"com.cj.twoplugin.SecondActivity");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void click3(View view){
        try {
            Class clazz = getClassLoader().loadClass("com.cj.plugin.ThirdActivity");
            Intent intent = new Intent(this,clazz);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void click4(View view){
        PluginManager.getInstance().startLauncherActivity("com.cj.oneplugin");
    }

}
