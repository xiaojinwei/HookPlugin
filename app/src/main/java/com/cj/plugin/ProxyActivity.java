package com.cj.plugin;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cj.plugin.R;

/**
 * 占坑Activity
 */
public class ProxyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy);
        System.out.println("---------------------ProxyActivity-onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("---------------------ProxyActivity-onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("---------------------ProxyActivity-onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("---------------------ProxyActivity-onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("---------------------ProxyActivity-onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("---------------------ProxyActivity-onDestroy");
    }
}
