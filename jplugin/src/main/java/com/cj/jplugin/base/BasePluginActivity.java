package com.cj.jplugin.base;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import androidx.appcompat.app.AppCompatActivity;
import com.cj.jplugin.PluginManager;
import com.cj.jplugin.plugin.PluginInfo;

/**
 * 插件中的Activity需要继承BasePluginActivity
 * 主要用于资源隔离
 */
public abstract class BasePluginActivity extends AppCompatActivity {

    private PluginInfo pluginInfo;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        pluginInfo = PluginManager.getInstance().getPluginInfo(getApkPackageName());
    }

    /**
     * 插件apk的包名
     * @return
     */
    public abstract String getApkPackageName();

    @Override
    public Resources getResources() {
        return (pluginInfo == null || pluginInfo.getResources() == null) ? super.getResources() :  pluginInfo.getResources();
    }

    @Override
    public AssetManager getAssets() {
        return (pluginInfo == null || pluginInfo.getAssetManager() == null) ? super.getAssets() : pluginInfo.getAssetManager();
    }

}
