package com.cj.twoplugin;

import android.os.Bundle;
import android.widget.TextView;

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
        TextView textView = findViewById(R.id.textView);
        textView.setText(textView.getText().toString() + "\n" + getClass().getName());
    }
}
